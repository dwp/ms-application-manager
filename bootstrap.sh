#!/bin/sh
if [ -n "${V3_BOOTSTRAP_ENABLED}" ]; then
  touch repo.list
  apk add --repositories-file=repo.list --allow-untrusted --no-network --no-cache ./*.apk

  echo "using v3 deployment"
  echo "INFO: resolving external certificates from Health PKI API"

  PAYLOAD_JSON_FILE=payload.json
  PAYLOAD=$(jq -r -n --arg domain "$COMMON_NAME" --arg altNames "$ALT_NAMES" '. | {"domain": $domain, "sans": $altNames | split(",") }')

  curl -X POST -H "x-dwp-requested-by: ${PRODUCT}-${ENV_ID}" -d "$PAYLOAD" "${HEALTH_PKI_ENDPOINT}/issue/external" > "${PAYLOAD_JSON_FILE}"

  echo "INFO: getting public cert"
  jq -r '.certificate' ${PAYLOAD_JSON_FILE} > cert.crt
  echo "INFO: getting private key"
  jq -r '.privateKey' ${PAYLOAD_JSON_FILE}  > cert.key
  echo "INFO: getting CA cert"
  jq -r '.chain' ${PAYLOAD_JSON_FILE} > ca.crt

  cat ca.crt >> cert.crt
  echo "importing cert to keystore..."
  KEY_STORE_PASSWORD=$(aws ssm get-parameter --name "${PIP_BANK_VALIDATION_KEY_STORE_PASSWORD}" --with-decryption --output json --region eu-west-2 | jq --raw-output '.Parameter.Value')
  openssl pkcs12 -export -in cert.crt -inkey cert.key \
                 -out cert.p12 \
                 -CAfile ca.crt -caname root \
                 -passout pass:"${KEY_STORE_PASSWORD}"

  keytool -importkeystore \
          -deststorepass "${KEY_STORE_PASSWORD}" -destkeypass "${KEY_STORE_PASSWORD}" -destkeystore "${PIP_BANK_VALIDATION_KEY_STORE}" \
          -srckeystore cert.p12 -srcstoretype PKCS12 -srcstorepass "${KEY_STORE_PASSWORD}"

  # Creating the truststore for the service

  if [ "${V3_CREATE_TRUSTSTORE}" = "true" ]; then
    echo "importing ca to truststore..."
    TRUST_STORE_PASSWORD=$(aws ssm get-parameter --name "${PIP_BANK_VALIDATION_TRUST_STORE_PASSWORD}" --with-decryption --output json --region eu-west-2 | jq --raw-output '.Parameter.Value')
    keytool -import \
          -trustcacerts \
          -keystore "${PIP_BANK_VALIDATION_TRUST_STORE}" -storepass "${TRUST_STORE_PASSWORD}" \
          -noprompt \
          -alias "${COMMON_NAME}".vault \
          -file ca.crt

    echo "getting public cert $CIS_ROOT_CA from SSM"
    aws ssm get-parameter --name "${CIS_ROOT_CA}" --with-decryption --output json --region eu-west-2 | jq --raw-output '.Parameter.Value' > cis_ca.pem
    echo "Putting cis_integration_root.pem into Java Truststore"
    keytool -import  -file cis_ca.pem -keystore "${PIP_BANK_VALIDATION_TRUST_STORE}" -noprompt -trustcacerts -alias "${COMMON_NAME}".cis -storepass "${TRUST_STORE_PASSWORD}"

  else
    echo "truststore creation skipped"
  fi
  apk del aws-cli jq curl openssl
fi

echo "starting app"
exec java -jar /ms-application-manager.jar "$@"


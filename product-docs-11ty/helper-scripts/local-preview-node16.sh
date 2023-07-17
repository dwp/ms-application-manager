#!/usr/bin/env sh
# shellcheck disable=SC3010,SC2181

HIGHLIGHT="\033[1;33;4;44m"
CLEAR="\033[0m"

#Run from the product-docs-11ty directory, so we can run eleventy commands
if [[ $(basename "$(pwd)") = "helper-scripts" ]]; then
  cd ..
fi

#Check version of node available
NODE_VERSION=$(node --version | cut -d '.' -f1)
if [[ "$NODE_VERSION" != "v16" ]]; then
  echo "${HIGHLIGHT}Eleventy requires node 16, exiting.${CLEAR}"
  exit 1
fi

#From the product-docs-11ty directory, the .gitlab-ci.yml file should be available one level up
GITLAB_CI_FILE=../.gitlab-ci.yml

#Default SERVICE_NAME and BRAND_COLOUR values
SERVICE_NAME='Update SERVICE_NAME variable'
BRAND_COLOUR='#00c0b5'

if [[ ! -f ${GITLAB_CI_FILE} ]]; then
  echo "${HIGHLIGHT}### No .gitlab-ci.yml file found, using defaults for SERVICE_NAME and BRAND_COLOUR variables ###${CLEAR}"
else
  echo "${HIGHLIGHT}### Attempting to parse SERVICE_NAME from ${GITLAB_CI_FILE} ###${CLEAR}"
  SERVICE_NAME_GL=$(grep 'SERVICE_NAME:' ${GITLAB_CI_FILE})
  if [[ $? -ne 0 ]]; then
    echo "${HIGHLIGHT}### Unable to parse SERVICE_NAME variable from ${GITLAB_CI_FILE}, using default value of \"${SERVICE_NAME}\" ###${CLEAR}"
  else
    SERVICE_NAME=$(echo "${SERVICE_NAME_GL}" | xargs | cut -f2- -d' ')
    echo "${HIGHLIGHT}### Found \"${SERVICE_NAME}\" ###${CLEAR}"
  fi

  echo "${HIGHLIGHT}### Attempting to parse BRAND_COLOUR from ${GITLAB_CI_FILE} ###${CLEAR}"
  BRAND_COLOUR_GL=$(grep 'BRAND_COLOUR:' ${GITLAB_CI_FILE})
  if [[ $? -ne 0 ]]; then
    echo "${HIGHLIGHT}### Unable to parse BRAND_COLOUR variable from ${GITLAB_CI_FILE}, using default brand colour ###${CLEAR}"
  else
    BRAND_COLOUR=$(echo "${BRAND_COLOUR_GL}" | xargs | cut -f2- -d' ')
    echo "${HIGHLIGHT}### Found \"${BRAND_COLOUR}\" ###${CLEAR}"
  fi
fi

echo "${HIGHLIGHT}### Performing 'npm install' ###${CLEAR}"
npm install

echo "${HIGHLIGHT}### Starting eleventy preview ###${CLEAR}" 
SERVICE_NAME=${SERVICE_NAME} BRAND_COLOUR=${BRAND_COLOUR} npx eleventy --serve

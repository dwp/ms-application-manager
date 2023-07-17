FROM amazoncorretto:11.0.19-alpine@sha256:95ad6229a4bafb4e81651416b955542fb9430c057bb42198422fb0d8adeb6a99
COPY target/ms-application-manager-*.jar /ms-application-manager.jar
COPY bootstrap.sh /bootstrap.sh

RUN apk upgrade \
    && apk --no-cache fetch -R aws-cli jq curl openssl \
    && chmod +x /bootstrap.sh

EXPOSE 8080

ENTRYPOINT ["./bootstrap.sh"]

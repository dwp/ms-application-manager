FROM amazoncorretto:17.0.8-alpine3.17@sha256:1d23f2cd0f81bd51b798d860c8b7966692af5e02c1e8e35d75faa4f4691c1131

COPY target/ms-application-manager-*.jar /ms-application-manager.jar
COPY bootstrap.sh /bootstrap.sh

COPY --from=pik94420.live.dynatrace.com/linux/oneagent-codemodules-musl:java / /
ENV LD_PRELOAD /opt/dynatrace/oneagent/agent/lib64/liboneagentproc.so

RUN apk upgrade \
    && apk --no-cache fetch -R aws-cli jq curl openssl \
    && chmod +x /bootstrap.sh

EXPOSE 8080

ENTRYPOINT ["./bootstrap.sh"]
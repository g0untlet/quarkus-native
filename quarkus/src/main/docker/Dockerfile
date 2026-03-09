# This Dockerfile expects a native executable built at target/*-runner
# It uses a minimal UBI image optimized for native binaries

FROM registry.access.redhat.com/ubi9/ubi-minimal:9.4 
WORKDIR /work/
RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && mkdir -p /work/application \
    && chown -R 1001 /work/application

COPY target/*-runner /work/application/application 
RUN chmod +x /work/application/application

EXPOSE 8080
USER 1001
ENTRYPOINT ["/work/application/application", "-Dquarkus.http.host=0.0.0.0"]

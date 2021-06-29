FROM registry.access.redhat.com/ubi8-minimal

ENV KEYCLOAK_VERSION 14.0.0
ENV JDBC_POSTGRES_VERSION 42.2.5
ENV JDBC_MYSQL_VERSION 8.0.22
ENV JDBC_MARIADB_VERSION 2.5.4
ENV JDBC_MSSQL_VERSION 8.2.2.jre11

ENV LAUNCH_JBOSS_IN_BACKGROUND 1
ENV PROXY_ADDRESS_FORWARDING false
ENV JBOSS_HOME /opt/jboss/keycloak
ENV LANG en_US.UTF-8

ARG GIT_REPO
ARG GIT_BRANCH
ARG KEYCLOAK_DIST=https://github.com/keycloak/keycloak/releases/download/$KEYCLOAK_VERSION/keycloak-$KEYCLOAK_VERSION.tar.gz

USER root

RUN microdnf update -y && microdnf install -y glibc-langpack-en gzip hostname java-11-openjdk-headless openssl tar which && microdnf clean all

ADD tools /opt/jboss/tools
RUN /opt/jboss/tools/build-keycloak.sh

USER 1000

EXPOSE 8080
EXPOSE 8443

ENTRYPOINT [ "/opt/jboss/tools/docker-entrypoint.sh" ]

ARG KEYCLOAK_HOME=/opt/jboss/keycloak
ADD https://github.com/leroyguillaume/keycloak-bcrypt/releases/download/1.5.0/keycloak-bcrypt-1.5.0.jar  $KEYCLOAK_HOME/standalone/deployments/keycloak-bcrypt-1.5.0.jar
ADD https://repo1.maven.org/maven2/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar $KEYCLOAK_HOME/standalone/deployments/jbcrypt-0.4.jar

RUN cd $KEYCLOAK_HOME/standalone/deployments/ && ls
RUN /opt/jboss/keycloak/bin/jboss-cli.sh --command="module add --name=org.mindrot.jbcrypt --resources=$KEYCLOAK_HOME/standalone/deployments/jbcrypt-0.4.jar"
RUN /opt/jboss/keycloak/bin/jboss-cli.sh --command="module add --name=org.mindrot.keycloakbcrypt --resources=$KEYCLOAK_HOME/standalone/deployments/keycloak-bcrypt-1.5.0.jar"

RUN echo '>>>>>>>>>> RJB BCRYPT SETUP >>>>>>>>>>>>>'

CMD ["-b", "0.0.0.0"]




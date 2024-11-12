FROM bitnami/java:21

COPY ./target/config-server-0.0.1-SNAPSHOT.jar /usr/app/

COPY config-repo/ /usr/app/config-repo

WORKDIR /usr/app

RUN sh -c 'touch config-server-0.0.1-SNAPSHOT.jar'

RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y git

WORKDIR /usr/app/config-repo
RUN sh -c 'git config --global user.email "you@example.com"'
RUN git init .
RUN git add -A .
RUN sh -c 'git commit -m "Add application.properties"'

WORKDIR /usr/app

ENTRYPOINT ["java", "-jar", "config-server-0.0.1-SNAPSHOT.jar","--spring.profiles.active=dev"]
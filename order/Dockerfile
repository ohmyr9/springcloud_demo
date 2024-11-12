FROM bitnami/java:21

COPY ./target/order-0.0.1-SNAPSHOT.jar /usr/app/

WORKDIR /usr/app

ENTRYPOINT ["java", "-jar", "order-0.0.1-SNAPSHOT.jar","--spring.profiles.active=dev"]
FROM eclipse-temurin:17-jdk-alpine as builder

RUN mkdir /src

WORKDIR /src
COPY . .
RUN chmod +x gradlew && ./gradlew clean build

RUN mkdir /newrelic
WORKDIR /newrelic
RUN wget -nv -O newrelic.zip https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip \
    && unzip newrelic.zip \
    && rm newrelic.zip

# ----------------------------------------------

FROM eclipse-temurin:17-jre-alpine as pod

ENV PORT=8080
ENV PROFILE=dev

RUN adduser -D nonroot && mkdir -p /home/nonroot/app

USER nonroot

WORKDIR /home/nonroot/app

COPY --from=builder /src/build/libs/konsign-api-0.0.1-SNAPSHOT.jar /home/nonroot/app
COPY --from=builder /newrelic/newrelic/newrelic.jar /home/nonroot/app

CMD ["java", "-javaagent:newrelic.jar", "-Dspring.profiles.actives=dev", "-jar", "konsign-api-0.0.1-SNAPSHOT.jar"]
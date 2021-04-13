FROM gradle:7.0 as builder

COPY build.gradle .
COPY ./gradle.properties .
COPY ./settings.gradle .
COPY gradlew .
COPY ./gradle ./gradle
COPY ./src ./src

RUN gradle clean build

FROM openjdk:8-jre

COPY --from=builder /home/gradle/build/distributions/stacked-off-1.0.2.zip /stacked-off-1.0.2.zip
RUN unzip /stacked-off-1.0.2.zip -d /root

CMD [ "/root/stacked-off-1.0.2/bin/stacked-off" ]

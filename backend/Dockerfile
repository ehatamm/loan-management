FROM gradle:8.10-jdk21-alpine AS build

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
COPY src ./src

RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache wget && \
    addgroup -S spring && adduser -S spring -G spring

COPY --from=build /app/build/libs/*.jar app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]


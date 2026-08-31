# ---- Stage 1: Build ----
FROM eclipse-temurin:17-jdk-alpine AS deps

WORKDIR /deps

COPY mvnw .
RUN chmod +x mvnw
COPY .mvn ./.mvn

COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean install -DskipTests

RUN touch /deps-ready
# ---- Stage 1: Build ----
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

COPY mvnw .
RUN chmod +x mvnw
COPY .mvn ./.mvn

# it's a trick, download and cache the wrapper
RUN ./mvnw --version

COPY pom.xml .
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw clean package -DskipTests


# ---- Stage 2: Runtime ----
FROM eclipse-temurin:17-jdk-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-Xmx256m", "-XX:+UseContainerSupport", "-jar", "app.jar"]
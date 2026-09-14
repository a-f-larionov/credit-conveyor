# ---- Stage 1: Build ----
FROM common-api-starter AS build

WORKDIR /app

COPY mvnw .
RUN chmod +x mvnw
COPY .mvn ./.mvn

COPY pom.xml .
RUN --mount=type=cache,id=maven-repo,target=/root/.m2/repository \
    ./mvnw dependency:go-offline -B

COPY src ./src
RUN --mount=type=cache,id=maven-repo,target=/root/.m2/repository \
    ./mvnw clean package -DskipTests


# ---- Stage 2: Runtime ----
FROM eclipse-temurin:17-jdk-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-Xmx256m", "-XX:+UseContainerSupport", "-jar", "app.jar"]
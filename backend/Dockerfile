# ---- Stage 1: build the fat jar ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Cache dependencies first (only re-downloads when pom.xml changes)
COPY pom.xml .
RUN mvn -q -e dependency:go-offline

# Build the application
COPY src ./src
RUN mvn -q clean package -DskipTests

# ---- Stage 2: slim runtime ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# The Spring Boot fat jar (artifactId-version.jar from pom.xml)
COPY --from=build /build/target/java-service-0.1.0.jar app.jar

# Default port; overridden by the PORT env var at runtime.
EXPOSE 8081

# Reads PORT, SPRING_PROFILES_ACTIVE, DB_*, APP_JWT_SECRET, GROQ_API_KEY from the environment.
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

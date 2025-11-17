# =============================
# Multi-stage Dockerfile for Spring Boot
# =============================
# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the service port (replace accordingly)
ARG SERVICE_PORT=8082
EXPOSE ${SERVICE_PORT}

# Set JVM memory limits to 700 MB
ENV JAVA_OPTS="-Xmx700m -Xms512m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

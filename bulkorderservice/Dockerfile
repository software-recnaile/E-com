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

# Create config directory for credentials
RUN mkdir -p /app/config

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the service port
ARG SERVICE_PORT=8087
EXPOSE ${SERVICE_PORT}

# Set JVM memory limits to 700 MB
ENV JAVA_OPTS="-Xmx700m -Xms512m -XX:+UseG1GC"

# Create a script to handle credentials
RUN echo '#!/bin/bash\n\
if [ -f "/app/config/credentials.json" ]; then\n\
  echo "Using credentials file from /app/config/credentials.json"\n\
  echo "File size: $(wc -c < /app/config/credentials.json) bytes"\n\
else\n\
  echo "Warning: No credentials file found at /app/config/credentials.json"\n\
  echo "Please mount credentials.json to /app/config/credentials.json"\n\
fi\n\
exec java $JAVA_OPTS -jar app.jar' > /app/entrypoint.sh

RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]
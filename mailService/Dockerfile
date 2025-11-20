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

# Copy credentials file (if available during build)
COPY docker/credentials.json /app/config/credentials.json

# Expose the service port
EXPOSE 8089

# Set JVM memory limits to 700 MB
ENV JAVA_OPTS="-Xmx700m -Xms512m -XX:+UseG1GC"

# Create a script to handle credentials
RUN echo '#!/bin/bash\n\
echo "=== Mail Service Starting ==="\n\
echo "Checking for credentials..."\n\
if [ -f "/app/config/credentials.json" ]; then\n\
  echo "✅ Using credentials file: /app/config/credentials.json"\n\
  echo "📁 File size: $(wc -c < /app/config/credentials.json) bytes"\n\
  echo "📄 File content preview:"\n\
  head -5 /app/config/credentials.json\n\
else\n\
  echo "❌ WARNING: No credentials file found at /app/config/credentials.json"\n\
  echo "💡 Please ensure credentials.json is mounted to /app/config/credentials.json"\n\
  echo "💡 Or check if docker/credentials.json exists in build context"\n\
fi\n\
echo "Starting application..."\n\
exec java $JAVA_OPTS -jar app.jar' > /app/entrypoint.sh

RUN chmod +x /app/entrypoint.sh

ENTRYPOINT ["/app/entrypoint.sh"]
# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# First download dependencies (improves layer caching)
RUN mvn dependency:go-offline
COPY src ./src
# Build the application
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Create config directory and copy credentials
RUN mkdir -p /app/config
COPY src/main/resources/credentials.json /app/config/credentials.json

## Environment variables for configuration
#ENV GOOGLE_CREDENTIALS=/app/config/credentials.json
#ENV GOOGLE_SHEETS_SPREADSHEET_ID=your-spreadsheet-id
#ENV GOOGLE_SHEETS_SHEET_NAME=Submissions

EXPOSE 8090
ENTRYPOINT ["java", "-jar", "app.jar"]
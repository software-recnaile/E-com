FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar



# Create config directory and copy credentials
RUN mkdir -p /app/config
COPY src/main/resources/credentials.json /app/config/credentials.json

## Environment variables for configuration
#ENV GOOGLE_CREDENTIALS=/app/config/credentials.json
#ENV GOOGLE_SHEETS_SPREADSHEET_ID=your-spreadsheet-id
#ENV GOOGLE_SHEETS_SHEET_NAME=Submissions
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]


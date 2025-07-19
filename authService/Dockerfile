# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

## -------- Build Stage --------
#FROM maven:3.9.5-eclipse-temurin-21 AS build
#WORKDIR /app
#
## Copy project files
#COPY pom.xml .
#RUN mvn dependency:go-offline
#
#COPY src ./src
#
## Package the app
#RUN mvn clean package -DskipTests
#
## -------- Run Stage --------
#FROM eclipse-temurin:21-jdk-jammy
#WORKDIR /app
#
## Copy jar from build stage
#COPY --from=build /app/target/*.jar app.jar
#
#EXPOSE 8081
#
## Start the service
#ENTRYPOINT ["java", "-jar", "app.jar"]

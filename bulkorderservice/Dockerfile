#
#FROM maven:3.9.6-eclipse-temurin-21 AS build
#WORKDIR /app
#
#
#
#COPY pom.xml .
#RUN mvn dependency:go-offline
#COPY src ./src
#RUN mvn clean package -DskipTests
##
### Run stage
##FROM eclipse-temurin:21-jre-jammy
##WORKDIR /app
##COPY --from=build /app/target/*.jar app.jar
##EXPOSE 8091
##ENTRYPOINT ["java", "-jar", "app.jar"]
#
#
#FROM eclipse-temurin:21-jre-jammy
#WORKDIR /app
#COPY target/bulkorderservice-*.jar app.jar
#COPY docker/credentials.json /app/config/credentials.json
#EXPOSE 8091
#ENTRYPOINT ["java", "-jar", "app.jar"]


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

# Copy the built JAR from the build stage
COPY --from=build /app/target/bulkorderservice-*.jar app.jar

# Create config directory and copy credentials
RUN mkdir -p /app/config
COPY docker/credentials.json /app/config/credentials.json

EXPOSE 8091
ENTRYPOINT ["java", "-jar", "app.jar"]
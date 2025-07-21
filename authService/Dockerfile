#FROM maven:3.9.6-eclipse-temurin-21 AS build
#WORKDIR /app
#
#
#
#
#COPY pom.xml .
#RUN mvn dependency:go-offline
#COPY src ./src
#RUN mvn clean package -DskipTests
#
## Run stage
#FROM eclipse-temurin:21-jre-jammy
#WORKDIR /app
#COPY --from=build /app/target/*.jar app.jar
#EXPOSE 8081
#ENTRYPOINT ["java", "-jar", "app.jar"]


# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# First copy only POM to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Then copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
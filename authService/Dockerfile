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
# ---------- Build Stage ----------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests


# ---------- Run Stage ----------
FROM eclipse-temurin:21-jre-jammy

# Add DNS & SSL packages required for MongoDB Atlas SRV
RUN apt-get update && \
    apt-get install -y dnsutils ca-certificates netbase && \
    update-ca-certificates && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

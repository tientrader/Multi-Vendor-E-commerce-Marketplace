# Stage 1: build
# Start with a Maven image based on Alpine with JDK 21
FROM maven:3.9.8-eclipse-temurin-21-alpine AS build

# Copy source code and pom.xml file to /app folder
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build source code with Maven
RUN mvn package -DskipTests

# Stage 2: create image
# Start with OpenJDK 21 Alpine image for a smaller image
FROM eclipse-temurin:21-alpine

# Set working folder to /app and copy the compiled file from above step
WORKDIR /app
COPY --from=build /app/target/*.jar api-gateway-0.0.1-SNAPSHOT.jar

# Expose the port the application will run on
EXPOSE 8888

# Command to run the application
ENTRYPOINT ["java", "-jar", "api-gateway-0.0.1-SNAPSHOT.jar"]
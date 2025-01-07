# Stage 1: build
# Start with a Maven image that includes JDK 21
FROM maven:3.9.8-amazoncorretto-21 AS build

# Copy source code and pom.xml file to /app folder
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build source code with Maven
RUN mvn package -DskipTests

# Stage 2: create image
# Start with Amazon Corretto JDK 21
FROM amazoncorretto:21.0.4

# Set working folder to /app and copy the compiled file from above step
WORKDIR /app
COPY --from=build /app/target/*.jar payment-service-0.0.1-SNAPSHOT.jar

# Expose the port the application will run on
EXPOSE 8087

# Command to run the application
ENTRYPOINT ["java", "-jar", "payment-service-0.0.1-SNAPSHOT.jar"]
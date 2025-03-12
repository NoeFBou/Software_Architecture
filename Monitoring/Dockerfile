# Use an official Maven image as the base image
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and the project files to the container
COPY pom.xml .  
COPY src ./src  

# Build the application using Maven
RUN mvn clean package -DskipTests && cp target/*.jar app.jar  

# Use a Debian-based OpenJDK image
FROM openjdk:17-slim  

# Set the working directory in the container
WORKDIR /app  

# Copy the built JAR file from the previous stage to the container
COPY --from=build /app/app.jar .  

# Install Docker CLI (ensure it's available)
RUN apt-get update && apt-get install -y docker.io  

# Expose the application port
EXPOSE 8080  

# Set the command to run the application
CMD ["java", "-jar", "app.jar"]

# First stage: Build the JAR using Maven and JDK 18
FROM maven:3.8.7-openjdk-18 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and source code into the working directory
COPY pom.xml .
COPY src ./src

# Run the Maven package command to build the JAR file
RUN mvn clean package

# Second stage: Create the final image with JDK 18
FROM amazoncorretto:18

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the first stage (build)
COPY --from=build /app/target/shoppinglist-1.0-SNAPSHOT.jar /app/shoppinglist-1.0-SNAPSHOT.jar

# Expose port 8082 to the outside world
EXPOSE 8082

# Run the JAR file when the container starts
CMD ["java", "-jar", "shoppinglist-1.0-SNAPSHOT.jar"]

# Use an official OpenJDK runtime as a parent image with Java 18
FROM amazoncorretto:18

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY target/shoppinglist-1.0-SNAPSHOT.jar /app/shoppinglist-1.0-SNAPSHOT.jar

# Make port 8080 available to the world outside this container
EXPOSE 8082

# Specify the command to run the application
CMD ["java", "-jar", "shoppinglist-1.0-SNAPSHOT.jar"]

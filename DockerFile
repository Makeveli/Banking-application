$Stage 1: Build the application
FROM eclipse-temurin:21-jdk-jammy AS builder

#Set the working directory inside the container
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends maven && rm -rf /var/lib/apt/lists/*

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -Dmaven.test.skip=true

$Stage 2: Build a production ready image and run
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy final executable jar files from the 'builder' stage's target directory
# This is key advantage of multi-stage builds: only the artifact is copied and not build tools or source
COPY -- from=builder /app/targets/*.jar app.jar

EXPOSE 8090

# Define the command to run the application when the container starts
ENTRYPOINT ["java","-jar","app.jar"]
# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy only pom first (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source
COPY src ./src

# Build (skip EVERYTHING test related)
RUN mvn clean package -Dmaven.test.skip=true

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java","-jar","app.jar"]
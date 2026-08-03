# ==========================================
# Stage 1: Build the application using Maven
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

# Set working directory
WORKDIR /build

# Copy the pom.xml and download dependencies
# This caches the dependencies layer unless pom.xml changes
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code
COPY src ./src

# Build the application, skipping tests to speed up the build (tests run in CI)
RUN mvn clean package -DskipTests

# ==========================================
# Stage 2: Create the minimal runtime image
# ==========================================
FROM eclipse-temurin:21-jre-alpine

# Add a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Set working directory
WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /build/target/*.jar app.jar

# Change ownership of the app to the non-root user
RUN chown -R spring:spring /app

# Switch to the non-root user
USER spring:spring

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

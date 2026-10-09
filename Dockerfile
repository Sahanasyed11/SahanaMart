# ==============================================================================
# SahanaMart - Multi-Stage Production Dockerfile
# Anna University R2025 Semester 3 Capstone
# Compatible with Render, Railway, Fly.io, Koyeb, AWS, and GCP
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build & Package
# ------------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Cache Maven dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy project source
COPY src ./src

# Compile classes and copy runtime dependencies
RUN mvn clean compile dependency:copy-dependencies -DincludeScope=runtime -DskipTests

# ------------------------------------------------------------------------------
# Stage 2: Minimal Production Runtime
# ------------------------------------------------------------------------------
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy compiled bytecode, web assets, and runtime libraries from builder
COPY --from=builder /app/target/classes ./target/classes
COPY --from=builder /app/target/dependency ./target/dependency
COPY --from=builder /app/src/main/webapp ./src/main/webapp

# Create persistent storage folder for H2 database
RUN mkdir -p /app/data

# Default port (Render/Railway override this dynamically via the PORT env var)
ENV PORT=8080
EXPOSE 8080

# Launch embedded Tomcat server
CMD ["sh", "-c", "java -cp 'target/classes:target/dependency/*' com.sahana.sahanamart.AppLauncher"]

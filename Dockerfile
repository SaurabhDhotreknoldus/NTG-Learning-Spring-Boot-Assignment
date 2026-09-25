# ==========================================
# Multi-Stage Build Dockerfile for Employee Management Service
# Stage 1: Build & Package Application
# Stage 2: Lightweight, Secure Runtime Container
# ==========================================

# ----------------- Stage 1: Build -----------------
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /workspace

# Copy Gradle wrapper and configuration files first to leverage Docker layer caching
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle/ gradle/

# Pre-fetch Gradle dependencies
RUN ./gradlew dependencies --no-daemon || true

# Copy source code and build executable bootJar without running tests (tests run in CI pipeline)
COPY src/ src/
RUN ./gradlew bootJar --no-daemon -x test

# ----------------- Stage 2: Runtime -----------------
FROM eclipse-temurin:21-jre-jammy AS runtime

# Metadata & Security
LABEL maintainer="NashTech Global Engineering Team"
LABEL application="employee-service"

# Create unprivileged application user and group for security hardening
RUN groupadd -r appgroup && useradd -r -g appgroup -s /sbin/nologin -d /app appuser

WORKDIR /app

# Copy executable jar from builder stage
COPY --from=builder /workspace/build/libs/employee-service-0.0.1-SNAPSHOT.jar app.jar

# Adjust ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose standard application port
EXPOSE 8080

# Environment variables with sensible production defaults
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"
ENV DB_HOST="localhost"
ENV DB_PORT="3306"
ENV DB_NAME="employee_db"
ENV DB_USER="root"
ENV DB_PASSWORD="rootpassword"

# Container healthcheck via Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

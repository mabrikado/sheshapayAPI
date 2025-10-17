FROM openjdk:21-jdk-slim

# Install system dependencies and Maven
RUN apt-get update && \
    apt-get install -y curl git maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Set JAVA_HOME
ENV JAVA_HOME=/usr/local/openjdk-21
ENV PATH=$JAVA_HOME/bin:$PATH

# Set working directory
WORKDIR /app

# Copy entire project into container
COPY . /app

# Build the backend (skip tests)
RUN mvn clean install -DskipTests

# Make start script executable
RUN chmod +x start.sh

# Expose Spring Boot default port
EXPOSE 8080

# Start Spring Boot backend
CMD ["/app/start.sh"]

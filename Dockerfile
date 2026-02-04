# Stage 1: Buildear la aplicación con Maven
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -q

# Stage 2: Runtime
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copiar JAR de la etapa anterior
COPY --from=builder /app/target/paginaFinanzasBackEnd-0.0.1-SNAPSHOT.jar app.jar

# Crear script de inicio con log de bienvenida
RUN echo '#!/bin/bash' > /app/start.sh && \
    echo 'echo "========================================="' >> /app/start.sh && \
    echo 'echo "🚀 Backend Finanzas iniciado"' >> /app/start.sh && \
    echo 'echo "📅 Versión: $(date +%Y%m%d-%H%M%S)"' >> /app/start.sh && \
    echo 'echo "📍 Profile: $SPRING_PROFILES_ACTIVE"' >> /app/start.sh && \
    echo 'echo "========================================="' >> /app/start.sh && \
    echo 'exec java -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-production} -jar app.jar' >> /app/start.sh && \
    chmod +x /app/start.sh

EXPOSE 8080

ENTRYPOINT ["/app/start.sh"]

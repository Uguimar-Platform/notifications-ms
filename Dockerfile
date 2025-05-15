FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace/app

# Copiar archivos del proyecto
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src ./src

# Asegurar que los ejecutables tengan permisos correctos
RUN chmod +x mvnw

# Compilar el proyecto
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp

# Instalar curl para healthcheck
RUN apk add --no-cache curl

COPY --from=build /workspace/app/target/*.jar app.jar

# Crear usuario no-root
RUN addgroup --system --gid 1001 appgroup && \
    adduser --system --uid 1001 --ingroup appgroup appuser && \
    chown appuser:appgroup /app.jar
USER appuser

# Exponer puertos HTTP y gRPC
EXPOSE 8082 9091

# Healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
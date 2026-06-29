# Lynx - notification-service · © 2026 Abel Gomez. Todos los derechos reservados.
# Contexto de build: raíz del proyecto (lynx/)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY eureka-server eureka-server
COPY api-gateway api-gateway
COPY auth-service auth-service
COPY face-service face-service
COPY access-service access-service
COPY notification-service notification-service
RUN mvn -q -pl notification-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/notification-service/target/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]

# ---------- Build Stage ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar pom.xml y descargar dependencias primero (mejora caché)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar WAR
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Runtime Stage ----------
FROM eclipse-temurin:17-jdk AS runtime
WORKDIR /app

# Variables configurables
ARG APP_VERSION=0.0.1-SNAPSHOT
ARG PROFILE=dev
ARG DB_URL=jdbc:postgresql://postgres:5432/book_social_network

ENV SPRING_PROFILES_ACTIVE=$PROFILE \
    SPRING_DATASOURCE_URL=$DB_URL \
    SPRING_DATASOURCE_USERNAME=postgres \
    SPRING_DATASOURCE_PASSWORD=1234

# Copiar el WAR generado desde el build stage
COPY --from=build /app/target/book-network-*.war app.jar

# Exponer el puerto que usa el backend
EXPOSE 8088

# Ejecutar el WAR como Spring Boot ejecutable
ENTRYPOINT ["java", "-jar", "app.jar"]



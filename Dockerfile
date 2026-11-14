# ---------- Этап 1: сборка .jar ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Сначала только pom.xml: зависимости скачаются один раз и попадут в кеш слоёв
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Потом исходники: при изменении кода зависимости заново не скачиваются
COPY src ./src
RUN mvn package -DskipTests -B

# ---------- Этап 2: запуск ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
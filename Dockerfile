# Этап 1: Сборка приложения
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app

# Копируем gradle wrapper и конфигурационные файлы
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Загружаем зависимости (кешируется если не меняется build.gradle)
RUN ./gradlew dependencies --no-daemon

# Копируем исходный код
COPY src src

# Собираем jar
RUN ./gradlew bootJar --no-daemon

# Этап 2: Запуск приложения
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Копируем jar из этапа сборки
COPY --from=build /app/build/libs/*.jar app.jar

# Открываем порт
EXPOSE 8080

# Запускаем приложение с профилем docker
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
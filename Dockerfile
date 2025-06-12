# Etap 1: Budowanie
FROM gradle:8.8.0-jdk21 AS build
WORKDIR /home/gradle/src
COPY build.gradle settings.gradle ./
RUN gradle build --no-daemon || return 0
COPY . .
RUN gradle build --no-daemon -x test

# Etap 2: Uruchamianie
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/clinics-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-Dvaadin.productionMode=true", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
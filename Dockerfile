FROM gradle:8.8.0-jdk21 AS build
WORKDIR /home/gradle/src
COPY build.gradle settings.gradle ./
RUN gradle clean build --no-daemon -x test -Pvaadin.productionMode=true

COPY . .

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/clinics-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-Dvaadin.productionMode=true", "-jar", "app.jar"]

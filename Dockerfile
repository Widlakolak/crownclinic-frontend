# --- Etap 1: Budowanie Aplikacji ---
# Używamy oficjalnego obrazu Gradle z Javą 21 do zbudowania projektu.
FROM gradle:8.5.0-jdk21 AS build

# Ustawiamy katalog roboczy wewnątrz kontenera
WORKDIR /home/gradle/src

# Kopiujemy tylko pliki potrzebne do pobrania zależności
COPY build.gradle settings.gradle ./

# Pobieramy zależności (to jest cachowane, więc przyspiesza kolejne buildy)
RUN gradle build --no-daemon || return 0

# Kopiujemy resztę kodu źródłowego
COPY . .

# Budujemy aplikację, pomijając testy
RUN gradle build --no-daemon -x test


# --- Etap 2: Uruchomienie Aplikacji ---
# Używamy lekkiego obrazu z samą Javą do uruchomienia aplikacji.
FROM eclipse-temurin:21-jre-jammy

# Ustawiamy katalog roboczy
WORKDIR /app

# Kopiujemy zbudowany plik .jar z etapu "build" do naszego finalnego obrazu
# UWAGA: Upewnij się, że nazwa pliku .jar jest poprawna!
COPY --from=build /home/gradle/src/build/libs/clinics-0.0.1-SNAPSHOT.jar app.jar

# Ustawiamy port, na którym aplikacja frontendowa będzie nasłuchiwać
EXPOSE 8081

# Komenda, która zostanie wykonana przy starcie kontenera
ENTRYPOINT ["java", "-jar", "app.jar"]

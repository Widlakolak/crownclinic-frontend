# **Crown Clinic - Frontend**

---

## **Opis Projektu**

Projekt ten stanowi warstwę widoku dla aplikacji "Crown Clinic". Został stworzony w całości w języku Java przy użyciu biblioteki **Vaadin 24+**. Aplikacja komunikuje się z backendem poprzez REST API w sposób asynchroniczny, wykorzystując `WebClient` z pakietu Spring WebFlux.

Interfejs użytkownika jest podzielony na widoki dla różnych ról (lekarz, recepcja) i umożliwia m.in. zarządzanie wizytami w interaktywnym kalendarzu.

---

## **Instrukcja Manualnego Uruchomienia**

Aby uruchomić pełną aplikację (backend + frontend), postępuj zgodnie z krokami:

1.  **Uruchom Backend:** Aplikacja frontendowa do działania wymaga uruchomionego i dostępnego backendu. Upewnij się, że projekt `crownclinic-backend` jest włączony.
2.  **Sklonuj Repozytorium:**
    ```bash
    git clone https://github.com/Widlakolak/crownclinic-frontend.git
    ```
3.  **Skonfiguruj Frontend:**
    *   Otwórz projekt w swoim IDE.
    *   W pliku `src/main/resources/application.properties` upewnij się, że właściwość `backend.url` wskazuje na poprawny adres backendu (domyślnie `http://localhost:8080`).
4.  **Uruchom Aplikację Frontendową:**
    ```bash
    ./gradlew bootRun
    ```
5.  **Otwórz Aplikację:** Wejdź w przeglądarce na adres `http://localhost:8081`.

---

## **Technologie i Zależności**

-   **Java 21**
-   **Vaadin 24+**
-   **Spring Boot 3.5** (używany jako serwer dla aplikacji Vaadin)
-   **Spring WebFlux**: `WebClient` do asynchronicznej komunikacji z backendem.
-   **Lombok**
-   **System Budowania:** Gradle

---

## **Struktura Aplikacji (Widoki i Komponenty)**

Aplikacja jest zbudowana w oparciu o reużywalne komponenty i widoki:

### **Główne Widoki (`/view`)**

-   **`LoginView`**: Strona logowania, obsługuje logowanie przez e-mail/hasło oraz inicjuje przepływ OAuth2 z Google.
-   **`DoctorView`**: Główny panel dla lekarza. Pełni rolę "dyrygenta", który zarządza i łączy `CalendarComponent` i `AppointmentForm`.
-   **`ReceptionView`**: (w przygotowaniu) Panel dla recepcji, który w przyszłości będzie reużywał `CalendarComponent` do wyświetlania grafików wszystkich lekarzy.
-   **`LoginSuccessView`**: "Niewidzialny" widok, który przechwytuje przekierowanie z backendu po udanym logowaniu OAuth2, odczytuje token JWT z URL-a i finalizuje proces logowania w `AuthService`.

### **Komponenty Reużywalne (`/component`)**

-   **`AppointmentForm`**: Formularz do tworzenia i edycji wizyt.
-   **`CalendarComponent`**: Komponent wyświetlający wizyty na dany dzień w siatce (`Grid`) z możliwością nawigacji po datach i filtrowania po lekarzach.
-   **`PatientForm`**: Formularz do tworzenia i edycji danych pacjenta, używany w wyskakującym oknie (`Dialog`).
-   **`WeatherWidget`**: Prosty widżet wyświetlający pogodę pobraną z API backendu.

### **Serwisy (`/service`)**

-   **`AuthService`**: Zarządza stanem autentykacji na frontendzie. Przechowuje token JWT, obsługuje logowanie/wylogowanie i pobiera dane zalogowanego użytkownika.
-   **`BackendService`**: Centralny punkt komunikacji z API backendu dla wszystkich operacji biznesowych (pobieranie wizyt, pacjentów itd.). Wszystkie metody są asynchroniczne i zwracają `Mono` lub `Flux`.

---

## **Spełnienie Wymagań Projektu Kursu**

1.  **Warstwa Widoku**: Aplikacja w pełni implementuje warstwę widoku przy użyciu biblioteki **Vaadin**, zgodnie z wymaganiami. Interfejs jest w pełni funkcjonalny i pozwala na interakcję z backendem.
2.  **Komunikacja z REST API**: Wszystkie operacje (CRUD na wizytach, pobieranie pacjentów/lekarzy) odbywają się poprzez wywołania do zewnętrznego API (naszego backendu), co jest kluczowym założeniem architektury.
3.  **Asynchroniczność**: Frontend wykorzystuje `WebClient` i Project Reactor (`Mono`) do asynchronicznej obsługi zapytań sieciowych, co zapobiega blokowaniu interfejsu użytkownika i jest zgodne z nowoczesnymi praktykami.

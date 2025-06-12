## CrownClinic Frontend


Frontend aplikacji CrownClinic zbudowany w oparciu o **Vaadin 24+** jako aplikacja **SPA (Single Page Application)** komunikująca się z backendem przez REST API.

Aplikacja umożliwia zarządzanie kliniką, obsługując role: lekarza, recepcjonisty i administratora.

---

## 📄 Wymagania

- Java 17+
- Node.js (dla budowania zasobów Vaadin)
- Maven

---

## 🏑 Dashboardy

### Lekarz:

- widok kalendarza z wizytami
- widok wiadomości do pacjentów
- tablica z notatkami
- lista pacjentów

### Recepcja:

- zarządzanie lekarzami (dodawanie, edycja godzin przyjęć)
- kalendarz wizyt z filtracją po lekarzach i pacjentach
- wiadomości
- tablice

> ⚠ Obecnie dashboard dla pacjenta nie jest zaimplementowany.

---

## 🏃‍♂️ Jak uruchomić

```
Konto google do testowania funkcjonalności:
Login - crownclinictest@gmail.com
Hasło - crownclinictest1206
```

```
Zalecane uruchomienie na Render

Frontend: [https://crownclinic-frontend.onrender.com](https://crownclinic-frontend.onrender.com)\
Backend (Swagger UI): [https://crownclinic-backend.onrender.com/swagger-ui.html](https://crownclinic-backend.onrender.com/swagger-ui.html)

> ⚠ Uwaga: Aplikacja może wczytywać się dłużej (nawet 30 sekund) ze względu na darmowy hosting Render (Free Tier), który usypia instancje przy bezczynności.
```

```bash
# 1. Klonuj repozytorium
git clone https://github.com/twoj-uzytkownik/crownclinic-frontend.git
cd crownclinic-frontend

# 2. Uruchom aplikację (Vaadin + Spring Boot)
mvn spring-boot:run

# Domyślny adres: http://localhost:8080
```

---

## 📚 Struktura projektu (skrócona)

```
crownclinic-frontend/
├── src/main/java/com/crownclinic/frontend
│   ├── views/              # widoki UI (Vaadin Views)
│   ├── services/           # klient REST do backendu
│   ├── layout/             # szablony i layouty
│   └── security/           # integracja z JWT/OAuth2
├── src/main/resources/
│   └── application.properties
├── pom.xml
└── README.md
```

---

## 🚸 Technologie

- **Vaadin 24+** (SPA mode)
- **Spring Boot** (web client)
- **OAuth2 / JWT** (uwierzytelnianie)
- **Lombok**

---

## 💪 Deployment (Render)

- Frontend: Render (Java + Maven)
- Backend: Render (Spring Boot)
- Konfiguracja: `render.yaml` + `application.properties`

---

## 🔄 Planowane funkcjonalności

- dashboard pacjenta
- powiadomienia push
- eksport danych pacjenta
- ciemny motyw (dark mode)

---

## ✉ Kontakt

Autorzy projektu CrownClinic - Zespół Java Developerów (2025)

---

## 📅 Licencja

Projekt objęty licencją MIT. Zobacz plik [LICENSE](LICENSE).

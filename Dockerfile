services:
  - type: web
    name: crownclinic-backend
    # Zamiast 'env: java', mówimy mu, żeby użył Dockera
    env: docker 
    plan: free
    # Komendy build i start są teraz w Dockerfile, więc możemy je usunąć stąd
    # buildCommand: ...  <-- USUŃ LUB ZAKOMENTUJ
    # startCommand: ...  <-- USUŃ LUB ZAKOMENTUJ
    dockerfilePath: ./Dockerfile # Ścieżka do naszego pliku
    envVars:
      - key: DATABASE_URL
        fromDatabase:
          name: crownclinic-db
          property: connectionString
      - key: GOOGLE_CLIENT_ID
        sync: false
      - key: GOOGLE_CLIENT_SECRET
        sync: false
      - key: JWT_SECRET
        sync: false
      - key: MAIL_USERNAME
        sync: false
      - key: MAIL_PASSWORD
        sync: false
      # Dodajemy port, aby Spring Boot wiedział, na czym działać wewnątrz kontenera
      - key: PORT
        value: 8080

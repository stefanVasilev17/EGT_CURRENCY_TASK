FROM eclipse-temurin:17-jdk

# ✅ Install netcat-openbsd (not virtual netcat)
RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/*.jar app.jar
COPY wait-for-it.sh wait-for-it.sh

RUN chmod +x wait-for-it.sh

EXPOSE 8080

# 🟡 This makes the app wait for PostgreSQL before starting
ENTRYPOINT ["./wait-for-it.sh", "postgres", "5432", "--", "java", "-jar", "app.jar"]
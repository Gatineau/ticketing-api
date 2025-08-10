# Utilise une image légère avec Java 21
FROM eclipse-temurin:21-jdk-alpine

# Dossier de travail dans le conteneur
WORKDIR /app

# Copie du fichier JAR (assure-toi qu'il est bien compilé avec Maven avant)
COPY target/*.jar app.jar

# Expose le port (doit correspondre à celui utilisé par Spring Boot)
EXPOSE 8080

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "app.jar"]
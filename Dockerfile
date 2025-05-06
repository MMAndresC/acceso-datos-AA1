FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
EXPOSE 8089
COPY target/tournament-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
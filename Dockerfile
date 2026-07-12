FROM amazoncorretto:17-alpine

WORKDIR /app

ARG JAR_FILE=build/libs/*-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Duser.timezone=UTC", "app.jar"]
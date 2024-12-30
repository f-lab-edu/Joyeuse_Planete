FROM openjdk:17

ARG JAVA_FILE=build/libs/*.jar

WORKDIR usr/src/app

COPY ${JAVA_FILE} la_planete-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD [ "java", "-jar", "la_planete-0.0.1-SNAPSHOT.jar" ]
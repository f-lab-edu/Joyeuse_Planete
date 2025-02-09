FROM openjdk:17

ARG JAVA_FILE=build/libs/*.jar

WORKDIR /usr/src/app

COPY ${JAVA_FILE} joyeuse_planete-orders.jar

EXPOSE 8080

CMD [ "java", "-Dspring.profiles.active=prod", "-jar", "joyeuse_planete-orders.jar" ]
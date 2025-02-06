FROM openjdk:17

ARG JAVA_FILE=build/libs/*.jar

WORKDIR /usr/src/app

COPY ${JAVA_FILE} joyeuse_planete-payment.jar

EXPOSE 8080

CMD [ "java", "-jar", "joyeuse_planete-payment.jar" ]
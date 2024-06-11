FROM openjdk:11.0.13-jre-slim
VOLUME /tmp
ADD ./build/libs/notification-service-0.0.1-SNAPSHOT.jar notification-service.jar
ENTRYPOINT ["java", "-jar" , "/notification-service.jar"]

FROM adoptopenjdk:11
RUN mkdir /app
WORKDIR /app

COPY build/libs/*.jar /app/reviewbot.jar

ENTRYPOINT ["java", "-jar" , "/app/reviewbot.jar"]

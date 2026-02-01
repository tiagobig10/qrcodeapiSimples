<<<<<<< HEAD
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/qrcodeapi-1.0.0.jar qrcodeapi-1.0.0.jar
EXPOSE 8081
CMD ["java", "-jar","qrcodeapi-1.0.0.jar"]
=======
FROM openjdk:17-jdk-alpine
RUN mkdir /app
WORKDIR /app
COPY target/*.jar /app/app.jar
CMD ["java","-jar","/app/app.jar"]
>>>>>>> origin/master

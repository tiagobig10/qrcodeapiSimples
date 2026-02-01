FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/qrcodeapi-1.0.0.jar qrcodeapi-1.0.0.jar
EXPOSE 8082
CMD ["java", "-jar","qrcodeapi-1.0.0.jar"]


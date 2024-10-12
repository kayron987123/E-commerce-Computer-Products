FROM openjdk:21-jdk-slim
ARG JAR_FILE=target/ecommerce-computer-components-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app_ecommerce-efsrt.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app_ecommerce-efsrt.jar"]
FROM eclipse-temurin:17-jdk-jammy
ARG JAR_FILE=target/rag-chat-history-0.1.0.jar
WORKDIR /app
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
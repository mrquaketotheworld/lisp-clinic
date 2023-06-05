FROM eclipse-temurin:17-jdk-alpine

COPY ./clj/target/lisp-clinic.jar /app/
WORKDIR /app

ENTRYPOINT ["java", "-jar", "lisp-clinic.jar"]


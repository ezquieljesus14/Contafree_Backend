FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY common ./common
RUN mvn -f common/pom.xml install -DskipTests -B

COPY accounting-service/pom.xml ./accounting-service/pom.xml
RUN mvn -f accounting-service/pom.xml dependency:go-offline -B

COPY accounting-service/src ./accounting-service/src
RUN mvn -f accounting-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/accounting-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

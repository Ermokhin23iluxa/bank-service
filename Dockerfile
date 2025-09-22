FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar bank-service.jar
EXPOSE 8080

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://bank-postgres:5432/bank-service \
    SPRING_DATASOURCE_USERNAME=bombino \
    SPRING_DATASOURCE_PASSWORD=1234 \
    SPRING_REDIS_HOST=bank-redis \
    SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "bank-service.jar"]
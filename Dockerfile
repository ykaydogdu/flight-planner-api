FROM maven:3.9.10-eclipse-temurin-21 AS build
WORKDIR /app
COPY src ./src
COPY pom.xml ./pom.xml
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
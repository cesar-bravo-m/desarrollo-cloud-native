FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY Wallet_QSYVMD9J8GOANRS4 ./Wallet_QSYVMD9J8GOANRS4

COPY --from=build /app/target/*.jar app.jar

ENV TZ=America/Santiago
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 
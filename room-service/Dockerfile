FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean shadowJar -x test --no-daemon

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/build/libs/*-all.jar app.jar

EXPOSE 8080
EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]

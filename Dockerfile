FROM eclipse-temurin:17.0.4.1_1-jdk as build
WORKDIR /app
COPY . .

RUN ./gradlew build

FROM eclipse-temurin:17.0.4.1_1-jre as final
WORKDIR /app
COPY --from=build /app/build/libs/credits-1.0-SNAPSHOT.jar .

CMD java -jar credits-1.0-SNAPSHOT.jar
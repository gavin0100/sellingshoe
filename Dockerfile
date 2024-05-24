FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/doanxinviec_fourleavesshoes-0.0.1-SNAPSHOT.jar doanxinviec_fourleavesshoes.jar
EXPOSE 3030
ENTRYPOINT ["java", "-jar", "DoAnXinViec_fourleavesshoes.jar"]
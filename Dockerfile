# Estagio 1: compila o projeto com o Maven Wrapper (imagem com JDK completo)
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia primeiro so o que define as dependencias, para o Docker reaproveitar o download
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Agora copia o codigo e gera o .jar
COPY src src
RUN ./mvnw -B -DskipTests clean package

# Estagio 2: imagem final, menor, so com o Java de execucao e o .jar
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]

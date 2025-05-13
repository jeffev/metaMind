# Use uma imagem base com JDK e Maven
FROM maven:3.8.3-openjdk-17 AS build

# Defina o diretório de trabalho
WORKDIR /app

# Copie os arquivos do projeto para o contêiner
COPY pom.xml .
COPY src ./src

# Construa o JAR
RUN mvn clean package -DskipTests

# Use uma imagem base mais leve para execução
FROM openjdk:17-jdk-slim

# Defina o diretório de trabalho
WORKDIR /app

# Copie o JAR gerado da etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Exponha a porta do serviço
EXPOSE 8761

# Comando para executar o JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
# Use uma imagem base com JDK e Maven
FROM maven:3.8.3-openjdk-17

# Defina o diretório de trabalho dentro do container
WORKDIR /app

# Copie os arquivos pom.xml e src para o container
COPY pom.xml .
COPY src ./src

# Execute o build do Maven para baixar dependências e construir o JAR
RUN mvn clean package -DskipTests

# Use uma imagem base mais leve para a execução (apenas JRE)
FROM openjdk:17-oracle

# Defina o diretório de trabalho dentro do container
WORKDIR /app

# Copie o JAR construído da etapa anterior
COPY --from=0 /app/target/*.jar usuario-service.jar

# Exponha a porta
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "usuario-service.jar"]
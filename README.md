# Projeto MetaMind

## Visão Geral

O Projeto MetaMind é uma plataforma de serviços de microsserviços construída com Spring Boot e Docker. Atualmente, inclui um serviço de gerenciamento de usuários (`usuario-service`) e um servidor de registro de serviços (`eureka-server`) para facilitar a descoberta e comunicação entre os serviços. Os dados do serviço de usuário são persistidos em um banco de dados MongoDB.

## Arquitetura

O projeto segue uma arquitetura de microsserviços, onde cada funcionalidade é implementada como um serviço independente. Os principais componentes são:

* **`eureka-server`**: Um servidor de registro e descoberta de serviços Eureka. Os outros serviços se registram nele para que possam ser localizados uns pelos outros.
* **`usuario-service`**: Um serviço responsável por gerenciar as operações relacionadas aos usuários, como criação, leitura, atualização e exclusão. Ele utiliza um banco de dados MongoDB para persistência.
* **`mongodb`**: Uma instância do banco de dados NoSQL MongoDB utilizada pelo `usuario-service` para armazenar os dados dos usuários.

A comunicação entre os serviços (neste momento, apenas o `usuario-service` se comunicaria com outros potenciais serviços via Eureka) é facilitada pelo servidor Eureka.

## Tecnologias Utilizadas

* **Java**: Linguagem de programação principal.
* **Spring Boot**: Framework para construção rápida de aplicações Java baseadas em Spring.
* **Spring Cloud Netflix Eureka**: Para descoberta de serviços.
* **Spring Data MongoDB**: Para interação com o banco de dados MongoDB.
* **Spring Security**: Para segurança da aplicação (configurações básicas podem estar presentes ou serão adicionadas).
* **Docker**: Plataforma de conteinerização para empacotar e executar as aplicações em ambientes isolados.
* **Docker Compose**: Ferramenta para definir e gerenciar aplicações multi-container Docker.
* **JUnit 5 e Mockito**: Frameworks para testes unitários e de integração.
* **Jackson**: Biblioteca para manipulação de JSON.
* **AWS Cognito**: Utilizado para autenticação e gerenciamento de identidade de usuários.

## Pré-requisitos

Antes de executar o projeto, você precisará ter as seguintes ferramentas instaladas:

* **Docker**: Versão 19.03 ou superior ([https://docs.docker.com/engine/install/](https://docs.docker.com/engine/install/))
* **Docker Compose**: Geralmente instalado com o Docker Desktop ou pode ser instalado separadamente ([https://docs.docker.com/compose/install/](https://docs.docker.com/compose/install/))
* **Java Development Kit (JDK)**: Versão 17 ou superior ([https://www.oracle.com/java/technologies/javase-downloads.html](https://www.oracle.com/java/technologies/javase-downloads.html) ou outra distribuição OpenJDK)
* **Maven** ou **Gradle**: Para construir as aplicações Java (dependendo da sua preferência).

## Como Executar

1.  **Clone o repositório do projeto** (se ainda não o fez).
2.  **Navegue até o diretório raiz do projeto** no seu terminal.
3.  **Execute o Docker Compose** para construir e iniciar os containers:

    ```bash
    docker-compose up --build -d
    ```

    Este comando irá:
    * Construir as imagens Docker para o `eureka-server` e o `usuario-service` (usando os Dockerfiles nas respectivas pastas).
    * Iniciar os containers para o `eureka-server`, `mongodb` e `usuario-service`.
    * Configurar a rede `metamind-network` para permitir a comunicação entre os containers.

4.  **Acesse os serviços:**
    * **Eureka Server**: Pode ser acessado no seu navegador em `http://localhost:8761`. Você deverá ver a interface do Eureka com os serviços registrados (inicialmente, apenas o `usuario-service` após o registro).
    * **Usuario Service**: A API do serviço de usuário estará disponível em `http://localhost:8080/api/users`.

## Endpoints da API do `usuario-service`

* **`POST /api/users`**: Cria um novo usuário. Espera um objeto JSON `User` no corpo da requisição.
* **`GET /api/users/{id}`**: Retorna um usuário específico pelo seu ID.
* **`GET /api/users/email/{email}`**: Retorna um usuário específico pelo seu e-mail.
* **`GET /api/users`**: Retorna uma lista de todos os usuários.
* **`PUT /api/users/{id}`**: Atualiza um usuário existente pelo seu ID. Espera um objeto JSON `User` no corpo da requisição.
* **`DELETE /api/users/{id}`**: Exclui um usuário pelo seu ID.

## Testes

O projeto inclui testes unitários e de integração para garantir a qualidade e o correto funcionamento dos serviços. Os testes estão localizados nos diretórios `src/test/java` de cada módulo (`eureka-server` e `usuario-service`).

Para executar os testes (fora do ambiente Docker):

1.  **Navegue até o diretório raiz do projeto** no seu terminal.
2.  **Execute o comando do Maven** (se estiver usando Maven):

    ```bash
    ./mvnw test
    ```

## Configurações

As configurações específicas de cada serviço podem ser encontradas nos arquivos `application.properties` ou `application.yml` dentro da pasta `src/main/resources` de cada módulo.

* **`eureka-server`**: Configurado para rodar na porta 8761 e não se registrar como cliente.
* **`usuario-service`**: Configurado para rodar na porta 8080, se registrar no Eureka Server (`http://eureka-server:8761/eureka`), e conectar-se ao MongoDB. As credenciais do MongoDB são definidas como variáveis de ambiente no `docker-compose.yml`.

## Próximos Passos e Melhorias Possíveis

* Implementar mais microsserviços para outras funcionalidades da plataforma MetaMind.
* Adicionar mecanismos de autenticação e autorização mais robustos (Spring Security com OAuth2).
* Implementar um gateway de API para rotear as requisições para os serviços apropriados.
* Adicionar mecanismos de logging e monitoramento centralizados.
* Implementar testes de integração mais abrangentes, incluindo testes de contrato.
* Configurar um pipeline de Continuous Integration/Continuous Deployment (CI/CD).
* Explorar o uso de um sistema de configuração distribuída (Spring Cloud Config).
* Melhorar o tratamento de erros e a resiliência dos serviços (Hystrix/Resilience4j).

## Contribuição

Contribuições para o projeto são bem-vindas. Sinta-se à vontade para abrir issues com sugestões de melhorias ou pull requests com suas implementações.

## Licença

[Adicione aqui a licença sob a qual o projeto está distribuído, se aplicável.]
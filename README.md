[![Build Status](https://travis-ci.org/raffael404/banking-services.svg?branch=master)](https://travis-ci.org/raffael404/banking-services)

# Banking Services
API Restful genérica para serviços bancários.

### Detalhes da API RESTful
A API Restful para serviços bancários foi pensada como um sistema para integrar vários bancos e clientes em um mesmo local. Ela possui 2 tipos de usuários, um Pessoa Física (Cliente) e um Pessoa Jurídica (Banco), cada um podendo executar o serviços associados ao mesmo (o cliente, por exemplo pode abrir contas em qualquer banco cadastrado no sistema, enquanto o banco pode cadastrar agências associadas ao mesmo). Este projeto foi desenvolvido em Java 11 em conjunto com o framework Spring Boot e o Banco de Dados MySQL.

### Como executar a aplicação
Certifique-se de ter o Maven instalado e adicionado ao PATH de seu sistema operacional, assim como o Git e o MySQL com um banco nomeado banking_services.
```
git clone https://github.com/raffael404/banking-services.git
cd banking-services
mvn spring-boot:run
Acesse os endpoints através da url http://localhost:8080/banking
```
As requisições aceitas são todas do tipo POST e podem ser enviadas através de um cliente HTTP qualquer, como o Postman. A aplicação também pode ser testada diretamente pelo Swagger, através da URL http://localhost:8080/banking/swagger-ui.html.
### Endpoints
```
/usuario/cadastrar/banco
/usuario/cadastrar/cliente
/banco/cadastrar/agencia
/banco/remover/agencia
/conta/abrir
/conta/fechar
/conta/depositar
/conta/sacar
/conta/transferir
/conta/extrato
```

### Observações
Eu não consegui implementar o servidor de autenticação. Assim, o sistema está usando apenas uma autenticação bem básica, passando-se a senha como um dos atributos do objeto JSON enviado na requisição.
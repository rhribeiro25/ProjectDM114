# Provedor de serviços de envio de mensagens

## 1 - Informações de acesso

O provedor de serviços de envio de mensagens encontra-se hospedado no seguinte endereço:

https://message-provider.appspot.com



## 2 - Autenticação e token de acesso

Todos os serviços dessa aplicação utilizam o mecanismo de autenticação OAuth 2.0.

Para obter o token de acesso, a aplicação cliente deve fazer uma requisição ao servidor com as seguintes informações:

**Método:** POST

**URL:** https://message-provider.appspot.com/oauth/token

**Cabeçalhos:** 

- Content-Type: application/x-www-form-urlencoded


- Authorization: Basic c2llY29sYTptYXRpbGRl

**Corpo da mensagem:**

```
grant_type=password&username=matilde@siecola.com.br&password=matilde
```

Os valores dos campos **username** e **password** devem ser trocados para as credenciais de acesso do usuário que deseja obter o token.

A resposta a essa autenticação é o token de acesso no seguinte formato:

```Son
{
    "access_token": "13968c9d-e3ef-4b32-b119-b6d93f59963f",
    "token_type": "bearer",
    "refresh_token": "e567d8b8-def6-4f37-8b6d-d532a47a08ac",
    "expires_in": 3599,
    "scope": "read write"
}
```



> **Usuário com papel ADMIN**
>
> O provedor de serviços de envio de mensagens já possui um usuário com papel de administrador, que não pode ser alterado nem apagado. Seu login é `matilde@siecola.com.br` e sua senha é `matilde`.



## 3 - Serviço de gerenciamento de usuários

### a) Listar todos os usuários

**Método:** GET

**URL:** https://message-provider.appspot.com/api/users

**Permissão de acesso:** somente usuário com papel ADMIN

**Exemplo de resposta:**

```json
[
    {
        "id": 5741031244955648,
        "email": "doralice@siecola.com.br",
        "password": "doralice",
        "gcmRegId": null,
        "lastLogin": null,
        "lastGCMRegister": null,
        "role": "USER",
        "enabled": true
    },
    {
        "id": 5668600916475904,
        "email": "matilde@siecola.com.br",
        "password": "matilde",
        "gcmRegId": null,
        "lastLogin": null,
        "lastGCMRegister": null,
        "role": "ADMIN",
        "enabled": true
    }
]
```



### b) Criar um usuário

**Método:** POST

**URL:** https://message-provider.appspot.com/api/users

**Permissão de acesso:** somente usuário com papel ADMIN

**Exemplo de corpo de requisição:**

```json
{
    "email": "doralice@siecola.com.br",
    "password": "doralice",
    "gcmRegId": null,
    "lastLogin": null,
    "lastGCMRegister": null,
    "role": "USER",
    "enabled": true
}
```

**Exemplo de mensagem de resposta:**

```json
{
    "id": 5629499534213120,
    "email": "doralice@siecola.com.br",
    "password": "doralice",
    "gcmRegId": null,
    "lastLogin": null,
    "lastGCMRegister": null,
    "role": "USER",
    "enabled": true
}
```



### c) Alterar um usuário pelo e-mail

**Método:** PUT

**URL:** https://message-provider.appspot.com/api/users/byemail?email={email}

**Permissão de acesso:** somente usuário com papel ADMIN ou o próprio usuário alvo da operação

**Exemplo de corpo de requisição:**

```json
{
    "id": 5629499534213120,
    "email": "doralice@siecola.com.br",
    "password": "doralice",
    "gcmRegId": null,
    "lastLogin": null,
    "lastGCMRegister": null,
    "role": "USER",
    "enabled": true
}
```

**Exemplo de mensagem de resposta:**

```json
{
    "id": 5629499534213120,
    "email": "doralice@siecola.com.br",
    "password": "doralice",
    "gcmRegId": null,
    "lastLogin": null,
    "lastGCMRegister": null,
    "role": "USER",
    "enabled": true
}
```



### d) Buscar um usuário pelo e-mail

**Método:** GET

**URL:** https://message-provider.appspot.com/api/users/byemail?email={email}

**Permissão de acesso:** somente usuário com papel ADMIN ou o próprio usuário alvo da operação

**Exemplo de mensagem de resposta:**

```json
{
    "id": 5629499534213120,
    "email": "doralice@siecola.com.br",
    "password": "doralice",
    "gcmRegId": null,
    "lastLogin": null,
    "lastGCMRegister": null,
    "role": "USER",
    "enabled": true
}
```



### e) Apagar um usuário pelo e-mail

**Método:** DELETE

**URL:** https://message-provider.appspot.com/api/users/byemail?email={email}

**Permissão de acesso:** somente usuário com papel ADMIN ou o próprio usuário alvo da operação

**Exemplo de mensagem de resposta:**

```json
{
    "id": 5629499534213120,
    "email": "doralice@siecola.com.br",
    "password": "doralice",
    "gcmRegId": null,
    "lastLogin": null,
    "lastGCMRegister": null,
    "role": "USER",
    "enabled": true
}
```



### f) Atualizar registro GCM por e-mail

**Método:** PUT

**URL:** https://message-provider.appspot.com/api/users/updatereggcm?email={email}&reggcm={reggcm}

**Permissão de acesso:** somente usuário com papel ADMIN ou o próprio usuário alvo da operação

**Exemplo de mensagem de resposta:**

```json
{
    "id": 5629499534213120,
    "email": "doralice@siecola.com.br",
    "password": "doralice",
    "gcmRegId": {reggcm},
    "lastLogin": null,
    "lastGCMRegister": null,
    "role": "USER",
    "enabled": true
}
```



## 3 - Serviço de envio de notificação de status de pedido

**Método:** POST

**URL:** https://message-provider.appspot.com/api/orderinfo

**Permissão de acesso:** somente usuário com papel ADMIN

**Exemplo de corpo de requisição:**

```json
{
    "id": 1,
    "email": "doralice@siecola.com.br",
    "status": "status pedido",
    "reason": "reason pedido"
}
```



**Códigos de resposta**

* HTTP 200 OK: mensagem enviada
* HTTP 400 Bad Request: formato do payload de requisição inválido
* HTTP 412 Precondition Failed: usuário não registrado no GCM
* HTTP 503 Service Unavailable: falha ao enviar a mensagem
* HTTP 428 Precondition Required: usuário não possui o registro GCM cadastrado na base de dados
* HTTP 404 Not Found: usuário não encontrado


**Notificação enviada para o dispositivo móvel**
* Chave: `orderInfo`
* Payload:
```json
{
    "id": 123456,
    "email": "matilde@siecola.com.br",
    "status": "ENTREGUE",
    "reason": "Produto entregue"
}
```

## 4 - Serviço de gerenciamento de produtos de interesse

### a) Listar todos os produtos de interesse cadastrados

**Método:** GET

**URL:** https://message-provider.appspot.com/api/products

**Permissão de acesso:** somente usuário com papel ADMIN

**Exemplo de resposta:**

```json
[
    {
        "id": 5105650963054592,
        "code": "2",
        "price": 10,
        "email": "doralice@siecola.com.br"
    },
    {
        "id": 5685265389584384,
        "code": "3",
        "price": 10,
        "email": "doralice@siecola.com.br"
    },
    {
        "id": 5757334940811264,
        "code": "1",
        "price": 10,
        "email": "doralice@siecola.com.br"
    }
]
```



### b) Salvar ou alterar produto de interesse

**Método:** POST

**URL:** https://message-provider.appspot.com/api/products

**Permissão de acesso:** somente usuário com papel ADMIN ou o próprio usuário alvo da operação

**Exemplo de corpo de requisição:**

```json
{
    "code": "2",
    "price": 10,
    "email": "doralice@siecola.com.br"
}
```

**Exemplo de resposta:**

```json
{
    "id": 5105650963054592,
    "code": "2",
    "price": 10,
    "email": "doralice@siecola.com.br"
}
```



### c) Buscar todos os produtos de interesse de um usuário pelo seu e-mail

**Método:** GET

**URL:** https://message-provider.appspot.com/api/products/byemail?email={email}

**Permissão de acesso:** somente usuário com papel ADMIN ou o próprio usuário alvo da operação

**Exemplo de resposta:**

```json
[
    {
        "id": 5105650963054592,
        "code": "2",
        "price": 10,
        "email": "doralice@siecola.com.br"
    },
    {
        "id": 5685265389584384,
        "code": "3",
        "price": 10,
        "email": "doralice@siecola.com.br"
    },
    {
        "id": 5757334940811264,
        "code": "1",
        "price": 10,
        "email": "doralice@siecola.com.br"
    }
]
```



### d) Apagar um produto de interesse pelo id

**Método:** DELETE

**URL:** https://message-provider.appspot.com/api/products/{id}

**Permissão de acesso:** somente usuário com papel ADMIN ou o próprio usuário alvo da operação

**Exemplo de resposta:**

```json
{
    "id": 5105650963054592,
    "code": "2",
    "price": 10,
    "email": "doralice@siecola.com.br"
}
```



### e) Notificar usuários sobre alteração de preço de um produto de interesse

**Método:** POST

**URL:** https://message-provider.appspot.com/api/products/notify/{code}/{price}

**Permissão de acesso:** somente usuário com papel ADMIN

**Código de resposta**: o código sempre será HTTP 200 OK, com uma mensagem informando qual o número de mensagens que foram enviadas.

**Notificação enviada para o dispositivo móvel**
* Chave: `productOfInterest`
* Payload:
```json
{
    "id": 123456,
    "code": "COD1",
    "price": 10.5,
    "email": "matilde@siecola.com.br"
}
```

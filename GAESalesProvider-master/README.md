# Provedor de serviços de vendas
## 1 - Informações de acesso

O provedor de serviços de vendas encontra-se hospedado no seguinte endereço:

https://sales-provider.appspot.com



## 2 - Autenticação e token de acesso

Todos os serviços dessa aplicação utilizam o mecanismo de autenticação OAuth 2.0.

Para obter o token de acesso, a aplicação cliente deve fazer uma requisição ao servidor com as seguintes informações:

**Método:** POST

**URL:** http://sales-provider.appspot.com/oauth/token

**Cabeçalhos:** 

* Content-Type: application/x-www-form-urlencoded


* Authorization: Basic c2llY29sYTptYXRpbGRl

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
> O provedor de serviço de vendas já possui um usuário com papel de administrador, que não pode ser alterado nem apagado. Seu login é `matilde@siecola.com.br` e sua senha é `matilde`.



## 3 - Serviço de gerenciamento de usuários

### a) Listar todos os usuários

**Método:** GET

**URL:** https://sales-provider.appspot.com/api/users

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

**URL:** https://sales-provider.appspot.com/api/users

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

**URL:** https://sales-provider.appspot.com/api/users/byemail?email={email}

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

**URL:** https://sales-provider.appspot.com/api/users/byemail?email={email}

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

**URL:** https://sales-provider.appspot.com/api/users/byemail?email={email}

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



## 4 - Serviço de gerenciamento de produtos

### a) Listar todos os produtos

**Método:** GET

**URL:** https://sales-provider.appspot.com/api/products

**Permissão de acesso:** qualquer usuário autenticado

**Exemplo de resposta:**

```json
[
    {
        "id": 5707702298738688,
        "name": "product1",
        "description": "description1",
        "code": "COD1",
        "price": 10
    },
    {
        "id": 5668600916475904,
        "name": "product2",
        "description": "description2",
        "code": "COD2",
        "price": 20
    }
]
```



### b) Buscar produto pelo código

**Método:** GET

**URL:** https://sales-provider.appspot.com/api/products/{code}

**Permissão de acesso:** qualquer usuário autenticado

**Exemplo de resposta:**

```json
{
    "id": 5707702298738688,
    "name": "product1",
    "description": "description1",
    "code": "COD1",
    "price": 10
}
```



### c) Criar produto

**Método:** POST

**URL:** https://sales-provider.appspot.com/api/products

**Permissão de acesso:** somente usuário com papel ADMIN

**Exemplo de corpo de requisição:**

```json
{
    "name": "product1",
    "description": "description1",
    "code": "COD1",
    "price": 10
}
```

**Exemplo de resposta:**

```json
{
    "id": 5707702298738688,
    "name": "product1",
    "description": "description1",
    "code": "COD1",
    "price": 10
}
```

### 

### d) Alterar produto

**Método:** PUT

**URL:** https://sales-provider.appspot.com/api/products/{code}

**Permissão de acesso:** somente usuário com papel ADMIN

**Exemplo de corpo de requisição:**

```json
{
    "id": 5707702298738688,
    "name": "product1",
    "description": "description1",
    "code": "COD1",
    "price": 10
}
```

**Exemplo de resposta:**

```json
{
    "id": 5707702298738688,
    "name": "product1",
    "description": "description1",
    "code": "COD1",
    "price": 10
}
```

### 

### e) Apagar produto

**Método:** DELETE

**URL:** https://sales-provider.appspot.com/api/products/{code}

**Permissão de acesso:** somente usuário com papel ADMIN

**Exemplo de resposta:**

```json
{
    "id": 5707702298738688,
    "name": "product1",
    "description": "description1",
    "code": "COD1",
    "price": 10
}
```

### 

## 5 - Serviço de gerenciamento de pedidos

### a) Listar todos os pedidos

**Método:** GET

**URL:** https://sales-provider.appspot.com/api/orders

**Permissão de acesso:** somente usuário com papel ADMIN

**Exemplo de resposta:**

```json
[
    {
        "id": 5668600916475904,
        "email": "doralice@siecola.com.br",
        "freightPrice": 40,
        "orderItems": [
            {
                "id": 5649050225344512,
                "productId": 5707702298738688,
                "orderId": 5668600916475904
            },
            {
                "id": 5724160613416960,
                "productId": 5668600916475904,
                "orderId": 5668600916475904
            }
        ]
    },
    {
        "id": 5741031244955648,
        "email": "doralice@siecola.com.br",
        "freightPrice": 40,
        "orderItems": [
            {
                "id": 5685265389584384,
                "productId": 5707702298738688,
                "orderId": 5741031244955648
            },
            {
                "id": 5757334940811264,
                "productId": 5668600916475904,
                "orderId": 5741031244955648
            }
        ]
    }
]
```



### b) Criar pedido

**Método:** POST

**URL:** https://sales-provider.appspot.com/api/orders

**Permissão de acesso:** usuário com papel ADMIN ou usuário autenticado dono do pedido

**Exemplo de corpo de requisição:**

```json
{ 
    "email": "doralice@siecola.com.br",
    "freightPrice": 40,
    "orderItems": [
    	{
    		"productId": 5707702298738688
    	},
    	{
    		"productId": 5668600916475904
    	}
	]
}
```

**Exemplo de resposta:**

```json
{
    "id": 5741031244955648,
    "email": "doralice@siecola.com.br",
    "freightPrice": 40,
    "orderItems": [
        {
            "id": 5685265389584384,
            "productId": 5707702298738688,
            "orderId": 5741031244955648
        },
        {
            "id": 5757334940811264,
            "productId": 5668600916475904,
            "orderId": 5741031244955648
        }
    ]
}
```

### 

### c) Buscar pedido pelo Id

**Método:** GET

**URL:** https://sales-provider.appspot.com/api/orders/{id}

**Permissão de acesso:** usuário com papel ADMIN ou usuário autenticado dono do pedido

**Exemplo de resposta:**

```json
{
    "id": 5741031244955648,
    "email": "doralice@siecola.com.br",
    "freightPrice": 40,
    "orderItems": [
        {
            "id": 5685265389584384,
            "productId": 5707702298738688,
            "orderId": 5741031244955648
        },
        {
            "id": 5757334940811264,
            "productId": 5668600916475904,
            "orderId": 5741031244955648
        }
    ]
}
```

### 

### d) Buscar pedidos de um usuário pelo seu e-mail

**Método:** GET

**URL:** https://sales-provider.appspot.com/api/orders/byemail?email={email}

**Permissão de acesso:** usuário com papel ADMIN ou usuário autenticado dono do pedido

**Exemplo de resposta:**

```json
[
    {
        "id": 5668600916475904,
        "email": "doralice@siecola.com.br",
        "freightPrice": 40,
        "orderItems": [
            {
                "id": 5649050225344512,
                "productId": 5707702298738688,
                "orderId": 5668600916475904
            },
            {
                "id": 5724160613416960,
                "productId": 5668600916475904,
                "orderId": 5668600916475904
            }
        ]
    },
    {
        "id": 5741031244955648,
        "email": "doralice@siecola.com.br",
        "freightPrice": 40,
        "orderItems": [
            {
                "id": 5685265389584384,
                "productId": 5707702298738688,
                "orderId": 5741031244955648
            },
            {
                "id": 5757334940811264,
                "productId": 5668600916475904,
                "orderId": 5741031244955648
            }
        ]
    }
]
```



### e) Apagar pedido

**Método:** DELETE

**URL:** https://sales-provider.appspot.com/api/orders/{id}

**Permissão de acesso:** usuário com papel ADMIN ou usuário autenticado dono do pedido

**Exemplo de resposta:**

```json
{
    "id": 5741031244955648,
    "email": "doralice@siecola.com.br",
    "freightPrice": 40,
    "orderItems": [
        {
            "id": 5685265389584384,
            "productId": 5707702298738688,
            "orderId": 5741031244955648
        },
        {
            "id": 5757334940811264,
            "productId": 5668600916475904,
            "orderId": 5741031244955648
        }
    ]
}
```

# ECOMMERCE PROJECT SUMMARY
Este es un pequeño resuemen de los ENDPOINTS, SERVCIOS Y RECURSOS que utiliza esta API ECOMMERCE
## ENDPOINTS
### USUARIO
- > **Registro del usuario**

Request URL **POST**
~~~

~~~


- > **Logueo del usuario**

Request URL **POST**
~~~
https://ecommercespring-a9fthwekhac7f6b6.brazilsouth-01.azurewebsites.net/users/login/user?tempCartId
~~~
**Request Body**
~~~json
{
    "username": "test2345",
    "password": "test123456"
}
~~~
**RESPONSE**
~~~json
{
    "code": 200,
    "message": "Successful authentication",
    "token": "[bearer token generado]"
}
~~~

![Static Badge](https://img.shields.io/badge/redis-black?style=for-the-badge&logo=redis)
![Static Badge](https://img.shields.io/badge/azure-black?style=for-the-badge&logo=icloud)

# ECOMMERCE PROJECT SUMMARY
Este es un pequeño resumen de los ENDPOINTS Y RECURSOS que necesitaras saber para utilizar esta API ECOMMERCE 
> - URL de la aplicación: https://ecommercespring-a9fthwekhac7f6b6.brazilsouth-01.azurewebsites.net
## ENDPOINTS
### 1. USUARIO
- > **Registrar usuario**

Request URL **POST**
~~~
https://ecommercespring-a9fthwekhac7f6b6.brazilsouth-01.azurewebsites.net/users/register/user
~~~
**Request Body e.g**
~~~json
{
  "name": "Ivan",
  "lastName": "Rupay",
  "username": "ivanar69",
  "email": "ivanrupay123@gmail.com",
  "password": "ivanrupay123",
  "address": "123 Main St",
  "cellphone": "933777024",
  "dni": "74231720",
  "file": "Download.jpg"
}
~~~
**Response e.g**
~~~
{
    "code": 200,
    "message": "Usuario registrado con éxito y Token generado"
}
~~~

- > **Loguear usuario**

Request URL **POST**
~~~
https://ecommercespring-a9fthwekhac7f6b6.brazilsouth-01.azurewebsites.net/users/login/user?tempCartId
~~~
**Request Body e.g**
~~~json
{
    "username": "test2345",
    "password": "test123456"
}
~~~
**RESPONSE e.g**
~~~json
{
    "code": 200,
    "message": "Successful authentication",
    "token": "[bearer token de acceso generado]"
}
~~~

- > **Recuperar contraseña de usuario**

Request URL **POST**
~~~
https://ecommercespring-a9fthwekhac7f6b6.brazilsouth-01.azurewebsites.net/users/recoverPassword/user
~~~
**Request Body e.g**
~~~json
{
    "email": "josuealva920@gmail.com",
    "firstPassword": "123456789",
    "secondPassword": "123456789"
}
~~~
**RESPONSE e.g**
~~~json
{
    "code": 200,
    "message": "Password updated successfully"
}
~~~

- > **Verificar token**

Request URL **POST**
~~~
https://ecommercespring-a9fthwekhac7f6b6.brazilsouth-01.azurewebsites.net/users/verifyToken/user
~~~
**Request Body e.g**
~~~json
{
    "token": "[Introducir token]"
}
~~~
**RESPONSE e.g**
~~~json
{
    "code": 200,
    "message": "Token verified successfully",
    "token": "[bearer token de acceso generado]"
}
~~~

### 2. PRODUCTO
- > **Listado de productos**

Request URL **GET**
~~~
https://ecommercespring-a9fthwekhac7f6b6.brazilsouth-01.azurewebsites.net/product/list/
~~~

**~~Request Body~~**

**RESPONSE e.g**
~~~json
{
    "success": true,
    "message": "Products retrieved",
    "data": [
        {
            "id": 1,
            "name": "Paracetamol 500mg",
            "description": "Analgésico y antipirético",
            "price": 5.50,
            "stock": "193",
            "image": null,
            "creationDate": [
                2024,
                9,
                30,
                14,
                42,
                54
            ],
            "updateDate": [
                2024,
                10,
                4,
                0,
                25,
                29
            ],
            "status": "DISPONIBLE",
            "warranty": "2 años",
            "specs": "No mezclar con alcohol",
            "compatibility": "Adultos y niños mayores de 12 años"
        },
        ...
    }
}
~~~

### 3. CARRITO DE COMPRAS
- > **Listar productos del carrito**

Request URL **GET**
~~~
https://ecommercespring-a9fthwekhac7f6b6.brazilsouth-01.azurewebsites.net/shopping-carts/getListCarts/cart
~~~

**~~Request Body~~**

**RESPONSE e.g**
~~~json
{
    "code": 200,
    "message": "Shopping cart retrieved successfully",
    "listShoppingCartDTO": [
        {
            "id": null,
            "user": {
                "id": 22,
                "username": "test2345",
                "email": "josuealva9210@gmail.com",
                "cellphone": "223456222"
            },
            "product": {
                "id": 1,
                "image": null,
                "name": "Paracetamol 500mg",
                "price": 5.50,
                "stock": 173,
                "model": "Comprimidos"
            },
            "amount": 10
        }
    ]
}
~~~

- > **Agregar productos al carrito**

Request URL **POST**
~~~
https://ecommercespring-a9fthwekhac7f6b6.brazilsouth-01.azurewebsites.net/shopping-carts/addProduct/cart
~~~

**Request Body e.g**
~~~
{
    "productId": 1,
    "amount": 10
}
~~~

**RESPONSE e.g**
~~~json
{
    "code": 200,
    "message": "Product added to cart successfully"
}
~~~

- > **Eliminar productos del carrito**

Request URL **DELETE**
~~~
https://ecommercespring-a9fthwekhac7f6b6.brazilsouth-01.azurewebsites.net/shopping-carts/removeProduct/cart
~~~

**Request Body e.g**
~~~
{
    "productId": 1,
    "amount": 1
}
~~~

**RESPONSE e.g**
~~~json
{
    "code": 200,
    "message": "Product removed from cart successfully"
}
~~~

- > **Eliminar todos los productos del carrito**

Request URL **DELETE**
~~~
https://ecommercespring-a9fthwekhac7f6b6.brazilsouth-01.azurewebsites.net/shopping-carts/clearCart
~~~

**~~Request Body e.g~~**

**RESPONSE e.g**
~~~json
{
    "code": 200,
    "message": "Shopping cart cleared successfully"
}
~~~

### 4. PASARELA DE PAGO
- > **Crear Pedido**

Request URL **POST**
~~~
https://ecommercespring-a9fthwekhac7f6b6.brazilsouth-01.azurewebsites.net/order/createOrder
~~~

**Request Body e.g**
{
    "shippingAddress" : "San Juan de Lurigancho"
}

**RESPONSE e.g**
~~~json
{
    "code": 200,
    "message": "https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=EC-7JT10651SR690803A"
}
~~~

## NOTAS ADICIONALES

- El endpoint "Registrar usuario" generará un token que tiene que ser verificado por el endpoint "verificar token", el token se generará en la caché de redis en la nube, dura 30 segundos aproximadamente y es necesario para completar el registro de usuario.
- El endpoint "Recuperar contraseña" mandara un código al correo que el usuario ingrese. Este código se tiene que enviar como body request a "verificar token" para que se confirme el cambio de contraseña, de otra manera no se podra cambiar la contraseña.
- Los tokens que generan los usuarios al iniciar sesión(loguear usuario) son necesarios para poder hacer las peticiones los demas endpoints. Exceptuando los endpoints de: "registrar usuario", "verificar token" y "recuperar contraseña" .
- Al momento de realizar el pago ("Crear pedido"), el usuario sera enviado a una página en paypal para terminar la transacción de compra.

#### ESTE PROYECTO UTILIZA:
 ![Static Badge](https://img.shields.io/badge/redis-black?style=for-the-badge&logo=redis)
#### PROYECTO DESPLEGADO EN:
 ![Static Badge](https://img.shields.io/badge/azure-black?style=for-the-badge&logo=icloud)

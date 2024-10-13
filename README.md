# E-COMMERCE PROJECT SUMMARY

Este es un resumen de los **endpoints** y recursos necesarios para utilizar la API de e-commerce.

**URL de la aplicación:**  
[https://ecommercespring-a9fthwekhac7f6b6.mexicocentral-01.azurewebsites.net/](https://ecommercespring-a9fthwekhac7f6b6.mexicocentral-01.azurewebsites.net/)

### AUTORIZACIÓN

Se requiere incluir un **Bearer Token** proporcionado al usuario en el encabezado de autorización para ciertas solicitudes.

## ENDPOINTS

### 1. USUARIO

- **Registrar usuario**
  - **Request URL:** `POST /users/register/user`
  - **Request Body (form data):**
    ```json
    {
      "key": "user",
      "value": {
        "name": "Ivan",
        "lastName": "Rupay",
        "username": "ivanar69",
        "email": "ivanrupay123@gmail.com",
        "password": "ivanrupay123",
        "address": "123 Main St",
        "cellphone": "933777024",
        "dni": "74231720"
      },
      "key": "file",
      "value": "imagen.jpg-jpeg-png"
    }
    ```
  - **Response:**
    ```json
    {
      "code": 200,
      "message": "Usuario registrado con éxito y Token generado"
    }
    ```

- **Loguear usuario**
  - **Request URL:** `POST /users/login/user?tempCartId`
  - **Params:** `tempCartId` (código del carrito de compras)
  - **Request Body:**
    ```json
    {
      "username": "test2345",
      "password": "test123456"
    }
    ```
  - **Response:**
    ```json
    {
      "code": 200,
      "message": "Successful authentication",
      "token": "[bearer token de acceso generado]"
    }
    ```

- **Recuperar contraseña de usuario**
  - **Request URL:** `POST /recoverPassword/user`
  - **Request Body:**
    ```json
    {
      "email": "josuealva920@gmail.com",
      "firstPassword": "123456789",
      "secondPassword": "123456789"
    }
    ```
  - **Response:**
    ```json
    {
      "code": 200,
      "message": "Password updated successfully"
    }
    ```

- **Verificar token**
  - **Request URL:** `POST /users/verifyToken/user`
  - **Request Body:**
    ```json
    {
      "token": "[Introducir token]"
    }
    ```
  - **Response:**
    ```json
    {
      "code": 200,
      "message": "Token verified successfully",
      "token": "[bearer token de acceso generado]"
    }
    ```
    
- **Actualizar usuario**
  - **Obligatorio Authentication: Bearer Token del Usuario**
  - **Request URL:** `POST /users/update/user`
  - **Request Body (form data):**
    ```json
    {
      "key": "user",
      "value": {
        "name": "Ivan",
        "lastName": "Rupay",
        "username": "ivanar69",
        "email": "ivanrupay123@gmail.com",
        "password": "ivanrupay123",
        "address": "123 Main St",
        "cellphone": "933777024",
        "dni": "74231720"
      },
      "key": "file",
      "value": "imagen.jpg-jpeg-png"
    }
    ```
  - **Response:**
    ```json
    {
      "code": 200,
      "message": "Usuario actualizado con éxito"
    }
    ```

- **Eliminar usuario**
  - **Obligatorio Authentication: Bearer Token del Usuario**
  - **Request URL:** `POST /users/delete/user`
  - **Response:**
    ```json
    {
      "code": 200,
      "message": "Usuario eliminado con éxito"
    }
    ```

### 2. PRODUCTO

- **Listado de productos**
  - **Request URL:** `GET /product/list/`
  - **Response:**
    ```json
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
          "creationDate": [2024, 9, 30, 14, 42, 54],
          "updateDate": [2024, 10, 4, 0, 25, 29],
          "status": "DISPONIBLE",
          "warranty": "2 años",
          "specs": "No mezclar con alcohol",
          "compatibility": "Adultos y niños mayores de 12 años"
        }
      ]
    }
    ```

### 3. CARRITO DE COMPRAS
- **Obligatorio Authentication: Bearer Token del Usuario**
- **Listar productos del carrito**
  - **Request URL:** `GET /shopping-carts/getListCarts/cart`
  - **Response:**
    ```json
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
    ```

- **Agregar productos al carrito**
  - **Obligatorio Authentication: Bearer Token del Usuario**
  - **Request URL:** `POST /shopping-carts/getListCarts/cart`
  - **Request Body:**
    ```json
    {
      "productId": 1,
      "amount": 10
    }
    ```
  - **Response:**
    ```json
    {
      "code": 200,
      "message": "Product added to cart successfully"
    }
    ```

- **Eliminar productos del carrito**
  - **Obligatorio Authentication: Bearer Token del Usuario**
  - **Request URL:** `DELETE /shopping-carts/removeProduct/cart`
  - **Request Body:**
    ```json
    {
      "productId": 1,
      "amount": 1
    }
    ```
  - **Response:**
    ```json
    {
      "code": 200,
      "message": "Product removed from cart successfully"
    }
    ```

- **Eliminar Carrito de compras con sus productos**
  - **Obligatorio Authentication: Bearer Token del Usuario**
  - **Request URL:** `DELETE /shopping-carts/clearCart`
  - **Response:**
    ```json
    {
      "code": 200,
      "message": "Shopping cart cleared successfully"
    }
    ```

### 3.5 CARRITO DE COMPRAS SIN AUTORIZACIÓN

- **Crear un carrito temporal**
  - **Request URL:** `GET /noauth/shopping-carts/createTempCart/cart`
  - **Response:**
    ```json
    {
      "code": 201,
      "message": "Cart created",
      "cartId": "3f64de6c-636a-4bfe-a5b4-46eacb055da0"
    }
    ```

- **Agregar productos al carrito sin autorización**
  - **Request URL:** `POST /noauth/shopping-carts/[id_carrito]/addProduct/cart`
  - **Request Body:**
    ```json
    {
      "productId": 1,
      "amount": 10
    }
    ```
  - **Response:**
    ```json
    {
      "code": 200,
      "message": "Product added to cart successfully"
    }
    ```

- **Listar productos del carrito sin autorización**
  - **Request URL:** `GET /noauth/shopping-carts/{id_carrito}/getTempCartItems/cart`
  - **Response:**
    ```json
    {
      "code": 200,
      "message": "Cart items retrieved successfully",
      "listShoppingCartDTO": [
        {
          "id": null,
          "user": null,
          "product": {
            "id": 1,
            "image": null,
            "name": "Paracetamol 500mg",
            "price": 5.50,
            "stock": 181,
            "model": "Comprimidos"
          },
          "amount": 2
        }
      ]
    }
    ```

- **Eliminar productos del carrito sin autorización**
  - **Request URL:** `DELETE /noauth/shopping-carts/{id_carrito}/removeProduct/cart`
  - **Request Body:**
    ```json
    {
      "productId": 1,
      "amount": 1
    }
    ```
  - **Response:**
    ```json
    {
      "code": 200,
      "message": "Product removed from cart successfully"
    }
    ```

- **Eliminar todos los productos del carrito sin autorización**
  - **Request URL:** `DELETE /noauth/shopping-carts/{id_carrito}/clearTempCart/cart`
  - **Response:**
    ```json
    {
      "code": 200,
      "message": "Shopping cart cleared successfully"
    }
    ```

### 4. PEDIDOS

- **Crear un pedido**
  - **Obligatorio Authentication: Bearer Token del Usuario**
  - **Request URL:** `POST /orders/create/order`
  - **Request Body:**
    ```json
    {
      "userId": 1,
      "products": [
        {
          "productId": 1,
          "quantity": 10
        }
      ]
    }
    ```
  - **Response:**
    ```json
    {
      "code": 200,
      "message": "Order created successfully",
      "orderId": 1
    }
    ```
---

Este es un resumen básico de la estructura de la API. Si necesitas más detalles o deseas incluir más información, no dudes en avisarme.

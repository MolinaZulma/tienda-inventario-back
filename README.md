🛒 Gestión de inventario y facturación - Backend
Este proyecto es una API REST robusta, la cual esta desarrollada con 🍃 Spring Boot 
para el control del inventario de una tienda y la facturación de las ventas.
Implementé capas de seguridad avanzadas y una arquitectura limpia para garantizar la integridad de los datos. 

📄 Lógica de Negocio: Proceso de Venta y Facturación
Para garantizar la consistencia, el sistema sigue este flujo atómico cada vez que se registra una venta:

* Validación de Existencias: Se verifica que el stock actual sea suficiente para cubrir el pedido. ✅
* Cálculo Financiero: El sistema calcula el total de la venta basándose en los precios vigentes y los impuestos configurados. 💰
* Actualización de Inventario: Se descuentan las unidades vendidas de la tabla de productos de forma automática. 📉
* Generación de Comprobante: Se crea el registro de la factura vinculado al usuario que realizó la operación. 🖨️

🧪 Pruebas con Postman-Endpoints
1. Para probar la API, asegúrate de enviar los datos en formato JSON y configurar el Content-Type: application/json en los encabezados.
* Autenticación (Login)
* Antes de realizar ventas, el usuario debe autenticarse para obtener la sesión.
* URL: http://localhost:8081/login
* Método: POST

2. Registro de una Venta (Facturación)
* Este endpoint procesa la transacción y descuenta el stock.
* URL: http://localhost:8081/api/ventas/crear
* Método: POST


✨ Características Principales
* Autenticación y Autorización: Implementación de Spring Security para proteger rutas críticas.
* Seguridad de los datos: gestión de contraseñas mediante cifrado BCrypt.
* Control de acceso RBAC: diferenciación de permisos entre roles ADMIN y EMPLEADO.
* Gestión de productos: CRUD completo (Crear, Leer, Actualizar, Eliminar) con validación de stock.

🛠️ Tecnologías que utilicé
* Lenguaje de programación Java 17 ☕
* Framework Spring Boot 3 🍃
* Seguridad con Spring Security 🔐
* Base de datos MySQL 🐬
* Gestión de dependencias con Maven 📦
* Ventas/Facturas 📄

Principales Endpoints 🚀

Método	  Endpoint	                    Descripción	                  Acceso
POST	    /login	                      Inicia sesión en el sistema	  Público
GET	      /api/productos	              Lista todos los productos	    Autenticado
DELETE	  /api/productos/eliminar/{id}	Elimina un producto	          Solo ADMIN

💡Retos Técnicos
La configuración de PasswordEncoder la realicé definiendo un Bean dentro de la clase de configuración de seguridad (SecurityConfig.java)
Un Bean es un objeto que Spring gestiona de forma automática. 

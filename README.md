# Order Pay Service

Microservicio para la gestión de órdenes y pagos del sistema de e-commerce.

## Características

- **Gestión de Órdenes**: Crear, consultar, actualizar y cancelar órdenes
- **Gestión de Pagos**: Procesar pagos, consultar estados y reembolsos
- **Integración con otros servicios**: Auth Service, Product Service, Shopping Cart Service
- **Base de datos**: MySQL con migraciones Flyway
- **Documentación**: OpenAPI/Swagger
- **Logging**: SLF4J con Logback

## Endpoints Principales

### Órdenes
- `POST /orders` - Crear nueva orden
- `POST /orders/from-cart/{userId}` - Crear orden desde carrito
- `GET /orders/{orderId}` - Obtener orden por ID
- `GET /orders/number/{orderNumber}` - Obtener orden por número
- `GET /orders/user/{userId}` - Obtener órdenes del usuario
- `PUT /orders/{orderId}/status` - Actualizar estado de orden
- `POST /orders/{orderId}/cancel` - Cancelar orden

### Pagos
- `POST /payments` - Crear nuevo pago
- `POST /payments/process` - Procesar pago
- `GET /payments/{paymentId}` - Obtener pago por ID
- `GET /payments/reference/{reference}` - Obtener pago por referencia
- `GET /payments/order/{orderId}` - Obtener pagos de una orden
- `POST /payments/refund/{reference}` - Reembolsar pago

## Estados de Órdenes

- **PENDING**: Orden creada, pendiente de confirmación
- **CONFIRMED**: Orden confirmada y pago recibido
- **PROCESSING**: Orden en procesamiento
- **SHIPPED**: Orden enviada
- **DELIVERED**: Orden entregada
- **CANCELLED**: Orden cancelada
- **RETURNED**: Orden devuelta

## Estados de Pagos

- **PENDING**: Pago creado, pendiente de procesamiento
- **PROCESSING**: Pago en proceso
- **COMPLETED**: Pago completado exitosamente
- **FAILED**: Pago falló
- **CANCELLED**: Pago cancelado
- **REFUNDED**: Pago reembolsado

## Configuración

El servicio se configura a través de Spring Cloud Config Server.
Variables principales:
- `spring.datasource.url`: URL de la base de datos MySQL
- `spring.datasource.username`: Usuario de la base de datos
- `spring.datasource.password`: Contraseña de la base de datos

## Ejecución

```bash
mvn clean install
mvn spring-boot:run
```

El servicio estará disponible en `http://localhost:8080`
La documentación Swagger en `http://localhost:8080/swagger-ui.html`
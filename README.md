
# Tienda Online - Arquitectura de Microservicios

## Descripción del Proyecto

Este proyecto implementa una tienda online completa utilizando una arquitectura de microservicios moderna.

## Arquitectura del Sistema

<img width="615" height="277" alt="image" src="https://github.com/user-attachments/assets/4fcc996e-4c22-4ae7-a817-689ed5db4e1a" />


## Componentes del Sistema

### 1. **Frontend (Angular)**
- **Tecnología**: Angular 19.2.0
- **Funcionalidad**: Interfaz de usuario para la tienda online
- **Características**:
  - Autenticación con Azure AD (MSAL)
  - Gestión de productos y carrito de compras
  - Generación de tickets en PDF

### 2. **API Gateway**
- **Funcionalidad**: Punto de entrada principal para todas las peticiones
- **Seguridad**: Validación de JWT tokens

### 3. **BFF (Backend for Frontend)**
- **Tecnología**: Spring Boot 3.5.0
- **Funcionalidad**: Adaptador entre el frontend y los microservicios
- **Seguridad**: Validación de JWT tokens
- **Características**:
  - Spring Security con OAuth2 Resource Server
  - WebClient para comunicación con microservicios

### 4. **Microservicio de Ventas**
- **Tecnología**: Spring Boot 3.5.3
- **Base de Datos**: Oracle Database
- **Funcionalidad**: Gestión de productos y carritos

### 5. **Microservicio de Promociones**
- **Tecnología**: Spring Boot 3.5.3
- **Funcionalidad**: Gestión de promociones y descuentos
- **Características**:
  - RabbitMQ para envío de mensajes

### 6. **Servicios Consumidores**

#### Promo-Consumidor
- **Funcionalidad**: Consume mensajes de la cola de promociones
- **Acción**: Escribe información de promociones en archivos JSON

#### Ventas-Consumidor
- **Funcionalidad**: Consume mensajes de la cola de ventas
- **Acción**: Actualiza el estado del carrito de compras

### 7. **Sistema de Mensajería**
- **Tecnología**: RabbitMQ
- **Colas**:
  - `promociones`: Para mensajes de promociones
  - `ventas_mq`: Para mensajes de transacciones de venta

## Tecnologías Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.5.x**
- **Spring Security**
- **Spring Data JPA**
- **RabbitMQ**
- **Oracle Database**
- **Maven**

### Frontend
- **Angular 19.2.0**
- **TypeScript**
- **Bootstrap 5.3.6**
- **Azure MSAL**
- **jspdf** (Generación de PDFs)

### Infraestructura
- **Docker**
- **Nginx** (Proxy reverso)
- **JWT** (Autenticación)

## 📦 Estructura del Proyecto

```
tienda/
├── frontend/                 # Aplicación Angular
├── bff/                     # Backend for Frontend
├── ventas-productor/        # Microservicio de Ventas
├── promo-productor/         # Microservicio de Promociones
├── ventas-consumidor/       # Consumidor de Ventas
├── promo-consumidor/        # Consumidor de Promociones
└── README.md
```

## 🚀 Instalación y Configuración

### Prerrequisitos
- Java 17 o superior
- Node.js 18+ y npm/pnpm
- Docker y Docker Compose
- Oracle Database (o usar contenedor Docker)
- RabbitMQ

## 🔐 Configuración de Seguridad

### JWT Configuration
El sistema utiliza JWT tokens para autenticación

## 📡 APIs Disponibles

### Ventas Microservice
- `GET /productos` - Obtener productos
- `GET /productos/{id}` - Obtener producto por id
- `POST /carro` - Crear carro
- `GET /carro/{id}` - Crear carro
- `POST /comprar/{id}` - Concretar compra

### Promociones Microservice
- `POST /promociones` - Crear promoción (escribe json a disco)

## Flujo de Datos

### Flujo de Promociones
1. El microservicio de promociones recibe una solicitud
2. Procesa la promoción y envía un mensaje a la cola `Promociones`
3. El servicio `promo-consumidor` lee el mensaje
4. Escribe la información de la promoción en un archivo JSON

### Flujo de Ventas
1. El microservicio de ventas procesa una transacción
2. Envía un mensaje a la cola `Ventas`
3. El servicio `ventas-consumidor` lee el mensaje
4. Actualiza el estado del carrito correspondiente

### RabbitMQ
- URL: `http://localhost:15672`
- Usuario: `guest`
- Contraseña: `guest`
- Colas: `ventas_mq` y `promociones`

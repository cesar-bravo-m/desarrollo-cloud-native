# JWT Security Setup

## Overview
The BFF application is configured to protect specific endpoints using JWT authentication with Azure AD B2C.

## Protected Endpoints
- `GET /hello` - Requires valid JWT token
- `POST /carro` - Requires valid JWT token

## Unprotected Endpoints
- All other endpoints are publicly accessible, including:
  - `GET /carro/{id}`
  - All `/usuario` endpoints
  - All `/producto` endpoints
  - Swagger documentation (`/swagger-ui.html`)

## Configuration
The JWT validation is configured using the issuer URI specified in `application.properties`:
```
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://3d84d72d-56be-45a9-a060-a33d33d992e9.ciamlogin.com/3d84d72d-56be-45a9-a060-a33d33d992e9/v2.0
```

## Testing Protected Endpoints

### Without Token (401 Unauthorized)
```bash
curl -X GET http://localhost:8080/hello
curl -X POST http://localhost:8080/carro -H "Content-Type: application/json" -d '{}'
```

### With Token (200 OK)
```bash
curl -X GET http://localhost:8080/hello -H "Authorization: Bearer YOUR_JWT_TOKEN"
curl -X POST http://localhost:8080/carro -H "Authorization: Bearer YOUR_JWT_TOKEN" -H "Content-Type: application/json" -d '{"marca":"Toyota","modelo":"Corolla"}'
```

## Token Format
The application expects JWT tokens in the Authorization header using the Bearer scheme:
```
Authorization: Bearer <jwt-token>
```

The token will be validated against the configured Azure AD B2C issuer to ensure:
- Token signature is valid
- Token is not expired
- Token issuer matches the configured issuer URI 
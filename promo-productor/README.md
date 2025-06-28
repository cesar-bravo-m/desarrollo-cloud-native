# Promociones Application

This is a Spring Boot application that sends Promocion objects to a RabbitMQ queue.

## Prerequisites

- Java 17 or higher
- Maven
- RabbitMQ running locally on port 5672

## Running the Application

1. Make sure RabbitMQ is running locally with the default configuration (guest/guest)
2. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

## API Usage

### POST /api/promociones

Sends a Promocion object to the RabbitMQ queue named "promociones".

**Request Body:**
```json
{
  "id": 1,
  "producto": "Laptop Gaming",
  "cantidad": 10,
  "valor": 999.99,
  "fechaInicio": "2024-01-01",
  "fechaFin": "2024-01-31"
}
```

**Response:**
```
Promoción enviada exitosamente a la cola: Promocion{id=1, producto='Laptop Gaming', cantidad=10, valor=999.99, fechaInicio=2024-01-01, fechaFin=2024-01-31}
```

## Promocion Object Fields

- `id`: Long - Unique identifier
- `producto`: String - Product name
- `cantidad`: Integer - Quantity available
- `valor`: BigDecimal - Price value
- `fechaInicio`: LocalDate - Start date (YYYY-MM-DD format)
- `fechaFin`: LocalDate - End date (YYYY-MM-DD format)

## RabbitMQ Configuration

The application is configured to connect to:
- Host: localhost
- Port: 5672
- Username: guest
- Password: guest
- Queue: promociones

## Example cURL Request

```bash
curl -X POST http://localhost:8080/api/promociones \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "producto": "Laptop Gaming",
    "cantidad": 10,
    "valor": 999.99,
    "fechaInicio": "2024-01-01",
    "fechaFin": "2024-01-31"
  }'
``` 
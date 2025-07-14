# Admin Promociones - Kafka Integration

This Spring Boot application is configured to work with a Kafka cluster consisting of three Zookeeper nodes and three Kafka nodes.

## Prerequisites

- Docker and Docker Compose installed
- Java 17 or higher
- Maven

## Quick Start

### 1. Start the Kafka Cluster

```bash
docker-compose up -d
```

This will start:
- 3 Zookeeper nodes (ports 2181, 2182, 2183)
- 3 Kafka nodes (ports 9092, 9093, 9094)
- Kafka UI for management (port 8080)

### 2. Verify Kafka Cluster is Running

Check the Kafka UI at: http://localhost:8080

You should see:
- The "ventas" topic with 3 partitions and 3 replicas
- The "stock" topic with 3 partitions and 3 replicas

### 3. Start the Spring Application

```bash
./mvnw spring-boot:run
```

The application will start on port 8087.

### 4. Test the Integration

#### Health Check
```bash
curl http://localhost:8087/api/kafka/health
```

#### Send a Venta Message
```bash
curl -X POST http://localhost:8087/api/kafka/ventas \
  -H "Content-Type: application/json" \
  -d '{"message": "Nueva venta: Producto A - $100"}'
```

#### Read Venta Messages
```bash
curl http://localhost:8087/api/kafka/ventas
```

#### Send a Stock Message
```bash
curl -X POST http://localhost:8087/api/kafka/stock \
  -H "Content-Type: application/json" \
  -d '{"message": "Stock actualizado: Producto A - 50 unidades"}'
```

## Architecture

### Kafka Topics

- **ventas**: Topic for sales-related messages
  - Partitions: 3
  - Replicas: 3
  - Auto-created on application startup

- **stock**: Topic for stock-related messages
  - Partitions: 3
  - Replicas: 3
  - Auto-created on application startup

### Application Components

- `KafkaConfig`: Configuration class that sets up Kafka topics and beans
- `KafkaService`: Service class for producing messages and reading from topics
- `KafkaController`: REST controller with endpoints for sending and reading Kafka messages

## Configuration

The application is configured to connect to the Kafka cluster via:
- Bootstrap servers: localhost:9092, localhost:9093, localhost:9094
- Consumer group: adminpromociones-group
- Auto offset reset: earliest

## Monitoring

- **Kafka UI**: http://localhost:8080 - Web interface for managing Kafka topics and viewing messages
- **Application Logs**: Check the console output for Kafka message production and consumption logs

## Stopping the Services

```bash
# Stop the Spring application
Ctrl+C

# Stop the Kafka cluster
docker-compose down
```

## Troubleshooting

1. **Topics not created**: Ensure the Spring application starts after the Kafka cluster is fully running
2. **Connection issues**: Verify all Kafka nodes are healthy in the Kafka UI
3. **Port conflicts**: Check if ports 9092-9094, 2181-2183, 8080, 8087 are available 
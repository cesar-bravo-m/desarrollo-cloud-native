# Host Configuration Guide

This guide explains how to use configurable host properties instead of hardcoded localhost references in the MS Productos application.

## Configuration Files

### Default Configuration (`application.properties`)
Contains the base configuration with localhost defaults for development.

### Environment-Specific Configurations
- `application-dev.properties` - Development environment settings
- `application-staging.properties` - Staging environment settings  
- `application-prod.properties` - Production environment settings

## Available Configuration Properties

### Application Host Properties (`app.host.*`)
```properties
app.host.base-url=http://localhost:8083          # This microservice's base URL
app.host.api-gateway=http://localhost:8080       # API Gateway URL
app.host.frontend=http://localhost:3000          # Frontend application URL
```

### External Services Properties (`external.services.*`)
```properties
external.services.user-service=http://localhost:8081     # User management service
external.services.order-service=http://localhost:8082    # Order management service
external.services.payment-service=http://localhost:8084  # Payment processing service
```

## Usage in Code

### Injecting Configuration Classes
```java
@Autowired
private HostConfig hostConfig;

@Autowired
private ExternalServicesConfig externalServicesConfig;
```

### Using Configured URLs
```java
// Instead of hardcoded: "http://localhost:8081/user/" + userId
String url = externalServicesConfig.getUserService() + "/user/" + userId;

// Instead of hardcoded: "http://localhost:8083/producto/" + productId
String productUrl = hostConfig.getBaseUrl() + "/producto/" + productId;
```

## Running with Different Profiles

### Development (default)
```bash
java -jar ms-productos.jar
# or
java -jar ms-productos.jar --spring.profiles.active=dev
```

### Staging
```bash
java -jar ms-productos.jar --spring.profiles.active=staging
```

### Production
```bash
java -jar ms-productos.jar --spring.profiles.active=prod
```

## Environment Variables Override

You can also override any property using environment variables:
```bash
export APP_HOST_BASE_URL=https://my-custom-api.com
export EXTERNAL_SERVICES_USER_SERVICE=https://my-user-service.com
java -jar ms-productos.jar
```

## Docker Environment

When using Docker, set environment variables in your docker-compose.yml:
```yaml
services:
  ms-productos:
    image: ms-productos:latest
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - APP_HOST_BASE_URL=https://api.tienda.com
      - EXTERNAL_SERVICES_USER_SERVICE=https://users.tienda.com
```

## Debugging Configuration

Use the `/config/hosts` endpoint to verify current host configurations:
```bash
curl http://localhost:8083/config/hosts
```

This will return the current configuration values being used by the application.

## Migration from Hardcoded URLs

### Before (Hardcoded)
```java
String userUrl = "http://localhost:8081/user/" + userId;
RestTemplate restTemplate = new RestTemplate();
return restTemplate.getForObject(userUrl, String.class);
```

### After (Configurable)
```java
@Autowired
private ExternalServicesConfig externalServicesConfig;

String userUrl = externalServicesConfig.getUserService() + "/user/" + userId;
RestTemplate restTemplate = new RestTemplate();
return restTemplate.getForObject(userUrl, String.class);
```

This approach ensures your application can be deployed across different environments without code changes, only configuration changes. 
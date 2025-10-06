# Testing del Microservicio de Pacientes

## Endpoints para Probar

Una vez que la aplicación esté ejecutándose en `http://localhost:8081`, puedes probar estos endpoints:

### 1. Obtener todos los pacientes (GET)
```bash
curl -X GET "http://localhost:8081/api/patients?page=0&size=10" \
  -H "Content-Type: application/json"
```

### 2. Buscar pacientes por nombre (GET)
```bash
curl -X GET "http://localhost:8081/api/patients/search?name=Juan&page=0&size=10" \
  -H "Content-Type: application/json"
```

### 3. Obtener paciente por ID (GET)
```bash
curl -X GET "http://localhost:8081/api/patients/1" \
  -H "Content-Type: application/json"
```

### 4. Obtener paciente por email (GET)
```bash
curl -X GET "http://localhost:8081/api/patients/email/juan.perez@example.com" \
  -H "Content-Type: application/json"
```

### 5. Crear nuevo paciente (POST)
```bash
curl -X POST "http://localhost:8081/api/patients" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "María",
    "lastName": "González",
    "email": "maria.gonzalez@example.com",
    "gender": "FEMALE"
  }'
```

### 6. Actualizar paciente (PUT)
```bash
curl -X PUT "http://localhost:8081/api/patients/1" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan Carlos",
    "lastName": "Pérez",
    "email": "juan.carlos.perez@example.com",
    "gender": "MALE"
  }'
```

### 7. Obtener prescripciones de un paciente (GET)
```bash
curl -X GET "http://localhost:8081/api/patients/1/prescriptions" \
  -H "Content-Type: application/json"
```

### 8. Eliminar paciente (DELETE)
```bash
curl -X DELETE "http://localhost:8081/api/patients/1" \
  -H "Content-Type: application/json"
```

## Usando Postman/Insomnia

### Collection para Postman:

```json
{
  "info": {
    "name": "Patients Microservice",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Get All Patients",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{base_url}}/api/patients?page=0&size=10",
          "host": ["{{base_url}}"],
          "path": ["api", "patients"],
          "query": [
            {"key": "page", "value": "0"},
            {"key": "size", "value": "10"}
          ]
        }
      }
    },
    {
      "name": "Create Patient",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"firstName\": \"Juan\",\n  \"lastName\": \"Pérez\",\n  \"email\": \"juan.perez@example.com\",\n  \"gender\": \"MALE\"\n}"
        },
        "url": {
          "raw": "{{base_url}}/api/patients",
          "host": ["{{base_url}}"],
          "path": ["api", "patients"]
        }
      }
    }
  ],
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:8081"
    }
  ]
}
```

## Swagger UI

Accede a la documentación interactiva en:
- **Swagger UI:** http://localhost:8081/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8081/api-docs

## Health Check

Verifica que el microservicio esté funcionando:
```bash
curl -X GET "http://localhost:8081/actuator/health"
```

Respuesta esperada:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    }
  }
}
```

## Notas Importantes

1. **Base de Datos:** El microservicio se conecta a Supabase, las tablas ya deben existir.
2. **Paginación:** Todos los endpoints que devuelven listas soportan paginación.
3. **Validación:** Los campos requeridos son validados automáticamente.
4. **Errores:** Los errores devuelven códigos HTTP apropiados (400, 404, 500).
5. **Logs:** Revisa los logs en la consola para ver las operaciones en detalle.
@echo off
echo === Probando API de Pacientes con CURL ===

echo.
echo 1. Verificando estado de la API...
curl -s http://localhost:8081/actuator/health
echo.

echo.
echo 2. Obteniendo pacientes existentes...
curl -s http://localhost:8081/api/patients
echo.

echo.
echo 3. Creando nuevo paciente...
curl -X POST http://localhost:8081/api/patients ^
-H "Content-Type: application/json" ^
-d "{\"firstName\":\"María Elena\",\"lastName\":\"Martínez\",\"email\":\"maria.martinez@email.com\",\"phone\":\"+595971234567\",\"address\":\"Calle Palma 567, Asunción\",\"birthDate\":\"1992-08-10\",\"bloodType\":\"B_POSITIVE\",\"isActive\":true}"
echo.

echo.
echo 4. Obteniendo lista actualizada...
curl -s http://localhost:8081/api/patients
echo.

echo === Prueba completada ===
echo.
echo Para más pruebas visita:
echo - Swagger UI: http://localhost:8081/swagger-ui.html
echo - API Docs: http://localhost:8081/v3/api-docs
pause
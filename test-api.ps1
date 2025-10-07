# Script para probar la API de pacientes
# Ejecuta este script desde una terminal separada mientras Spring Boot corre

Write-Host "=== Probando API de Pacientes ===" -ForegroundColor Green

# 1. Verificar que la API esté corriendo
Write-Host "`n1. Verificando estado de la API..." -ForegroundColor Yellow
try {
    $healthCheck = Invoke-RestMethod -Uri "http://localhost:8081/actuator/health" -Method GET
    Write-Host "✓ API está corriendo - Estado: $($healthCheck.status)" -ForegroundColor Green
} catch {
    Write-Host "✗ Error: La API no está disponible" -ForegroundColor Red
    exit 1
}

# 2. Obtener lista actual de pacientes
Write-Host "`n2. Obteniendo pacientes existentes..." -ForegroundColor Yellow
try {
    $existingPatients = Invoke-RestMethod -Uri "http://localhost:8081/api/patients" -Method GET
    Write-Host "✓ Pacientes encontrados: $($existingPatients.totalElements)" -ForegroundColor Green
} catch {
    Write-Host "✗ Error obteniendo pacientes: $($_.Exception.Message)" -ForegroundColor Red
}

# 3. Crear un nuevo paciente
Write-Host "`n3. Creando nuevo paciente..." -ForegroundColor Yellow
$newPatient = @{
    firstName = "Juan Carlos"
    lastName = "González"
    email = "juan.gonzalez@email.com"
    phone = "+595981234567"
    address = "Av. Mariscal López 1234, Asunción"
    birthDate = "1985-03-20"
    bloodType = "A_POS"
    isActive = $true
} | ConvertTo-Json

try {
    $createdPatient = Invoke-RestMethod -Uri "http://localhost:8081/api/patients" -Method POST -Body $newPatient -ContentType "application/json"
    Write-Host "✓ Paciente creado exitosamente!" -ForegroundColor Green
    Write-Host "  ID: $($createdPatient.id)" -ForegroundColor Cyan
    Write-Host "  Nombre: $($createdPatient.firstName) $($createdPatient.lastName)" -ForegroundColor Cyan
    Write-Host "  Email: $($createdPatient.email)" -ForegroundColor Cyan
    
    # 4. Verificar el paciente creado
    Write-Host "`n4. Verificando paciente creado..." -ForegroundColor Yellow
    $retrievedPatient = Invoke-RestMethod -Uri "http://localhost:8081/api/patients/$($createdPatient.id)" -Method GET
    Write-Host "✓ Paciente recuperado correctamente" -ForegroundColor Green
    Write-Host "  Nombre completo: $($retrievedPatient.firstName) $($retrievedPatient.lastName)" -ForegroundColor Cyan
    
} catch {
    Write-Host "✗ Error creando paciente: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Detalles del error:" -ForegroundColor Yellow
    Write-Host $_.Exception.Response.StatusDescription -ForegroundColor Red
}

# 5. Obtener lista actualizada
Write-Host "`n5. Obteniendo lista actualizada..." -ForegroundColor Yellow
try {
    $updatedPatients = Invoke-RestMethod -Uri "http://localhost:8081/api/patients" -Method GET
    Write-Host "✓ Total de pacientes ahora: $($updatedPatients.totalElements)" -ForegroundColor Green
} catch {
    Write-Host "✗ Error obteniendo lista actualizada: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n=== Prueba completada ===" -ForegroundColor Green
Write-Host "`nPara probar más endpoints, puedes usar:" -ForegroundColor Yellow
Write-Host "- Swagger UI: http://localhost:8081/swagger-ui.html" -ForegroundColor Cyan
Write-Host "- API Docs: http://localhost:8081/v3/api-docs" -ForegroundColor Cyan
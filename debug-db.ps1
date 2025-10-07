# Script para verificar la estructura de la tabla patients
Write-Host "=== Verificando estructura de tabla patients ===" -ForegroundColor Green

# Verificar conexión a la base de datos a través del endpoint de actuator
Write-Host "`nVerificando conexión..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Uri "http://localhost:8081/actuator/health" -Method GET
    Write-Host "✓ Conexión OK" -ForegroundColor Green
} catch {
    Write-Host "✗ Error de conexión" -ForegroundColor Red
    exit 1
}

# Crear un paciente con datos más pequeños para identificar el campo problemático
Write-Host "`nProbando con datos mínimos..." -ForegroundColor Yellow

$minimalPatient = @{
    firstName = "Ana"
    lastName = "Silva"
    email = "a@b.co"
    phone = "+1234"
    address = "Av 1"
    birthDate = "1990-01-01"
    bloodType = "O_POSITIVE"
    isActive = $true
} | ConvertTo-Json

try {
    $result = Invoke-RestMethod -Uri "http://localhost:8081/api/patients" -Method POST -Body $minimalPatient -ContentType "application/json"
    Write-Host "✓ Datos mínimos funcionaron" -ForegroundColor Green
    Write-Host "ID creado: $($result.id)" -ForegroundColor Cyan
} catch {
    Write-Host "✗ Error con datos mínimos: $($_.Exception.Message)" -ForegroundColor Red
    
    # Si falla con datos mínimos, hay un problema de configuración
    Write-Host "Verificando si el campo blood_type es el problema..." -ForegroundColor Yellow
    
    # Probar con blood_type más corto
    $testPatient = @{
        firstName = "A"
        lastName = "B"
        email = "x@y.z"
        phone = "123"
        address = "St1"
        birthDate = "1990-01-01"
        bloodType = "A"  # Muy corto
        isActive = $true
    } | ConvertTo-Json
    
    try {
        $result2 = Invoke-RestMethod -Uri "http://localhost:8081/api/patients" -Method POST -Body $testPatient -ContentType "application/json"
        Write-Host "✓ El problema no es blood_type" -ForegroundColor Green
    } catch {
        Write-Host "✗ Problema persiste: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "Revisa la configuración de la base de datos" -ForegroundColor Yellow
    }
}

Write-Host "`n=== Análisis completado ===" -ForegroundColor Green
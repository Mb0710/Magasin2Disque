# Script de démarrage des microservices
# Lancement dans l'ordre : Eureka Server -> User Service -> Transaction Service -> API Gateway

Write-Host "=== Démarrage des microservices ===" -ForegroundColor Cyan
Write-Host ""

$workspaceRoot = "C:\Users\mohamed\Downloads\Magasin2Disque-main (13)\Magasin2Disque-main"

# Fonction pour démarrer un service
function Start-Service {
    param(
        [string]$serviceName,
        [string]$servicePath,
        [int]$waitSeconds
    )
    
    Write-Host ">>> Démarrage de $serviceName..." -ForegroundColor Green
    
    # Démarrer le service dans une nouvelle fenêtre PowerShell
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$servicePath'; Write-Host '=== $serviceName ===' -ForegroundColor Yellow; mvn spring-boot:run"
    
    Write-Host "Attente de $waitSeconds secondes pour le démarrage de $serviceName..." -ForegroundColor Yellow
    Start-Sleep -Seconds $waitSeconds
    Write-Host ""
}

# 1. Démarrer Eureka Server
Start-Service -serviceName "Eureka Server" -servicePath "$workspaceRoot\eureka-server" -waitSeconds 15

# 2. Démarrer User Service
Start-Service -serviceName "User Service" -servicePath "$workspaceRoot\user-service" -waitSeconds 20

# 3. Démarrer Transaction Service
Start-Service -serviceName "Transaction Service" -servicePath "$workspaceRoot\transaction-service" -waitSeconds 15

# 4. Démarrer API Gateway
Start-Service -serviceName "API Gateway" -servicePath "$workspaceRoot\api-gateway" -waitSeconds 10

Write-Host "=== Tous les services sont en cours de démarrage ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "URLs des services :" -ForegroundColor White
Write-Host "  - Eureka Server:      http://localhost:8761" -ForegroundColor White
Write-Host "  - User Service:       http://localhost:8081" -ForegroundColor White
Write-Host "  - Transaction Service: http://localhost:8082" -ForegroundColor White
Write-Host "  - API Gateway:        http://localhost:8080" -ForegroundColor White
Write-Host ""
Write-Host "Application web accessible via : http://localhost:8080" -ForegroundColor Green
Write-Host ""
Write-Host "Appuyez sur une touche pour fermer cette fenêtre..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

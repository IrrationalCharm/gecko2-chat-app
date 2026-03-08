# helm/deploy-local.ps1

$services = @("user-service", "message-persistence-service", "mobile-bff", "messaging-service", "media-service", "api-gateway")

# 1. Step inside the folder where all your microservices actually live
Set-Location "gecko2-services"

foreach ($service in $services) {
    Write-Host "Deploying $service..." -ForegroundColor Cyan

    # 2. Go into the specific microservice folder
    Set-Location $service

    # 3. Update the gecko2-common dependency (Out-Null hides the messy download text)
    helm dependency update | Out-Null

    # 4. The Smart Deployment Logic
    if (Test-Path "values-secrets.yaml") {
        # If the secret file exists (like in media-service), use BOTH files
        Write-Host "   Found values-secrets.yaml for $service, injecting secrets..." -ForegroundColor Yellow
        helm upgrade --install $service . -f values-local.yaml -f values-secrets.yaml
    } else {
        # If there is no secret file (like in user-service), just use the local file
        helm upgrade --install $service . -f values-local.yaml
    }

    # 5. Go back out to the gecko2-services folder so the loop can continue
    Set-Location ..
}

# 6. Step back out to the main helm folder when everything is done
Set-Location ..

Write-Host "✅ All microservices deployed successfully to K3d!" -ForegroundColor Green

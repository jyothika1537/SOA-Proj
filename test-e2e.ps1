$ErrorActionPreference = "Stop"

function Test-Api {
    param($Name, $Method, $Url, $Body, $Headers)
    Write-Host "Running: $Name"
    try {
        $params = @{
            Method = $Method
            Uri = $Url
            Headers = $Headers
            ContentType = "application/json"
        }
        if ($Body) { $params.Body = $Body }
        $response = Invoke-RestMethod @params
        Write-Host "[PASS] $Name"
        return $response
    } catch {
        Write-Host "[FAIL] $Name - $($_.Exception.Message)"
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
            Write-Host "Error Body: $($reader.ReadToEnd())"
        }
        return $null
    }
}

$registerResponse = Test-Api -Name "Register" -Method Post -Url "http://localhost:8080/api/auth/register" -Body '{"name":"official5","email":"off5@test.com","password":"password","role":"OFFICIAL"}'
$loginResponse = Test-Api -Name "Login" -Method Post -Url "http://localhost:8080/api/auth/login" -Body '{"email":"off5@test.com","password":"password"}'
$token = $loginResponse.token
$headers = @{ "Authorization" = "Bearer $token" }

$resourceResponse = Test-Api -Name "Create Resource" -Method Post -Url "http://localhost:8080/api/resources" -Headers $headers -Body '{"resourceName":"Rescue Team Alpha","resourceType":"RESCUE_TEAM","quantity":10,"availableQuantity":10,"deploymentZone":"Emergency Zone A","status":"AVAILABLE"}'
$incidentResponse = Test-Api -Name "Create Incident" -Method Post -Url "http://localhost:8080/api/incidents" -Headers $headers -Body '{"title":"Flood Emergency","description":"Severe flooding reported","incidentType":"FLOOD","severity":"CRITICAL","location":"Emergency Zone A","reportedBy":"official5","requiredResourceType":"RESCUE_TEAM","requiredResourceQuantity":5}'

$resourceId = $resourceResponse.resourceId
$incidentId = $incidentResponse.incidentId

$responseResponse = Test-Api -Name "Create Response" -Method Post -Url "http://localhost:8080/api/responses" -Headers $headers -Body "{`"incidentId`":`"$incidentId`",`"resourceId`":`"$resourceId`",`"responderId`":`"official5`",`"priority`":`"HIGH`"}"

# Check resource availability
$getResource = Test-Api -Name "Get Resource" -Method Get -Url "http://localhost:8080/api/resources/$resourceId" -Headers $headers
if ($getResource.availableQuantity -eq 9) {
    Write-Host "[PASS] Resource Availability Decreased"
} else {
    Write-Host "[FAIL] Resource Availability NOT Decreased"
}

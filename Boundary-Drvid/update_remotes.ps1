# Update Git Remotes to Your Forks
# Run this script after forking the repos on GitHub

param(
    [Parameter(Mandatory=$true)]
    [string]$GitHubUsername
)

Write-Host "Updating remotes to point to your forks..." -ForegroundColor Green
Write-Host "GitHub Username/Org: $GitHubUsername" -ForegroundColor Cyan
Write-Host ""

# Update Android remote
Write-Host "Updating Android remote..." -ForegroundColor Yellow
Set-Location "android"
git remote set-url origin "https://github.com/$GitHubUsername/zashi-android.git"
Write-Host "Android remotes:" -ForegroundColor Cyan
git remote -v
Set-Location ..

Write-Host ""

# Update iOS remote
Write-Host "Updating iOS remote..." -ForegroundColor Yellow
Set-Location "ios"
git remote set-url origin "https://github.com/$GitHubUsername/zashi-ios.git"
Write-Host "iOS remotes:" -ForegroundColor Cyan
git remote -v
Set-Location ..

Write-Host ""
Write-Host "✅ Remotes updated successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "To verify, run:" -ForegroundColor Cyan
Write-Host "  cd android; git remote -v" -ForegroundColor White
Write-Host "  cd ios; git remote -v" -ForegroundColor White


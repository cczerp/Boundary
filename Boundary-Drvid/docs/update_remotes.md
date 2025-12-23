# Update Git Remotes to Your Forks

This document explains how to update the git remotes to point to your forked repositories.

## Current Status

Both `android/` and `ios/` directories currently have:
- `origin` → Points to upstream Electric-Coin-Company repos
- `upstream` → Points to upstream Electric-Coin-Company repos

## Quick Setup (Recommended)

1. **Fork the repositories on GitHub** (if you haven't already):
   - https://github.com/Electric-Coin-Company/zashi-android → Click "Fork"
   - https://github.com/Electric-Coin-Company/zashi-ios → Click "Fork"

2. **Run the update script**:
   ```powershell
   .\update_remotes.ps1 -GitHubUsername YOUR_GITHUB_USERNAME
   ```
   
   Replace `YOUR_GITHUB_USERNAME` with your actual GitHub username or organization name.

## Manual Setup

If you prefer to update manually:

### Update Android Remote

```powershell
cd android
git remote set-url origin https://github.com/YOUR_GITHUB_USERNAME/zashi-android.git
git remote -v  # Verify the change
cd ..
```

### Update iOS Remote

```powershell
cd ios
git remote set-url origin https://github.com/YOUR_GITHUB_USERNAME/zashi-ios.git
git remote -v  # Verify the change
cd ..
```

## Expected Result

After updating, `git remote -v` should show:

**Android:**
```
origin    https://github.com/YOUR_GITHUB_USERNAME/zashi-android.git (fetch)
origin    https://github.com/YOUR_GITHUB_USERNAME/zashi-android.git (push)
upstream  https://github.com/Electric-Coin-Company/zashi-android.git (fetch)
upstream  https://github.com/Electric-Coin-Company/zashi-android.git (push)
```

**iOS:**
```
origin    https://github.com/YOUR_GITHUB_USERNAME/zashi-ios.git (fetch)
origin    https://github.com/YOUR_GITHUB_USERNAME/zashi-ios.git (push)
upstream  https://github.com/Electric-Coin-Company/zashi-ios.git (fetch)
upstream  https://github.com/Electric-Coin-Company/zashi-ios.git (push)
```

## Note

If you haven't forked the repos on GitHub yet, you can either:
1. Fork them now and update remotes (recommended for pushing changes)
2. Keep current setup (cloned directly from upstream) - works fine for local development

The current setup works fine for local development. You only need your own forks if you want to push changes to GitHub or collaborate.

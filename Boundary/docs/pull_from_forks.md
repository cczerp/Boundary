# Pulling from Your Forks

This guide explains how to pull changes from your forked repositories.

## Current Setup

- **`origin`** → Points to your forks (`cczerp/zashi-android`, `cczerp/zashi-ios`)
- **`upstream`** → Points to original repos (`Electric-Coin-Company/zashi-android`, `Electric-Coin-Company/zashi-ios`)

## Pulling from Your Forks

### Android

```powershell
cd android
git pull origin boundary-android
```

Or if you want to pull from `main`:
```powershell
cd android
git checkout main
git pull origin main
git checkout boundary-android
```

### iOS

```powershell
cd ios
git pull origin boundary-ios
```

Or if you want to pull from `main`:
```powershell
cd ios
git checkout main
git pull origin main
git checkout boundary-ios
```

## Pushing Your Local Changes to Your Forks

If you have local changes and want to push them to your forks:

### Android

```powershell
cd android
git push origin boundary-android
```

### iOS

```powershell
cd ios
git push origin boundary-ios
```

**Note:** If this is the first push, you may need to set upstream:
```powershell
git push -u origin boundary-android  # For Android
git push -u origin boundary-ios       # For iOS
```

## Pulling from Upstream (Original Repos)

If you want to pull updates from the original Zashi repos:

### Android

```powershell
cd android
git fetch upstream
git merge upstream/main
```

### iOS

```powershell
cd ios
git fetch upstream
git merge upstream/main
```

## Quick Commands Summary

**Pull from your fork:**
- Android: `cd android; git pull origin boundary-android`
- iOS: `cd ios; git pull origin boundary-ios`

**Push to your fork:**
- Android: `cd android; git push origin boundary-android`
- iOS: `cd ios; git push origin boundary-ios`

**Pull from upstream:**
- Android: `cd android; git fetch upstream; git merge upstream/main`
- iOS: `cd ios; git fetch upstream; git merge upstream/main`


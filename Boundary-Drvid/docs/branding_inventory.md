# Boundary Wallet — Branding Inventory

This document tracks where "Zashi" and "Zcash" branding appears in the codebase, separating user-facing strings from code identifiers.

**Purpose:** Guide safe branding changes without breaking builds or code identity.

**Status:** Template — to be completed once Android/iOS repos are forked.

---

## Branding Change Policy

### ✅ CAN Change (User-Facing)

- App display name
- App icons and logos
- Splash screens
- About/credits screens
- UI strings (labels, descriptions, error messages)
- Marketing materials
- Documentation

### ❌ CANNOT Change (Code Identity)

- Package names (e.g., `com.electriccoinco.zashi.*`)
- Bundle identifiers (e.g., `co.electriccoin.zashi`)
- Module/namespace paths
- Class names (unless refactoring)
- API endpoints (unless migrating)
- Build configuration identifiers

**Rationale:** Changing code identifiers breaks builds, breaks compatibility, and loses upstream attribution. These should remain unchanged unless explicitly planned in Phase 6+.

---

## Android Inventory

**To be completed after Android repo is forked.**

### User-Facing Strings

**Location:** `app/src/main/res/values/strings.xml`

- [ ] App name: `app_name` → Change to "Boundary" or "Boundary Wallet"
- [ ] App description: `app_description` → Update to Boundary description
- [ ] About screen: `about_*` strings → Update with Boundary attribution
- [ ] Error messages: `error_*` strings → Review for Zashi references
- [ ] Settings: `settings_*` strings → Review for branding

### Resources

**Location:** `app/src/main/res/`

- [ ] App icon: `mipmap-*/ic_launcher.png` → Replace with Boundary icon
- [ ] Splash screen: `drawable/splash.xml` or `layout/activity_splash.xml` → Update
- [ ] About screen logo: `drawable/logo_*.xml` → Replace with Boundary logo

### Manifest

**Location:** `app/src/main/AndroidManifest.xml`

- [ ] `android:label` → Change to "Boundary" (display name)
- [ ] `android:icon` → Update to Boundary icon
- [ ] `android:roundIcon` → Update to Boundary round icon
- **DO NOT** change `package` attribute (code identity)

### Build Configuration

**Location:** `app/build.gradle` or `build.gradle`

- [ ] `applicationId` → **DO NOT CHANGE** (bundle identifier)
- [ ] `versionName` → Can change (version string)
- [ ] `versionCode` → Can change (version number)

---

## iOS Inventory

**To be completed after iOS repo is forked.**

### User-Facing Strings

**Location:** `*.lproj/Localizable.strings` or `InfoPlist.strings`

- [ ] App name: `CFBundleDisplayName` → Change to "Boundary"
- [ ] App description: `CFBundleShortVersionString` → Update
- [ ] About screen: Storyboard/XIB strings → Update
- [ ] Error messages: Localized strings → Review for Zashi references

### Resources

**Location:** `*.xcassets/` or `Images.xcassets/`

- [ ] App icon: `AppIcon.appiconset/` → Replace with Boundary icon
- [ ] Splash screen: `LaunchScreen.storyboard` → Update
- [ ] About screen logo: Image assets → Replace with Boundary logo

### Info.plist

**Location:** `Info.plist`

- [ ] `CFBundleDisplayName` → Change to "Boundary" (display name)
- [ ] `CFBundleName` → Can change (short name)
- **DO NOT** change `CFBundleIdentifier` (bundle ID)

### Project Configuration

**Location:** `*.xcodeproj/project.pbxproj`

- [ ] `PRODUCT_BUNDLE_IDENTIFIER` → **DO NOT CHANGE**
- [ ] `PRODUCT_NAME` → Can change (display name)
- [ ] `MARKETING_VERSION` → Can change (version string)

---

## Code Identifiers (DO NOT CHANGE)

### Android Package Names

**Location:** Throughout codebase

- `com.electriccoinco.zashi.*` → **PRESERVE**
- `com.electriccoinco.zcash.*` → **PRESERVE**
- Any `zashi` or `zcash` in package names → **PRESERVE**

### iOS Bundle Identifiers

**Location:** `Info.plist`, `*.xcodeproj`

- `co.electriccoin.zashi` → **PRESERVE**
- `co.electriccoin.zcash` → **PRESERVE**

### Module/Namespace Paths

**Location:** Source code

- Any `zashi` or `zcash` in module paths → **PRESERVE**
- Import statements → **PRESERVE**

---

## Attribution Requirements

### Must Preserve

- Electric Coin Company attribution in about/credits screens
- Zashi upstream references in documentation
- License headers in source files
- Upstream repository links in README

### Can Update

- Add Boundary attribution alongside ECC attribution
- Update project description to mention Boundary
- Add Boundary-specific credits/contributors

---

## Implementation Checklist

Once repos are forked:

- [ ] Complete Android inventory (all sections above)
- [ ] Complete iOS inventory (all sections above)
- [ ] Create Boundary-branded assets (icons, logos, splash screens)
- [ ] Update Android strings.xml
- [ ] Update iOS Localizable.strings
- [ ] Update AndroidManifest.xml (display name only)
- [ ] Update Info.plist (display name only)
- [ ] Update about/credits screens
- [ ] Verify builds still work after changes
- [ ] Test on devices/emulators

---

## Design Assets Needed

**To be created:**

- [ ] Boundary app icon (Android: multiple sizes)
- [ ] Boundary app icon (iOS: multiple sizes)
- [ ] Boundary logo (for about screen)
- [ ] Splash screen design
- [ ] Brand guidelines document (colors, fonts, etc.)

---

## Notes

- This inventory should be completed **before** making any branding changes
- Test builds after **each** change to catch issues early
- Keep a backup of original assets (for reference/attribution)
- Document any exceptions or special cases

---

**Last Updated:** [Current Date]  
**Status:** Template — awaiting repo forks


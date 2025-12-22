# Boundary Wallet — Code Identity Preservation Policy

This document defines what code identifiers MUST remain unchanged and what CAN be changed during Boundary's development.

**Purpose:** Prevent accidental breaking changes that would break builds, lose upstream compatibility, or violate attribution requirements.

---

## Policy Overview

Boundary maintains **upstream code identity** (package names, bundle identifiers, module paths) while changing **user-facing branding** (display names, UI strings, icons).

This separation allows Boundary to:
- Maintain build compatibility
- Preserve upstream attribution
- Avoid breaking changes during early development
- Enable future upstream sync (if needed for security patches)

---

## What MUST Stay Unchanged

### Android Package Names

**Rule:** All package names containing `zashi` or `zcash` MUST remain unchanged.

**Examples:**
- `com.electriccoinco.zashi.*` → **PRESERVE**
- `com.electriccoinco.zcash.*` → **PRESERVE**
- `co.electriccoin.zashi.*` → **PRESERVE**

**Rationale:**
- Changing package names breaks builds
- Breaks compatibility with existing tools/services
- Loses upstream attribution
- Makes upstream sync impossible

### iOS Bundle Identifiers

**Rule:** Bundle identifiers MUST remain unchanged.

**Examples:**
- `co.electriccoin.zashi` → **PRESERVE**
- `co.electriccoin.zcash` → **PRESERVE**

**Rationale:**
- Changing bundle IDs breaks app store continuity
- Breaks compatibility with existing installations
- Requires new app store listings
- Loses upstream attribution

### Module/Namespace Paths

**Rule:** Module paths and namespace identifiers MUST remain unchanged.

**Examples:**
- `com.electriccoinco.zashi.sdk` → **PRESERVE**
- `co.electriccoin.zashi.core` → **PRESERVE**

**Rationale:**
- Changing module paths breaks imports
- Breaks compatibility with existing code
- Makes upstream sync difficult

### Build Configuration Identifiers

**Rule:** Build configuration identifiers (applicationId, PRODUCT_BUNDLE_IDENTIFIER) MUST remain unchanged.

**Examples:**
- Android `applicationId` → **PRESERVE**
- iOS `PRODUCT_BUNDLE_IDENTIFIER` → **PRESERVE**

**Rationale:**
- These are code-level identifiers, not user-facing
- Changing them breaks builds and compatibility

---

## What CAN Be Changed

### User-Facing Strings

**Rule:** All user-visible strings CAN be changed.

**Examples:**
- App display name: "Zashi" → "Boundary" ✅
- UI labels: "Zashi Wallet" → "Boundary Wallet" ✅
- Error messages: Update to mention Boundary ✅
- About screens: Update with Boundary branding ✅

**Rationale:**
- These are presentation-layer changes
- Do not affect code identity or builds
- Required for branding separation

### Resource Files

**Rule:** Icons, logos, splash screens, and other assets CAN be changed.

**Examples:**
- App icons → Replace with Boundary icons ✅
- Splash screens → Replace with Boundary branding ✅
- About screen logos → Replace with Boundary logos ✅

**Rationale:**
- These are resource files, not code identifiers
- Do not affect builds or compatibility

### Documentation

**Rule:** All documentation CAN be updated.

**Examples:**
- README.md → Update with Boundary branding ✅
- Architecture docs → Update with Boundary design ✅
- Comments in code → Update to mention Boundary ✅

**Rationale:**
- Documentation is not code identity
- Should reflect Boundary's identity

### Version Strings

**Rule:** Version strings (versionName, MARKETING_VERSION) CAN be changed.

**Examples:**
- Android `versionName` → Can change ✅
- iOS `MARKETING_VERSION` → Can change ✅

**Rationale:**
- Version strings are user-facing
- Do not affect code identity

---

## When Code Identity Changes Might Happen

**Current Policy:** Code identity changes are **NOT planned** for Phases 0-5.

**Future Consideration (Phase 6+):**
- Code identity changes might be considered if:
  - Boundary becomes a fully independent project
  - Upstream sync is no longer needed
  - Breaking changes are acceptable
  - Full migration plan is documented

**If Code Identity Changes Are Needed:**

1. **Document the rationale** — Why is the change necessary?
2. **Create a migration plan** — How will existing users/data be migrated?
3. **Update all references** — Package names, bundle IDs, module paths
4. **Test thoroughly** — Ensure builds and compatibility still work
5. **Update documentation** — Reflect new code identity

**Recommendation:** Avoid code identity changes unless absolutely necessary. The separation of branding and code identity is a feature, not a limitation.

---

## Enforcement

### Pre-Commit Checks (Future)

When code exists, add pre-commit hooks to prevent accidental changes:

- Check for package name changes (Android)
- Check for bundle ID changes (iOS)
- Check for module path changes
- Warn on changes to code identity files

### Code Review

All pull requests should:
- Review changes to code identity files
- Verify branding changes don't affect code identity
- Ensure attribution is preserved

### Documentation

- Keep this policy document up to date
- Reference it in CONTRIBUTING.md
- Include it in onboarding materials

---

## Examples

### ✅ CORRECT: Changing Display Name

**Android:**
```xml
<!-- AndroidManifest.xml -->
<application
    android:label="@string/app_name"  <!-- Can change -->
    android:icon="@mipmap/ic_launcher"  <!-- Can change -->
    ...>
```

```xml
<!-- strings.xml -->
<string name="app_name">Boundary</string>  <!-- ✅ CORRECT -->
```

**iOS:**
```xml
<!-- Info.plist -->
<key>CFBundleDisplayName</key>
<string>Boundary</string>  <!-- ✅ CORRECT -->
```

### ❌ INCORRECT: Changing Package Name

**Android:**
```gradle
// build.gradle
android {
    defaultConfig {
        applicationId "com.boundary.wallet"  // ❌ WRONG - breaks build
    }
}
```

**Should remain:**
```gradle
android {
    defaultConfig {
        applicationId "com.electriccoinco.zashi"  // ✅ CORRECT
    }
}
```

### ❌ INCORRECT: Changing Bundle Identifier

**iOS:**
```xml
<!-- Info.plist -->
<key>CFBundleIdentifier</key>
<string>co.boundary.wallet</string>  <!-- ❌ WRONG - breaks build -->
```

**Should remain:**
```xml
<key>CFBundleIdentifier</key>
<string>co.electriccoin.zashi</string>  <!-- ✅ CORRECT -->
```

---

## Attribution Preservation

Even when changing user-facing strings, **preserve upstream attribution**:

**✅ CORRECT:**
```
Boundary Wallet

Derived from Zashi wallet by Electric Coin Company.
```

**❌ INCORRECT:**
```
Boundary Wallet

(No attribution)
```

---

## Summary

| Category | Can Change? | Examples |
|----------|-------------|----------|
| Package names | ❌ NO | `com.electriccoinco.zashi.*` |
| Bundle identifiers | ❌ NO | `co.electriccoin.zashi` |
| Module paths | ❌ NO | `com.electriccoinco.zashi.sdk` |
| Display names | ✅ YES | "Zashi" → "Boundary" |
| UI strings | ✅ YES | Labels, descriptions |
| Icons/logos | ✅ YES | App icons, splash screens |
| Documentation | ✅ YES | README, comments |
| Version strings | ✅ YES | `versionName`, `MARKETING_VERSION` |

---

**Last Updated:** [Current Date]  
**Status:** Active policy — all contributors must follow


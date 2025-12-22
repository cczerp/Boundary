# Boundary Wallet — Fork Points

This document tracks the fork points from upstream Zashi repositories.

**Purpose:** Maintain clear attribution and enable potential upstream sync if needed.

---

## Repository Structure

Boundary uses a monorepo structure:
- Root: Documentation and coordination
- `android/`: Android app code (forked from `zashi-android`)
- `ios/`: iOS app code (forked from `zashi-ios`)

---

## Android Fork

**Upstream Repository:** https://github.com/Electric-Coin-Company/zashi-android

**Fork Date:** December 22, 2025

**Fork Point Commit:**
- **Hash:** `cf75434e` (Bump actions/checkout from 6.0.0 to 6.0.1)
- **Branch:** `main`
- **Tag:** [If applicable]

**Boundary Branch:** `boundary-android`

**Upstream Remote:** `upstream` → https://github.com/Electric-Coin-Company/zashi-android.git

**Fork Remote:** `origin` → https://github.com/cczerp/zashi-android.git

**Status:** ✅ Forked and cloned into `android/` directory, remotes configured

---

## iOS Fork

**Upstream Repository:** https://github.com/Electric-Coin-Company/zashi-ios

**Fork Date:** December 22, 2025

**Fork Point Commit:**
- **Hash:** `49036fdc` (Merge pull request #1617 from LukasKorba/new-rpc-options)
- **Branch:** `main`
- **Tag:** [If applicable]

**Boundary Branch:** `boundary-ios`

**Upstream Remote:** `upstream` → https://github.com/Electric-Coin-Company/zashi-ios.git

**Fork Remote:** `origin` → https://github.com/cczerp/zashi-ios.git

**Status:** ✅ Forked and cloned into `ios/` directory, remotes configured

---

## Upstream Sync Policy

**Current Policy:** No upstream sync unless critical security patches.

**Rationale:**
- Boundary will diverge significantly from Zashi
- Maintaining sync adds complexity
- Security patches can be cherry-picked if needed

**If Upstream Sync Needed:**
1. Review upstream changes
2. Cherry-pick security patches only
3. Document sync in this file
4. Test thoroughly before merging

---

## Attribution

All original code, licensing, and attribution from upstream repositories are preserved.

Boundary does **not** claim endorsement, affiliation, or sponsorship by Electric Coin Company.

---

**Last Updated:** [Current Date]


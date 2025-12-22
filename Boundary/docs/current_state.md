# Boundary Wallet — Current State

This document tracks what currently exists, what's documented vs. implemented, and known limitations.

**Last Updated:** [Current Date]  
**Status:** Early-stage planning and foundation

---

## What Exists

### Documentation ✅

- **README.md** — Project overview and status
- **docs/architecture.md** — Architecture and design document (complete)
- **docs/roadmap.md** — Planned evolution and milestones
- **docs/next_steps.md** — Concrete action plan
- **docs/current_state.md** — This document
- **wallet_threat_model.md** — Security threat model (adapted from Zashi)
- **responsible_disclosure.md** — Security disclosure policy (from ECC)

### Code Repositories

- **Main/Zashi**: ✅ Forked (documentation/metadata hub)
- **Android**: ✅ Forked in `android/` directory (from `zashi-android`)
- **iOS**: ✅ Forked in `ios/` directory (from `zashi-ios`)

**Status:** Monorepo structure with Android and iOS code in subdirectories.

---

## What's Documented vs. Implemented

### Intent Model

- **Documented:** ✅ Complete schema defined in `docs/architecture.md`
- **Implemented:** ❌ Not yet implemented
- **Status:** Ready for implementation (Phase 2)

### Routing Engine

- **Documented:** ✅ Interface defined in `docs/architecture.md`
- **Implemented:** ❌ Not yet implemented
- **Status:** Ready for stub implementation (Phase 2-3)

### Execution Providers

- **Documented:** ✅ Abstraction defined in `docs/architecture.md`
- **Implemented:** ❌ Not yet implemented
- **Status:** Design complete, implementation pending (Phase 4)

### Branding

- **Documented:** ✅ Inventory template exists (`docs/branding_inventory.md`)
- **Implemented:** ❌ Not yet implemented (requires repos)
- **Status:** Ready to implement once repos are forked (Phase 1)

---

## Known Limitations

### Current Limitations

1. **No Code Repositories**
   - Android/iOS repos must be forked before any code work
   - Cannot verify builds or test implementations yet

2. **No Build System**
   - No Gradle/CMake/Xcode configuration yet
   - Cannot build or test until repos are forked

3. **No Intent Processing**
   - Intent schema is designed but not implemented
   - No normalization or validation logic exists

4. **No Routing Logic**
   - Routing interface is designed but not implemented
   - No route discovery or scoring exists

5. **No Execution Layer**
   - Execution provider abstraction is designed but not implemented
   - No cross-chain execution possible yet

### Design Limitations

1. **Single-Chain Focus Initially**
   - Phase 2-3 will only support single-chain intents
   - Cross-chain routing comes in Phase 4

2. **Limited Privacy Features Initially**
   - Basic privacy scoring in Phase 3
   - Advanced privacy features in Phase 5

3. **No Bridge Integration Yet**
   - Bridge providers are not yet researched
   - No execution providers integrated

---

## Blockers

### Immediate Blockers

1. **Repository Forking**
   - Must fork Zashi Android and iOS repos
   - Requires GitHub access and organization setup

2. **Baseline Build Verification**
   - Must verify upstream builds work
   - Must document build requirements

### Future Blockers

1. **Bridge Provider Research**
   - Need to research and evaluate bridge providers
   - Must decide on trust models and integration approach

2. **Privacy Scoring Implementation**
   - Need concrete privacy metrics
   - Must define privacy vs. cost trade-offs

---

## Next Steps

See [docs/next_steps.md](./next_steps.md) for the complete action plan.

**Immediate priorities:**
1. Fork Android/iOS repositories
2. Verify baseline builds
3. Complete branding inventory
4. Begin intent model implementation (interfaces only)

---

## Dependencies

### External Dependencies (Future)

- Zashi Android SDK
- Zashi iOS SDK
- Bridge providers (TBD)
- Swap aggregators (TBD)
- Blockchain RPC nodes

### Internal Dependencies

- Architecture document (✅ complete)
- Intent schema (✅ complete)
- Routing interface (✅ complete)
- Execution provider abstraction (✅ complete)

---

## Testing Status

**Current:** No tests exist (no code yet)

**Future Requirements:**
- Unit tests for intent normalization
- Unit tests for routing logic
- Integration tests for execution providers
- Security tests for privacy guarantees
- UI tests for critical flows

---

## Build Status

**Current:** Cannot build (no repos)

**Future:**
- Android: Gradle-based build (when repo exists)
- iOS: Xcode project (when repo exists)

---

## Notes

- This is a living document — update as state changes
- Mark items as ✅ when complete
- Add new limitations as they're discovered
- Update blockers as they're resolved


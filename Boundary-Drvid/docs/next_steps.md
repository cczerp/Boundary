# Boundary Wallet — Next Steps & Action Plan

This document provides a concrete, ordered list of next steps to begin building Boundary safely and intentionally. It complements the [Roadmap](./roadmap.md) and [Architecture](./architecture.md) documents.

**Status:** Early-stage planning and foundation work.

---

## Phase: IMMEDIATE (Next 1-2 weeks)

**Goal:** Establish a clean, maintainable foundation before any code changes.

### Repo Hygiene & Workflow

- **✅ SAFE** — Set up git workflow and branching strategy
  - **Rationale:** Prevents accidental upstream contamination and enables safe experimentation
  - **Action:** Define `main` (stable), `develop` (integration), `feature/*` (experiments)
  - **Prerequisite:** None
  - **Decision needed:** Will you maintain upstream sync? (Recommendation: No, unless critical security patches)

- **✅ SAFE** — Create `.gitignore` and contribution guidelines
  - **Rationale:** Prevents accidental commits of build artifacts, keys, or IDE files
  - **Action:** Add standard Android/iOS ignores, plus any Boundary-specific patterns
  - **Prerequisite:** None

- **✅ SAFE** — Document current state and known limitations
  - **Rationale:** Prevents future confusion about what works vs. what's planned
  - **Action:** Create `docs/current_state.md` listing:
    - What currently builds (if anything)
    - What's documented vs. implemented
    - Known blockers or missing dependencies
  - **Prerequisite:** None

### Branding vs Code Identity Separation

- **✅ SAFE** — Create a branding inventory document
  - **Rationale:** Maps where "Zashi" appears in user-facing strings vs. code identifiers
  - **Action:** Create `docs/branding_inventory.md` with:
    - UI strings (app name, splash screens, about pages)
    - Resource files (icons, logos, assets)
    - Code identifiers (packages, namespaces) — **DO NOT CHANGE YET**
  - **Prerequisite:** Need Android/iOS repos forked first (see below)
  - **Decision needed:** What will the app display name be? (Recommendation: "Boundary" or "Boundary Wallet")

- **✅ SAFE** — Define a code identity preservation policy
  - **Rationale:** Prevents accidental breaking changes to package names, bundle IDs, etc.
  - **Action:** Document in `docs/code_identity_policy.md`:
    - What identifiers MUST stay unchanged (package names, bundle IDs, module paths)
    - What CAN change (display names, UI strings, comments)
    - When/if code identity changes might happen (Phase 6+ only, if ever)
  - **Prerequisite:** None

### Android & iOS Fork Strategy

- **✅ COMPLETED** — Fork Zashi Android repository into `android/` directory
  - **Rationale:** Required before any mobile work, but must be done carefully to preserve git history and attribution
  - **Note:** The main `zashi` repo is just a documentation hub. Android code is in a **separate repository**.
  - **Action:**
    1. Fork `Electric-Coin-Company/zashi-android` to your org on GitHub
    2. Clone the forked repo into `android/` directory: `git clone <your-fork-url> android`
    3. Create `boundary-android` branch from `main`
    4. Add upstream remote: `git remote add upstream https://github.com/Electric-Coin-Company/zashi-android.git`
    5. Document fork point commit hash in `docs/fork_points.md`
  - **Prerequisite:** GitHub org/repo access
  - **Decision needed:** Will you maintain upstream sync? (Recommendation: No, unless critical security patches)
  - **Risk mitigation:** Test that upstream builds before forking
  - **Status:** ✅ Completed - Fork point: `cf75434e`

- **⚠️ HIGH RISK** — Fork Zashi iOS repository into `ios/` directory
  - **Note:** iOS code is also in a **separate repository** from the main repo.
  - **Rationale:** Same as Android — required foundation, but risky if done incorrectly
  - **Action:**
    1. Fork `Electric-Coin-Company/zashi-ios` to your org on GitHub
    2. Clone the forked repo into `ios/` directory: `git clone <your-fork-url> ios`
    3. Create `boundary-ios` branch from `main`
    4. Add upstream remote: `git remote add upstream https://github.com/Electric-Coin-Company/zashi-ios.git`
    5. Document fork point commit hash in `docs/fork_points.md`
  - **Prerequisite:** GitHub org/repo access
  - **Decision needed:** Same as Android
  - **Status:** ✅ Completed

- **✅ SAFE** — Verify baseline builds work
  - **Rationale:** Confirms fork integrity before any changes
  - **Action:** Build both Android and iOS from forked repos without modifications
  - **Prerequisite:** Android/iOS forks completed
  - **Risk mitigation:** If builds fail, fix upstream issues before proceeding

### Architecture Decisions (Before Coding)

- **✅ SAFE** — Complete the architecture document
  - **Rationale:** Missing sections will lead to inconsistent implementation
  - **Action:** Finish `docs/architecture.md` sections:
    - High-Level Architecture (currently incomplete)
    - Intent schema definition (data structures, not implementation)
    - Routing engine interface (contracts, not implementations)
    - Execution provider abstraction (interfaces, not concrete classes)
  - **Prerequisite:** None
  - **Decision needed:** What is the minimal viable Intent schema? (Recommendation: Start with `{action, sourceAsset, targetAsset, destination}`)

- **✅ SAFE** — Define intent normalization rules
  - **Rationale:** Prevents ambiguity in how user requests map to intents
  - **Action:** Document in `docs/intent_normalization.md`:
    - How "send X to Y" normalizes
    - How "swap A for B" normalizes
    - Edge cases (same-chain swaps, cross-chain sends, etc.)
  - **Prerequisite:** Intent schema defined
  - **Decision needed:** Should normalization happen client-side or server-side? (Recommendation: Client-side for privacy)

- **✅ SAFE** — Design routing engine interface (no implementation)
  - **Rationale:** Interface-first design prevents tight coupling
  - **Action:** Create `docs/routing_interface.md` with:
    - `RouteRequest` structure
    - `RouteResponse` structure
    - `Router` interface (methods: `findRoutes()`, `compareRoutes()`, `validateRoute()`)
    - Error handling contract
  - **Prerequisite:** Intent schema defined
  - **Decision needed:** Should routing be synchronous or asynchronous? (Recommendation: Async for network calls)

### Security & Privacy Considerations

- **✅ SAFE** — Review and adapt threat model
  - **Rationale:** Cross-chain routing introduces new attack vectors
  - **Action:** Update `wallet_threat_model.md` with:
    - New adversary: "Malicious routing provider"
    - New adversary: "Bridge compromise"
    - Privacy leakage from route selection (metadata)
  - **Prerequisite:** Threat model already exists
  - **Decision needed:** What privacy guarantees can Boundary make vs. Zashi? (Recommendation: Document limitations explicitly)

- **✅ SAFE** — Define privacy constraints for routing
  - **Rationale:** Privacy must be built into routing, not added later
  - **Action:** Create `docs/privacy_routing.md` with:
    - What metadata routers can see (minimize)
    - What metadata routers MUST NOT see (keys, full history)
    - How to score routes by privacy (shielded > transparent, etc.)
  - **Prerequisite:** Routing interface defined
  - **Decision needed:** Can routers be privacy-preserving by default? (Recommendation: Yes, but document trade-offs)

---

## Phase: SHORT-TERM (Next 1-2 months)

**Goal:** Make Boundary visually distinct while preserving all functionality.

### Branding Implementation

- **✅ SAFE** — Replace app display names (Android + iOS)
  - **Rationale:** First visible change users will see
  - **Action:**
    - Android: Update `strings.xml` / `AndroidManifest.xml` app name
    - iOS: Update `Info.plist` CFBundleDisplayName
    - **DO NOT** change package names or bundle identifiers
  - **Prerequisite:** Branding inventory completed, baseline builds verified
  - **Risk mitigation:** Test builds after each change

- **✅ SAFE** — Replace app icons and splash screens
  - **Rationale:** Visual identity separation
  - **Action:** Create Boundary-branded assets, replace in both platforms
  - **Prerequisite:** Design assets ready
  - **Risk mitigation:** Ensure icons meet platform guidelines (sizes, formats)

- **✅ SAFE** — Update about/credits screens
  - **Rationale:** Proper attribution and identity
  - **Action:** Update UI strings to mention Boundary, preserve ECC/Zashi attribution
  - **Prerequisite:** Branding inventory completed

### Intent Model Scaffolding (No Execution Logic Yet)

- **⚠️ HIGH RISK** — Create intent data structures (interfaces only)
  - **Rationale:** Foundation for all future work, but must be designed correctly
  - **Action:**
    - Define `Intent` interface/protocol (no implementation)
    - Define `IntentNormalizer` interface (no implementation)
    - Create unit tests for intent parsing (mock data only)
  - **Prerequisite:** Intent schema finalized in architecture doc
  - **Decision needed:** Should intents be serializable? (Recommendation: Yes, for debugging and future features)
  - **Risk mitigation:** Keep interfaces minimal, avoid over-engineering

- **✅ SAFE** — Create intent-to-action mapper (stub implementation)
  - **Rationale:** Maps intents to existing Zashi actions without changing execution
  - **Action:**
    - Create `IntentMapper` that takes an `Intent` and returns a `ZashiAction` (or equivalent)
    - Initially, all intents map to single-chain Zashi actions
    - Add logging/tracing to see what intents are being created
  - **Prerequisite:** Intent interfaces created, Zashi action types understood
  - **Decision needed:** Should mapping be bidirectional? (Recommendation: No, intents are one-way)

- **✅ SAFE** — Add intent logging/debugging infrastructure
  - **Rationale:** Essential for understanding user behavior and debugging routing
  - **Action:** Create logging system that records:
    - Intent creation (user input → intent)
    - Intent normalization (raw → normalized)
    - Intent mapping (intent → action)
    - **DO NOT** log sensitive data (keys, addresses, amounts in production)
  - **Prerequisite:** Intent interfaces created
  - **Decision needed:** Where should logs be stored? (Recommendation: Local-only, user-controlled export)

### Routing Engine Design (Still No Execution)

- **✅ SAFE** — Implement routing engine interface (stub implementations)
  - **Rationale:** Establishes the contract before any real routing logic
  - **Action:**
    - Create `Router` interface/protocol
    - Create `NullRouter` implementation (returns "no routes found")
    - Create `SingleChainRouter` implementation (returns single direct route)
    - Wire up router selection (initially always uses `SingleChainRouter`)
  - **Prerequisite:** Routing interface defined in docs
  - **Decision needed:** Should routers be pluggable at runtime? (Recommendation: Yes, via dependency injection)

- **✅ SAFE** — Create route comparison/scoring framework (no real scoring yet)
  - **Rationale:** Foundation for privacy-aware routing
  - **Action:**
    - Define `RouteScore` structure (cost, latency, privacy, trust)
    - Create `RouteComparator` interface
    - Create `BasicComparator` that only compares cost (placeholder)
  - **Prerequisite:** Routing interface defined
  - **Decision needed:** How to weight privacy vs. cost? (Recommendation: User preference, default privacy-first)

### Security Hardening

- **✅ SAFE** — Add input validation for intents
  - **Rationale:** Prevents malformed intents from causing issues downstream
  - **Action:**
    - Validate intent structure (required fields, types)
    - Validate addresses (format, checksums)
    - Validate amounts (non-negative, within reasonable bounds)
  - **Prerequisite:** Intent interfaces created
  - **Risk mitigation:** Fail fast with clear error messages

- **✅ SAFE** — Document security assumptions for routing
  - **Rationale:** Future routing providers must meet these assumptions
  - **Action:** Create `docs/routing_security.md` with:
    - What routers can be trusted with (minimal: intent, not keys)
    - What routers MUST NOT receive (keys, full wallet state)
    - How to verify router responses (if applicable)
  - **Prerequisite:** Routing interface defined

---

## Phase: MEDIUM-TERM (Next 3-6 months)

**Goal:** Introduce real routing logic while maintaining single-chain execution.

### Intent Model Implementation

- **⚠️ HIGH RISK** — Implement intent normalization layer
  - **Rationale:** Core functionality, but complex edge cases
  - **Action:**
    - Implement `IntentNormalizer` with all normalization rules
    - Add comprehensive test coverage (edge cases, malformed input)
    - Integrate with UI (user input → normalized intent)
  - **Prerequisite:** Intent interfaces created, normalization rules documented
  - **Risk mitigation:** Extensive testing, gradual rollout

- **✅ SAFE** — Wire intent flow through existing UI
  - **Rationale:** Users start expressing intents, but execution remains unchanged
  - **Action:**
    - Modify send screen to create `SendIntent` instead of direct action
    - Modify swap screen to create `SwapIntent` instead of direct action
    - Intent mapper converts to existing Zashi actions (no behavior change)
  - **Prerequisite:** Intent normalization implemented
  - **Decision needed:** Should UI show intent vs. action? (Recommendation: Show intent to user, action in debug mode)

### Routing Engine (Local, Single-Chain)

- **⚠️ HIGH RISK** — Implement local routing engine (single-chain only)
  - **Rationale:** First real routing logic, but constrained to one chain
  - **Action:**
    - Create `LocalRouter` that finds routes within one chain
    - Support: direct send, same-chain swap (if applicable)
    - Return multiple route options (even if only one exists)
    - Score routes by cost and latency (privacy scoring later)
  - **Prerequisite:** Routing interface implemented, route scoring framework created
  - **Risk mitigation:** Start with simplest case (direct send), expand gradually
  - **Decision needed:** Should routing be cached? (Recommendation: Yes, with TTL)

- **✅ SAFE** — Add route selection UI (user chooses from options)
  - **Rationale:** Users see routing in action, but execution remains simple
  - **Action:**
    - Show route options to user before execution
    - Display: cost, estimated time, privacy level (if applicable)
    - User confirms route selection
  - **Prerequisite:** Local routing engine implemented
  - **Decision needed:** Should there be an "auto-select best" option? (Recommendation: Yes, with user override)

- **✅ SAFE** — Implement privacy-aware route scoring
  - **Rationale:** Privacy becomes a first-class routing constraint
  - **Action:**
    - Extend `RouteScore` to include privacy metrics
    - Score routes: shielded > transparent, private > public
    - Update comparator to weight privacy (user preference)
  - **Prerequisite:** Local routing engine implemented, privacy constraints documented
  - **Decision needed:** How to quantify privacy? (Recommendation: Categorical: High/Medium/Low)

### Cross-Chain Preparation (Design Only)

- **✅ SAFE** — Research and document bridge/swap providers
  - **Rationale:** Must understand options before implementing
  - **Action:** Create `docs/bridge_providers.md` with:
    - List of potential providers (bridges, DEX aggregators)
    - Trust model for each (custodial vs. non-custodial)
    - Privacy characteristics (if any)
    - Integration complexity
  - **Prerequisite:** None
  - **Decision needed:** Which provider to integrate first? (Recommendation: Non-custodial, privacy-preserving if possible)

- **✅ SAFE** — Design execution provider abstraction
  - **Rationale:** Must be designed before any cross-chain code
  - **Action:** Create `docs/execution_provider.md` with:
    - `ExecutionProvider` interface
    - `ExecutionRequest` structure
    - `ExecutionResponse` structure
    - Error handling and retry logic
  - **Prerequisite:** Bridge providers researched
  - **Decision needed:** Should execution be synchronous or asynchronous? (Recommendation: Async with status polling)

---

## Decisions to Make BEFORE Coding

These decisions should be made during the **IMMEDIATE** phase to avoid rework:

1. **Intent Schema Design**
   - What fields are required vs. optional?
   - How to represent cross-chain intents vs. single-chain?
   - Should intents be versioned?

2. **Routing Provider Trust Model**
   - Can routers be untrusted? (Recommendation: Yes, but with validation)
   - What data can routers see? (Recommendation: Intent only, not wallet state)
   - How to verify router responses? (Recommendation: Client-side validation)

3. **Privacy Defaults**
   - Should privacy be opt-in or opt-out? (Recommendation: Opt-out, default private)
   - How to handle privacy vs. cost trade-offs? (Recommendation: User preference, default privacy-first)

4. **Execution Model**
   - Should execution be atomic or multi-step? (Recommendation: Multi-step with rollback)
   - How to handle partial failures? (Recommendation: Rollback, user notification)

5. **User Experience**
   - How much detail should users see about routing? (Recommendation: Summary by default, details on request)
   - Should routing be automatic or manual? (Recommendation: Automatic with override)

---

## When NOT to Code Yet

**DO NOT** start coding until:

- ❌ Architecture document is complete (missing sections filled)
- ❌ Intent schema is finalized and documented
- ❌ Routing interface is designed (even if stub)
- ❌ Security assumptions are documented
- ❌ Android/iOS repos are forked and building
- ❌ Branding inventory is complete

**DO NOT** rush to:

- ❌ App store deployment (not in scope yet)
- ❌ Cross-chain execution (Phase 4, after local routing works)
- ❌ Production routing providers (research first)
- ❌ Complex privacy features (get basics working first)

---

## Risk Summary

**SAFE** tasks:
- Documentation
- Interface/contract definitions
- Branding (UI strings, assets)
- Logging/infrastructure
- Research and design

**HIGH RISK** tasks:
- Forking repositories (must preserve history)
- Implementing intent normalization (complex edge cases)
- Implementing routing engine (core functionality)
- Any changes to package names/bundle IDs (breaks builds)

**Risk Mitigation Strategy:**
- Always test builds after changes
- Keep interfaces minimal and extensible
- Document assumptions and decisions
- Maintain upstream compatibility where possible
- Fail fast with clear error messages

---

## Success Criteria

**IMMEDIATE phase complete when:**
- ✅ Repos are forked and building
- ✅ Architecture document is complete
- ✅ Intent schema is finalized
- ✅ Routing interface is designed
- ✅ Security assumptions are documented

**SHORT-TERM phase complete when:**
- ✅ Boundary looks like Boundary (branding done)
- ✅ Intents can be created and normalized
- ✅ Intents map to existing Zashi actions (no behavior change)
- ✅ Routing engine interface exists (stub implementations)

**MEDIUM-TERM phase complete when:**
- ✅ Users can express intents in UI
- ✅ Local routing engine finds routes (single-chain)
- ✅ Users can select routes before execution
- ✅ Privacy-aware scoring is implemented

---

## Next Document to Read

After completing IMMEDIATE phase tasks, review:
- `docs/architecture.md` (should be complete)
- `docs/intent_normalization.md` (should exist)
- `docs/routing_interface.md` (should exist)
- `docs/fork_points.md` (should exist)

Then proceed to SHORT-TERM phase.

---

**Last Updated:** [Current Date]  
**Status:** Living document — update as decisions are made and phases complete.


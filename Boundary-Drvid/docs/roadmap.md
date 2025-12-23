# Boundary Wallet — Roadmap

This roadmap outlines the planned evolution of the Boundary wallet from a Zashi-derived foundation into a fully intent-driven, privacy-first, cross-chain wallet.

Dates are intentionally omitted. Progress is milestone-based, not calendar-driven.

---

## Phase 0 — Foundation (Current)

**Goal:** Establish a stable, attributable base with clear architectural intent.

Status:
- ✅ Zashi codebase forked with full attribution
- ✅ Repository ownership established
- ✅ Architecture document defined
- ✅ Project scope clarified
- 🔧 Documentation and identity cleanup in progress

Deliverables:
- Clean README
- Architecture documentation
- Roadmap definition
- Stable baseline build (no feature changes yet)

---

## Phase 1 — Identity & Stabilization

**Goal:** Make Boundary a distinct project without breaking upstream functionality.

Key work:
- Replace visible branding (name, icons, splash screens)
- Rename app display names (Android + iOS)
- Preserve internal package/module names temporarily
- Ensure builds still succeed on both platforms

Non-goals:
- No protocol changes
- No routing logic yet
- No cross-chain execution yet

Outcome:
> Boundary looks like Boundary, but behaves exactly like Zashi.

---

## Phase 2 — Intent Model Introduction

**Goal:** Introduce intent as a first-class concept without altering execution.

Key work:
- Define internal Intent schema
- Implement intent normalization layer
- Map intents to existing single-chain actions
- Maintain backward compatibility with current flows

Example intent:
- “Send asset X to address Y”
- “Swap asset A for asset B on the same chain”

Outcome:
> The wallet begins thinking in *intent*, even if execution remains simple.

---

## Phase 3 — Routing Engine (Local)

**Goal:** Separate *decision-making* from *execution*.

Key work:
- Implement routing engine interface
- Support multiple execution strategies internally
- Compare routes by cost, latency, and trust
- Add basic fallback handling

Execution remains:
- client-side
- transparent
- user-confirmable

Outcome:
> Boundary decides *how* to act, not just *what* to do.

---

## Phase 4 — Cross-Chain Execution

**Goal:** Enable cross-chain intent resolution.

Key work:
- Introduce execution provider abstraction
- Integrate at least one cross-chain route
- Handle partial failures and retries
- Improve user-facing explanations of outcomes

Initial focus:
- Limited chain pairs
- Conservative execution paths
- Clear warnings and confirmations

Outcome:
> Users can express cross-chain intent without manual bridges.

---

## Phase 5 — Privacy Expansion

**Goal:** Treat privacy as a routing constraint, not a feature toggle.

Key work:
- Privacy-aware route scoring
- Optional delayed or split execution
- Shielded or anonymized routing where available
- Address isolation per intent

User control:
- Privacy preference profiles
- Transparent trade-offs shown in UI

Outcome:
> Privacy becomes part of the routing decision, not an afterthought.

---

## Phase 6 — Hardening & Review

**Goal:** Prepare for real-world use.

Key work:
- Threat model review
- Failure-mode testing
- Metadata leakage analysis
- UX audits for clarity and safety

Outcome:
> Boundary becomes safe enough for cautious, real usage.

---

## Out of Scope (For Now)

Boundary intentionally avoids:
- Custodial services
- KYC requirements
- Protocol lock-in
- Aggressive automation
- Speed-first routing at the cost of privacy

---

## Guiding Principle

Boundary prioritizes:
1. User sovereignty
2. Privacy guarantees
3. Modular design
4. Long-term adaptability

Convenience is secondary.

---

## Final Note

This roadmap is a living document.  
Milestones may evolve as research, testing, and implementation inform design decisions.


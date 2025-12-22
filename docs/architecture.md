# Boundary Wallet — Architecture & Design Document

## Overview

**Boundary** is a privacy-first, intent-driven wallet derived from the Zashi codebase.  
Its goal is to remove artificial boundaries between chains, assets, and privacy domains while maintaining strong user sovereignty and minimal trust assumptions.

Boundary is not just a wallet that *holds* assets — it is a wallet that *expresses intent*.

---

## Design Principles

### 1. Privacy by Default
- No forced account linking
- No analytics tied to addresses
- Minimal metadata leakage
- Preference for shielded, private, or trust-minimized routes when available

### 2. Intent Over Transactions
Users should not need to understand:
- bridges
- routers
- wrapped assets
- intermediate hops

They express **what they want**, not **how to do it**.

Example:
> “Swap NEAR → BTC to this address”

The wallet figures out the rest.

---

### 3. Chain-Agnostic Core
Boundary treats blockchains as **execution environments**, not identities.

The wallet should:
- support heterogeneous chains (account-based, UTXO, shielded)
- route across ecosystems without privileging one
- avoid tight coupling to any single bridge or protocol

---

### 4. Modular & Replaceable Components
Every major system must be swappable without rewriting the app:

- Routers
- Privacy layers
- Bridges
- Swap providers
- Quote engines

This avoids protocol lock-in and future-proofs the wallet.

---

## High-Level Architecture


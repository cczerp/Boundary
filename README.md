# Boundary Wallet

**Boundary** is a privacy-first, intent-driven cryptocurrency wallet derived from the Zashi codebase.

Boundary is designed to remove artificial boundaries between blockchains, assets, and privacy domains by allowing users to express *what they want to do*, not *how to do it*.

> Boundary is currently in **early development** and is **not yet production-ready**.

---

## What Is Boundary?

Boundary is an experimental wallet that explores:

- Cross-chain asset movement without user-managed bridges
- Intent-based transaction design
- Privacy-preserving routing and execution
- Modular, replaceable routing and execution providers
- Reduced trust assumptions where possible

Rather than asking users to understand swaps, bridges, wrapped assets, or liquidity paths, Boundary focuses on **user intent** and resolves execution behind the scenes.

---

## Project Status

- 🧪 Early-stage development
- 🔧 Architecture and routing model under active design
- 📱 Mobile clients will be derived from upstream Zashi Android and iOS codebases
- 🚫 Not yet available on app stores
- 🚫 No beta program or waitlist at this time

---

## Codebase Origins & Attribution

Boundary is derived from the **Zashi wallet**, originally developed by the **Electric Coin Company**.

Upstream repositories:
- Zashi (root): https://github.com/Electric-Coin-Company/zashi
- Zashi Android: https://github.com/Electric-Coin-Company/zashi-android
- Zashi iOS: https://github.com/Electric-Coin-Company/zashi-ios

All original licensing, attribution, and authorship are preserved in accordance with upstream licenses.

Boundary does **not** claim endorsement, affiliation, or sponsorship by Electric Coin Company.

---

## Architecture Overview

Boundary introduces an **intent-based wallet architecture** layered on top of traditional wallet primitives.

High-level flow:



# Boundary Wallet

**Boundary** is a privacy-first, intent-driven cryptocurrency wallet derived from the Zashi codebase.

Boundary is designed to remove artificial boundaries between blockchains, assets, and privacy domains by allowing users to express *what they want to do*, not *how to do it*.

> Boundary is currently in **early development** and is **not yet production-ready**.

---

## Repository Structure

This is a **monorepo** containing:

- **Root**: Documentation, architecture, and coordination
- **`android/`**: Android app code (forked from `zashi-android`)
- **`ios/`**: iOS app code (forked from `zashi-ios`)

See [Fork Points](./docs/fork_points.md) for upstream repository information.

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
- 📱 Mobile clients forked from upstream Zashi Android and iOS codebases
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

## Documentation

- **[Architecture & Design](./docs/architecture.md)** — High-level architecture and design principles
- **[Roadmap](./docs/roadmap.md)** — Planned evolution and milestones
- **[Next Steps](./docs/next_steps.md)** — Concrete action plan for getting started
- **[Fork Points](./docs/fork_points.md)** — Upstream repository fork information

---

## Architecture Overview

Boundary introduces an **intent-based wallet architecture** layered on top of traditional wallet primitives.

High-level flow:

1. **User expresses intent** → "Send X to Y" or "Swap A for B"
2. **Intent normalization** → Structured Intent object
3. **Route discovery** → Find possible routes to fulfill intent
4. **Route selection** → Choose optimal route (privacy-first)
5. **Execution** → Execute route through appropriate providers
6. **Wallet core** → Sign transactions (unchanged from Zashi)

See [Architecture Document](./docs/architecture.md) for detailed design.

---

## Getting Started

### Prerequisites

- Android Studio (for Android development)
- Xcode (for iOS development)
- Git

### Building

**Android:**
```bash
cd android
./gradlew build
```

**iOS:**
```bash
cd ios
# Open in Xcode and build
```

### Development

See [Next Steps](./docs/next_steps.md) for the development roadmap and current priorities.

---

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md) for contribution guidelines.

---

## License

See [LICENSE](./LICENSE) for license information.

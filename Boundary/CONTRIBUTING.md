# Contributing to Boundary Wallet

Thank you for your interest in contributing to Boundary! This document provides guidelines and processes for contributing.

---

## Code of Conduct

Boundary is a privacy-first, user-sovereignty-focused project. Contributors should:

- Respect user privacy and data minimization
- Prioritize security and correctness over convenience
- Maintain clear separation between branding and code identity
- Preserve upstream attribution (Zashi, Electric Coin Company)

---

## Development Workflow

### Branching Strategy

- `main` — Stable, production-ready code (when applicable)
- `develop` — Integration branch for features
- `feature/*` — Feature branches (e.g., `feature/intent-normalization`)
- `docs/*` — Documentation-only changes
- `fix/*` — Bug fixes

### Getting Started

1. Fork the repository
2. Create a feature branch from `develop`
3. Make your changes
4. Ensure builds succeed (when applicable)
5. Submit a pull request

---

## Contribution Guidelines

### What Can Be Changed

**✅ SAFE to Change:**
- User-facing strings (app names, labels, descriptions)
- Documentation (README, architecture docs, comments)
- UI/UX elements (icons, splash screens, layouts)
- Intent processing and routing logic (new features)
- Execution providers (new integrations)

**❌ DO NOT Change:**
- Package names (Android: `com.electriccoinco.zashi.*`, iOS: `co.electriccoin.zashi.*`)
- Bundle identifiers (unless explicitly planned)
- Module/namespace paths (unless refactoring is approved)
- Cryptographic logic or protocol constants
- API schemas or serialized data formats
- License headers or attribution

### Code Identity Policy

Boundary maintains upstream package names and identifiers to:
- Preserve compatibility with existing tools and services
- Avoid breaking changes during early development
- Maintain clear attribution to upstream

See [Code Identity Policy](./docs/code_identity_policy.md) for details.

---

## Documentation Standards

### Architecture Changes

If you're proposing architectural changes:

1. Update `docs/architecture.md` first
2. Get review/approval before implementation
3. Document design decisions and trade-offs

### New Features

For new features:

1. Document the feature in `docs/architecture.md` or a new doc
2. Update `docs/roadmap.md` if it affects milestones
3. Add examples and use cases

---

## Testing Requirements

**When code exists** (future):

- Unit tests for intent normalization and routing logic
- Integration tests for execution providers
- Security tests for privacy guarantees
- UI tests for critical user flows

**Current State:**

- Documentation changes require no tests
- Design documents should be reviewed before implementation

---

## Pull Request Process

### Before Submitting

- [ ] Code follows project style (when applicable)
- [ ] Documentation is updated
- [ ] No breaking changes to code identity (package names, etc.)
- [ ] Attribution is preserved
- [ ] Security implications are considered

### PR Description Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Documentation
- [ ] Feature (intent/routing)
- [ ] Bug fix
- [ ] Branding/UI

## Testing
How was this tested? (if applicable)

## Security Considerations
Any security implications? (if applicable)

## Related Issues
Closes #X
```

---

## Security Disclosures

**DO NOT** report security vulnerabilities via public issues.

See [responsible_disclosure.md](./responsible_disclosure.md) for the disclosure process.

---

## Questions?

- Open an issue for questions or discussions
- Check `docs/` for architecture and design decisions
- Review `docs/next_steps.md` for current priorities

---

## Attribution

Boundary is derived from the Zashi wallet (Electric Coin Company). All contributions should preserve upstream attribution and licensing.


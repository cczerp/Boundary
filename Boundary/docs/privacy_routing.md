# Boundary Wallet — Privacy Constraints for Routing

This document defines privacy constraints and requirements for the routing engine and execution providers.

**Purpose:** Ensure privacy is built into routing from the start, not added as an afterthought.

---

## Privacy Principles

### 1. Data Minimization

**Principle:** Routers and execution providers receive only the minimum data necessary to fulfill an intent.

**Constraints:**
- Routers receive **only** the `Intent` object
- Routers **do not** receive wallet state, balances, or transaction history
- Routers **do not** receive user's other addresses
- Execution providers receive **only** the `RouteStep` they need to execute

**Enforcement:**
- Client-side validation before sending data to routers
- Clear API contracts (routers cannot request additional data)
- Audit logging (what data was sent to which router)

### 2. Privacy-First Routing

**Principle:** Privacy is a first-class routing criterion, not an optional feature.

**Constraints:**
- Privacy score is **required** for all routes
- Default routing preference: **HIGH privacy**
- Privacy-aware route scoring (privacy weighted higher than cost/latency)
- User can override privacy preferences, but default is privacy-first

**Enforcement:**
- Privacy scoring is mandatory (no routes without privacy scores)
- Default user preferences prioritize privacy
- Privacy trade-offs are clearly communicated to users

### 3. Address Isolation

**Principle:** Generate and use addresses in a way that minimizes linkability.

**Constraints:**
- Generate new addresses per intent when possible
- Avoid address reuse across intents
- Prefer shielded addresses over transparent addresses
- Isolate addresses by intent (don't link intents via addresses)

**Enforcement:**
- Address generation per intent (when applicable)
- Address reuse detection and warnings
- Shielded address preference in route selection

---

## Privacy Scoring

### Privacy Levels

**HIGH Privacy:**
- Fully shielded/private transactions
- No metadata leakage
- No linkability between transactions
- Examples: Zcash shielded transactions, privacy-preserving bridges

**MEDIUM Privacy:**
- Partially private transactions
- Some metadata leakage (amounts, but not addresses)
- Limited linkability
- Examples: Shielded input, transparent output

**LOW Privacy:**
- Transparent/public transactions
- Full metadata visible on blockchain
- Full linkability
- Examples: Bitcoin transparent transactions, Ethereum public transactions

### Privacy Score Calculation

**For Single-Step Routes:**
- Privacy score = Privacy level of the transaction type
- Shielded → HIGH
- Transparent → LOW
- Mixed → MEDIUM

**For Multi-Step Routes:**
- Privacy score = Minimum privacy level across all steps
- If any step is LOW, overall route is LOW
- If all steps are HIGH, overall route is HIGH
- Mixed steps → MEDIUM

**Privacy Score Weighting:**
- Default weight: 0.4 (40% of route score)
- User can adjust weight via preferences
- Privacy-first default: Higher weight for privacy

---

## Metadata Leakage Prevention

### What Routers Can See

**Allowed:**
- Intent structure (action, assets, amounts, destination)
- Route discovery requests
- Quote requests

**Not Allowed:**
- User's wallet balances
- User's transaction history
- User's other addresses
- User's keys or seed phrases
- User's IP address (if possible, use privacy-preserving network layer)

### What Execution Providers Can See

**Allowed:**
- Route step they need to execute
- Transaction data for their step only
- Execution status requests

**Not Allowed:**
- Other route steps
- User's wallet state
- User's keys (signing happens client-side)
- User's transaction history

### Network Privacy

**Recommendation:** Use privacy-preserving network layer when possible

**Options:**
- Tor/Onion routing (if supported)
- VPN (user's choice)
- Direct connections (avoid intermediaries)

**Current Limitation:** Standard HTTPS/TLS (metadata visible to network observers)

**Future Enhancement:** Integrate Tor/VPN support for router communication

---

## Privacy-Aware Route Selection

### Route Filtering

**Privacy Filters:**
- Filter out routes with LOW privacy if HIGH/MEDIUM available
- Filter out routes with custodial providers if non-custodial available
- Filter out routes that leak metadata unnecessarily

**User Override:**
- User can disable privacy filters (with warning)
- User can prefer cost/latency over privacy
- User preferences are respected, but defaults are privacy-first

### Route Scoring

**Privacy Weight:**
- Default: 0.4 (40% of score)
- Privacy-first: Higher weight (0.5-0.6)
- Cost-first: Lower weight (0.2-0.3)

**Privacy Score Impact:**
- HIGH privacy routes score higher than LOW privacy routes
- Privacy score is multiplied by privacy weight
- Privacy can override cost/latency if weight is high enough

**Example:**
```
Route A: HIGH privacy, cost $0.10, latency 60s
Route B: LOW privacy, cost $0.05, latency 30s

With privacy-first weights (privacy=0.5):
- Route A score: (0.5 × 3) + (0.2 × (1/0.10)) + (0.2 × (1/60)) = 1.5 + 2.0 + 0.003 = 3.503
- Route B score: (0.5 × 1) + (0.2 × (1/0.05)) + (0.2 × (1/30)) = 0.5 + 4.0 + 0.007 = 4.507

Route A wins (privacy-first) despite higher cost/latency.
```

---

## Shielded Route Prioritization

### Zcash Shielded Transactions

**Priority:** Always prefer shielded transactions over transparent

**Enforcement:**
- Shielded routes score HIGH privacy
- Transparent routes score LOW privacy
- Shielded routes ranked higher by default

**User Override:**
- User can prefer transparent (with warning about privacy loss)
- User can set privacy preference to LOW (allows transparent)

### Other Privacy-Preserving Chains

**Priority:** Prefer privacy-preserving mechanisms when available

**Examples:**
- Monero (ring signatures)
- Zcash (shielded transactions)
- Privacy-preserving bridges (if available)

**Enforcement:**
- Privacy-preserving mechanisms score HIGH privacy
- Public mechanisms score LOW privacy
- Privacy-preserving routes ranked higher by default

---

## Cross-Chain Privacy Considerations

### Bridge Privacy

**Custodial Bridges:**
- Privacy: LOW (bridge operator sees all transactions)
- Trust: LOW (requires trust in bridge operator)
- **Recommendation:** Avoid if possible, warn user if used

**Non-Custodial Bridges:**
- Privacy: MEDIUM (transactions visible on blockchain, but no single operator)
- Trust: MEDIUM (requires trust in bridge protocol)
- **Recommendation:** Prefer over custodial, but still warn about privacy

**Privacy-Preserving Bridges:**
- Privacy: HIGH (shielded/private bridge transactions)
- Trust: MEDIUM-HIGH (depends on bridge implementation)
- **Recommendation:** Prefer if available

### Multi-Hop Privacy

**Privacy Score:**
- Multi-hop routes: Privacy score = Minimum across all hops
- If any hop is LOW privacy, overall route is LOW
- All hops must be HIGH for route to be HIGH

**Privacy Leakage:**
- Each hop may leak metadata
- Linkability across hops (if addresses are reused)
- **Mitigation:** Use different addresses per hop when possible

---

## User Privacy Preferences

### Privacy Profiles

**Privacy-First (Default):**
- Privacy weight: 0.5
- Max privacy: HIGH
- Min privacy: MEDIUM (reject LOW privacy routes)
- Prefer shielded/private routes

**Balanced:**
- Privacy weight: 0.3
- Max privacy: MEDIUM
- Min privacy: LOW (allow transparent routes)
- Balance privacy and cost

**Cost-First:**
- Privacy weight: 0.2
- Max privacy: LOW (allow transparent routes)
- Min privacy: LOW
- Prefer cheapest routes

**Custom:**
- User sets privacy weight
- User sets min/max privacy levels
- User sets other preferences

### Privacy Trade-Offs

**Communication:**
- Clearly explain privacy trade-offs to users
- Show privacy level for each route option
- Warn about privacy loss for LOW privacy routes

**User Control:**
- User can override privacy preferences
- User can see privacy impact of their choices
- User can change preferences per intent

---

## Privacy Guarantees

### What Boundary Guarantees

**Router Privacy:**
- Routers receive only intent data (no wallet state)
- Routers cannot link intents to user identity
- Routers cannot see transaction history

**Execution Privacy:**
- Execution providers receive only route step data
- Execution providers cannot see other steps or wallet state
- Signing happens client-side (keys never leave device)

**Route Privacy:**
- Privacy score is calculated and displayed
- Privacy-first routing by default
- User can see privacy impact of route selection

### What Boundary Cannot Guarantee

**Blockchain Privacy:**
- Transparent transactions are public by design
- Shielded transactions provide privacy (Zcash)
- Privacy depends on blockchain capabilities

**Network Privacy:**
- Network observers may see router communication (HTTPS metadata)
- IP addresses may be visible (use VPN/Tor if needed)
- Future: Tor/VPN integration for network privacy

**Bridge Privacy:**
- Bridge operators may see transactions (custodial bridges)
- Bridge protocols may leak metadata (non-custodial bridges)
- Privacy-preserving bridges preferred, but not always available

---

## Privacy Testing

### Test Cases

**Data Minimization:**
- Verify routers receive only intent data
- Verify execution providers receive only route step data
- Verify keys never leave device

**Privacy Scoring:**
- Verify all routes have privacy scores
- Verify privacy scores are accurate
- Verify privacy-first routing works

**Address Isolation:**
- Verify new addresses generated per intent
- Verify address reuse detection
- Verify shielded address preference

**Privacy Preferences:**
- Verify default privacy-first preferences
- Verify user can override preferences
- Verify privacy trade-offs are communicated

---

## Future Enhancements

### Advanced Privacy Features

**Delayed Execution:**
- Delay transaction execution to break timing patterns
- User can set delay preferences
- Privacy vs. latency trade-off

**Split Routing:**
- Split large transactions into multiple smaller transactions
- Reduces linkability
- Privacy vs. cost trade-off

**Privacy-Preserving Bridges:**
- Integrate privacy-preserving bridge protocols
- Shielded bridge transactions
- Enhanced cross-chain privacy

**Tor/VPN Integration:**
- Integrate Tor for router communication
- VPN support for network privacy
- Enhanced network-layer privacy

---

**Last Updated:** [Current Date]  
**Status:** Privacy constraints defined — ready for implementation (Phase 3-5)


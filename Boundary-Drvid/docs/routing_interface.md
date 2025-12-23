# Boundary Wallet — Routing Engine Interface

This document defines the routing engine interface and contract for discovering and evaluating routes to fulfill intents.

**Purpose:** Establish a clear contract for routing implementations before any code is written.

---

## Overview

The routing engine is responsible for:
1. **Route Discovery** — Finding possible routes to fulfill an intent
2. **Route Scoring** — Evaluating routes by multiple criteria
3. **Route Selection** — Ranking routes and selecting optimal options
4. **Route Validation** — Ensuring routes are still executable

**Key Principle:** Routers are **untrusted** by default. They receive only the `Intent` object, not wallet state or keys.

---

## Router Interface

### Core Interface

```typescript
interface Router {
  // Unique router identifier
  id: string;
  
  // Human-readable name
  name: string;
  
  // Which chains this router supports
  supportedChains: ChainId[];
  
  // Find all possible routes for an intent
  findRoutes(intent: Intent): Promise<Route[]>;
  
  // Compare two routes (returns: -1 if route1 < route2, 0 if equal, 1 if route1 > route2)
  compareRoutes(
    route1: Route,
    route2: Route,
    preferences: UserPreferences
  ): number;
  
  // Validate that a route can still be executed (prices may have changed)
  validateRoute(route: Route): Promise<boolean>;
  
  // Get updated quote for a route (refresh prices)
  refreshQuote(route: Route): Promise<Route>;
}
```

### Route Structure

```typescript
interface Route {
  // Unique route identifier
  id: string;
  
  // Intent this route fulfills
  intentId: string;
  
  // Router that discovered this route
  routerId: string;
  
  // Execution steps (may be multi-hop)
  steps: RouteStep[];
  
  // Total estimated cost (in user's preferred currency)
  estimatedCost: Amount;
  
  // Estimated time from start to completion (seconds)
  estimatedLatency: number;
  
  // Privacy characteristics
  privacyScore: PrivacyScore;
  
  // Trust assumptions
  trustScore: TrustScore;
  
  // Quote expiration time (timestamp)
  expiresAt: number;
  
  // Route metadata
  metadata?: RouteMetadata;
}

interface RouteStep {
  // Step sequence number (0-indexed)
  sequence: number;
  
  // Step type
  type: StepType; // BRIDGE, SWAP, SEND, etc.
  
  // Provider that executes this step
  provider: ProviderId;
  
  // Input asset for this step
  inputAsset: Asset;
  
  // Output asset for this step
  outputAsset: Asset;
  
  // Estimated cost for this step
  estimatedCost: Amount;
  
  // Estimated latency for this step (seconds)
  estimatedLatency: number;
  
  // Trust model for this step
  trustModel: TrustModel; // CUSTODIAL, NON_CUSTODIAL, TRUSTLESS
  
  // Step-specific metadata
  metadata?: StepMetadata;
}
```

### Supporting Types

```typescript
type StepType = "BRIDGE" | "SWAP" | "SEND" | "RECEIVE";

type PrivacyScore = "HIGH" | "MEDIUM" | "LOW";

type TrustScore = "HIGH" | "MEDIUM" | "LOW";

type TrustModel = "CUSTODIAL" | "NON_CUSTODIAL" | "TRUSTLESS";

interface UserPreferences {
  // Privacy preference (default: HIGH)
  privacyLevel: PrivacyLevel;
  
  // Maximum acceptable cost
  maxCost?: Amount;
  
  // Maximum acceptable latency (seconds)
  maxLatency?: number;
  
  // Preferred providers (if any)
  preferredProviders?: ProviderId[];
  
  // Blocked providers (if any)
  blockedProviders?: ProviderId[];
  
  // Scoring weights (if custom)
  weights?: ScoringWeights;
}

interface ScoringWeights {
  privacy: number;  // Default: 0.4
  cost: number;     // Default: 0.2
  latency: number;  // Default: 0.2
  trust: number;    // Default: 0.2
}
```

---

## Route Discovery

### `findRoutes(intent: Intent): Promise<Route[]>`

**Purpose:** Discover all possible routes to fulfill an intent.

**Input:** Structured `Intent` object (see [Architecture](./architecture.md))

**Output:** Array of `Route` objects (may be empty if no routes found)

**Behavior:**
- Router receives **only** the `Intent` object
- Router **does not** receive wallet state, keys, or balances
- Router discovers routes based on intent alone
- Router returns **all** possible routes (client filters by preferences)

**Error Handling:**
- Network errors: Return empty array or throw (router-specific)
- Invalid intent: Throw error with clear message
- Timeout: Return empty array or throw (router-specific)

**Example:**
```typescript
const intent: Intent = {
  type: "SWAP",
  sourceAsset: { chain: "ethereum", token: "ETH" },
  targetAsset: { chain: "bitcoin", token: "BTC" },
  amount: "1",
  // ...
};

const routes = await router.findRoutes(intent);
// Returns: [
//   { id: "route1", steps: [...], estimatedCost: "0.05", ... },
//   { id: "route2", steps: [...], estimatedCost: "0.08", ... },
// ]
```

---

## Route Scoring

### `compareRoutes(route1: Route, route2: Route, preferences: UserPreferences): number`

**Purpose:** Compare two routes and determine which is better.

**Input:**
- `route1`: First route to compare
- `route2`: Second route to compare
- `preferences`: User preferences for scoring

**Output:**
- `-1`: route1 is worse than route2
- `0`: route1 is equal to route2
- `1`: route1 is better than route2

**Scoring Algorithm:**

1. **Normalize Scores:**
   - Privacy: HIGH=3, MEDIUM=2, LOW=1
   - Trust: HIGH=3, MEDIUM=2, LOW=1
   - Cost: Invert (lower is better): `1 / (cost + 1)`
   - Latency: Invert (lower is better): `1 / (latency + 1)`

2. **Apply Weights:**
   ```
   score1 = (privacyWeight × privacy1) +
            (costWeight × (1 / cost1)) +
            (latencyWeight × (1 / latency1)) +
            (trustWeight × trust1)
   
   score2 = (privacyWeight × privacy2) +
            (costWeight × (1 / cost2)) +
            (latencyWeight × (1 / latency2)) +
            (trustWeight × trust2)
   ```

3. **Compare:**
   - If score1 > score2: return 1
   - If score1 < score2: return -1
   - If score1 == score2: return 0

**Default Weights:**
- Privacy: 0.4 (privacy-first)
- Cost: 0.2
- Latency: 0.2
- Trust: 0.2

**User Override:**
- User can adjust weights via preferences
- User can set max cost/latency (filter routes, don't score)

---

## Route Validation

### `validateRoute(route: Route): Promise<boolean>`

**Purpose:** Verify that a route is still executable (prices may have changed).

**Input:** `Route` object to validate

**Output:** `true` if route is still valid, `false` if expired or invalid

**Behavior:**
- Check if route has expired (`expiresAt < now`)
- Verify route steps are still executable
- Check if providers are still available
- Validate that prices haven't changed significantly (router-specific threshold)

**Error Handling:**
- Network errors: Return `false` or throw (router-specific)
- Invalid route: Return `false`

**Example:**
```typescript
const isValid = await router.validateRoute(route);
if (!isValid) {
  // Route expired or invalid, refresh quote
  const refreshedRoute = await router.refreshQuote(route);
}
```

---

## Quote Refresh

### `refreshQuote(route: Route): Promise<Route>`

**Purpose:** Get updated quote for a route (prices may have changed).

**Input:** Existing `Route` object

**Output:** Updated `Route` object with fresh quotes

**Behavior:**
- Re-fetch prices for all route steps
- Update `estimatedCost`, `estimatedLatency`, `expiresAt`
- Preserve route structure (steps, IDs, etc.)

**Error Handling:**
- Network errors: Throw error
- Route no longer available: Throw error with clear message
- Partial failure: Update available steps, mark unavailable steps

**Example:**
```typescript
const refreshedRoute = await router.refreshQuote(route);
// Returns: Updated route with fresh prices and expiration
```

---

## Router Types

### Local Router (Phase 3)

**Purpose:** Single-chain routing (no cross-chain)

**Capabilities:**
- Direct sends (same chain)
- Same-chain swaps (if applicable)
- No bridges

**Implementation:**
- `LocalRouter` implements `Router` interface
- Only supports intents on single chain
- Returns single-step routes (direct send/swap)

### Cross-Chain Router (Phase 4)

**Purpose:** Multi-chain routing with bridges

**Capabilities:**
- Cross-chain sends
- Cross-chain swaps
- Multi-hop routes

**Implementation:**
- `CrossChainRouter` implements `Router` interface
- Integrates with bridge providers
- Returns multi-step routes (bridge + send/swap)

### Privacy Router (Phase 5)

**Purpose:** Privacy-aware routing

**Capabilities:**
- Privacy-first route selection
- Shielded route prioritization
- Privacy-preserving bridges (if available)

**Implementation:**
- `PrivacyRouter` wraps other routers
- Filters and scores routes by privacy
- Default privacy preference: HIGH

---

## Error Handling

### Router Errors

**Network Errors:**
- Timeout: Router throws `RouterTimeoutError`
- Connection failed: Router throws `RouterConnectionError`
- Rate limited: Router throws `RouterRateLimitError`

**Validation Errors:**
- Invalid intent: Router throws `InvalidIntentError`
- Unsupported chain: Router throws `UnsupportedChainError`
- No routes found: Router returns empty array (not an error)

**Quote Errors:**
- Quote expired: `validateRoute()` returns `false`
- Price changed: `refreshQuote()` returns updated route or throws

### Error Types

```typescript
class RouterError extends Error {
  routerId: string;
  code: string;
}

class RouterTimeoutError extends RouterError {
  code: "ROUTER_TIMEOUT";
}

class RouterConnectionError extends RouterError {
  code: "ROUTER_CONNECTION_FAILED";
}

class InvalidIntentError extends RouterError {
  code: "INVALID_INTENT";
  details: string;
}
```

---

## Security Considerations

### Data Minimization

**Router receives:**
- `Intent` object only
- No wallet state
- No keys or addresses (except destination in intent)
- No transaction history

**Router cannot access:**
- User's wallet balances
- User's private keys
- User's transaction history
- User's other addresses

### Route Validation

**Client-side validation:**
- Validate route structure before execution
- Verify route steps are executable
- Check route expiration
- Verify provider availability

**Trust assumptions:**
- Routers are untrusted by default
- Routes are validated client-side
- User must confirm route selection

---

## Implementation Phases

### Phase 2: Interface Only
- Define interfaces (this document)
- Create stub implementations
- No real routing logic

### Phase 3: Local Routing
- Implement `LocalRouter` (single-chain)
- Basic route discovery
- Basic route scoring

### Phase 4: Cross-Chain Routing
- Implement `CrossChainRouter`
- Bridge integration
- Multi-hop routes

### Phase 5: Privacy Routing
- Implement `PrivacyRouter`
- Privacy-aware scoring
- Shielded route prioritization

---

## Testing Requirements

**Unit Tests:**
- Route discovery (valid intents)
- Route scoring (various preferences)
- Route validation (expired routes)
- Quote refresh (price updates)

**Integration Tests:**
- Router with mock providers
- Router with real providers (testnet)
- Error handling (network failures)

**Security Tests:**
- Data minimization (router doesn't receive keys)
- Route validation (malformed routes)
- Privacy guarantees (routes don't leak data)

---

**Last Updated:** [Current Date]  
**Status:** Interface design complete — ready for implementation (Phase 2-3)


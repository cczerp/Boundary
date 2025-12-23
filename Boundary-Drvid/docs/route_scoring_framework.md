# Boundary Wallet — Route Scoring Framework Design

This document defines the framework for scoring and comparing routes, establishing the foundation for privacy-aware routing.

**Purpose:** Provide a flexible, extensible scoring system that can evolve from simple cost comparison to complex privacy-aware scoring.

---

## Scoring Criteria

### 1. Privacy Score

**Values:** HIGH, MEDIUM, LOW

**Calculation:**
- HIGH: Fully shielded/private transactions (all steps)
- MEDIUM: Partially private (some steps private, some transparent)
- LOW: Fully transparent/public transactions (all steps)

**Numeric Mapping:**
- HIGH = 3.0
- MEDIUM = 2.0
- LOW = 1.0

**Rationale:** Privacy is a first-class criterion. Higher privacy scores better.

---

### 2. Cost Score

**Values:** Numeric (amount in user's preferred currency)

**Calculation:**
- Sum of all step costs
- Includes: fees, slippage, bridge costs
- Expressed in canonical units (smallest unit)

**Normalization:**
- Invert for scoring (lower cost = higher score)
- Formula: `1 / (cost + 1)` to avoid division by zero
- Scale to 0-1 range: `normalized = 1 / (1 + cost / maxCost)`

**Rationale:** Lower cost is better, but privacy can override cost.

---

### 3. Latency Score

**Values:** Numeric (seconds)

**Calculation:**
- Sum of all step latencies
- Includes: blockchain confirmation times, bridge processing times
- Expressed in seconds

**Normalization:**
- Invert for scoring (lower latency = higher score)
- Formula: `1 / (latency + 1)` to avoid division by zero
- Scale to 0-1 range: `normalized = 1 / (1 + latency / maxLatency)`

**Rationale:** Lower latency is better, but privacy can override latency.

---

### 4. Trust Score

**Values:** HIGH, MEDIUM, LOW

**Calculation:**
- HIGH: Trustless (no third-party custody, client-side signing)
- MEDIUM: Non-custodial (requires trust in provider, but no custody)
- LOW: Custodial (provider holds funds temporarily)

**Numeric Mapping:**
- HIGH = 3.0
- MEDIUM = 2.0
- LOW = 1.0

**Rationale:** Higher trust (less trust required) is better.

---

## Scoring Weights

### Default Weights (Privacy-First)

```typescript
const DEFAULT_WEIGHTS: ScoringWeights = {
  privacy: 0.4,   // 40% - Privacy is most important
  cost: 0.2,      // 20% - Cost matters, but less than privacy
  latency: 0.2,   // 20% - Latency matters, but less than privacy
  trust: 0.2      // 20% - Trust matters, but less than privacy
};
```

### User Preference Profiles

**Privacy-First:**
```typescript
{
  privacy: 0.5,
  cost: 0.15,
  latency: 0.15,
  trust: 0.2
}
```

**Balanced:**
```typescript
{
  privacy: 0.3,
  cost: 0.25,
  latency: 0.25,
  trust: 0.2
}
```

**Cost-First:**
```typescript
{
  privacy: 0.2,
  cost: 0.4,
  latency: 0.2,
  trust: 0.2
}
```

---

## Scoring Algorithm

### Step 1: Normalize Scores

```typescript
function normalizeScores(route: Route): NormalizedScores {
  return {
    privacy: privacyToNumber(route.privacyScore),  // 1.0 - 3.0
    cost: normalizeCost(route.estimatedCost),      // 0.0 - 1.0
    latency: normalizeLatency(route.estimatedLatency), // 0.0 - 1.0
    trust: trustToNumber(route.trustScore)         // 1.0 - 3.0
  };
}
```

### Step 2: Apply Weights

```typescript
function calculateWeightedScore(
  normalized: NormalizedScores,
  weights: ScoringWeights
): number {
  return (
    weights.privacy * normalized.privacy +
    weights.cost * normalized.cost +
    weights.latency * normalized.latency +
    weights.trust * normalized.trust
  );
}
```

### Step 3: Compare Routes

```typescript
function compareRoutes(
  route1: Route,
  route2: Route,
  preferences: UserPreferences
): number {
  const weights = preferences.weights || DEFAULT_WEIGHTS;
  
  const normalized1 = normalizeScores(route1);
  const normalized2 = normalizeScores(route2);
  
  const score1 = calculateWeightedScore(normalized1, weights);
  const score2 = calculateWeightedScore(normalized2, weights);
  
  if (score1 > score2) return 1;   // route1 is better
  if (score1 < score2) return -1;  // route2 is better
  return 0;  // Equal
}
```

---

## RouteComparator Interface

### Core Interface

```typescript
interface RouteComparator {
  // Compare two routes
  compare(
    route1: Route,
    route2: Route,
    preferences: UserPreferences
  ): number;
  
  // Rank multiple routes
  rank(
    routes: Route[],
    preferences: UserPreferences
  ): Route[];
  
  // Get score explanation (for debugging)
  explain(
    route: Route,
    preferences: UserPreferences
  ): ScoreExplanation;
}

interface ScoreExplanation {
  normalizedScores: NormalizedScores;
  weightedScore: number;
  weights: ScoringWeights;
  breakdown: {
    privacy: { value: number; weight: number; contribution: number };
    cost: { value: number; weight: number; contribution: number };
    latency: { value: number; weight: number; contribution: number };
    trust: { value: number; weight: number; contribution: number };
  };
}
```

---

## Implementations

### 1. BasicRouteComparator (Phase 2)

**Purpose:** Simple comparator for Phase 2 (privacy-first, basic cost/latency).

**Implementation:**
```typescript
class BasicRouteComparator implements RouteComparator {
  compare(route1: Route, route2: Route, preferences: UserPreferences): number {
    const weights = preferences.weights || DEFAULT_WEIGHTS;
    
    // Simple comparison: privacy first, then cost, then latency
    const privacy1 = privacyToNumber(route1.privacyScore);
    const privacy2 = privacyToNumber(route2.privacyScore);
    
    if (privacy1 !== privacy2) {
      return privacy1 > privacy2 ? 1 : -1;
    }
    
    // If privacy equal, compare cost
    const cost1 = parseFloat(route1.estimatedCost.value);
    const cost2 = parseFloat(route2.estimatedCost.value);
    
    if (cost1 !== cost2) {
      return cost1 < cost2 ? 1 : -1;  // Lower cost is better
    }
    
    // If cost equal, compare latency
    return route1.estimatedLatency < route2.estimatedLatency ? 1 : -1;
  }
  
  rank(routes: Route[], preferences: UserPreferences): Route[] {
    return routes.sort((a, b) => this.compare(a, b, preferences));
  }
  
  explain(route: Route, preferences: UserPreferences): ScoreExplanation {
    // Stub implementation for Phase 2
    return {
      normalizedScores: normalizeScores(route),
      weightedScore: 0,  // Not calculated in Phase 2
      weights: preferences.weights || DEFAULT_WEIGHTS,
      breakdown: {}  // Stub
    };
  }
}
```

---

### 2. WeightedRouteComparator (Phase 3+)

**Purpose:** Full weighted scoring with all criteria.

**Implementation:**
```typescript
class WeightedRouteComparator implements RouteComparator {
  compare(route1: Route, route2: Route, preferences: UserPreferences): number {
    const weights = preferences.weights || DEFAULT_WEIGHTS;
    const normalized1 = normalizeScores(route1);
    const normalized2 = normalizeScores(route2);
    const score1 = calculateWeightedScore(normalized1, weights);
    const score2 = calculateWeightedScore(normalized2, weights);
    return score1 > score2 ? 1 : score1 < score2 ? -1 : 0;
  }
  
  rank(routes: Route[], preferences: UserPreferences): Route[] {
    return routes.sort((a, b) => this.compare(a, b, preferences));
  }
  
  explain(route: Route, preferences: UserPreferences): ScoreExplanation {
    const weights = preferences.weights || DEFAULT_WEIGHTS;
    const normalized = normalizeScores(route);
    const weightedScore = calculateWeightedScore(normalized, weights);
    
    return {
      normalizedScores: normalized,
      weightedScore,
      weights,
      breakdown: {
        privacy: {
          value: normalized.privacy,
          weight: weights.privacy,
          contribution: weights.privacy * normalized.privacy
        },
        cost: {
          value: normalized.cost,
          weight: weights.cost,
          contribution: weights.cost * normalized.cost
        },
        latency: {
          value: normalized.latency,
          weight: weights.latency,
          contribution: weights.latency * normalized.latency
        },
        trust: {
          value: normalized.trust,
          weight: weights.trust,
          contribution: weights.trust * normalized.trust
        }
      }
    };
  }
}
```

---

## Normalization Functions

### Privacy Normalization

```typescript
function privacyToNumber(privacy: PrivacyScore): number {
  switch (privacy) {
    case "HIGH": return 3.0;
    case "MEDIUM": return 2.0;
    case "LOW": return 1.0;
    default: return 1.0;
  }
}
```

### Cost Normalization

```typescript
function normalizeCost(cost: Amount, maxCost?: Amount): number {
  const costValue = parseFloat(cost.value);
  const max = maxCost ? parseFloat(maxCost.value) : 1000000;  // Default max
  return 1 / (1 + costValue / max);
}
```

### Latency Normalization

```typescript
function normalizeLatency(latency: number, maxLatency?: number): number {
  const max = maxLatency || 3600;  // Default: 1 hour
  return 1 / (1 + latency / max);
}
```

### Trust Normalization

```typescript
function trustToNumber(trust: TrustScore): number {
  switch (trust) {
    case "HIGH": return 3.0;
    case "MEDIUM": return 2.0;
    case "LOW": return 1.0;
    default: return 1.0;
  }
}
```

---

## Testing

### Unit Tests

**Scoring:**
- Privacy normalization (HIGH=3, MEDIUM=2, LOW=1)
- Cost normalization (lower cost = higher score)
- Latency normalization (lower latency = higher score)
- Trust normalization (HIGH=3, MEDIUM=2, LOW=1)

**Comparison:**
- Privacy-first comparison (HIGH > MEDIUM > LOW)
- Cost comparison (lower cost wins when privacy equal)
- Latency comparison (lower latency wins when cost equal)
- Weighted comparison (respects user weights)

**Ranking:**
- Routes ranked correctly by score
- Ties handled correctly
- Empty array handled correctly

---

## Phase 2 Implementation

### Stub Implementation

**BasicRouteComparator:**
- Simple privacy-first comparison
- Basic cost/latency comparison
- No weighted scoring yet

**Future:**
- Full weighted scoring in Phase 3
- Score explanations in Phase 3
- User preference profiles in Phase 3

---

**Last Updated:** [Current Date]  
**Status:** Framework design complete — ready for stub implementation (Phase 2)


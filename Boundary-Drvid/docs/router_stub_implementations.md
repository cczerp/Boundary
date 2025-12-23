# Boundary Wallet — Router Stub Implementations Design

This document defines stub implementations of the Router interface for Phase 2, before real routing logic is implemented.

**Purpose:** Establish the routing contract and provide testable implementations without complex routing logic.

---

## Stub Router Types

### 1. NullRouter

**Purpose:** Router that always returns no routes (for testing error handling).

**Implementation:**
```typescript
class NullRouter implements Router {
  id = "null_router";
  name = "Null Router";
  supportedChains: ChainId[] = [];
  
  async findRoutes(intent: Intent): Promise<Route[]> {
    // Always return empty array
    return [];
  }
  
  async compareRoutes(route1: Route, route2: Route, preferences: UserPreferences): number {
    // Always return 0 (equal)
    return 0;
  }
  
  async validateRoute(route: Route): Promise<boolean> {
    // Always return false (invalid)
    return false;
  }
  
  async refreshQuote(route: Route): Promise<Route> {
    // Return route unchanged (stub)
    return route;
  }
}
```

**Use Cases:**
- Testing error handling when no routes found
- Testing fallback logic
- Placeholder during development

---

### 2. SingleChainRouter

**Purpose:** Router that returns a single direct route for same-chain intents.

**Implementation:**
```typescript
class SingleChainRouter implements Router {
  id = "single_chain_router";
  name = "Single Chain Router";
  supportedChains: ChainId[] = ["zcash"];  // Phase 2: Zcash only
  
  async findRoutes(intent: Intent): Promise<Route[]> {
    // Only support same-chain intents
    if (intent.sourceAsset.chain !== intent.targetAsset.chain) {
      return [];  // Cross-chain not supported
    }
    
    // Only support Zcash in Phase 2
    if (intent.sourceAsset.chain !== "zcash") {
      return [];  // Other chains not supported
    }
    
    // Create single direct route
    const route: Route = {
      id: `route_${intent.id}_direct`,
      intentId: intent.id,
      routerId: this.id,
      steps: [{
        sequence: 0,
        type: "SEND",
        provider: "zashi_wallet",  // Stub provider
        inputAsset: intent.sourceAsset,
        outputAsset: intent.targetAsset,
        estimatedCost: {
          value: "0",  // Stub: no cost
          displayValue: "0",
          currency: "ZEC",
          decimals: 8
        },
        estimatedLatency: 60,  // Stub: 60 seconds
        trustModel: "TRUSTLESS",  // Client-side signing
        metadata: {}
      }],
      estimatedCost: {
        value: "0",
        displayValue: "0",
        currency: "ZEC",
        decimals: 8
      },
      estimatedLatency: 60,
      privacyScore: intent.destination.type === "SHIELDED" ? "HIGH" : "LOW",
      trustScore: "HIGH",
      expiresAt: Date.now() + 60000,  // 1 minute expiration
      metadata: {}
    };
    
    return [route];
  }
  
  async compareRoutes(route1: Route, route2: Route, preferences: UserPreferences): number {
    // Simple comparison: prefer HIGH privacy
    if (route1.privacyScore === "HIGH" && route2.privacyScore !== "HIGH") {
      return 1;  // route1 is better
    }
    if (route2.privacyScore === "HIGH" && route1.privacyScore !== "HIGH") {
      return -1;  // route2 is better
    }
    return 0;  // Equal
  }
  
  async validateRoute(route: Route): Promise<boolean> {
    // Check expiration
    if (route.expiresAt < Date.now()) {
      return false;
    }
    // Stub: always valid if not expired
    return true;
  }
  
  async refreshQuote(route: Route): Promise<Route> {
    // Update expiration time
    return {
      ...route,
      expiresAt: Date.now() + 60000
    };
  }
}
```

**Use Cases:**
- Phase 2: Single-chain routing
- Testing route structure
- Testing route selection UI

---

### 3. MockRouter (Testing)

**Purpose:** Router that returns configurable mock routes for testing.

**Implementation:**
```typescript
class MockRouter implements Router {
  id = "mock_router";
  name = "Mock Router";
  supportedChains: ChainId[] = ["zcash", "bitcoin", "ethereum"];
  
  private mockRoutes: Route[] = [];
  
  // Set mock routes for testing
  setMockRoutes(routes: Route[]): void {
    this.mockRoutes = routes;
  }
  
  async findRoutes(intent: Intent): Promise<Route[]> {
    // Return configured mock routes
    return this.mockRoutes.filter(route => route.intentId === intent.id);
  }
  
  async compareRoutes(route1: Route, route2: Route, preferences: UserPreferences): number {
    // Use basic comparator
    return new BasicRouteComparator().compare(route1, route2, preferences);
  }
  
  async validateRoute(route: Route): Promise<boolean> {
    return route.expiresAt > Date.now();
  }
  
  async refreshQuote(route: Route): Promise<Route> {
    return { ...route, expiresAt: Date.now() + 60000 };
  }
}
```

**Use Cases:**
- Unit testing
- Integration testing
- UI testing (route selection screens)

---

## Router Selection

### Router Registry

**Purpose:** Manage available routers and select appropriate router for intent.

**Implementation:**
```typescript
class RouterRegistry {
  private routers: Router[] = [];
  
  // Register a router
  register(router: Router): void {
    this.routers.push(router);
  }
  
  // Find routers that support an intent
  findSupportedRouters(intent: Intent): Router[] {
    return this.routers.filter(router => {
      return router.supportedChains.includes(intent.sourceAsset.chain) ||
             router.supportedChains.includes(intent.targetAsset.chain);
    });
  }
  
  // Get default router for Phase 2
  getDefaultRouter(): Router {
    return this.routers.find(r => r.id === "single_chain_router") || 
           this.routers[0] || 
           new NullRouter();
  }
}
```

### Router Selection Logic

**Phase 2 Logic:**
1. Find routers that support intent's chains
2. If none found, return empty routes
3. Try each router in order
4. Return first router that finds routes
5. If all routers return empty, return empty

**Future (Phase 3+):**
- Try all routers
- Aggregate routes from all routers
- Score and rank routes
- Return top N routes

---

## Dependency Injection

### Router Injection

**Purpose:** Make routers pluggable and testable.

**Android (Kotlin):**
```kotlin
interface RouterProvider {
    fun getRouter(intent: Intent): Router
    fun getAllRouters(): List<Router>
}

class DefaultRouterProvider : RouterProvider {
    private val registry = RouterRegistry()
    
    init {
        registry.register(SingleChainRouter())
        // Add more routers as they're implemented
    }
    
    override fun getRouter(intent: Intent): Router {
        val supported = registry.findSupportedRouters(intent)
        return supported.firstOrNull() ?: NullRouter()
    }
    
    override fun getAllRouters(): List<Router> {
        return registry.getAllRouters()
    }
}
```

**iOS (Swift):**
```swift
protocol RouterProvider {
    func getRouter(for intent: Intent) -> Router
    func getAllRouters() -> [Router]
}

class DefaultRouterProvider: RouterProvider {
    private let registry = RouterRegistry()
    
    init() {
        registry.register(SingleChainRouter())
        // Add more routers as they're implemented
    }
    
    func getRouter(for intent: Intent) -> Router {
        let supported = registry.findSupportedRouters(intent)
        return supported.first ?? NullRouter()
    }
    
    func getAllRouters() -> [Router] {
        return registry.getAllRouters()
    }
}
```

---

## Testing

### Unit Tests

**NullRouter:**
- Always returns empty routes
- Always returns invalid for validation
- Compare always returns 0

**SingleChainRouter:**
- Returns route for same-chain intents
- Returns empty for cross-chain intents
- Returns empty for unsupported chains
- Route structure is correct
- Privacy score is correct (shielded = HIGH)

**RouterRegistry:**
- Registers routers correctly
- Finds supported routers correctly
- Returns default router when no match

### Integration Tests

**Router Selection:**
- Correct router selected for intent
- Fallback to NullRouter when no match
- Multiple routers tried in order

---

## Phase 2 Implementation Plan

### Step 1: Create Interfaces
- Define Router interface
- Define Route structures
- Define supporting types

### Step 2: Implement Stubs
- Implement NullRouter
- Implement SingleChainRouter
- Implement RouterRegistry

### Step 3: Wire Up
- Inject RouterProvider into IntentProcessor
- Use router in route discovery flow
- Add logging

### Step 4: Test
- Unit tests for all stubs
- Integration tests for router selection
- UI tests for route display

---

## Future Enhancements

### Phase 3: Local Routing
- Implement LocalRouter (same-chain swaps)
- Add route scoring
- Add route comparison

### Phase 4: Cross-Chain Routing
- Implement CrossChainRouter
- Add bridge integration
- Add multi-hop routes

### Phase 5: Privacy Routing
- Implement PrivacyRouter wrapper
- Add privacy-aware scoring
- Add shielded route prioritization

---

**Last Updated:** [Current Date]  
**Status:** Design complete — ready for implementation (Phase 2)


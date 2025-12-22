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

Boundary's architecture is organized into distinct layers, each with clear responsibilities:

```
┌─────────────────────────────────────────────────────────────┐
│                    User Interface Layer                     │
│  (Intent Expression: "Send X to Y", "Swap A for B", etc.)  │
└───────────────────────┬─────────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────────┐
│                  Intent Processing Layer                     │
│  • Intent Normalization                                      │
│  • Intent Validation                                         │
│  • Intent-to-Action Mapping (Phase 1-2)                     │
└───────────────────────┬─────────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────────┐
│                    Routing Layer                            │
│  • Route Discovery                                          │
│  • Route Scoring (cost, latency, privacy, trust)            │
│  • Route Selection                                          │
└───────────────────────┬─────────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────────┐
│                 Execution Layer                              │
│  • Execution Provider Selection                             │
│  • Transaction Construction                                 │
│  • Execution Monitoring & Retry                              │
└───────────────────────┬─────────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────────┐
│              Wallet Core (Zashi-derived)                    │
│  • Key Management                                           │
│  • Transaction Signing                                       │
│  • Blockchain Interaction                                    │
└─────────────────────────────────────────────────────────────┘
```

### Component Responsibilities

**User Interface Layer**
- Captures user intent in natural language or structured form
- Displays route options and execution status
- Handles user confirmations and preferences
- **Does NOT** understand routing or execution details

**Intent Processing Layer**
- Normalizes user input into structured `Intent` objects
- Validates intent feasibility (addresses, amounts, assets)
- Maps intents to concrete actions (initially single-chain, later cross-chain)
- **Does NOT** execute transactions

**Routing Layer**
- Discovers possible routes to fulfill an intent
- Scores routes by multiple criteria (privacy, cost, latency, trust)
- Selects optimal route(s) based on user preferences
- **Does NOT** execute transactions or manage keys

**Execution Layer**
- Executes selected route through appropriate provider
- Monitors execution status and handles failures
- Manages retries and rollbacks
- **Does NOT** manage keys or sign transactions

**Wallet Core**
- Manages cryptographic keys and seed phrases
- Signs transactions
- Interacts with blockchain networks
- Maintains wallet state and history
- **Unchanged** from Zashi (protocol logic preserved)

---

## Intent Schema

An `Intent` represents what the user wants to accomplish, independent of how it will be executed.

### Core Intent Structure

```typescript
interface Intent {
  id: string;                    // Unique identifier
  type: IntentType;              // SEND, SWAP, RECEIVE, etc.
  sourceAsset: Asset;            // What user is sending/swapping
  targetAsset: Asset;            // What user wants to receive
  destination: Address;          // Where to send (may be cross-chain)
  amount: Amount;                // How much (source asset)
  metadata?: IntentMetadata;     // Optional: memo, privacy preferences, etc.
  timestamp: number;             // When intent was created
  expiresAt?: number;            // Optional: expiration time
}

interface Asset {
  chain: ChainId;                // e.g., "zcash", "ethereum", "near"
  token: TokenId;                // e.g., "ZEC", "ETH", "USDC"
  address?: string;              // Optional: contract address for tokens
}

interface IntentMetadata {
  memo?: string;                 // User-provided memo
  privacyLevel?: PrivacyLevel;   // HIGH, MEDIUM, LOW
  maxCost?: Amount;              // Maximum acceptable cost
  maxLatency?: number;           // Maximum acceptable time (seconds)
  preferredRoutes?: RouteId[];  // User preferences
}
```

### Intent Types

- **SEND**: Transfer asset X to address Y (may be cross-chain)
- **SWAP**: Exchange asset A for asset B (may be cross-chain)
- **RECEIVE**: Prepare to receive asset X (address generation, etc.)
- **BRIDGE**: Explicitly bridge asset X from chain A to chain B (advanced)

### Intent Normalization

User input is normalized into structured intents:

- "Send 100 ZEC to zaddr..." → `SEND` intent
- "Swap 1 ETH for BTC" → `SWAP` intent
- "I want to receive NEAR" → `RECEIVE` intent

Normalization handles:
- Address format detection and validation
- Asset name resolution (e.g., "Bitcoin" → BTC)
- Amount parsing and validation
- Chain inference from addresses or context

---

## Routing Engine Interface

The routing engine discovers and evaluates possible routes to fulfill an intent.

### Router Interface

```typescript
interface Router {
  // Find all possible routes for an intent
  findRoutes(intent: Intent): Promise<Route[]>;
  
  // Compare two routes by scoring criteria
  compareRoutes(route1: Route, route2: Route, preferences: UserPreferences): number;
  
  // Validate that a route can still be executed
  validateRoute(route: Route): Promise<boolean>;
  
  // Get updated quote for a route (prices may change)
  refreshQuote(route: Route): Promise<Route>;
}

interface Route {
  id: string;                    // Unique route identifier
  intentId: string;              // Intent this route fulfills
  steps: RouteStep[];            // Execution steps (may be multi-hop)
  estimatedCost: Amount;         // Total cost estimate
  estimatedLatency: number;      // Estimated time (seconds)
  privacyScore: PrivacyScore;   // Privacy characteristics
  trustScore: TrustScore;        // Trust assumptions
  expiresAt: number;             // Quote expiration time
}

interface RouteStep {
  type: StepType;                // BRIDGE, SWAP, SEND, etc.
  provider: ProviderId;          // Which provider executes this step
  inputAsset: Asset;
  outputAsset: Asset;
  estimatedCost: Amount;
  estimatedLatency: number;
  trustModel: TrustModel;        // CUSTODIAL, NON_CUSTODIAL, TRUSTLESS
}
```

### Route Scoring

Routes are scored by multiple criteria:

1. **Privacy Score** (HIGH/MEDIUM/LOW)
   - HIGH: Fully shielded/private transactions
   - MEDIUM: Partially private (e.g., shielded input, transparent output)
   - LOW: Transparent/public transactions

2. **Cost Score** (numeric)
   - Total fees + slippage + bridge costs
   - Expressed in user's preferred currency

3. **Latency Score** (seconds)
   - Estimated time from start to completion
   - Includes blockchain confirmation times

4. **Trust Score** (HIGH/MEDIUM/LOW)
   - HIGH: Trustless (no third-party custody)
   - MEDIUM: Non-custodial but requires trust in provider
   - LOW: Custodial (provider holds funds temporarily)

### Route Selection

Routes are ranked by weighted scoring:

```
finalScore = (privacyWeight × privacyScore) +
             (costWeight × (1 / costScore)) +
             (latencyWeight × (1 / latencyScore)) +
             (trustWeight × trustScore)
```

User preferences determine weights (default: privacy-first).

---

## Execution Provider Abstraction

Execution providers handle the actual execution of route steps.

### Execution Provider Interface

```typescript
interface ExecutionProvider {
  id: ProviderId;                // Unique provider identifier
  name: string;                  // Human-readable name
  supportedChains: ChainId[];    // Which chains this provider supports
  trustModel: TrustModel;        // CUSTODIAL, NON_CUSTODIAL, TRUSTLESS
  
  // Check if provider can execute a route step
  canExecute(step: RouteStep): Promise<boolean>;
  
  // Get quote for executing a step
  getQuote(step: RouteStep): Promise<ExecutionQuote>;
  
  // Execute a route step
  execute(step: RouteStep, wallet: WalletContext): Promise<ExecutionResult>;
  
  // Check execution status
  getStatus(executionId: string): Promise<ExecutionStatus>;
  
  // Cancel an in-flight execution (if possible)
  cancel(executionId: string): Promise<boolean>;
}

interface ExecutionResult {
  executionId: string;           // Unique execution identifier
  status: ExecutionStatus;       // PENDING, CONFIRMED, FAILED
  transactionHashes?: string[];  // Blockchain transaction hashes
  error?: ExecutionError;        // Error details if failed
}

interface ExecutionStatus {
  status: ExecutionStatus;       // PENDING, CONFIRMED, FAILED
  progress?: number;             // 0-100 if in progress
  confirmations?: number;        // Blockchain confirmations
  estimatedCompletion?: number;  // Timestamp
}
```

### Execution Flow

1. **Route Selection**: User selects a route (or auto-selects best)
2. **Provider Preparation**: Execution layer prepares wallet context for each step
3. **Step Execution**: Each step is executed sequentially (or in parallel if possible)
4. **Status Monitoring**: Execution layer polls for status updates
5. **Completion/Retry**: On success, proceed to next step; on failure, retry or rollback

### Error Handling

- **Retry Logic**: Automatic retries for transient failures (network, temporary provider issues)
- **Rollback**: If a step fails after previous steps succeeded, attempt rollback where possible
- **User Notification**: Clear error messages explaining what failed and why
- **Partial Success**: Handle cases where some steps succeed but others fail

---

## Privacy & Security Architecture

### Privacy Constraints

**Data Minimization**
- Routers receive only the `Intent` object, not wallet state or keys
- Execution providers receive only the `RouteStep` they need to execute
- No analytics or tracking tied to addresses or transactions

**Privacy-Aware Routing**
- Privacy score is a first-class routing criterion
- Default preference for shielded/private routes
- User can override privacy preferences for cost/latency trade-offs

**Address Isolation**
- Generate new addresses per intent when possible
- Avoid address reuse across intents
- Shielded addresses preferred over transparent addresses

### Security Assumptions

**Router Trust Model**
- Routers are **untrusted** by default
- Routers cannot access wallet keys or full wallet state
- Route validation happens client-side before execution
- User must confirm route selection

**Execution Provider Trust Model**
- Trust model varies by provider (custodial vs. non-custodial)
- Non-custodial providers preferred
- User is warned about custodial providers
- Execution providers cannot access wallet keys (signing happens client-side)

**Wallet Core Security**
- All key management and signing happens in wallet core (unchanged from Zashi)
- No keys leave the device
- Seed phrase management unchanged from Zashi

---

## Modularity & Extensibility

### Pluggable Components

All major components are replaceable:

- **Routers**: Multiple router implementations can coexist
- **Execution Providers**: New providers can be added without code changes
- **Privacy Layers**: Privacy scoring can be enhanced or replaced
- **Quote Engines**: Different quote sources can be integrated

### Configuration

Boundary uses configuration files to specify:
- Which routers to use (and in what order)
- Which execution providers are available
- Default privacy preferences
- Chain-specific settings

This allows customization without code changes.

---

## Implementation Phases

See [Roadmap](./roadmap.md) for detailed phase breakdown:

- **Phase 0**: Foundation (current)
- **Phase 1**: Identity & Stabilization (branding only)
- **Phase 2**: Intent Model Introduction (interfaces, no execution changes)
- **Phase 3**: Routing Engine (local, single-chain)
- **Phase 4**: Cross-Chain Execution (first cross-chain routes)
- **Phase 5**: Privacy Expansion (privacy-first routing)
- **Phase 6**: Hardening & Review (security audit, UX polish)

---

## Future Considerations

**Not Yet Designed** (will be added in later phases):
- Multi-hop routing optimization
- Route caching and prefetching
- User preference profiles
- Advanced privacy features (delayed execution, split routing)
- Bridge aggregation (multiple bridges for same route)


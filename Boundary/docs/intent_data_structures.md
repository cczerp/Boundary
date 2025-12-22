# Boundary Wallet — Intent Data Structures Specification

This document defines the language-agnostic specification for Intent data structures that will be implemented in Android (Kotlin/Java) and iOS (Swift).

**Purpose:** Provide a clear, implementation-ready specification for Intent interfaces before code implementation.

---

## Core Intent Interface

### Intent Base Structure

```typescript
// TypeScript-like specification (language-agnostic)
interface Intent {
  // Unique identifier (UUID v4)
  id: string;
  
  // Intent type
  type: IntentType;
  
  // Source asset (what user is sending/swapping)
  sourceAsset: Asset;
  
  // Target asset (what user wants to receive)
  targetAsset: Asset;
  
  // Destination address (where to send)
  destination: Address;
  
  // Amount (in source asset units)
  amount: Amount;
  
  // Optional metadata
  metadata?: IntentMetadata;
  
  // Timestamp when intent was created (Unix epoch, milliseconds)
  timestamp: number;
  
  // Optional expiration time (Unix epoch, milliseconds)
  expiresAt?: number;
  
  // Intent status
  status: IntentStatus;
}
```

### Intent Types

```typescript
enum IntentType {
  SEND = "SEND",           // Transfer asset to address
  SWAP = "SWAP",           // Exchange one asset for another
  RECEIVE = "RECEIVE",     // Prepare to receive asset (address generation)
  BRIDGE = "BRIDGE"        // Explicitly bridge asset (advanced)
}
```

### Asset Structure

```typescript
interface Asset {
  // Chain identifier (e.g., "zcash", "bitcoin", "ethereum", "near")
  chain: ChainId;
  
  // Token identifier (e.g., "ZEC", "BTC", "ETH", "USDC")
  token: TokenId;
  
  // Optional: Contract address for tokens (EVM chains)
  contractAddress?: string;
  
  // Optional: Token decimals (for display)
  decimals?: number;
}

type ChainId = string;  // Standardized chain identifiers
type TokenId = string;  // Standardized token identifiers
```

### Address Structure

```typescript
interface Address {
  // Address string (chain-specific format)
  value: string;
  
  // Chain identifier
  chain: ChainId;
  
  // Address type
  type: AddressType;
  
  // Optional: Address label/name
  label?: string;
}

enum AddressType {
  SHIELDED = "SHIELDED",       // Zcash shielded address
  TRANSPARENT = "TRANSPARENT", // Transparent address
  CONTRACT = "CONTRACT",       // Smart contract address
  ACCOUNT = "ACCOUNT"          // Account-based address (Ethereum, NEAR)
}
```

### Amount Structure

```typescript
interface Amount {
  // Amount in canonical units (smallest unit: satoshis, wei, zatoshis)
  value: string;  // Use string to avoid precision loss
  
  // Display amount (user-friendly units)
  displayValue: string;
  
  // Currency symbol for display
  currency: string;
  
  // Number of decimals
  decimals: number;
}
```

### Intent Metadata

```typescript
interface IntentMetadata {
  // Optional memo field
  memo?: string;
  
  // Privacy preference
  privacyLevel?: PrivacyLevel;
  
  // Maximum acceptable cost
  maxCost?: Amount;
  
  // Maximum acceptable latency (seconds)
  maxLatency?: number;
  
  // Preferred route IDs (if user has preferences)
  preferredRoutes?: string[];
  
  // Blocked route IDs (if user wants to exclude)
  blockedRoutes?: string[];
  
  // User notes (internal, not sent to routers)
  notes?: string;
}

enum PrivacyLevel {
  HIGH = "HIGH",     // Prefer shielded/private routes
  MEDIUM = "MEDIUM", // Balance privacy and cost
  LOW = "LOW"        // Prefer cheapest/fastest routes
}
```

### Intent Status

```typescript
enum IntentStatus {
  CREATED = "CREATED",           // Intent created, not yet processed
  NORMALIZING = "NORMALIZING",   // Being normalized
  VALIDATING = "VALIDATING",     // Being validated
  ROUTING = "ROUTING",           // Finding routes
  ROUTE_SELECTED = "ROUTE_SELECTED", // Route selected, ready to execute
  EXECUTING = "EXECUTING",       // Currently executing
  COMPLETED = "COMPLETED",       // Successfully completed
  FAILED = "FAILED",             // Execution failed
  CANCELLED = "CANCELLED"        // User cancelled
}
```

---

## Intent Normalizer Interface

### IntentNormalizer Interface

```typescript
interface IntentNormalizer {
  // Normalize user input into structured Intent
  normalize(input: UserInput): Promise<Intent>;
  
  // Validate normalized intent
  validate(intent: Intent): Promise<ValidationResult>;
  
  // Parse specific intent type from input
  parseSend(input: string): Promise<SendIntent>;
  parseSwap(input: string): Promise<SwapIntent>;
  parseReceive(input: string): Promise<ReceiveIntent>;
}

interface UserInput {
  // Raw user input (text or structured)
  text?: string;
  
  // Structured input (if available)
  structured?: {
    action: string;
    amount?: string;
    asset?: string;
    destination?: string;
  };
  
  // Context (user's current wallet state, if available)
  context?: UserContext;
}

interface UserContext {
  // User's available assets
  availableAssets?: Asset[];
  
  // User's default chain
  defaultChain?: ChainId;
  
  // User's privacy preferences
  defaultPrivacyLevel?: PrivacyLevel;
}

interface ValidationResult {
  isValid: boolean;
  errors?: ValidationError[];
  warnings?: ValidationWarning[];
}

interface ValidationError {
  field: string;
  message: string;
  code: string;
}

interface ValidationWarning {
  field: string;
  message: string;
  code: string;
}
```

---

## Intent Serialization

### Serialization Requirements

**Format:** JSON (for debugging, logging, future features)

**Required Fields:**
- All non-optional fields must be serializable
- Optional fields can be omitted if null/undefined
- Timestamps as Unix epoch milliseconds (number)
- Amounts as strings (to preserve precision)

**Example JSON:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "type": "SEND",
  "sourceAsset": {
    "chain": "zcash",
    "token": "ZEC"
  },
  "targetAsset": {
    "chain": "zcash",
    "token": "ZEC"
  },
  "destination": {
    "value": "zaddr1...",
    "chain": "zcash",
    "type": "SHIELDED"
  },
  "amount": {
    "value": "10000000000",
    "displayValue": "100",
    "currency": "ZEC",
    "decimals": 8
  },
  "metadata": {
    "privacyLevel": "HIGH",
    "memo": "Payment for services"
  },
  "timestamp": 1699123456789,
  "status": "CREATED"
}
```

### Deserialization Requirements

- Must validate all fields on deserialization
- Must handle missing optional fields gracefully
- Must validate address formats and checksums
- Must validate amounts (non-negative, reasonable bounds)

---

## Platform-Specific Implementations

### Android (Kotlin)

**Package:** `com.electriccoinco.zashi.intent` (preserve upstream package)

**Data Classes:**
```kotlin
data class Intent(
    val id: String,
    val type: IntentType,
    val sourceAsset: Asset,
    val targetAsset: Asset,
    val destination: Address,
    val amount: Amount,
    val metadata: IntentMetadata? = null,
    val timestamp: Long,
    val expiresAt: Long? = null,
    val status: IntentStatus
)

enum class IntentType {
    SEND, SWAP, RECEIVE, BRIDGE
}

// ... other data classes
```

**Serialization:** Use Kotlinx Serialization or Gson

### iOS (Swift)

**Module:** `ZashiIntent` (preserve upstream module)

**Structs:**
```swift
struct Intent: Codable {
    let id: String
    let type: IntentType
    let sourceAsset: Asset
    let targetAsset: Asset
    let destination: Address
    let amount: Amount
    let metadata: IntentMetadata?
    let timestamp: Int64
    let expiresAt: Int64?
    let status: IntentStatus
}

enum IntentType: String, Codable {
    case send = "SEND"
    case swap = "SWAP"
    case receive = "RECEIVE"
    case bridge = "BRIDGE"
}

// ... other structs
```

**Serialization:** Use Swift `Codable` protocol

---

## Validation Rules

### Intent Validation

**Required Fields:**
- `id`: Must be valid UUID v4
- `type`: Must be valid IntentType
- `sourceAsset`: Must have valid chain and token
- `targetAsset`: Must have valid chain and token
- `destination`: Must have valid address format and checksum
- `amount`: Must be positive, within reasonable bounds
- `timestamp`: Must be valid Unix timestamp

**Address Validation:**
- Format validation (chain-specific)
- Checksum validation
- Chain compatibility (address chain must match asset chain for SEND)

**Amount Validation:**
- Non-negative
- Non-zero (unless explicitly allowed)
- Within reasonable bounds (e.g., < 21M BTC)
- Respects asset decimals

**Metadata Validation:**
- Memo length within chain limits (Zcash: 512 bytes)
- Privacy level valid enum value
- Max cost/latency reasonable values

---

## Error Handling

### Intent Creation Errors

**InvalidInputError:**
- User input cannot be parsed
- Missing required fields
- Invalid format

**ValidationError:**
- Intent fails validation
- Address invalid
- Amount invalid

**NormalizationError:**
- Ambiguous input (needs user clarification)
- Unsupported intent type
- Chain not supported

---

## Testing Requirements

### Unit Tests

**Intent Creation:**
- Valid intents (all types)
- Invalid intents (missing fields, invalid formats)
- Edge cases (zero amounts, very large amounts)

**Serialization:**
- Serialize to JSON
- Deserialize from JSON
- Round-trip (serialize → deserialize → compare)

**Validation:**
- Valid intents pass validation
- Invalid intents fail with clear errors
- Edge cases handled correctly

---

## Migration Path

### Phase 2 (Current)
- Define interfaces (this document)
- Create stub implementations
- Add unit tests (mock data)

### Phase 3
- Implement IntentNormalizer
- Wire up intent creation from UI
- Add logging

### Phase 4+
- Extend with cross-chain support
- Add advanced metadata
- Optimize serialization

---

**Last Updated:** [Current Date]  
**Status:** Specification complete — ready for implementation (Phase 2)


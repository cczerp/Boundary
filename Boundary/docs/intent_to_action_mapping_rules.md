# Boundary Wallet — Intent-to-Action Mapping Rules

This document defines the rules for mapping Boundary Intent objects to Zashi actions.

**Purpose:** Provide clear, unambiguous mapping rules before implementation.

**Status:** Design document — requires Zashi action type research before implementation.

---

## Mapping Principles

### 1. One-Way Mapping

**Principle:** Intents map to actions, but not actions to intents.

**Rationale:**
- Intents are user-facing abstractions
- Actions are implementation details
- No need for reverse mapping

### 2. Preserve Intent Semantics

**Principle:** Mapping must preserve the user's intent.

**Rationale:**
- User wants to send X to Y → action must send X to Y
- User wants to swap A for B → action must swap A for B
- Mapping should not change user's intent

### 3. Fail Fast on Unsupported Intents

**Principle:** Unsupported intents should fail immediately with clear errors.

**Rationale:**
- Better to fail early than execute incorrectly
- Clear errors help users understand limitations
- Prevents partial execution

---

## Mapping Rules by Intent Type

### SEND Intent Mapping

#### Same-Chain Send (Phase 2)

**Zcash → Zcash (Shielded):**

```typescript
if (intent.type === "SEND" &&
    intent.sourceAsset.chain === "zcash" &&
    intent.targetAsset.chain === "zcash" &&
    intent.destination.type === "SHIELDED") {
  
  return {
    type: "SEND_SHIELDED",  // TBD: Actual Zashi action type
    parameters: {
      toAddress: intent.destination.value,
      amount: intent.amount.value,  // In zatoshis
      memo: intent.metadata?.memo || ""
    }
  };
}
```

**Zcash → Zcash (Transparent):**

```typescript
if (intent.type === "SEND" &&
    intent.sourceAsset.chain === "zcash" &&
    intent.targetAsset.chain === "zcash" &&
    intent.destination.type === "TRANSPARENT") {
  
  return {
    type: "SEND_TRANSPARENT",  // TBD: Actual Zashi action type
    parameters: {
      toAddress: intent.destination.value,
      amount: intent.amount.value  // In zatoshis
      // Note: No memo for transparent transactions
    }
  };
}
```

**Cross-Chain Send (Phase 4+):**

```typescript
if (intent.type === "SEND" &&
    intent.sourceAsset.chain !== intent.targetAsset.chain) {
  
  // Phase 2: Not supported
  throw new UnsupportedIntentError(
    "Cross-chain sends not supported in Phase 2"
  );
  
  // Phase 4+: Will require bridge integration
  // TBD: Map to bridge action
}
```

---

### SWAP Intent Mapping

#### Same-Chain Swap (Phase 2)

**Zcash → Zcash Swap:**

```typescript
if (intent.type === "SWAP" &&
    intent.sourceAsset.chain === "zcash" &&
    intent.targetAsset.chain === "zcash" &&
    intent.sourceAsset.token !== intent.targetAsset.token) {
  
  // Check if Zashi supports swaps
  if (!zashiSupportsSwaps()) {
    throw new UnsupportedIntentError(
      "Swaps not supported in Zashi"
    );
  }
  
  return {
    type: "SWAP",  // TBD: Actual Zashi action type
    parameters: {
      fromAsset: intent.sourceAsset.token,
      toAsset: intent.targetAsset.token,
      amount: intent.amount.value,
      destination: intent.destination.value  // Where to send swapped asset
    }
  };
}
```

**Cross-Chain Swap (Phase 4+):**

```typescript
if (intent.type === "SWAP" &&
    intent.sourceAsset.chain !== intent.targetAsset.chain) {
  
  // Phase 2: Not supported
  throw new UnsupportedIntentError(
    "Cross-chain swaps not supported in Phase 2"
  );
  
  // Phase 4+: Will require bridge + swap integration
  // TBD: Map to bridge + swap actions
}
```

---

### RECEIVE Intent Mapping

#### Address Generation

**Shielded Address:**

```typescript
if (intent.type === "RECEIVE" &&
    intent.metadata?.privacyLevel === "HIGH") {
  
  return {
    type: "GENERATE_SHIELDED_ADDRESS",  // TBD: Actual Zashi action type
    parameters: {
      chain: intent.targetAsset.chain
    }
  };
}
```

**Transparent Address:**

```typescript
if (intent.type === "RECEIVE" &&
    intent.metadata?.privacyLevel !== "HIGH") {
  
  return {
    type: "GENERATE_TRANSPARENT_ADDRESS",  // TBD: Actual Zashi action type
    parameters: {
      chain: intent.targetAsset.chain
    }
  };
}
```

---

## Parameter Mapping

### Address Mapping

**Intent Address → Zashi Address:**
- `intent.destination.value` → `action.parameters.toAddress`
- Address format must match Zashi expectations
- Address validation happens before mapping

### Amount Mapping

**Intent Amount → Zashi Amount:**
- `intent.amount.value` → `action.parameters.amount`
- Amount must be in canonical units (zatoshis for ZEC)
- Amount validation happens before mapping

### Memo Mapping

**Intent Memo → Zashi Memo:**
- `intent.metadata.memo` → `action.parameters.memo`
- Only for shielded transactions
- Memo length validation (512 bytes for Zcash)

---

## Error Handling

### Unsupported Intent Types

**Error:** `UnsupportedIntentError`

**Cases:**
- Cross-chain intents (Phase 2)
- Unsupported asset pairs
- Unsupported address types
- Missing required parameters

**Response:**
```typescript
throw new UnsupportedIntentError(
  "Intent type not supported: " + intent.type,
  {
    intentId: intent.id,
    reason: "CROSS_CHAIN_NOT_SUPPORTED"
  }
);
```

### Invalid Parameters

**Error:** `InvalidParameterError`

**Cases:**
- Invalid address format
- Invalid amount
- Invalid memo length
- Missing required parameters

**Response:**
```typescript
throw new InvalidParameterError(
  "Invalid parameter: " + parameterName,
  {
    intentId: intent.id,
    parameter: parameterName,
    value: parameterValue
  }
);
```

---

## Mapping Validation

### Pre-Mapping Checks

**Required:**
1. Intent is valid (passed validation)
2. Intent type is supported
3. Source and target chains are supported
4. Required parameters are present

**Validation:**
```typescript
async function canMap(intent: Intent): Promise<boolean> {
  // Check intent validity
  const validation = await validateIntent(intent);
  if (!validation.isValid) {
    return false;
  }
  
  // Check intent type support
  if (!isSupportedIntentType(intent.type)) {
    return false;
  }
  
  // Check chain support
  if (!isSupportedChain(intent.sourceAsset.chain) ||
      !isSupportedChain(intent.targetAsset.chain)) {
    return false;
  }
  
  // Check parameter completeness
  if (!hasRequiredParameters(intent)) {
    return false;
  }
  
  return true;
}
```

---

## Implementation Stub

### Phase 2 Stub Implementation

```typescript
class StubIntentMapper implements IntentMapper {
  async mapToAction(intent: Intent): Promise<ZashiAction> {
    // Check if can map
    if (!await this.canMap(intent)) {
      throw new UnsupportedIntentError("Intent not supported");
    }
    
    // Map based on type
    switch (intent.type) {
      case "SEND":
        return this.mapSend(intent);
      case "SWAP":
        return this.mapSwap(intent);
      case "RECEIVE":
        return this.mapReceive(intent);
      default:
        throw new UnsupportedIntentError(
          `Intent type ${intent.type} not supported`
        );
    }
  }
  
  private mapSend(intent: Intent): ZashiAction {
    // Single-chain Zcash only (Phase 2)
    if (intent.sourceAsset.chain !== "zcash" ||
        intent.targetAsset.chain !== "zcash") {
      throw new UnsupportedIntentError(
        "Cross-chain sends not supported in Phase 2"
      );
    }
    
    // Map based on address type
    if (intent.destination.type === "SHIELDED") {
      return {
        type: "SEND_SHIELDED",  // TBD: Actual type
        parameters: {
          toAddress: intent.destination.value,
          amount: intent.amount.value,
          memo: intent.metadata?.memo || ""
        }
      };
    } else {
      return {
        type: "SEND_TRANSPARENT",  // TBD: Actual type
        parameters: {
          toAddress: intent.destination.value,
          amount: intent.amount.value
        }
      };
    }
  }
  
  // ... other mapping methods
}
```

---

## Testing Requirements

### Unit Tests

**Valid Mappings:**
- SEND intent → SEND_SHIELDED action
- SEND intent → SEND_TRANSPARENT action
- RECEIVE intent → GENERATE_ADDRESS action

**Invalid Mappings:**
- Cross-chain intents → UnsupportedIntentError
- Unsupported asset pairs → UnsupportedIntentError
- Missing parameters → InvalidParameterError

**Edge Cases:**
- Zero amounts
- Very large amounts
- Invalid addresses
- Missing metadata

---

## Future Enhancements

### Phase 3: Same-Chain Swaps
- Add swap mapping (if Zashi supports)
- Add swap parameter mapping
- Add swap validation

### Phase 4: Cross-Chain Mapping
- Add bridge action mapping
- Add multi-step action mapping
- Add cross-chain validation

### Phase 5: Privacy-Aware Mapping
- Prefer shielded actions
- Privacy preference handling
- Address isolation per intent

---

## Research Dependencies

**Before Implementation:**
- [ ] Research Zashi action types (see `zashi_action_types_research.md`)
- [ ] Document Zashi action parameters
- [ ] Document Zashi execution flow
- [ ] Update mapping rules with actual types

---

**Last Updated:** [Current Date]  
**Status:** Design complete — requires Zashi research before implementation


# Boundary Wallet — Intent Mapper Design

This document defines the design for mapping Intent objects to Zashi actions without changing execution logic.

**Purpose:** Bridge the gap between Boundary's intent model and Zashi's existing action system.

---

## Overview

The IntentMapper translates Boundary's `Intent` objects into Zashi's existing action types, allowing Boundary to express intents while executing through Zashi's proven execution paths.

**Key Principle:** Intents are one-way. We map intents → actions, but not actions → intents.

---

## IntentMapper Interface

### Core Interface

```typescript
interface IntentMapper {
  // Map an Intent to a ZashiAction (or equivalent)
  mapToAction(intent: Intent): Promise<ZashiAction>;
  
  // Check if an intent can be mapped to an action
  canMap(intent: Intent): Promise<boolean>;
  
  // Get mapping explanation (for debugging/logging)
  explainMapping(intent: Intent): Promise<MappingExplanation>;
}
```

### ZashiAction Types

**Note:** These are placeholder types. Actual Zashi action types need to be researched from upstream codebase.

```typescript
// Placeholder - needs to be updated with actual Zashi types
interface ZashiAction {
  type: ZashiActionType;
  parameters: ActionParameters;
  metadata?: ActionMetadata;
}

enum ZashiActionType {
  SEND_TRANSPARENT = "SEND_TRANSPARENT",
  SEND_SHIELDED = "SEND_SHIELDED",
  SWAP = "SWAP",  // If Zashi supports swaps
  RECEIVE = "RECEIVE"
}

interface ActionParameters {
  // Action-specific parameters
  // Structure depends on Zashi implementation
  [key: string]: any;
}
```

---

## Mapping Rules

### SEND Intent → ZashiAction

**Single-Chain Send (Phase 2):**

```typescript
// Zcash → Zcash (shielded)
if (intent.sourceAsset.chain === "zcash" && 
    intent.targetAsset.chain === "zcash" &&
    intent.destination.type === "SHIELDED") {
  return {
    type: "SEND_SHIELDED",
    parameters: {
      toAddress: intent.destination.value,
      amount: intent.amount.value,
      memo: intent.metadata?.memo
    }
  };
}

// Zcash → Zcash (transparent)
if (intent.sourceAsset.chain === "zcash" && 
    intent.targetAsset.chain === "zcash" &&
    intent.destination.type === "TRANSPARENT") {
  return {
    type: "SEND_TRANSPARENT",
    parameters: {
      toAddress: intent.destination.value,
      amount: intent.amount.value
    }
  };
}
```

**Cross-Chain Send (Phase 4+):**
- Not supported in Phase 2
- Will require bridge integration

### SWAP Intent → ZashiAction

**Same-Chain Swap (Phase 2):**

```typescript
// Zcash → Zcash swap (if Zashi supports)
if (intent.sourceAsset.chain === "zcash" && 
    intent.targetAsset.chain === "zcash" &&
    intent.sourceAsset.token !== intent.targetAsset.token) {
  // Check if Zashi supports swaps
  // If not, return error or mark as unsupported
  return {
    type: "SWAP",
    parameters: {
      fromAsset: intent.sourceAsset.token,
      toAsset: intent.targetAsset.token,
      amount: intent.amount.value
    }
  };
}
```

**Cross-Chain Swap (Phase 4+):**
- Not supported in Phase 2
- Will require bridge + swap integration

### RECEIVE Intent → ZashiAction

**Address Generation:**

```typescript
if (intent.type === "RECEIVE") {
  return {
    type: "RECEIVE",
    parameters: {
      chain: intent.targetAsset.chain,
      addressType: intent.metadata?.privacyLevel === "HIGH" ? "SHIELDED" : "TRANSPARENT"
    }
  };
}
```

---

## Mapping Validation

### Pre-Mapping Checks

**Can Map Check:**
1. Intent is valid (passed validation)
2. Source and target chains are supported
3. Action type is supported by Zashi
4. Required parameters are available

**Unsupported Cases:**
- Cross-chain intents (Phase 2)
- Unsupported asset pairs
- Unsupported address types
- Missing required parameters

### Error Handling

**UnsupportedIntentError:**
- Intent cannot be mapped (cross-chain, unsupported assets)
- Clear error message explaining why

**MissingParameterError:**
- Required parameter missing from intent
- Suggestion for how to fix

**InvalidMappingError:**
- Mapping logic error (should not happen)
- Log for debugging

---

## Stub Implementation

### Phase 2 Stub

**Initial Implementation:**
- Only supports single-chain Zcash sends (shielded and transparent)
- Returns error for unsupported intents
- Logs all mapping attempts

**Example:**
```typescript
class StubIntentMapper implements IntentMapper {
  async mapToAction(intent: Intent): Promise<ZashiAction> {
    // Log mapping attempt
    logger.debug("Mapping intent", { intentId: intent.id, type: intent.type });
    
    // Check if can map
    if (!await this.canMap(intent)) {
      throw new UnsupportedIntentError("Intent not supported in Phase 2");
    }
    
    // Map based on type
    switch (intent.type) {
      case "SEND":
        return this.mapSend(intent);
      case "RECEIVE":
        return this.mapReceive(intent);
      default:
        throw new UnsupportedIntentError(`Intent type ${intent.type} not supported`);
    }
  }
  
  private mapSend(intent: Intent): ZashiAction {
    // Single-chain Zcash only
    if (intent.sourceAsset.chain !== "zcash" || 
        intent.targetAsset.chain !== "zcash") {
      throw new UnsupportedIntentError("Cross-chain sends not supported in Phase 2");
    }
    
    if (intent.destination.type === "SHIELDED") {
      return {
        type: "SEND_SHIELDED",
        parameters: {
          toAddress: intent.destination.value,
          amount: intent.amount.value,
          memo: intent.metadata?.memo
        }
      };
    } else {
      return {
        type: "SEND_TRANSPARENT",
        parameters: {
          toAddress: intent.destination.value,
          amount: intent.amount.value
        }
      };
    }
  }
}
```

---

## Logging and Tracing

### Mapping Logs

**Log Events:**
- Intent received for mapping
- Mapping successful
- Mapping failed (with reason)
- Unsupported intent type

**Log Data (Non-Sensitive):**
- Intent ID
- Intent type
- Source/target chains
- Mapping result (success/failure)
- Error codes (if failed)

**Do NOT Log:**
- Full addresses (use hashed/truncated)
- Full amounts (use ranges or hashed)
- Memo content
- User identifiers

---

## Future Enhancements

### Phase 3 (Local Routing)
- Support same-chain swaps (if Zashi supports)
- Support multiple route options
- Map routes to actions (not just intents)

### Phase 4 (Cross-Chain)
- Support cross-chain intents
- Map multi-step routes to actions
- Handle bridge actions

### Phase 5 (Privacy)
- Privacy-aware mapping (prefer shielded)
- Privacy preference handling
- Address isolation per intent

---

## Testing Requirements

### Unit Tests

**Valid Mappings:**
- SEND intent → SEND_SHIELDED action
- SEND intent → SEND_TRANSPARENT action
- RECEIVE intent → RECEIVE action

**Invalid Mappings:**
- Cross-chain intents (should fail)
- Unsupported asset pairs (should fail)
- Missing parameters (should fail)

**Edge Cases:**
- Zero amounts
- Very large amounts
- Invalid addresses
- Missing metadata

---

## Research Needed

**Before Implementation:**
1. **Zashi Action Types:** Research actual Zashi action types from upstream codebase
2. **Zashi Parameters:** Understand parameter structure for each action type
3. **Zashi Swap Support:** Does Zashi support swaps? If so, how?
4. **Zashi Address Types:** What address types does Zashi support?

**Action Items:**
- [ ] Review Zashi Android codebase for action types
- [ ] Review Zashi iOS codebase for action types
- [ ] Document Zashi action API
- [ ] Create Zashi action type definitions

---

**Last Updated:** [Current Date]  
**Status:** Design complete — requires Zashi action type research before implementation


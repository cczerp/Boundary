# Boundary Wallet — Zashi Action Types Research

This document outlines what needs to be researched from the Zashi codebase to understand action types and execution flow.

**Purpose:** Guide research into Zashi's action system before implementing IntentMapper.

**Status:** Research plan — to be completed once Android/iOS repos are forked.

---

## Research Objectives

### Primary Objectives

1. **Identify Zashi Action Types**
   - What action types does Zashi support?
   - How are actions structured?
   - What parameters do actions require?

2. **Understand Execution Flow**
   - How are actions executed?
   - What is the execution pipeline?
   - How are transactions constructed?

3. **Map Intent → Action**
   - How to map Boundary intents to Zashi actions?
   - What are the parameter mappings?
   - What are the limitations?

---

## Research Areas

### Android Codebase

**Key Files to Review:**

1. **Action Types:**
   - `app/src/main/java/com/electriccoinco/zashi/action/` (if exists)
   - `app/src/main/java/com/electriccoinco/zashi/transaction/` (if exists)
   - Look for action classes, enums, or interfaces

2. **Transaction Building:**
   - `app/src/main/java/com/electriccoinco/zashi/wallet/` (if exists)
   - Look for transaction builder classes
   - Look for send/receive transaction methods

3. **UI Actions:**
   - `app/src/main/java/com/electriccoinco/zashi/ui/send/` (if exists)
   - Look for send screen implementation
   - Look for how user input becomes transactions

**Search Terms:**
- "SendTransaction"
- "TransactionBuilder"
- "Action"
- "Send"
- "Receive"
- "Shielded"
- "Transparent"

---

### iOS Codebase

**Key Files to Review:**

1. **Action Types:**
   - `Zashi/Models/Action.swift` (if exists)
   - `Zashi/Models/Transaction.swift` (if exists)
   - Look for action structs, enums, or protocols

2. **Transaction Building:**
   - `Zashi/Wallet/` (if exists)
   - Look for transaction builder classes
   - Look for send/receive transaction methods

3. **UI Actions:**
   - `Zashi/UI/Send/` (if exists)
   - Look for send screen implementation
   - Look for how user input becomes transactions

**Search Terms:**
- "SendTransaction"
- "TransactionBuilder"
- "Action"
- "Send"
- "Receive"
- "Shielded"
- "Transparent"

---

## Expected Action Types

### Based on Zcash Functionality

**Likely Action Types:**

1. **Send Shielded Transaction**
   - Send ZEC to shielded address (zaddr)
   - Parameters: toAddress, amount, memo
   - Privacy: HIGH

2. **Send Transparent Transaction**
   - Send ZEC to transparent address (t1/t3)
   - Parameters: toAddress, amount
   - Privacy: LOW

3. **Receive (Address Generation)**
   - Generate new shielded or transparent address
   - Parameters: addressType (shielded/transparent)
   - Privacy: HIGH if shielded

**Possible Action Types:**

4. **Shield Funds**
   - Convert transparent ZEC to shielded ZEC
   - Parameters: amount
   - Privacy: HIGH

5. **Unshield Funds**
   - Convert shielded ZEC to transparent ZEC
   - Parameters: amount
   - Privacy: LOW

**Unlikely (Phase 2):**

6. **Swap**
   - Exchange one asset for another
   - May not be supported in Zashi
   - Requires research

---

## Research Checklist

### Phase 1: Discovery

- [ ] Fork Android repository
- [ ] Fork iOS repository
- [ ] Review repository structure
- [ ] Identify action-related files
- [ ] Identify transaction-related files

### Phase 2: Analysis

- [ ] Document action types (Android)
- [ ] Document action types (iOS)
- [ ] Document action parameters
- [ ] Document execution flow
- [ ] Document transaction building

### Phase 3: Mapping

- [ ] Map SEND intent → Zashi action
- [ ] Map SWAP intent → Zashi action (if supported)
- [ ] Map RECEIVE intent → Zashi action
- [ ] Document parameter mappings
- [ ] Document limitations

### Phase 4: Documentation

- [ ] Create Zashi action type definitions
- [ ] Create mapping rules document
- [ ] Create implementation guide
- [ ] Update IntentMapper design

---

## Research Questions

### Action Types

1. **What action types exist?**
   - Are there explicit action types/enums?
   - Or are actions implicit in transaction types?

2. **How are actions structured?**
   - Are actions classes/structs?
   - Or are they just method calls?

3. **What parameters do actions require?**
   - Address format?
   - Amount format?
   - Memo format?

### Execution Flow

4. **How are actions executed?**
   - Direct method calls?
   - Action queue?
   - Event-driven?

5. **How are transactions constructed?**
   - Transaction builder pattern?
   - Direct transaction creation?
   - SDK methods?

6. **How is signing handled?**
   - Where does signing happen?
   - What keys are used?
   - How is security ensured?

### Mapping

7. **How to map SEND intent?**
   - Shielded send → which action?
   - Transparent send → which action?

8. **How to map SWAP intent?**
   - Does Zashi support swaps?
   - If not, how to handle?

9. **How to map RECEIVE intent?**
   - Address generation → which action?
   - How to specify address type?

---

## Documentation Template

### Action Type Definition

```typescript
// To be filled after research
interface ZashiAction {
  type: ZashiActionType;  // TBD: What are the types?
  parameters: {
    // TBD: What are the parameters?
  };
}
```

### Mapping Example

```typescript
// To be filled after research
function mapSendIntent(intent: Intent): ZashiAction {
  // TBD: How to map?
  return {
    type: "SEND_SHIELDED",  // TBD: Actual type?
    parameters: {
      // TBD: Actual parameters?
    }
  };
}
```

---

## Next Steps

1. **Fork Repositories** (Phase 0)
2. **Review Codebase** (Phase 1)
3. **Document Findings** (Phase 2)
4. **Create Mappings** (Phase 3)
5. **Implement IntentMapper** (Phase 2)

---

**Last Updated:** [Current Date]  
**Status:** Research plan — awaiting repository forks


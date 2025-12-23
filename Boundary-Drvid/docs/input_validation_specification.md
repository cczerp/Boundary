# Boundary Wallet — Input Validation Specification

This document defines the validation rules for user input and Intent objects.

**Purpose:** Ensure data integrity and prevent malformed intents from causing issues downstream.

---

## Validation Layers

### Layer 1: User Input Validation

**Purpose:** Validate raw user input before normalization.

**Checks:**
- Input is not empty
- Input format is parseable
- Required fields are present
- Input length is reasonable

**Errors:**
- `EMPTY_INPUT`: User input is empty
- `INVALID_FORMAT`: Input cannot be parsed
- `MISSING_FIELD`: Required field is missing
- `INPUT_TOO_LONG`: Input exceeds maximum length

---

### Layer 2: Intent Structure Validation

**Purpose:** Validate Intent object structure after normalization.

**Checks:**
- All required fields are present
- Field types are correct
- Field formats are valid
- Field values are within bounds

**Errors:**
- `MISSING_REQUIRED_FIELD`: Required field is missing
- `INVALID_FIELD_TYPE`: Field type is incorrect
- `INVALID_FIELD_FORMAT`: Field format is invalid
- `FIELD_OUT_OF_BOUNDS`: Field value is out of bounds

---

### Layer 3: Intent Semantic Validation

**Purpose:** Validate Intent semantics (business logic).

**Checks:**
- Address format matches chain
- Amount is valid for asset
- Chain compatibility
- Asset existence

**Errors:**
- `INVALID_ADDRESS_FORMAT`: Address format is invalid
- `ADDRESS_CHAIN_MISMATCH`: Address chain doesn't match asset chain
- `INVALID_AMOUNT`: Amount is invalid (negative, zero, too large)
- `UNSUPPORTED_CHAIN`: Chain is not supported
- `UNSUPPORTED_ASSET`: Asset is not supported

---

## Field Validation Rules

### Intent ID

**Format:** UUID v4

**Validation:**
```typescript
function validateIntentId(id: string): ValidationResult {
  const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
  if (!uuidRegex.test(id)) {
    return { isValid: false, error: "INVALID_INTENT_ID" };
  }
  return { isValid: true };
}
```

**Errors:**
- `INVALID_INTENT_ID`: Not a valid UUID v4

---

### Intent Type

**Values:** SEND, SWAP, RECEIVE, BRIDGE

**Validation:**
```typescript
function validateIntentType(type: string): ValidationResult {
  const validTypes = ["SEND", "SWAP", "RECEIVE", "BRIDGE"];
  if (!validTypes.includes(type)) {
    return { isValid: false, error: "INVALID_INTENT_TYPE" };
  }
  return { isValid: true };
}
```

**Errors:**
- `INVALID_INTENT_TYPE`: Not a valid intent type

---

### Asset

**Structure:**
- `chain`: ChainId (required)
- `token`: TokenId (required)
- `contractAddress`: string (optional, for tokens)
- `decimals`: number (optional)

**Validation:**
```typescript
function validateAsset(asset: Asset): ValidationResult {
  // Chain validation
  if (!asset.chain || asset.chain.trim() === "") {
    return { isValid: false, error: "MISSING_CHAIN" };
  }
  
  const supportedChains = ["zcash", "bitcoin", "ethereum", "near"];
  if (!supportedChains.includes(asset.chain)) {
    return { isValid: false, error: "UNSUPPORTED_CHAIN" };
  }
  
  // Token validation
  if (!asset.token || asset.token.trim() === "") {
    return { isValid: false, error: "MISSING_TOKEN" };
  }
  
  // Contract address validation (if provided)
  if (asset.contractAddress) {
    if (!isValidContractAddress(asset.contractAddress, asset.chain)) {
      return { isValid: false, error: "INVALID_CONTRACT_ADDRESS" };
    }
  }
  
  // Decimals validation (if provided)
  if (asset.decimals !== undefined) {
    if (asset.decimals < 0 || asset.decimals > 18) {
      return { isValid: false, error: "INVALID_DECIMALS" };
    }
  }
  
  return { isValid: true };
}
```

**Errors:**
- `MISSING_CHAIN`: Chain is missing
- `UNSUPPORTED_CHAIN`: Chain is not supported
- `MISSING_TOKEN`: Token is missing
- `INVALID_CONTRACT_ADDRESS`: Contract address is invalid
- `INVALID_DECIMALS`: Decimals out of range (0-18)

---

### Address

**Structure:**
- `value`: string (required)
- `chain`: ChainId (required)
- `type`: AddressType (required)
- `label`: string (optional)

**Validation:**
```typescript
function validateAddress(address: Address): ValidationResult {
  // Value validation
  if (!address.value || address.value.trim() === "") {
    return { isValid: false, error: "MISSING_ADDRESS_VALUE" };
  }
  
  // Chain validation
  if (!address.chain || address.chain.trim() === "") {
    return { isValid: false, error: "MISSING_ADDRESS_CHAIN" };
  }
  
  // Format validation (chain-specific)
  if (!isValidAddressFormat(address.value, address.chain)) {
    return { isValid: false, error: "INVALID_ADDRESS_FORMAT" };
  }
  
  // Checksum validation
  if (!isValidAddressChecksum(address.value, address.chain)) {
    return { isValid: false, error: "INVALID_ADDRESS_CHECKSUM" };
  }
  
  // Type validation
  const validTypes = ["SHIELDED", "TRANSPARENT", "CONTRACT", "ACCOUNT"];
  if (!validTypes.includes(address.type)) {
    return { isValid: false, error: "INVALID_ADDRESS_TYPE" };
  }
  
  // Type-chain compatibility
  if (!isCompatibleAddressType(address.type, address.chain)) {
    return { isValid: false, error: "INCOMPATIBLE_ADDRESS_TYPE" };
  }
  
  return { isValid: true };
}
```

**Chain-Specific Format Validation:**

**Zcash:**
- Shielded: `zaddr` prefix, 78 characters, valid checksum
- Transparent: `t1` or `t3` prefix, valid checksum

**Bitcoin:**
- Legacy: `1` prefix, Base58 checksum
- SegWit: `3` prefix, Base58 checksum
- Bech32: `bc1` prefix, Bech32 checksum

**Ethereum:**
- `0x` prefix, 42 characters, hex, EIP-55 checksum

**NEAR:**
- Account ID format, valid characters

**Errors:**
- `MISSING_ADDRESS_VALUE`: Address value is missing
- `MISSING_ADDRESS_CHAIN`: Address chain is missing
- `INVALID_ADDRESS_FORMAT`: Address format is invalid
- `INVALID_ADDRESS_CHECKSUM`: Address checksum is invalid
- `INVALID_ADDRESS_TYPE`: Address type is invalid
- `INCOMPATIBLE_ADDRESS_TYPE`: Address type incompatible with chain

---

### Amount

**Structure:**
- `value`: string (required, canonical units)
- `displayValue`: string (required, user-friendly units)
- `currency`: string (required)
- `decimals`: number (required)

**Validation:**
```typescript
function validateAmount(amount: Amount): ValidationResult {
  // Value validation
  if (!amount.value || amount.value.trim() === "") {
    return { isValid: false, error: "MISSING_AMOUNT_VALUE" };
  }
  
  // Parse value
  const value = parseFloat(amount.value);
  if (isNaN(value)) {
    return { isValid: false, error: "INVALID_AMOUNT_VALUE" };
  }
  
  // Non-negative
  if (value < 0) {
    return { isValid: false, error: "NEGATIVE_AMOUNT" };
  }
  
  // Non-zero (unless explicitly allowed)
  if (value === 0) {
    return { isValid: false, error: "ZERO_AMOUNT" };
  }
  
  // Reasonable bounds
  const maxAmount = getMaxAmountForCurrency(amount.currency);
  if (value > maxAmount) {
    return { isValid: false, error: "AMOUNT_TOO_LARGE" };
  }
  
  // Display value validation
  if (!amount.displayValue || amount.displayValue.trim() === "") {
    return { isValid: false, error: "MISSING_DISPLAY_VALUE" };
  }
  
  // Decimals validation
  if (amount.decimals < 0 || amount.decimals > 18) {
    return { isValid: false, error: "INVALID_DECIMALS" };
  }
  
  return { isValid: true };
}
```

**Max Amounts:**
- BTC: 21,000,000 (21M BTC)
- ZEC: 21,000,000 (21M ZEC)
- ETH: No hard limit (but reasonable: 1B ETH)
- Tokens: Check token supply

**Errors:**
- `MISSING_AMOUNT_VALUE`: Amount value is missing
- `INVALID_AMOUNT_VALUE`: Amount value is not a number
- `NEGATIVE_AMOUNT`: Amount is negative
- `ZERO_AMOUNT`: Amount is zero
- `AMOUNT_TOO_LARGE`: Amount exceeds maximum
- `MISSING_DISPLAY_VALUE`: Display value is missing
- `INVALID_DECIMALS`: Decimals out of range

---

### Intent Metadata

**Structure:**
- `memo`: string (optional)
- `privacyLevel`: PrivacyLevel (optional)
- `maxCost`: Amount (optional)
- `maxLatency`: number (optional)
- `preferredRoutes`: string[] (optional)
- `blockedRoutes`: string[] (optional)
- `notes`: string (optional)

**Validation:**
```typescript
function validateMetadata(metadata: IntentMetadata, chain: ChainId): ValidationResult {
  // Memo validation
  if (metadata.memo) {
    const maxMemoLength = getMaxMemoLength(chain);
    if (metadata.memo.length > maxMemoLength) {
      return { isValid: false, error: "MEMO_TOO_LONG" };
    }
    
    // Check encoding (UTF-8)
    if (!isValidUTF8(metadata.memo)) {
      return { isValid: false, error: "INVALID_MEMO_ENCODING" };
    }
  }
  
  // Privacy level validation
  if (metadata.privacyLevel) {
    const validLevels = ["HIGH", "MEDIUM", "LOW"];
    if (!validLevels.includes(metadata.privacyLevel)) {
      return { isValid: false, error: "INVALID_PRIVACY_LEVEL" };
    }
  }
  
  // Max cost validation (if provided)
  if (metadata.maxCost) {
    const costValidation = validateAmount(metadata.maxCost);
    if (!costValidation.isValid) {
      return costValidation;
    }
  }
  
  // Max latency validation (if provided)
  if (metadata.maxLatency !== undefined) {
    if (metadata.maxLatency < 0 || metadata.maxLatency > 86400) {  // Max 24 hours
      return { isValid: false, error: "INVALID_MAX_LATENCY" };
    }
  }
  
  return { isValid: true };
}
```

**Memo Length Limits:**
- Zcash: 512 bytes
- Bitcoin: No memo field
- Ethereum: No memo field (use data field)
- NEAR: No memo field

**Errors:**
- `MEMO_TOO_LONG`: Memo exceeds maximum length
- `INVALID_MEMO_ENCODING`: Memo encoding is invalid
- `INVALID_PRIVACY_LEVEL`: Privacy level is invalid
- `INVALID_MAX_LATENCY`: Max latency out of range

---

## Semantic Validation

### Chain Compatibility

**SEND Intent:**
- Source asset chain must match destination address chain
- Exception: Cross-chain sends (Phase 4+)

**SWAP Intent:**
- Source and target assets can be on different chains (cross-chain swap)
- Or same chain (same-chain swap)

**Validation:**
```typescript
function validateChainCompatibility(intent: Intent): ValidationResult {
  if (intent.type === "SEND") {
    if (intent.sourceAsset.chain !== intent.destination.chain) {
      // Cross-chain send (Phase 4+)
      if (!isCrossChainSupported(intent.sourceAsset.chain, intent.destination.chain)) {
        return { isValid: false, error: "CROSS_CHAIN_NOT_SUPPORTED" };
      }
    }
  }
  
  return { isValid: true };
}
```

**Errors:**
- `CROSS_CHAIN_NOT_SUPPORTED`: Cross-chain not supported in Phase 2

---

## Validation Interface

### Validator Interface

```typescript
interface IntentValidator {
  // Validate intent structure
  validateStructure(intent: Intent): ValidationResult;
  
  // Validate intent semantics
  validateSemantics(intent: Intent): ValidationResult;
  
  // Validate complete intent
  validate(intent: Intent): ValidationResult;
}

interface ValidationResult {
  isValid: boolean;
  errors?: ValidationError[];
  warnings?: ValidationWarning[];
}

interface ValidationError {
  field: string;
  code: string;
  message: string;
}

interface ValidationWarning {
  field: string;
  code: string;
  message: string;
}
```

---

## Error Messages

### User-Friendly Messages

**Technical Error → User Message:**

- `INVALID_ADDRESS_FORMAT` → "The address format is invalid. Please check and try again."
- `INVALID_ADDRESS_CHECKSUM` → "The address checksum is invalid. Please check for typos."
- `NEGATIVE_AMOUNT` → "Amount must be positive."
- `ZERO_AMOUNT` → "Amount must be greater than zero."
- `AMOUNT_TOO_LARGE` → "Amount exceeds maximum limit."
- `CROSS_CHAIN_NOT_SUPPORTED` → "Cross-chain transactions are not yet supported."

---

## Testing

### Unit Tests

**Structure Validation:**
- Valid intents pass validation
- Missing required fields fail validation
- Invalid field types fail validation
- Invalid field formats fail validation

**Semantic Validation:**
- Valid semantics pass validation
- Chain incompatibility fails validation
- Invalid amounts fail validation
- Invalid addresses fail validation

**Error Messages:**
- Error messages are clear and actionable
- Error codes are consistent
- Warnings are informative

---

**Last Updated:** [Current Date]  
**Status:** Specification complete — ready for implementation (Phase 2)


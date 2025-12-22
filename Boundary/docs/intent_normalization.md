# Boundary Wallet — Intent Normalization Rules

This document defines how user input is normalized into structured `Intent` objects.

**Purpose:** Ensure consistent, unambiguous intent representation across all user inputs.

---

## Overview

Intent normalization converts natural language or structured user input into a standardized `Intent` object that can be processed by the routing engine.

**Input Examples:**
- "Send 100 ZEC to zaddr..."
- "Swap 1 ETH for BTC"
- "I want to receive NEAR"

**Output:** Structured `Intent` object (see [Architecture](./architecture.md) for schema)

---

## Normalization Process

### Step 1: Input Parsing

Parse user input to extract:
- **Action type** (SEND, SWAP, RECEIVE, etc.)
- **Source asset** (what user is sending/swapping)
- **Target asset** (what user wants to receive)
- **Destination** (address, if applicable)
- **Amount** (how much)
- **Metadata** (memo, preferences, etc.)

### Step 2: Validation

Validate extracted fields:
- Address format and checksum
- Asset existence and chain compatibility
- Amount format and range
- Required fields present

### Step 3: Normalization

Normalize to standard format:
- Standardize asset identifiers (e.g., "Bitcoin" → BTC)
- Infer chain from address or context
- Convert amounts to canonical units
- Apply default metadata if missing

### Step 4: Intent Creation

Create structured `Intent` object with normalized data.

---

## Normalization Rules by Intent Type

### SEND Intent

**Pattern:** "Send [amount] [asset] to [address]"

**Examples:**
- "Send 100 ZEC to zaddr1..." → `SEND` intent
- "Send 0.5 BTC to bc1..." → `SEND` intent
- "Transfer 10 USDC to 0x..." → `SEND` intent

**Normalization:**
1. Extract amount: Parse number, handle decimals
2. Extract asset: Resolve asset name (e.g., "ZEC" → `{chain: "zcash", token: "ZEC"}`)
3. Extract address: Parse address, validate format
4. Infer chain: From address format (zaddr → Zcash, bc1 → Bitcoin, 0x → Ethereum)
5. Validate: Ensure asset matches chain inferred from address

**Edge Cases:**
- **Cross-chain send:** "Send 1 ETH to bc1..." → Error (ETH cannot be sent to Bitcoin address)
- **Ambiguous asset:** "Send 1 USDC" → Error (need destination to infer chain)
- **Invalid amount:** "Send -5 ZEC" → Error (negative amounts invalid)

### SWAP Intent

**Pattern:** "Swap [amount] [source asset] for [target asset]"

**Examples:**
- "Swap 1 ETH for BTC" → `SWAP` intent
- "Exchange 100 ZEC for USDC" → `SWAP` intent
- "Convert 50 NEAR to ETH" → `SWAP` intent

**Normalization:**
1. Extract source amount: Parse number
2. Extract source asset: Resolve asset name
3. Extract target asset: Resolve asset name
4. Infer chains: From asset identifiers (ETH → Ethereum, BTC → Bitcoin)
5. Validate: Ensure assets exist and are swappable

**Edge Cases:**
- **Same-chain swap:** "Swap 1 ETH for USDC" → Valid (both Ethereum)
- **Cross-chain swap:** "Swap 1 ETH for BTC" → Valid (requires bridge)
- **Invalid asset:** "Swap 1 FOO for BAR" → Error (assets don't exist)
- **Missing amount:** "Swap ETH for BTC" → Error (amount required)

### RECEIVE Intent

**Pattern:** "Receive [asset]" or "I want to receive [asset]"

**Examples:**
- "Receive NEAR" → `RECEIVE` intent
- "I want to receive BTC" → `RECEIVE` intent
- "Generate address for ZEC" → `RECEIVE` intent

**Normalization:**
1. Extract target asset: Resolve asset name
2. Infer chain: From asset identifier
3. Generate address: Create new address for chain (if needed)
4. Validate: Ensure asset exists

**Edge Cases:**
- **Asset doesn't exist:** "Receive FOO" → Error
- **Address already exists:** Use existing address or generate new (user preference)

---

## Asset Resolution

### Asset Name Mapping

Map common asset names to standardized identifiers:

**Zcash:**
- "ZEC", "Zcash", "zcash" → `{chain: "zcash", token: "ZEC"}`

**Bitcoin:**
- "BTC", "Bitcoin", "bitcoin" → `{chain: "bitcoin", token: "BTC"}`

**Ethereum:**
- "ETH", "Ethereum", "ethereum" → `{chain: "ethereum", token: "ETH"}`

**Tokens:**
- "USDC" → Resolve chain from context (Ethereum, Polygon, etc.)
- "USDT" → Resolve chain from context
- "DAI" → Resolve chain from context

### Chain Inference

**From Address Format:**
- `zaddr...` → Zcash (shielded)
- `t1...` or `t3...` → Zcash (transparent)
- `bc1...` or `1...` or `3...` → Bitcoin
- `0x...` → Ethereum (or EVM-compatible)
- `near...` → NEAR

**From Asset:**
- ZEC → Zcash
- BTC → Bitcoin
- ETH → Ethereum
- NEAR → NEAR

**From Context:**
- If user has wallet on chain X, default to chain X
- If destination address is on chain Y, use chain Y

---

## Address Validation

### Format Validation

**Zcash:**
- Shielded: `zaddr` prefix, 78 characters, valid checksum
- Transparent: `t1` or `t3` prefix, valid checksum

**Bitcoin:**
- Legacy: `1...` prefix, valid checksum
- SegWit: `3...` prefix, valid checksum
- Bech32: `bc1...` prefix, valid checksum

**Ethereum:**
- `0x` prefix, 42 characters, valid hex

**NEAR:**
- Account ID format, valid characters

### Checksum Validation

All addresses must pass checksum validation:
- Zcash: Sapling/Transparent address checksums
- Bitcoin: Base58/Bech32 checksums
- Ethereum: EIP-55 checksum (case-sensitive)
- NEAR: Account ID validation

**Invalid addresses:** Reject with clear error message

---

## Amount Normalization

### Parsing

- Parse numbers with decimals: "100.5" → `100.5`
- Handle commas: "1,000" → `1000`
- Handle scientific notation: "1e6" → `1000000`
- Handle units: "1.5 BTC" → `1.5` (in BTC units)

### Unit Conversion

Convert to canonical units (smallest unit):
- BTC: Convert to satoshis (1 BTC = 100,000,000 satoshis)
- ETH: Convert to wei (1 ETH = 10^18 wei)
- ZEC: Convert to zatoshis (1 ZEC = 10^8 zatoshis)
- Tokens: Use token decimals (USDC: 6 decimals)

**Store both:** Canonical units (for execution) and display units (for UI)

### Validation

- **Non-negative:** Reject negative amounts
- **Non-zero:** Reject zero amounts (unless explicitly allowed)
- **Within bounds:** Reject amounts exceeding reasonable limits (e.g., > 21M BTC)
- **Precision:** Respect asset decimal places

---

## Metadata Normalization

### Memo Field

- **Max length:** Respect chain-specific limits (Zcash: 512 bytes)
- **Encoding:** UTF-8, handle special characters
- **Optional:** Can be empty/null

### Privacy Preferences

**Default:** HIGH (privacy-first)

**User Override:**
- HIGH: Prefer shielded/private routes
- MEDIUM: Balance privacy and cost
- LOW: Prefer cheapest/fastest routes

**Normalization:**
- "privacy" → HIGH
- "private" → HIGH
- "cheap" → LOW
- "fast" → LOW
- Default → HIGH

### Cost Preferences

- **Max cost:** User-specified maximum acceptable cost
- **Currency:** User's preferred currency for cost display
- **Normalization:** Convert to canonical units if needed

### Latency Preferences

- **Max latency:** User-specified maximum acceptable time (seconds)
- **Normalization:** Convert to seconds if user specifies minutes/hours

---

## Error Handling

### Validation Errors

**Invalid Address:**
- Error: "Invalid address format"
- Suggestion: "Please check the address and try again"

**Invalid Asset:**
- Error: "Asset not recognized: [asset]"
- Suggestion: "Supported assets: ZEC, BTC, ETH, ..."

**Invalid Amount:**
- Error: "Invalid amount: [amount]"
- Suggestion: "Amount must be positive and within valid range"

**Missing Required Field:**
- Error: "Missing required field: [field]"
- Suggestion: "Please provide [field]"

### Ambiguity Resolution

**Ambiguous Asset:**
- "USDC" could be Ethereum, Polygon, etc.
- **Resolution:** Infer from context (user's wallet, destination address)
- **Fallback:** Ask user to specify chain

**Ambiguous Chain:**
- User says "Send 1 ETH" without destination
- **Resolution:** Use user's default chain or ask for destination

---

## Implementation Notes

### Client-Side Normalization

**Recommendation:** Normalize on client-side (in wallet app)

**Rationale:**
- Privacy: User input doesn't leave device
- Speed: No network round-trip
- Offline: Works without network connection

### Normalization Cache

Cache asset name mappings and address format validators:
- Asset name → Asset identifier mapping
- Address format → Chain mapping
- Validation functions

### Testing

**Test Cases:**
- Valid inputs (all intent types)
- Invalid inputs (malformed addresses, invalid amounts)
- Edge cases (cross-chain, ambiguous assets)
- Error messages (clear and actionable)

---

## Examples

### Example 1: Simple Send

**Input:** "Send 100 ZEC to zaddr1abc123..."

**Normalization:**
1. Parse: `{action: "send", amount: "100", asset: "ZEC", destination: "zaddr1abc123..."}`
2. Validate: Address format valid, amount valid, asset exists
3. Normalize: `{chain: "zcash", token: "ZEC"}`, amount: `10000000000` (zatoshis)
4. Create Intent: `SEND` intent with normalized data

### Example 2: Cross-Chain Swap

**Input:** "Swap 1 ETH for BTC"

**Normalization:**
1. Parse: `{action: "swap", sourceAmount: "1", sourceAsset: "ETH", targetAsset: "BTC"}`
2. Validate: Both assets exist, amounts valid
3. Normalize: `{sourceAsset: {chain: "ethereum", token: "ETH"}, targetAsset: {chain: "bitcoin", token: "BTC"}}`
4. Create Intent: `SWAP` intent (cross-chain, requires bridge)

### Example 3: Receive with Address Generation

**Input:** "Receive NEAR"

**Normalization:**
1. Parse: `{action: "receive", targetAsset: "NEAR"}`
2. Validate: Asset exists
3. Normalize: `{chain: "near", token: "NEAR"}`
4. Generate: New NEAR address (if needed)
5. Create Intent: `RECEIVE` intent with generated address

---

**Last Updated:** [Current Date]  
**Status:** Design complete — ready for implementation (Phase 2)


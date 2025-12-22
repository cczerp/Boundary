# Boundary Wallet — Intent Serialization Specification

This document defines the detailed specification for serializing and deserializing Intent objects.

**Purpose:** Ensure consistent serialization across platforms and enable debugging, logging, and future features.

---

## Serialization Format

### Primary Format: JSON

**Rationale:**
- Human-readable (for debugging)
- Language-agnostic (works across platforms)
- Widely supported (all platforms have JSON libraries)
- Extensible (easy to add fields)

**Alternative Formats (Future):**
- MessagePack (binary, more compact)
- Protocol Buffers (binary, type-safe)
- CBOR (binary, compact)

---

## JSON Schema

### Intent JSON Schema

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": [
    "id",
    "type",
    "sourceAsset",
    "targetAsset",
    "destination",
    "amount",
    "timestamp",
    "status"
  ],
  "properties": {
    "id": {
      "type": "string",
      "format": "uuid",
      "description": "Unique identifier (UUID v4)"
    },
    "type": {
      "type": "string",
      "enum": ["SEND", "SWAP", "RECEIVE", "BRIDGE"],
      "description": "Intent type"
    },
    "sourceAsset": {
      "$ref": "#/definitions/Asset",
      "description": "Source asset"
    },
    "targetAsset": {
      "$ref": "#/definitions/Asset",
      "description": "Target asset"
    },
    "destination": {
      "$ref": "#/definitions/Address",
      "description": "Destination address"
    },
    "amount": {
      "$ref": "#/definitions/Amount",
      "description": "Amount in source asset units"
    },
    "metadata": {
      "$ref": "#/definitions/IntentMetadata",
      "description": "Optional metadata"
    },
    "timestamp": {
      "type": "integer",
      "description": "Unix epoch milliseconds"
    },
    "expiresAt": {
      "type": "integer",
      "description": "Unix epoch milliseconds (optional)"
    },
    "status": {
      "type": "string",
      "enum": [
        "CREATED",
        "NORMALIZING",
        "VALIDATING",
        "ROUTING",
        "ROUTE_SELECTED",
        "EXECUTING",
        "COMPLETED",
        "FAILED",
        "CANCELLED"
      ],
      "description": "Intent status"
    }
  },
  "definitions": {
    "Asset": {
      "type": "object",
      "required": ["chain", "token"],
      "properties": {
        "chain": {
          "type": "string",
          "description": "Chain identifier"
        },
        "token": {
          "type": "string",
          "description": "Token identifier"
        },
        "contractAddress": {
          "type": "string",
          "description": "Contract address (optional, for tokens)"
        },
        "decimals": {
          "type": "integer",
          "minimum": 0,
          "maximum": 18,
          "description": "Token decimals (optional)"
        }
      }
    },
    "Address": {
      "type": "object",
      "required": ["value", "chain", "type"],
      "properties": {
        "value": {
          "type": "string",
          "description": "Address string"
        },
        "chain": {
          "type": "string",
          "description": "Chain identifier"
        },
        "type": {
          "type": "string",
          "enum": ["SHIELDED", "TRANSPARENT", "CONTRACT", "ACCOUNT"],
          "description": "Address type"
        },
        "label": {
          "type": "string",
          "description": "Address label (optional)"
        }
      }
    },
    "Amount": {
      "type": "object",
      "required": ["value", "displayValue", "currency", "decimals"],
      "properties": {
        "value": {
          "type": "string",
          "description": "Amount in canonical units (string to preserve precision)"
        },
        "displayValue": {
          "type": "string",
          "description": "User-friendly display value"
        },
        "currency": {
          "type": "string",
          "description": "Currency symbol"
        },
        "decimals": {
          "type": "integer",
          "minimum": 0,
          "maximum": 18,
          "description": "Number of decimals"
        }
      }
    },
    "IntentMetadata": {
      "type": "object",
      "properties": {
        "memo": {
          "type": "string",
          "description": "Memo field (optional)"
        },
        "privacyLevel": {
          "type": "string",
          "enum": ["HIGH", "MEDIUM", "LOW"],
          "description": "Privacy preference (optional)"
        },
        "maxCost": {
          "$ref": "#/definitions/Amount",
          "description": "Maximum acceptable cost (optional)"
        },
        "maxLatency": {
          "type": "integer",
          "description": "Maximum acceptable latency in seconds (optional)"
        },
        "preferredRoutes": {
          "type": "array",
          "items": {
            "type": "string"
          },
          "description": "Preferred route IDs (optional)"
        },
        "blockedRoutes": {
          "type": "array",
          "items": {
            "type": "string"
          },
          "description": "Blocked route IDs (optional)"
        },
        "notes": {
          "type": "string",
          "description": "User notes (optional)"
        }
      }
    }
  }
}
```

---

## Serialization Rules

### Required Fields

**Must Always Be Present:**
- `id` (UUID v4)
- `type` (SEND, SWAP, RECEIVE, BRIDGE)
- `sourceAsset` (chain, token)
- `targetAsset` (chain, token)
- `destination` (value, chain, type)
- `amount` (value, displayValue, currency, decimals)
- `timestamp` (Unix epoch milliseconds)
- `status` (CREATED, NORMALIZING, etc.)

### Optional Fields

**Can Be Omitted If Null/Undefined:**
- `metadata` (entire object)
- `expiresAt` (if not set)
- `metadata.memo` (if not set)
- `metadata.privacyLevel` (if not set)
- `metadata.maxCost` (if not set)
- `metadata.maxLatency` (if not set)
- `metadata.preferredRoutes` (if empty)
- `metadata.blockedRoutes` (if empty)
- `metadata.notes` (if not set)
- `address.label` (if not set)
- `asset.contractAddress` (if not set)
- `asset.decimals` (if not set)

### Field Formatting

**Timestamps:**
- Format: Unix epoch milliseconds (integer)
- Example: `1699123456789`

**Amounts:**
- Format: String (to preserve precision)
- Example: `"10000000000"` (not `10000000000` as number)

**UUIDs:**
- Format: UUID v4 (lowercase with hyphens)
- Example: `"550e8400-e29b-41d4-a716-446655440000"`

**Enums:**
- Format: Uppercase strings
- Example: `"SEND"`, `"HIGH"`, `"SHIELDED"`

---

## Example JSON

### SEND Intent

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
    "value": "zaddr1abc123def456ghi789jkl012mno345pqr678stu901vwx234yz",
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

### SWAP Intent

```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "type": "SWAP",
  "sourceAsset": {
    "chain": "ethereum",
    "token": "ETH"
  },
  "targetAsset": {
    "chain": "bitcoin",
    "token": "BTC"
  },
  "destination": {
    "value": "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
    "chain": "bitcoin",
    "type": "TRANSPARENT"
  },
  "amount": {
    "value": "1000000000000000000",
    "displayValue": "1",
    "currency": "ETH",
    "decimals": 18
  },
  "metadata": {
    "privacyLevel": "MEDIUM",
    "maxCost": {
      "value": "50000000",
      "displayValue": "0.5",
      "currency": "BTC",
      "decimals": 8
    },
    "maxLatency": 3600
  },
  "timestamp": 1699123456790,
  "status": "ROUTING"
}
```

---

## Deserialization Rules

### Validation on Deserialization

**Required:**
- Validate all required fields are present
- Validate field types match schema
- Validate field formats (UUID, timestamps, etc.)
- Validate enum values
- Validate address formats and checksums
- Validate amounts (non-negative, reasonable bounds)

**Error Handling:**
- Invalid JSON: Return parse error
- Missing required field: Return validation error
- Invalid field type: Return validation error
- Invalid field format: Return validation error
- Invalid enum value: Return validation error

---

## Platform Implementations

### Android (Kotlin)

**Library:** Kotlinx Serialization or Gson

**Kotlinx Serialization:**
```kotlin
@Serializable
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

// Serialize
val json = Json.encodeToString(Intent.serializer(), intent)

// Deserialize
val intent = Json.decodeFromString<Intent>(json)
```

**Gson:**
```kotlin
val gson = Gson()
val json = gson.toJson(intent)
val intent = gson.fromJson(json, Intent::class.java)
```

### iOS (Swift)

**Library:** Swift Codable

**Swift:**
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

// Serialize
let encoder = JSONEncoder()
let jsonData = try encoder.encode(intent)
let jsonString = String(data: jsonData, encoding: .utf8)

// Deserialize
let decoder = JSONDecoder()
let intent = try decoder.decode(Intent.self, from: jsonData)
```

---

## Versioning

### Schema Versioning

**Current Version:** 1.0

**Versioning Strategy:**
- Add new fields as optional (backward compatible)
- Don't remove fields (mark as deprecated)
- Increment version for breaking changes

**Version Field (Future):**
```json
{
  "version": "1.0",
  "intent": {
    // ... intent data
  }
}
```

---

## Use Cases

### Debugging

**Log Intent Creation:**
```json
{
  "event": "intent_created",
  "intent": {
    // ... serialized intent (anonymized)
  }
}
```

### Logging

**Log Intent Processing:**
- Serialize intent at each stage
- Log serialized intent (anonymized)
- Track intent through processing pipeline

### Future Features

**Intent Persistence:**
- Save intents to local storage
- Load intents from storage
- Resume interrupted intents

**Intent Sharing:**
- Export intent as JSON
- Import intent from JSON
- Share intent with others (if desired)

---

## Testing

### Unit Tests

**Serialization:**
- Serialize valid intent → valid JSON
- Serialize intent with optional fields → JSON includes fields
- Serialize intent without optional fields → JSON omits fields

**Deserialization:**
- Deserialize valid JSON → valid intent
- Deserialize JSON with missing required field → error
- Deserialize JSON with invalid field type → error
- Deserialize JSON with invalid enum → error

**Round-Trip:**
- Serialize intent → JSON → deserialize → compare
- Verify all fields preserved
- Verify optional fields handled correctly

---

**Last Updated:** [Current Date]  
**Status:** Specification complete — ready for implementation (Phase 2)


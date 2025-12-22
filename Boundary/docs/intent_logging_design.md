# Boundary Wallet — Intent Logging Design

This document defines the design for logging intent processing without exposing sensitive user data.

**Purpose:** Enable debugging and analysis while maintaining user privacy.

---

## Privacy-First Logging Principles

### What We Log

**✅ Safe to Log:**
- Intent IDs (UUIDs, not linkable to user)
- Intent types (SEND, SWAP, etc.)
- Chain identifiers (zcash, bitcoin, etc.)
- Token identifiers (ZEC, BTC, etc.)
- Timestamps (when intents were created)
- Status changes (CREATED → NORMALIZING → etc.)
- Error codes and error types
- Mapping results (success/failure)
- Route discovery results (number of routes found)

### What We DON'T Log

**❌ Never Log:**
- Wallet addresses (full addresses)
- Transaction amounts (exact amounts)
- Memo content
- Private keys or seed phrases
- User identifiers
- IP addresses
- Device identifiers

### Anonymization Rules

**Addresses:**
- Log only first 4 and last 4 characters: `zaddr1abc...xyz9`
- Or hash addresses: `sha256(address).substring(0, 16)`

**Amounts:**
- Log only ranges: `"1-10 ZEC"` instead of `"5.23456789 ZEC"`
- Or log order of magnitude: `"~1 ZEC"`, `"~100 ZEC"`

**Memo:**
- Log only length: `"memo_length: 42"` instead of content
- Or hash: `sha256(memo).substring(0, 16)`

---

## Logging Architecture

### Log Levels

```typescript
enum LogLevel {
  DEBUG = "DEBUG",     // Detailed debugging info
  INFO = "INFO",       // General information
  WARN = "WARN",       // Warnings (non-critical issues)
  ERROR = "ERROR"      // Errors (failures)
}
```

### Log Events

**Intent Creation:**
```typescript
{
  event: "intent_created",
  level: "INFO",
  intentId: "550e8400-...",
  type: "SEND",
  sourceChain: "zcash",
  targetChain: "zcash",
  sourceToken: "ZEC",
  targetToken: "ZEC",
  destinationType: "SHIELDED",
  timestamp: 1699123456789
}
```

**Intent Normalization:**
```typescript
{
  event: "intent_normalized",
  level: "DEBUG",
  intentId: "550e8400-...",
  normalizationTime: 45,  // milliseconds
  warnings: ["address_format_ambiguous"]
}
```

**Intent Validation:**
```typescript
{
  event: "intent_validated",
  level: "INFO",
  intentId: "550e8400-...",
  isValid: true,
  validationTime: 12  // milliseconds
}
```

**Intent Mapping:**
```typescript
{
  event: "intent_mapped",
  level: "INFO",
  intentId: "550e8400-...",
  actionType: "SEND_SHIELDED",
  mappingTime: 8,  // milliseconds
  success: true
}
```

**Route Discovery:**
```typescript
{
  event: "routes_discovered",
  level: "INFO",
  intentId: "550e8400-...",
  routerId: "local_router",
  routeCount: 1,
  discoveryTime: 234  // milliseconds
}
```

**Errors:**
```typescript
{
  event: "intent_error",
  level: "ERROR",
  intentId: "550e8400-...",
  errorType: "VALIDATION_ERROR",
  errorCode: "INVALID_ADDRESS",
  errorMessage: "Address format invalid",
  stackTrace: "..."  // Only in debug builds
}
```

---

## Log Storage

### Local Storage Only

**Storage Location:**
- Android: App's private data directory
- iOS: App's Documents directory (user-accessible for export)

**Storage Format:**
- JSON Lines (one JSON object per line)
- Rotating log files (max size: 10MB, keep last 5 files)
- Compressed old logs (gzip)

**File Naming:**
- `intent_logs_YYYY-MM-DD.jsonl`
- `intent_logs_YYYY-MM-DD.jsonl.gz` (compressed)

### Log Retention

**Default:**
- Keep logs for 7 days
- Auto-delete after retention period
- User can export logs manually

**User Control:**
- User can disable logging (privacy setting)
- User can export logs (for debugging)
- User can clear logs manually

---

## Log Export

### Export Format

**JSON Array:**
```json
[
  {
    "event": "intent_created",
    "timestamp": 1699123456789,
    "intentId": "550e8400-...",
    "type": "SEND",
    ...
  },
  ...
]
```

**CSV (Optional):**
```csv
timestamp,event,intentId,type,sourceChain,targetChain
1699123456789,intent_created,550e8400-...,SEND,zcash,zcash
```

### Export Security

**Before Export:**
- Re-anonymize all addresses
- Re-anonymize all amounts
- Remove any accidentally logged sensitive data
- User must confirm export

**Export Location:**
- User chooses location (file picker)
- Not automatically uploaded anywhere
- User responsible for secure storage

---

## Log Analysis

### Debugging Use Cases

**Intent Processing Flow:**
- Track intent from creation to execution
- Identify bottlenecks (normalization, validation, mapping)
- Debug errors (validation failures, mapping failures)

**Performance Analysis:**
- Measure normalization time
- Measure validation time
- Measure mapping time
- Measure route discovery time

**Error Analysis:**
- Count error types
- Identify common failure patterns
- Track error rates over time

### Privacy-Preserving Analytics

**Aggregate Statistics:**
- Intent type distribution (SEND: 60%, SWAP: 30%, etc.)
- Chain distribution (zcash: 80%, bitcoin: 20%, etc.)
- Error rate (5% validation errors, etc.)
- Performance metrics (avg normalization time: 50ms, etc.)

**No User-Specific Data:**
- No individual user tracking
- No address correlation
- No transaction linking

---

## Implementation

### Android (Kotlin)

**Library:** Use Timber or similar logging library

**Example:**
```kotlin
class IntentLogger {
    fun logIntentCreated(intent: Intent) {
        logger.info("intent_created") {
            "intentId" to intent.id
            "type" to intent.type.name
            "sourceChain" to intent.sourceAsset.chain
            "targetChain" to intent.targetAsset.chain
            // ... other safe fields
        }
    }
    
    fun logError(intentId: String, error: Throwable) {
        logger.error("intent_error") {
            "intentId" to intentId
            "errorType" to error.javaClass.simpleName
            "errorMessage" to error.message
        }
    }
}
```

### iOS (Swift)

**Library:** Use os.log or similar logging library

**Example:**
```swift
class IntentLogger {
    func logIntentCreated(_ intent: Intent) {
        logger.info("intent_created: \(intent.id), type: \(intent.type)")
        // Log safe fields only
    }
    
    func logError(intentId: String, error: Error) {
        logger.error("intent_error: \(intentId), error: \(error.localizedDescription)")
    }
}
```

---

## Configuration

### Logging Settings

**User Preferences:**
- Enable/disable logging (default: enabled)
- Log level (DEBUG, INFO, WARN, ERROR)
- Log retention period (default: 7 days)
- Auto-export (default: disabled)

**Build Configuration:**
- Debug builds: Full logging (including stack traces)
- Release builds: Reduced logging (no stack traces, no DEBUG logs)

---

## Testing

### Unit Tests

**Logging Functions:**
- Verify correct log format
- Verify sensitive data is not logged
- Verify anonymization works correctly

**Log Storage:**
- Verify logs are written to correct location
- Verify log rotation works
- Verify log retention works

**Log Export:**
- Verify export format is correct
- Verify sensitive data is removed before export
- Verify export works on both platforms

---

## Future Enhancements

### Phase 3+
- Add route discovery logging
- Add execution logging (non-sensitive)
- Add performance metrics logging
- Add user preference logging (privacy settings)

### Advanced Features
- Log aggregation (if user opts in)
- Remote logging (if user opts in, privacy-preserving)
- Log analysis dashboard (local only)

---

**Last Updated:** [Current Date]  
**Status:** Design complete — ready for implementation (Phase 2)


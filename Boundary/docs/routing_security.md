# Boundary Wallet — Routing Security Assumptions

This document defines security assumptions and requirements for routing providers and execution providers.

**Purpose:** Ensure routing and execution providers meet security requirements before integration.

---

## Security Principles

### 1. Data Minimization

**Principle:** Routers and execution providers receive only the minimum data necessary.

**Requirements:**
- Routers receive **only** the `Intent` object
- Routers **do not** receive wallet state, balances, or transaction history
- Routers **do not** receive user's other addresses
- Execution providers receive **only** the `RouteStep` they need to execute
- Execution providers **do not** receive other route steps or wallet state

**Enforcement:**
- Client-side validation before sending data
- Clear API contracts (providers cannot request additional data)
- Audit logging (what data was sent to which provider)

---

### 2. Key Security

**Principle:** Private keys never leave the device.

**Requirements:**
- Signing happens **client-side** only
- Execution providers **never** receive private keys
- Execution providers **never** receive seed phrases
- Execution providers **never** receive viewing keys (unless explicitly required for specific protocols)

**Enforcement:**
- All signing operations happen in wallet core (unchanged from Zashi)
- Execution providers receive only signed transactions (if needed)
- No key material in API requests

---

### 3. Trust Model

**Principle:** Routers and execution providers are **untrusted** by default.

**Requirements:**
- Routers are untrusted (can be malicious)
- Execution providers are untrusted (can be malicious)
- Client-side validation of all router responses
- User confirmation required for route selection
- User confirmation required for execution

**Enforcement:**
- Route validation before execution
- Transaction validation before signing
- User must approve routes and execution
- Clear warnings for custodial providers

---

## Router Security Requirements

### What Routers CAN Receive

**Allowed Data:**
- `Intent` object (action, assets, amounts, destination)
- Route discovery requests
- Quote refresh requests

**Intent Object Contains:**
- Intent type (SEND, SWAP, etc.)
- Source asset (chain, token)
- Target asset (chain, token)
- Destination address (where to send)
- Amount (how much)
- Optional metadata (privacy preferences, max cost, etc.)

**What Intent Object Does NOT Contain:**
- User's wallet addresses (except destination)
- User's wallet balances
- User's transaction history
- User's keys or seed phrases
- User's IP address (if possible)

---

### What Routers MUST NOT Receive

**Forbidden Data:**
- Private keys or seed phrases
- Wallet state or balances
- Transaction history
- User's other addresses
- Device identifiers
- IP addresses (if avoidable)

**Enforcement:**
- Client-side filtering before sending to routers
- API contracts that don't expose sensitive data
- Audit logging to detect violations

---

### Router Response Validation

**Requirements:**
- All router responses must be validated client-side
- Route structures must match expected format
- Route steps must be executable
- Route costs must be reasonable
- Route expiration must be valid

**Validation Checks:**
```typescript
function validateRouteResponse(route: Route): ValidationResult {
  // Structure validation
  if (!route.id || !route.steps || route.steps.length === 0) {
    return { isValid: false, error: "INVALID_ROUTE_STRUCTURE" };
  }
  
  // Step validation
  for (const step of route.steps) {
    if (!step.provider || !step.inputAsset || !step.outputAsset) {
      return { isValid: false, error: "INVALID_STEP_STRUCTURE" };
    }
    
    // Cost validation
    if (parseFloat(step.estimatedCost.value) < 0) {
      return { isValid: false, error: "INVALID_COST" };
    }
    
    // Latency validation
    if (step.estimatedLatency < 0) {
      return { isValid: false, error: "INVALID_LATENCY" };
    }
  }
  
  // Expiration validation
  if (route.expiresAt < Date.now()) {
    return { isValid: false, error: "ROUTE_EXPIRED" };
  }
  
  return { isValid: true };
}
```

---

## Execution Provider Security Requirements

### What Execution Providers CAN Receive

**Allowed Data:**
- `RouteStep` object (step they need to execute)
- Transaction data for their step only
- Execution status requests

**RouteStep Object Contains:**
- Step type (BRIDGE, SWAP, SEND)
- Input asset (what to receive)
- Output asset (what to send)
- Estimated cost and latency
- Trust model (CUSTODIAL, NON_CUSTODIAL, TRUSTLESS)

**What RouteStep Does NOT Contain:**
- Other route steps
- User's wallet state
- User's keys or seed phrases
- User's transaction history

---

### What Execution Providers MUST NOT Receive

**Forbidden Data:**
- Private keys or seed phrases
- Wallet state or balances
- Other route steps
- User's addresses (except destination)
- Device identifiers

**Enforcement:**
- Client-side filtering before sending to providers
- API contracts that don't expose sensitive data
- Audit logging to detect violations

---

### Execution Provider Trust Models

**TRUSTLESS (Preferred):**
- No third-party custody
- Client-side signing
- On-chain execution only
- **Security:** Highest (no trust required)

**NON_CUSTODIAL:**
- No custody, but requires trust in provider
- Provider facilitates execution but doesn't hold funds
- **Security:** Medium (trust in provider required)

**CUSTODIAL:**
- Provider holds funds temporarily
- Requires trust in provider
- **Security:** Lowest (trust in provider + custody risk)

**User Warnings:**
- CUSTODIAL providers: Clear warning about custody risk
- NON_CUSTODIAL providers: Warning about trust requirement
- TRUSTLESS providers: No warning (preferred)

---

## Route Validation

### Pre-Execution Validation

**Requirements:**
- Validate route structure before execution
- Validate route steps are executable
- Validate route expiration
- Validate provider availability
- Validate costs are acceptable

**Validation Process:**
```typescript
async function validateRouteBeforeExecution(route: Route): Promise<ValidationResult> {
  // Structure validation
  const structureValidation = validateRouteStructure(route);
  if (!structureValidation.isValid) {
    return structureValidation;
  }
  
  // Expiration validation
  if (route.expiresAt < Date.now()) {
    return { isValid: false, error: "ROUTE_EXPIRED" };
  }
  
  // Step validation
  for (const step of route.steps) {
    const stepValidation = await validateStep(step);
    if (!stepValidation.isValid) {
      return stepValidation;
    }
  }
  
  // Provider availability
  for (const step of route.steps) {
    const providerAvailable = await checkProviderAvailability(step.provider);
    if (!providerAvailable) {
      return { isValid: false, error: "PROVIDER_UNAVAILABLE" };
    }
  }
  
  return { isValid: true };
}
```

---

## Security Guarantees

### What Boundary Guarantees

**Router Privacy:**
- Routers receive only intent data (no wallet state)
- Routers cannot link intents to user identity (if addresses are isolated)
- Routers cannot see transaction history

**Execution Privacy:**
- Execution providers receive only route step data
- Execution providers cannot see other steps or wallet state
- Signing happens client-side (keys never leave device)

**Route Security:**
- All routes are validated client-side before execution
- User must confirm route selection
- User must confirm execution
- Clear warnings for custodial providers

---

### What Boundary Cannot Guarantee

**Blockchain Privacy:**
- Transparent transactions are public by design
- Shielded transactions provide privacy (Zcash)
- Privacy depends on blockchain capabilities

**Bridge Privacy:**
- Bridge operators may see transactions (custodial bridges)
- Bridge protocols may leak metadata (non-custodial bridges)
- Privacy-preserving bridges preferred, but not always available

**Network Privacy:**
- Network observers may see router communication (HTTPS metadata)
- IP addresses may be visible (use VPN/Tor if needed)

---

## Provider Integration Requirements

### Before Integration

**Security Review:**
- Review provider's security practices
- Review provider's privacy policy
- Review provider's trust model
- Review provider's API security

**Requirements:**
- Provider must not request sensitive data
- Provider must have clear API documentation
- Provider must support HTTPS/TLS
- Provider must have reasonable uptime

**Testing:**
- Test provider with mock data
- Verify provider doesn't request sensitive data
- Verify provider responses are valid
- Test error handling

---

## Audit Logging

### What to Log

**Router Interactions:**
- Which router was used
- What intent was sent (anonymized)
- What routes were returned
- Any errors encountered

**Execution Provider Interactions:**
- Which provider was used
- What step was executed
- Execution result (success/failure)
- Any errors encountered

**Do NOT Log:**
- Full addresses (use hashed/truncated)
- Full amounts (use ranges)
- Keys or seed phrases
- User identifiers

---

## Incident Response

### Security Incidents

**If Router Compromised:**
- Remove router from registry
- Notify users (if routes were executed)
- Review logs for suspicious activity
- Update threat model

**If Execution Provider Compromised:**
- Remove provider from registry
- Notify users (if executions occurred)
- Review logs for suspicious activity
- Update threat model

**If Data Leaked:**
- Assess impact
- Notify affected users
- Update security practices
- Document incident

---

## Compliance

### Privacy Regulations

**GDPR (if applicable):**
- Data minimization (only necessary data)
- User consent (route selection, execution)
- Right to deletion (log deletion)

**Best Practices:**
- Privacy by design
- Data minimization
- User control
- Transparency

---

**Last Updated:** [Current Date]  
**Status:** Security assumptions defined — must be enforced in implementation


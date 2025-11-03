# Architectural Decisions

This document records key architectural and technical decisions made during the development of the loan management MVP.

## Domain Model Decisions

### LoanType and ScheduleType as Enums
**Decision:** Use Java enums instead of database tables for loan and schedule types.

**Rationale:** Reduces complexity, eliminates join tables, simpler validation. Types (CONSUMER/CAR/MORTGAGE, ANNUITY/EQUAL_PRINCIPAL) are stable and well-defined for MVP scope.

**Trade-offs:** Less flexible for dynamic type configuration, requires code changes for new types.

## Testing Decisions

### Exclusion of Integration and MVC Tests
**Decision:** Exclude integration and MVC controller tests to reduce development time.

**Rationale:** Focus on core business logic via unit tests. Time constraints for MVP delivery. Manual end-to-end validation acceptable for MVP scope.

**Trade-offs:** Reduced API contract validation, manual testing required.

### Unit Testing with Mocked Dependencies
**Decision:** Mock `ScheduleCalculator` implementations in `RepaymentScheduleServiceTest`.

**Rationale:** Tests service in isolation. Service responsibility is calculator selection/delegation, not calculation logic (tested separately). Faster execution, clearer test intent.

**Implementation:** Mockito mocks verify calculator selection and error handling. Calculators tested in dedicated classes.

## Design Pattern Decisions

### Strategy Pattern for Schedule Calculators
**Decision:** Separate calculation algorithms (Annuity vs Equal Principal) into dedicated calculator classes.

**Rationale:** Isolates calculation-specific code, easier to understand/modify, straightforward to add new schedule types. Shared utilities in abstract base class.

**Implementation:** `ScheduleCalculator` interface, `AbstractScheduleCalculator` base, concrete calculators, `RepaymentScheduleService` delegates via map.

### Collector API for Schedule Building
**Decision:** Use `Collector.of()` to build schedules from payment date stream instead of for-loops.

**Rationale:** Clean accumulation pattern with immutable results. Explicit state tracking (balance, items). Avoids index-based loops.

**Implementation:** `ScheduleAccumulator` holds balance, accumulatedPrincipal, items. Collector built with supplier, accumulator, combiner (parallel disabled), finisher.

### Precision Management in Financial Calculations
**Decision:** Track accumulated principal separately from high-precision balance to ensure exact totals.

**Rationale:** Financial calculations must be exact to the cent. Intermediate calculations use high precision (scale 10+), final results rounded to scale 2.

**Implementation:**
- `ScheduleAccumulator` tracks `accumulatedPrincipal` (sum of rounded principals, scale 2)
- High-precision `balance` (scale 10) maintained for calculations
- Last payment adjustment uses accumulated principal to ensure total equals loan amount exactly
- All tests passing with exact totals verified

### Template Method for Schedule Item Formatting
**Decision:** Use template method pattern for formatting calculations into schedule items.

**Rationale:** Strategy-specific formatting logic (e.g., annuity constant payments) belongs in concrete calculators. Base class provides default rounding behavior.

**Implementation:** `toScheduleItem()` template method in `AbstractScheduleCalculator`. `AnnuityScheduleCalculator` overrides to use constant payment for non-last payments. Removed unused `MonthlyPaymentCalculation` methods.

## Infrastructure Decisions

### Docker Compose for Local Development
**Decision:** Multi-stage Docker builds for zero-configuration local development.

**Rationale:** Consistent environment, no local setup required, production-like testing, faster onboarding.

**Implementation:**
- Multi-stage: Backend (Gradle → JRE Alpine), Frontend (Node.js → Nginx)
- Health checks ensure startup order: database → backend → frontend
- Nginx proxies `/api/*` to backend
- Ports: Database 5432, Backend 8080, Frontend 3000

**Trade-offs:** Larger initial build time, requires Docker knowledge, but consistency benefits outweigh for MVP.

## Unimplemented Features and Future Improvements

### Balloon Loans (Car Lease Support)
**Not Implemented:** Time constraints. Would require `ScheduleType.BALLOON` enum and new calculator. Impact: Car loans use standard schedules, not true leases.

### Separate First Payment Date
**Not Implemented:** Initial assumption that start date = first payment date. Discovered limitation late. Would require migration/API changes. **Limitation:** Unrealistic for real-world scenarios with payment delays.

### Created Date in Frontend
**Not Implemented:** Time constraints, lower priority. **Impact:** No sorting by creation date in frontend.

### CSV Export for Repayment Schedules
**Not Implemented:** Precision issue became priority, nice-to-have deprioritized. **Impact:** Export endpoint `/api/loans/{id}/schedule/export` not implemented. Can be added post-precision fix.

## Known Limitations and Design Mistakes

### Annual Interest Rate Precision
**Limitation:** Interest rates stored as `NUMERIC(5, 2)` (2 decimal places, e.g., 5.00%).

**Issue:** Insufficient precision for floating interest loans based on EURIBOR (European Interbank Offer Rate), which typically requires three decimal places (e.g., 3.456%).

**Impact:** Cannot accurately represent EURIBOR-based floating rates. Would require migration to `NUMERIC(5, 3)` and validation updates.

**Rationale:** Initial design assumed fixed rates. EURIBOR precision requirement discovered later.

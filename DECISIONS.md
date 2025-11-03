# Architectural Decisions

This document records key architectural and technical decisions made during the development of the loan management MVP.

## Design Pattern Decisions

### How We Calculate Payment Schedules

**The Problem:** We need to calculate payment schedules for loans, but there are different calculation methods (Annuity and Equal Principal). Each method has its own formula, but they all need to build a schedule the same way - loop through months, calculate payments, and format results.

**The Solution:** We use two patterns together:
- **Strategy Pattern**: Each calculation method (Annuity, Equal Principal) gets its own calculator class. The service picks which calculator to use based on the loan type.
- **Template Method Pattern**: The actual schedule-building loop is shared in a base class. Each calculator provides its specific calculation logic, but the loop structure is the same.

**Why This Works:** 
- Adding a new schedule type? Just create a new calculator class. No need to touch existing code.
- Need to fix the building loop? Fix it once in the base class, not in every calculator.
- Want to test each calculator? They're separate classes, easy to test in isolation.

**What We Tried Instead:**
- One big method with if/else statements: Got messy fast, lots of duplicated code.
- Just Strategy pattern (no shared loop): Each calculator duplicated the same loop code.
- This approach: Clean, no duplication, easy to extend.

**The Cost:** We have a few more classes (3-4 instead of 1), but it's worth it. The code is easier to maintain and test. If you understand basic inheritance and interfaces, you'll get this pattern quickly.

### Building Schedules with Streams

**The Problem:** We need to build a list of payment items, one for each month. Each month's calculation depends on the previous month's balance.

**The Solution:** Use Java's `Collector.of()` to build up the schedule from a stream of dates. This keeps the state management explicit (current balance, accumulated principal, list of items) and avoids index-based loops.

**Why This Works:** Clear separation of concerns. The accumulator holds the state, the collector handles the iteration. We can see exactly what state we're tracking.

### Money Precision and Rounding

**The Problem:** Financial calculations need to be exact to the cent. But we do lots of intermediate math with decimal places, and rounding errors can add up.

**The Solution:** 
- Do all intermediate calculations with high precision (10+ decimal places)
- Track the sum of rounded principals separately (to 2 decimal places)
- Only round the final displayed values
- For the last payment, adjust it so total principal exactly equals the loan amount

**Why This Matters:** Without this, rounding errors mean the total principal might be $1000.01 instead of exactly $1000.00. In finance, that's unacceptable.

**How We Know It Works:** All tests verify that totals match exactly, including large loans over long terms.

## Other Decisions

### Loan Types as Enums

We store loan types (CONSUMER, CAR, MORTGAGE) and schedule types (ANNUITY, EQUAL_PRINCIPAL) as Java enums, not database tables. They're stable for an MVP, and this keeps things simple. If we need dynamic types later, we'll need a migration.

### Testing Approach

We focused on unit tests for the calculation logic. No integration tests or controller tests for now - time was tight for MVP. Each calculator is tested independently, and the service is tested with mocked calculators.

### Docker Setup

Everything runs in Docker Compose for zero-configuration local development. Database, backend, and frontend all start together with health checks. Takes longer to build initially, but everyone gets the same environment.

## Things We Didn't Build

### Balloon Loans
Would need a new schedule type and calculator. Car loans just use standard schedules for now.

### Separate First Payment Date
We assumed start date = first payment date. Real loans often have a delay. Would need schema changes to fix this.

### CSV Export
The export endpoint exists in the API design but isn't implemented. Precision fixes were higher priority.

## Known Issues

### Interest Rate Precision
We store interest rates with 2 decimal places (e.g., 5.00%). But EURIBOR-based floating rates need 3 decimal places (e.g., 3.456%). We can't accurately represent those yet. Would need a database migration to `NUMERIC(5, 3)` and validation updates.

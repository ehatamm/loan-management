# Architectural Decisions

This document records key architectural and technical decisions made during the development of the loan management MVP.

## Domain Model Decisions

### LoanType and ScheduleType as Enums
**Decision:** Use Java enums (`LoanType`, `ScheduleType`) instead of extendable database tables.

**Rationale:**
- Reduces complexity in the MVP
- Loan types (CONSUMER, CAR, MORTGAGE) and schedule types (ANNUITY, EQUAL_PRINCIPAL) are stable and well-defined
- Eliminates need for join tables and additional queries
- Simpler to validate and use in business logic
- Faster to implement and maintain

**Trade-offs:**
- Less flexible if new types need to be added frequently (would require code changes and redeployment)
- Not suitable if types need to be configurable by end users
- Acceptable for MVP scope where types are fixed and defined

## Testing Decisions

### Exclusion of Integration and MVC Tests
**Decision:** Exclude integration tests and MVC controller tests to reduce development time.

**Rationale:**
- Focus on core business logic validation through unit tests
- Time constraints for MVP delivery
- Unit tests for repayment schedule calculations provide sufficient coverage for critical functionality
- End-to-end validation can be performed manually during development

**Trade-offs:**
- Reduced confidence in API contract validation
- Manual testing required for endpoint behavior
- Potential for integration issues not caught by tests
- Acceptable for MVP scope where speed of delivery is prioritized

## Technology Stack Decisions

### Use of Lombok
**Decision:** Include Lombok for reducing boilerplate code in JPA entities.

**Rationale:**
- Industry standard in Spring Boot projects
- Reduces code verbosity while maintaining clarity
- Improves maintainability with less boilerplate
- Common practice in production Spring Boot applications

**Annotations Used:**
- `@Data` - generates getters, setters, toString, equals, hashCode
- `@NoArgsConstructor` - required by JPA
- `@AllArgsConstructor` - enables full constructor
- `@Builder` - provides builder pattern for cleaner object creation

## Design Pattern Decisions

### Strategy Pattern for Schedule Calculators
**Decision:** Use the Strategy pattern to separate different repayment schedule calculation algorithms (Annuity vs Equal Principal) into dedicated calculator classes.

**Rationale:**
- Each calculation type has distinct logic that was becoming difficult to maintain in a single service class
- Allows us to isolate calculation-specific code, making it easier to understand and modify
- Makes adding new schedule types straightforward - just create a new calculator class
- Shared utilities (like monthly rate calculation, interest calculation, schedule building) are centralized in the abstract base class
- Service layer becomes a simple facade that delegates to the right calculator based on loan schedule type

**Trade-offs:**
- Slightly more files to navigate compared to having everything in one service
- Requires understanding of the pattern, but it's a well-known design pattern
- Adds a bit of indirection, but the code organization benefits outweigh this

**Implementation:**
- `ScheduleCalculator` interface defines the contract
- `AbstractScheduleCalculator` provides shared functionality
- `AnnuityScheduleCalculator` and `EqualPrincipalScheduleCalculator` implement specific algorithms
- `RepaymentScheduleService` uses a map of calculators to delegate calls

### Collector API for Schedule Building
**Decision:** Use Java's `Collector.of()` to build repayment schedules from a stream of payment dates, instead of traditional for-loops or mutable state arrays.

**Rationale:**
- The schedule building logic requires accumulating payment items while maintaining a running balance that changes with each iteration
- `Collector.of()` provides a clean way to handle this accumulation pattern with immutable intermediate results
- More functional programming style that fits well with Java streams
- The accumulator pattern makes it explicit that we're building up a list while tracking state (the remaining balance)
- Avoids the need for index-based loops or array manipulation

**Trade-offs:**
- Might be less familiar to developers who haven't worked with custom collectors before
- Slightly more complex than a simple for-loop, but much cleaner than managing mutable state manually
- We explicitly disallow parallel streams since each month's calculation depends on the previous month's balance

**Implementation:**
- `ScheduleAccumulator` holds the current balance and list of items
- `Collector.of()` creates a custom collector with supplier, accumulator, combiner (throws exception for parallel), and finisher
- Stream of dates is generated with `Stream.iterate()`, then collected into the final schedule


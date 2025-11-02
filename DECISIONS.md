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


# TicketServiceTest - Test Documentation

## Overview

The `TicketServiceTest` class provides comprehensive unit tests for the `TicketService` functionality. These tests ensure that all ticket management operations work correctly and handle edge cases appropriately.

## Test Class Information

- **Package**: `com.apex.firefighter.ticket`
- **Test Framework**: JUnit 5 with Spring Boot Test
- **Database**: Uses in-memory H2 database for testing
- **Transaction Management**: `@Transactional` annotation ensures test isolation

## Test Cases

### 1. `testCreateAndGetTicket()`
**Purpose**: Tests basic ticket creation and retrieval functionality.

**Test Steps**:
- Creates a new ticket with ID "JIRA-123" and all new fields
- Verifies all ticket properties (ID, description, validity, userId, etc.)
- Tests retrieval by database ID
- Tests retrieval by ticket ID

**Expected Results**:
- Ticket is created successfully
- All properties match the input values
- Ticket can be retrieved by both ID types

### 2. `testUpdateTicket()`
**Purpose**: Tests the consolidated ticket update operation.

**Test Steps**:
- Creates a ticket with original data
- Updates multiple fields including description, validity, status, emergencyType, and emergencyContact in a single call.
- Verifies changes persist in database

**Expected Results**:
- All specified fields are updated successfully
- Changes are persisted and retrievable

### 3. `testDeleteTicket()`
**Purpose**: Tests ticket deletion by both database ID and ticket ID.

**Test Steps**:
- Creates tickets for deletion testing
- Deletes ticket by database ID
- Verifies ticket is removed
- Deletes ticket by ticket ID
- Verifies second ticket is removed

**Expected Results**:
- Deletion operations return `true`
- Deleted tickets are no longer retrievable
- Both deletion methods work correctly

### 4. `testVerifyTicket()`
**Purpose**: Tests ticket verification functionality.

**Test Steps**:
- Creates a valid ticket and verifies it
- Creates an invalid ticket and verifies it
- Attempts to verify non-existent ticket

**Expected Results**:
- Valid tickets return `true` when verified
- Invalid tickets return `false` when verified
- Non-existent tickets return `false`
- Verification count and timestamp are updated

### 5. `testGetAllTickets()`
**Purpose**: Tests retrieval of all tickets from the database.

**Test Steps**:
- Creates multiple tickets (valid and invalid)
- Retrieves all tickets
- Verifies count and content

**Expected Results**:
- All created tickets are returned
- Correct number of tickets is retrieved
- Ticket IDs match expected values

### 6. `testUpdateNonExistentTicket()`
**Purpose**: Tests error handling for operations on non-existent tickets.

**Test Steps**:
- Attempts to update a non-existent ticket using the consolidated `updateTicket` method.

**Expected Results**:
- The operation throws `RuntimeException`
- Error handling works correctly

## Running the Tests

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Spring Boot 3.x

### Command Line
```bash
# Run all tests
mvn test

# Run only TicketServiceTest
mvn test -Dtest=TicketServiceTest

# Run specific test method
mvn test -Dtest=TicketServiceTest#testCreateAndGetTicket

# Run with verbose output
mvn test -Dtest=TicketServiceTest -X
```

### IDE Integration
- **IntelliJ IDEA**: Right-click on test class or method → "Run"
- **Eclipse**: Right-click on test class or method → "Run As" → "JUnit Test"
- **VS Code**: Use Java Test Runner extension

## Test Configuration

### Database Configuration
The tests use an in-memory H2 database configured in `application-test.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

### Transaction Management
- Each test method runs in its own transaction
- Transactions are rolled back after each test
- Ensures test isolation and clean state

## Dependencies

### Required Dependencies
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>
```

## Best Practices

### Test Design
- Each test method focuses on a single functionality
- Tests are independent and can run in any order
- Clear, descriptive test method names
- Proper setup and teardown using `@Transactional`

### Assertions
- Uses AssertJ for fluent assertions
- Comprehensive validation of expected results
- Tests both positive and negative scenarios
- Verifies edge cases and error conditions

### Data Management
- Uses realistic test data
- Tests with various data combinations
- Verifies data persistence and retrieval
- Tests data integrity constraints

## Troubleshooting

### Common Issues

1. **Test Database Connection**
   - Ensure H2 dependency is included
   - Check application-test.properties configuration
   - Verify database dialect settings

2. **Transaction Issues**
   - Ensure `@Transactional` annotation is present
   - Check for nested transaction conflicts
   - Verify rollback behavior

3. **Dependency Injection**
   - Ensure `@Autowired` annotations are correct
   - Check component scanning configuration
   - Verify service and repository beans are available

### Debug Tips
- Use `@DirtiesContext` if tests affect application context
- Add logging to understand test flow
- Use breakpoints in IDE for step-by-step debugging
- Check test output for detailed error messages

## Coverage

The test suite covers:
- ✅ CRUD operations (Create, Read, Update, Delete)
- ✅ Business logic validation
- ✅ Error handling and edge cases
- ✅ Data persistence verification
- ✅ Service method integration
- ✅ Repository interaction

## Maintenance

### Adding New Tests
1. Follow existing naming conventions
2. Use descriptive test method names
3. Include both positive and negative test cases
4. Add appropriate assertions
5. Document complex test scenarios

### Updating Tests
- Update tests when service methods change
- Maintain test data consistency
- Review test coverage regularly
- Update documentation when needed

---

**Last Updated**: June 2024  
**Test Framework**: JUnit 5 + Spring Boot Test  
**Database**: H2 (In-Memory)  
**Coverage**: Comprehensive CRUD and business logic testing 
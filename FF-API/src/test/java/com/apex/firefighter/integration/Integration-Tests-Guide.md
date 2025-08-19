# Integration Tests

This directory contains integration tests that verify the interaction between multiple components and external systems.

## Directory Structure

```
integration/
├── api/                # API integration tests
├── database/           # Database integration tests
├── external-services/  # External service integration tests
└── README.md          # This file
```

## Test Categories

### API Integration (`api/`)
Tests that verify the complete API functionality from HTTP request to response.
- **Purpose**: End-to-end API testing, request/response validation
- **Scope**: Full application context with real or embedded dependencies
- **Technology**: Spring Boot Test, TestRestTemplate, MockMvc

**Files:**
- `SwaggerIntegrationTest.java` - API documentation integration
- `FireFighterAccessManagementApplicationTests.java` - Application startup test

### Database Integration (`database/`)
Tests that verify database operations with real database connections.
- **Purpose**: Database schema, queries, transactions, data integrity
- **Scope**: Repository layer with real database (H2 in-memory for tests)
- **Technology**: Spring Boot Test, TestContainers (optional)

**Files:**
- `NOTIFICATIONS_TESTING_README.md` - Notification database testing guide
- `NOTIFICATION_DATABASE_SETUP.md` - Database setup for notification tests
- `TICKET_DATABASE_SETUP.md` - Database setup for ticket tests
- `TicketService_README.md` - Ticket service testing documentation
- `TicketService_Postman_README.md` - Postman collection usage guide

### External Services (`external-services/`)
Tests that verify integration with external APIs and services.
- **Purpose**: Third-party service integration, API contracts
- **Scope**: Service layer with real or mocked external services
- **Technology**: WireMock, TestContainers, Contract Testing

**Files:**
- *No external service tests currently exist - needs to be added*

## Running Integration Tests

### Run All Integration Tests
```bash
mvn test -Dtest="com.apex.firefighter.integration.**"
```

### Run Specific Integration Categories
```bash
# API integration tests only
mvn test -Dtest="com.apex.firefighter.integration.api.**"

# Database integration tests only
mvn test -Dtest="com.apex.firefighter.integration.database.**"

# External service integration tests only
mvn test -Dtest="com.apex.firefighter.integration.external-services.**"
```

### Run with Different Profiles
```bash
# Run with test profile
mvn test -Dspring.profiles.active=test

# Run with integration test profile
mvn test -Dspring.profiles.active=integration-test
```

## Test Configuration

### Application Properties
Integration tests use `application-test.properties` for configuration:
- In-memory H2 database for fast, isolated testing
- Disabled external service calls (or mocked)
- Test-specific logging configuration

### Database Setup
- **H2 In-Memory**: Fast, isolated, reset between test classes
- **Schema**: Automatically created from `schema.sql` in test resources
- **Data**: Test data loaded from SQL scripts or test fixtures

### Test Profiles
- `test`: Default test profile with mocked external dependencies
- `integration-test`: Profile for testing with real external services
- `local-integration`: Profile for local development integration testing

## Test Guidelines

### Writing Integration Tests
1. **Realistic**: Use realistic data and scenarios
2. **Independent**: Tests should not depend on each other
3. **Cleanup**: Clean up test data after each test
4. **Idempotent**: Tests should produce same results when run multiple times
5. **Environment**: Tests should work in any environment

### Test Data Management
```java
@TestMethodOrder(OrderAnnotation.class)
@Transactional
@Rollback
class DatabaseIntegrationTest {
    
    @BeforeEach
    void setUp() {
        // Set up test data
        testDataHelper.createTestUsers();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up if needed (usually handled by @Rollback)
    }
}
```

### API Testing Pattern
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldCreateNotification_whenValidRequest() {
        // Arrange
        NotificationRequest request = new NotificationRequest("Test message");
        
        // Act
        ResponseEntity<NotificationResponse> response = restTemplate
            .postForEntity("/api/notifications", request, NotificationResponse.class);
        
        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getMessage()).isEqualTo("Test message");
    }
}
```

## Test Resources

### SQL Scripts (`src/test/resources/sql/`)
- `create_notifications_table.sql` - Notification table schema
- `create_tickets_table.sql` - Ticket table schema
- `schema.sql` - Complete test database schema

### Postman Collections (`src/test/resources/postman/`)
- API testing collections for manual and automated testing
- Environment configurations for different test environments

### Scripts (`src/test/resources/scripts/`)
- `run-notification-tests.sh` - Automated notification testing script
- Helper scripts for test setup and execution

## Coverage and Quality

### Integration Test Coverage
- **API Endpoints**: Verify all REST endpoints work correctly
- **Database Operations**: Test all CRUD operations and complex queries
- **Business Workflows**: Test complete user journeys
- **Error Scenarios**: Test error handling and edge cases

### Performance Considerations
- Integration tests are slower than unit tests
- Use `@DirtiesContext` sparingly to avoid context reloading
- Consider using TestContainers for more realistic database testing
- Profile tests to identify slow tests

## Continuous Integration

### CI Pipeline Integration
```yaml
# Example GitHub Actions configuration
- name: Run Integration Tests
  run: mvn test -Dtest="com.apex.firefighter.integration.**"
  env:
    SPRING_PROFILES_ACTIVE: test
```

### Test Reports
- Surefire reports generated in `target/surefire-reports/`
- JaCoCo coverage reports include integration test coverage
- Test results integrated with CI/CD pipeline

## Next Steps

1. **Add database integration tests** for complex queries and transactions
2. **Create external service integration tests** for AI services, email, etc.
3. **Implement contract testing** for API versioning
4. **Add performance integration tests** for critical endpoints
5. **Set up TestContainers** for more realistic database testing

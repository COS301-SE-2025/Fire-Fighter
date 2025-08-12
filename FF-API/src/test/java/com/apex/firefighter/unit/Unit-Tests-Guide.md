# Unit Tests

This directory contains all unit tests for the FireFighter Access Management API. Unit tests focus on testing individual components in isolation with minimal dependencies.

## Directory Structure

```
unit/
├── controllers/     # Controller layer tests
├── services/        # Service layer tests  
├── repositories/    # Repository layer tests
├── models/          # Model/Entity tests
├── config/          # Configuration tests (moved here from top-level test package)
├── dto/             # DTO tests (moved here from top-level test package)
└── README.md        # This file
```

## Test Categories

### Controllers (`controllers/`)
Tests for REST API controllers that handle HTTP requests and responses.
- **Purpose**: Verify request mapping, validation, response formatting
- **Scope**: Controller logic only, services are mocked
- **Current Coverage**: Low (11% instruction coverage)

**Files:**
- `NotificationControllerTest.java` - Tests notification endpoints

### Services (`services/`)
Tests for business logic and service layer components.
- **Purpose**: Verify business rules, data processing, external integrations
- **Scope**: Service logic with mocked dependencies
- **Current Coverage**: Variable (9-52% depending on service)

**Files:**
- `NotificationServiceTest.java` - Notification business logic
- `TicketScheduledServiceTest.java` - Scheduled ticket operations
- `TicketServiceTest.java` - Ticket management logic
- `ChatbotServiceTest.java` - AI chatbot functionality

### Repositories (`repositories/`)
Tests for data access layer components.
- **Purpose**: Verify database queries, data mapping, CRUD operations
- **Scope**: Repository methods with in-memory H2 database
- **Current Coverage**: Good (repository tests are comprehensive)

**Files:**
- `AccessLogRepositoryTest.java` - Access logging data access
- `AccessRequestRepositoryTest.java` - Access request data access
- `AccessSessionRepositoryTest.java` - Session management data access
- `NotificationRepositoryTest.java` - Notification data access
- `RoleRepositoryTest.java` - Role management data access
- `TicketRepositoryTest.java` - Ticket data access
- `UserRepositoryTest.java` - User data access

### Models (`models/`)
Tests for entity classes and data models.
- **Purpose**: Verify entity validation, relationships, serialization
- **Scope**: Model classes in isolation
- **Current Coverage**: Good (40% instruction coverage)

**Files:**
- `AccessLogTest.java`
- `AccessRequestTest.java`
- `AccessSessionTest.java`
- `TicketTest.java`
- `UserPreferencesTest.java`
- `UserRoleTest.java`
- `UserTest.java`

### Config (`config/`)
Tests for configuration classes and beans.
- **Purpose**: Verify configuration setup, bean creation, properties
- **Scope**: Configuration classes
- **Current Coverage**: Excellent (67% instruction coverage)

**Files:**
- `AIConfigTest.java`
- `SecurityConfigTest.java`

### DTO (`dto/`)
Tests for Data Transfer Objects.
- **Purpose**: Verify DTO construction, getters/setters, and `toString` formatting
- **Scope**: Pure POJOs, no Spring context

**Files:**
- `TicketCreateRequestTest.java`

## Running Unit Tests

### Run All Unit Tests
```bash
mvn test -Dtest="com.apex.firefighter.unit.**"
```

### Run Specific Test Categories
```bash
# Controllers only
mvn test -Dtest="com.apex.firefighter.unit.controllers.**"

# Services only  
mvn test -Dtest="com.apex.firefighter.unit.services.**"

# Repositories only
mvn test -Dtest="com.apex.firefighter.unit.repositories.**"

# Models only
mvn test -Dtest="com.apex.firefighter.unit.models.**"

# Config only
mvn test -Dtest="com.apex.firefighter.unit.config.**"

# DTO only
mvn test -Dtest="com.apex.firefighter.unit.dto.**"
```

### Run Individual Test Classes
```bash
mvn test -Dtest="NotificationServiceTest"
```

## Test Guidelines

### Writing Unit Tests
1. **Isolation**: Mock all external dependencies
2. **Fast**: Tests should run quickly (< 1 second each)
3. **Deterministic**: Same input should always produce same output
4. **Focused**: Test one specific behavior per test method
5. **Clear**: Test names should describe what is being tested

### Naming Conventions
- Test classes: `{ClassUnderTest}Test.java`
- Test methods: `should{ExpectedBehavior}_when{StateUnderTest}`
- Example: `shouldReturnNotifications_whenUserIdExists()`

### Test Structure (AAA Pattern)
```java
@Test
void shouldReturnUser_whenValidIdProvided() {
    // Arrange
    Long userId = 1L;
    User expectedUser = new User("John", "john@example.com");
    when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
    
    // Act
    User actualUser = userService.findById(userId);
    
    // Assert
    assertThat(actualUser).isEqualTo(expectedUser);
}
```

## Coverage Goals

| Component | Current Coverage | Target Coverage |
|-----------|------------------|-----------------|
| Controllers | 11% | 80% |
| Services | 9-52% | 80% |
| Repositories | Good | 90% |
| Models | 40% | 70% |
| Config | 67% | 80% |

## Next Steps

1. **Add missing test classes** for models and config
2. **Improve controller test coverage** - currently very low
3. **Enhance service test coverage** - focus on untested services
4. **Add edge case testing** for all components
5. **Implement test utilities** for common test scenarios

# FireFighter API Test Suite

This directory contains the complete test suite for the FireFighter Access Management API. The tests are organized into logical categories for better maintainability and understanding.

## 📁 Directory Structure

```
src/test/
├── java/com/apex/firefighter/
│   ├── unit/                    # Unit tests (isolated component testing)
│   │   ├── controllers/         # REST controller tests
│   │   ├── services/           # Business logic tests
│   │   ├── repositories/       # Data access tests
│   │   ├── models/             # Entity/model tests
│   │   └── config/             # Configuration tests
│   ├── integration/            # Integration tests (component interaction)
│   │   ├── api/                # End-to-end API tests
│   │   ├── database/           # Database integration tests
│   │   └── external-services/  # External service integration tests
│   └── utils/                  # Test utilities and helpers
│       ├── base/               # Base test classes
│       ├── fixtures/           # Test data builders
│       ├── helpers/            # Test helper utilities
│       └── mocks/              # Mock configurations
├── resources/                  # Test resources
│   ├── sql/                    # Database scripts
│   ├── postman/               # API testing collections
│   └── scripts/               # Test automation scripts
└── README.md                  # This file
```

## 🎯 Test Categories

### Unit Tests (`unit/`)
**Purpose**: Test individual components in isolation
**Coverage**: 17% overall (needs improvement)
**Speed**: Fast (< 1 second per test)

- **Controllers** (11% coverage) - HTTP request/response handling
- **Services** (9-52% coverage) - Business logic and processing
- **Repositories** (Good coverage) - Data access operations
- **Models** (40% coverage) - Entity validation and behavior
- **Config** (67% coverage) - Application configuration

### Integration Tests (`integration/`)
**Purpose**: Test component interactions and external integrations
**Coverage**: Basic API and database integration
**Speed**: Medium (1-10 seconds per test)

- **API** - End-to-end API functionality testing
- **Database** - Database schema and query testing
- **External Services** - Third-party service integration

### Test Utilities (`utils/`)
**Purpose**: Reusable test components and helpers
**Usage**: Shared across all test types

- **Base Classes** - Common test setup and configuration
- **Fixtures** - Test data builders and factories
- **Helpers** - Utility methods for testing
- **Mocks** - Pre-configured mock objects

## 🚀 Quick Start

### Run All Tests
```bash
mvn test
```

### Run Specific Test Categories
```bash
# Unit tests only
mvn test -Dtest="com.apex.firefighter.unit.**"

# Integration tests only  
mvn test -Dtest="com.apex.firefighter.integration.**"

# Specific layer tests
mvn test -Dtest="com.apex.firefighter.unit.services.**"
```

### Generate Coverage Report
```bash
mvn clean test jacoco:report
```
View report: `target/site/jacoco/index.html`

## 📊 Current Test Status

### Overall Coverage: 17%
- **Instructions**: 2,116 of 12,017 covered
- **Branches**: 69 of 622 covered  
- **Methods**: 177 of 533 covered
- **Classes**: 44 of 47 covered

### Test Count by Category
- **Unit Tests**: 14 test classes
- **Integration Tests**: 2 test classes
- **Total Test Methods**: ~50+ test methods

### Coverage by Component
| Component | Coverage | Status | Priority |
|-----------|----------|--------|----------|
| Controllers | 11% | ❌ Needs Work | High |
| Services | 9-52% | ⚠️ Variable | High |
| Repositories | Good | ✅ Complete | Low |
| Models | 40% | ⚠️ Partial | Medium |
| Config | 67% | ✅ Good | Low |

## 🎯 Testing Strategy

### Test Pyramid
```
    /\     E2E Tests (Few)
   /  \    Integration Tests (Some)  
  /____\   Unit Tests (Many)
```

- **Unit Tests (70%)**: Fast, isolated, comprehensive coverage
- **Integration Tests (20%)**: Component interaction verification
- **E2E Tests (10%)**: Critical user journey validation

### Test Types by Purpose
- **Functional Tests**: Verify business requirements
- **Regression Tests**: Prevent breaking changes
- **Performance Tests**: Ensure acceptable response times
- **Security Tests**: Validate authentication/authorization
- **Contract Tests**: API compatibility verification

## 🛠️ Development Workflow

### Before Committing
```bash
# Run tests and check coverage
mvn clean test jacoco:report

# Ensure minimum coverage thresholds
# Controllers: 80%, Services: 80%, Overall: 70%
```

### Test-Driven Development (TDD)
1. **Red**: Write failing test
2. **Green**: Write minimal code to pass
3. **Refactor**: Improve code while keeping tests green

### Adding New Features
1. Write unit tests for new components
2. Write integration tests for new endpoints
3. Update existing tests if behavior changes
4. Ensure coverage targets are met

## 📋 Test Guidelines

### Naming Conventions
- **Test Classes**: `{ClassUnderTest}Test.java`
- **Test Methods**: `should{ExpectedBehavior}_when{StateUnderTest}`
- **Test Data**: Use meaningful, realistic data

### Test Structure (AAA Pattern)
```java
@Test
void shouldReturnUser_whenValidIdProvided() {
    // Arrange - Set up test data and mocks
    Long userId = 1L;
    User expectedUser = UserFixture.builder().build();
    when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
    
    // Act - Execute the method under test
    User actualUser = userService.findById(userId);
    
    // Assert - Verify the results
    assertThat(actualUser).isEqualTo(expectedUser);
    verify(userRepository).findById(userId);
}
```

### Best Practices
- ✅ **One assertion per test** (when possible)
- ✅ **Mock external dependencies**
- ✅ **Use descriptive test names**
- ✅ **Test edge cases and error scenarios**
- ✅ **Keep tests independent**
- ❌ **Don't test framework code**
- ❌ **Don't use real external services in unit tests**

## 🔧 Test Configuration

### Test Profiles
- **test**: Default profile with mocked dependencies
- **integration-test**: Profile for integration testing
- **local-test**: Profile for local development testing

### Database Configuration
- **Unit Tests**: H2 in-memory database
- **Integration Tests**: H2 with persistent schema
- **Local Testing**: PostgreSQL (optional)

### Mock Configuration
- **Services**: Mockito for service layer mocking
- **External APIs**: WireMock for HTTP service mocking
- **Database**: @DataJpaTest for repository testing

## 📈 Improvement Roadmap

### Phase 1: Foundation (Current)
- ✅ Organize test structure
- ✅ Document test guidelines
- ✅ Set up coverage reporting

### Phase 2: Coverage Improvement
- 🎯 Increase controller test coverage to 80%
- 🎯 Add missing service tests (GmailEmailService, UserService, etc.)
- 🎯 Improve overall coverage to 70%

### Phase 3: Quality Enhancement
- 🎯 Add performance tests for critical endpoints
- 🎯 Implement contract testing for API versioning
- 🎯 Add security testing for authentication/authorization

### Phase 4: Automation
- 🎯 Set up automated test execution in CI/CD
- 🎯 Add test quality gates (coverage thresholds)
- 🎯 Implement test result reporting and notifications

## 🆘 Troubleshooting

### Common Issues
- **Tests fail locally but pass in CI**: Check test isolation and cleanup
- **Slow test execution**: Review database setup and mock usage
- **Flaky tests**: Check for timing issues and external dependencies

### Getting Help
- Check individual README files in each test directory
- Review existing test examples for patterns
- Consult team documentation for project-specific guidelines

## 📚 Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [AssertJ Documentation](https://assertj.github.io/doc/)

---

**Last Updated**: 2025-01-07
**Test Framework**: JUnit 5 + Mockito + Spring Boot Test
**Coverage Tool**: JaCoCo

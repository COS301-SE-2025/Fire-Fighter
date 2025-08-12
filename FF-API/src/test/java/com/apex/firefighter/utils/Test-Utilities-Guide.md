# Test Utilities

This directory contains reusable test utilities, helpers, fixtures, and base classes to support testing across the application.

## Directory Structure

```
utils/
├── base/        # Base test classes and abstract test classes
├── fixtures/    # Test data fixtures and builders
├── helpers/     # Test helper classes and utilities
├── mocks/       # Mock objects and mock configurations
└── README.md    # This file
```

## Utility Categories

### Base Classes (`base/`)
Abstract base classes and common test configurations.
- **Purpose**: Reduce code duplication, provide common test setup
- **Usage**: Extend these classes in your test classes
- **Examples**: BaseRepositoryTest, BaseServiceTest, BaseControllerTest

**Recommended Files to Create:**
- `BaseRepositoryTest.java` - Common repository test setup
- `BaseServiceTest.java` - Common service test setup with mocks
- `BaseControllerTest.java` - Common controller test setup with MockMvc
- `BaseIntegrationTest.java` - Common integration test setup

### Fixtures (`fixtures/`)
Test data builders and fixtures for creating test objects.
- **Purpose**: Consistent test data creation, reduce test setup code
- **Pattern**: Builder pattern for flexible object creation
- **Usage**: Create realistic test data with minimal code

**Recommended Files to Create:**
- `UserFixture.java` - User entity test data builder
- `NotificationFixture.java` - Notification entity test data builder
- `TicketFixture.java` - Ticket entity test data builder
- `AccessRequestFixture.java` - Access request test data builder

### Helpers (`helpers/`)
Utility classes for common test operations.
- **Purpose**: Reusable test logic, assertion helpers, test utilities
- **Usage**: Static methods for common test operations
- **Examples**: Database helpers, JSON helpers, assertion helpers

**Recommended Files to Create:**
- `TestDataHelper.java` - Database test data management
- `JsonTestHelper.java` - JSON serialization/deserialization helpers
- `AssertionHelper.java` - Custom assertion methods
- `TestSecurityHelper.java` - Security context setup for tests

### Mocks (`mocks/`)
Mock objects and mock configurations.
- **Purpose**: Consistent mock behavior, reusable mock setups
- **Usage**: Pre-configured mocks for common dependencies
- **Pattern**: Mock factories and builders

**Recommended Files to Create:**
- `MockUserService.java` - Pre-configured UserService mock
- `MockNotificationService.java` - Pre-configured NotificationService mock
- `MockEmailService.java` - Pre-configured EmailService mock
- `MockAIService.java` - Pre-configured AI service mocks

## Usage Examples

### Base Test Class Example
```java
// base/BaseServiceTest.java
@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest {
    
    @Mock
    protected UserRepository userRepository;
    
    @Mock
    protected NotificationRepository notificationRepository;
    
    protected User createTestUser() {
        return UserFixture.builder()
            .withEmail("test@example.com")
            .withUsername("testuser")
            .build();
    }
    
    protected void verifyNoMoreInteractionsOnMocks() {
        verifyNoMoreInteractions(userRepository, notificationRepository);
    }
}
```

### Fixture Example
```java
// fixtures/UserFixture.java
public class UserFixture {
    
    private String email = "default@example.com";
    private String username = "defaultuser";
    private String firstName = "John";
    private String lastName = "Doe";
    private Set<Role> roles = new HashSet<>();
    
    public static UserFixture builder() {
        return new UserFixture();
    }
    
    public UserFixture withEmail(String email) {
        this.email = email;
        return this;
    }
    
    public UserFixture withUsername(String username) {
        this.username = username;
        return this;
    }
    
    public UserFixture withRole(Role role) {
        this.roles.add(role);
        return this;
    }
    
    public User build() {
        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRoles(roles);
        return user;
    }
    
    public User buildAndSave(UserRepository repository) {
        return repository.save(build());
    }
}
```

### Helper Example
```java
// helpers/TestDataHelper.java
@Component
@TestConfiguration
public class TestDataHelper {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    public User createTestUser(String email) {
        return UserFixture.builder()
            .withEmail(email)
            .buildAndSave(userRepository);
    }
    
    public Role createTestRole(String name) {
        Role role = new Role();
        role.setName(name);
        return roleRepository.save(role);
    }
    
    public void cleanupTestData() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }
}
```

### Mock Configuration Example
```java
// mocks/MockUserService.java
public class MockUserService {
    
    public static UserService createMock() {
        UserService mock = Mockito.mock(UserService.class);
        
        // Default behavior
        when(mock.findById(anyLong()))
            .thenReturn(UserFixture.builder().build());
            
        when(mock.findByEmail(anyString()))
            .thenReturn(Optional.of(UserFixture.builder().build()));
            
        return mock;
    }
    
    public static UserService createMockWithUser(User user) {
        UserService mock = createMock();
        when(mock.findById(user.getId())).thenReturn(user);
        when(mock.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        return mock;
    }
}
```

## Best Practices

### Test Data Management
1. **Consistent Data**: Use fixtures for consistent test data
2. **Minimal Data**: Create only the data needed for the test
3. **Realistic Data**: Use realistic values that match production data
4. **Cleanup**: Always clean up test data after tests

### Mock Management
1. **Reusable Mocks**: Create reusable mock configurations
2. **Behavior Verification**: Verify mock interactions when important
3. **Reset Mocks**: Reset mocks between tests if needed
4. **Minimal Mocking**: Mock only what's necessary for the test

### Helper Utilities
1. **Single Responsibility**: Each helper should have a single purpose
2. **Static Methods**: Use static methods for stateless utilities
3. **Clear Naming**: Method names should clearly indicate their purpose
4. **Documentation**: Document complex helper methods

## Integration with Test Classes

### Using Base Classes
```java
class UserServiceTest extends BaseServiceTest {
    
    @InjectMocks
    private UserService userService;
    
    @Test
    void shouldFindUser_whenValidId() {
        // Arrange
        User testUser = createTestUser(); // From BaseServiceTest
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // Act & Assert
        User result = userService.findById(1L);
        assertThat(result).isEqualTo(testUser);
    }
}
```

### Using Fixtures
```java
@Test
void shouldCreateNotification_whenValidData() {
    // Arrange
    User user = UserFixture.builder()
        .withEmail("test@example.com")
        .build();
        
    Notification notification = NotificationFixture.builder()
        .withUser(user)
        .withMessage("Test message")
        .build();
    
    // Act & Assert
    // ... test logic
}
```

## Testing the Test Utilities

Test utilities should also be tested to ensure they work correctly:

```java
class UserFixtureTest {
    
    @Test
    void shouldCreateUserWithDefaultValues() {
        User user = UserFixture.builder().build();
        
        assertThat(user.getEmail()).isEqualTo("default@example.com");
        assertThat(user.getUsername()).isEqualTo("defaultuser");
    }
    
    @Test
    void shouldCreateUserWithCustomValues() {
        User user = UserFixture.builder()
            .withEmail("custom@example.com")
            .withUsername("customuser")
            .build();
            
        assertThat(user.getEmail()).isEqualTo("custom@example.com");
        assertThat(user.getUsername()).isEqualTo("customuser");
    }
}
```

## Next Steps

1. **Create base test classes** for common test patterns
2. **Implement fixture builders** for all major entities
3. **Add test helper utilities** for common operations
4. **Create mock configurations** for external dependencies
5. **Document usage patterns** and best practices

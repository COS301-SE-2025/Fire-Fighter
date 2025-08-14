# Service Unit Tests

This directory contains unit tests for service layer components. Service tests focus on business logic, data processing, and service interactions while mocking repository and external dependencies.

## Current Test Files

### NotificationServiceTest.java
Tests for the NotificationService business logic.
- **Coverage**: 52% instruction coverage
- **Features Tested**: CRUD operations, notification polling, statistics

### TicketScheduledServiceTest.java  
Tests for the TicketScheduledService scheduled operations.
- **Coverage**: 66% instruction coverage
- **Features Tested**: Scheduled ticket processing, batch operations

### TicketServiceTest.java
Tests for the TicketService business logic.
- **Coverage**: 36% instruction coverage  
- **Features Tested**: Ticket CRUD, status management, assignment

### ChatbotServiceTest.java
Tests for the ChatbotService AI functionality.
- **Coverage**: 61% instruction coverage
- **Features Tested**: Chat processing, AI integration, response formatting

## Missing Service Tests

The following services need unit tests to be created:

### High Priority (0% Coverage)
- **GmailEmailService** - Email sending functionality (2,364 missed instructions)
- **TicketQueryService** - AI-powered ticket querying (1,473 missed instructions)
- **UserService** - User management operations
- **UserProfileService** - User profile management
- **UserPreferencesService** - User preferences management

### Medium Priority (Low Coverage)
- **AccessRequestService** - Access request processing (4% coverage)
- **AccessSessionService** - Session management (4% coverage)
- **AuthenticationService** - Authentication logic (5% coverage)
- **AuthorizationService** - Authorization logic (5% coverage)
- **RoleService** - Role management (5% coverage)
- **DatabaseConnectionTestService** - Database connectivity testing

### AI Services (Partial Coverage)
- **GeminiAIService** - Google Gemini AI integration (5% coverage)

## Running Service Tests

```bash
# Run all service tests
mvn test -Dtest="com.apex.firefighter.unit.services.**"

# Run specific service test
mvn test -Dtest="NotificationServiceTest"

# Run with coverage
mvn test jacoco:report -Dtest="com.apex.firefighter.unit.services.**"
```

## Service Testing Pattern

### Test Setup
```java
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {
    
    @Mock
    private NotificationRepository notificationRepository;
    
    @Mock
    private UserService userService;
    
    @Mock
    private EmailService emailService;
    
    @InjectMocks
    private NotificationService notificationService;
    
    @BeforeEach
    void setUp() {
        // Additional setup if needed
    }
}
```

### Testing Business Logic
```java
@Test
void shouldCreateNotification_whenValidData() {
    // Arrange
    String userId = "user123";
    String message = "Test notification";
    User user = UserFixture.builder().withId(userId).build();
    Notification expectedNotification = new Notification(message, user);
    
    when(userService.findById(userId)).thenReturn(user);
    when(notificationRepository.save(any(Notification.class)))
        .thenReturn(expectedNotification);
    
    // Act
    Notification result = notificationService.createNotification(userId, message);
    
    // Assert
    assertThat(result.getMessage()).isEqualTo(message);
    assertThat(result.getUser()).isEqualTo(user);
    
    verify(userService).findById(userId);
    verify(notificationRepository).save(any(Notification.class));
}
```

### Testing Error Scenarios
```java
@Test
void shouldThrowException_whenUserNotFound() {
    // Arrange
    String userId = "nonexistent";
    String message = "Test notification";
    
    when(userService.findById(userId))
        .thenThrow(new UserNotFoundException("User not found"));
    
    // Act & Assert
    assertThatThrownBy(() -> 
        notificationService.createNotification(userId, message))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("User not found");
        
    verify(userService).findById(userId);
    verifyNoInteractions(notificationRepository);
}
```

### Testing External Service Integration
```java
@Test
void shouldSendEmail_whenNotificationCreated() {
    // Arrange
    String userId = "user123";
    String message = "Test notification";
    User user = UserFixture.builder()
        .withId(userId)
        .withEmail("user@example.com")
        .build();
    
    when(userService.findById(userId)).thenReturn(user);
    when(notificationRepository.save(any(Notification.class)))
        .thenReturn(new Notification(message, user));
    
    // Act
    notificationService.createNotificationWithEmail(userId, message);
    
    // Assert
    verify(emailService).sendNotificationEmail(
        eq("user@example.com"), 
        eq(message)
    );
}
```

### Testing Async Operations
```java
@Test
void shouldProcessNotificationsAsync_whenScheduledTaskRuns() {
    // Arrange
    List<Notification> pendingNotifications = Arrays.asList(
        new Notification("Message 1"),
        new Notification("Message 2")
    );
    
    when(notificationRepository.findPendingNotifications())
        .thenReturn(pendingNotifications);
    
    // Act
    notificationService.processPendingNotifications();
    
    // Assert
    verify(notificationRepository).findPendingNotifications();
    verify(emailService, times(2)).sendNotificationEmail(anyString(), anyString());
}
```

## Test Guidelines

### What to Test
1. **Business Logic**: Core business rules and calculations
2. **Data Validation**: Input validation and business rule validation
3. **Service Interactions**: Calls to other services and repositories
4. **Error Handling**: Exception scenarios and error responses
5. **State Changes**: Object state modifications and side effects
6. **External Integrations**: Calls to external services (mocked)

### What NOT to Test
1. **Database Queries**: This belongs in repository tests
2. **HTTP Handling**: This belongs in controller tests
3. **Framework Code**: Don't test Spring framework functionality

### Mocking Strategy
```java
// Mock repositories
@Mock
private NotificationRepository notificationRepository;

// Mock other services
@Mock
private UserService userService;
@Mock
private EmailService emailService;

// Mock external clients
@Mock
private RestTemplate restTemplate;

// Don't mock the service under test
@InjectMocks
private NotificationService notificationService;
```

### Assertion Best Practices
```java
// Good: Test business logic results
assertThat(result.getStatus()).isEqualTo(NotificationStatus.SENT);
assertThat(result.getSentAt()).isNotNull();

// Good: Verify service interactions
verify(emailService).sendEmail(
    argThat(email -> email.getTo().equals("user@example.com"))
);

// Good: Test collections
assertThat(notifications)
    .hasSize(2)
    .extracting(Notification::getMessage)
    .containsExactly("Message 1", "Message 2");
```

## Common Test Patterns

### Testing Pagination
```java
@Test
void shouldReturnPagedResults_whenPageRequested() {
    // Arrange
    Pageable pageable = PageRequest.of(0, 10);
    List<Notification> notifications = createTestNotifications(5);
    Page<Notification> page = new PageImpl<>(notifications, pageable, 5);
    
    when(notificationRepository.findByUserId("user123", pageable))
        .thenReturn(page);
    
    // Act
    Page<Notification> result = notificationService
        .getNotificationsByUserId("user123", pageable);
    
    // Assert
    assertThat(result.getContent()).hasSize(5);
    assertThat(result.getTotalElements()).isEqualTo(5);
}
```

### Testing Transactions
```java
@Test
void shouldRollbackTransaction_whenErrorOccurs() {
    // Arrange
    String userId = "user123";
    String message = "Test notification";
    
    when(userService.findById(userId)).thenReturn(new User());
    when(notificationRepository.save(any(Notification.class)))
        .thenThrow(new DataAccessException("Database error") {});
    
    // Act & Assert
    assertThatThrownBy(() -> 
        notificationService.createNotification(userId, message))
        .isInstanceOf(DataAccessException.class);
        
    // Verify rollback behavior if applicable
    verify(notificationRepository, never()).flush();
}
```

## Coverage Goals

| Service | Current Coverage | Target Coverage |
|---------|------------------|-----------------|
| NotificationService | 52% | 85% |
| TicketScheduledService | 66% | 80% |
| TicketService | 36% | 80% |
| ChatbotService | 61% | 75% |
| GmailEmailService | 0.1% | 70% |
| TicketQueryService | 0.2% | 70% |
| UserService | 12% | 80% |
| AccessRequestService | 4% | 75% |
| AccessSessionService | 4% | 75% |
| AuthenticationService | 5% | 80% |
| AuthorizationService | 5% | 80% |

## Next Steps

1. **Create GmailEmailServiceTest** - Highest impact (2,364 missed instructions)
2. **Create TicketQueryServiceTest** - AI service testing (1,473 missed instructions)
3. **Create UserServiceTest** - Core functionality
4. **Improve existing test coverage** - Add edge cases and error scenarios
5. **Add integration points testing** - Service-to-service interactions

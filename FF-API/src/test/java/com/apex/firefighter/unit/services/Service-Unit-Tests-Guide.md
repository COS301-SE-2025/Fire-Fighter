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

## Anomaly Detection and Notification Tests (NEW)

### GmailEmailServiceTest.java ✅ COMPLETED
Comprehensive unit tests for GmailEmailService including anomaly detection email notifications.

**Test Coverage Includes:**
1. **CSV Export and Email Tests**:
   - CSV generation with various ticket data scenarios
   - Email sending with attachments and proper formatting
   - Error handling for mail server failures

2. **Ticket Lifecycle Email Tests**:
   - Ticket creation notifications
   - Ticket completion notifications  
   - Ticket revocation notifications
   - Five-minute warning notifications

3. **Anomaly Detection Email Tests** (NEW):
   - Frequent request anomaly notifications (MEDIUM risk)
   - Dormant user activity notifications (HIGH risk)
   - Off-hours activity notifications (LOW risk)
   - Risk level classification and color coding
   - Error handling for anomaly notification failures

4. **Suspicious Group Change Email Tests** (NEW):
   - High-risk group changes (Financial/Management groups)
   - Medium-risk group changes (HR group transitions)
   - Suspicious group change detection logic
   - Enhanced security alert messaging

5. **Edge Cases and Error Handling**:
   - Null parameter handling
   - Mail server exceptions
   - Message creation failures
   - Large content handling

**Mock Strategy:**
- Uses Mockito to mock JavaMailSender and MimeMessage
- Verifies email sending without actual SMTP connections
- Tests HTML content generation indirectly through successful sends

**Key Features Tested:**
- Professional HTML email templates with risk-based styling
- Color-coded risk levels (RED=HIGH, ORANGE=MEDIUM, YELLOW=LOW)
- Comprehensive user and ticket information inclusion
- Contextual security notices based on anomaly/change type
- Robust error handling that doesn't break core functionality

### AnomalyNotificationServiceTest.java ✅ COMPLETED
Comprehensive unit tests for AnomalyNotificationService - the central orchestrator for anomaly detection notifications.

**Test Coverage Includes:**
1. **Anomaly Detection and Notification Tests**:
   - Multi-type anomaly detection (Frequent Requests, Dormant User, Off-Hours)
   - Risk-based email notification sending to admin users
   - Proper risk level classification (HIGH/MEDIUM/LOW)
   - Integration with GmailEmailService for actual email delivery

2. **Risk Level Classification Tests**:
   - HIGH Risk: Dormant User Activity (account takeover indicators)
   - MEDIUM Risk: Frequent Requests (potential abuse/automation)
   - LOW Risk: Off-Hours Activity (unusual but not necessarily malicious)

3. **Comprehensive Anomaly Checking**:
   - checkAndNotifyAnomalies() method testing
   - Multiple simultaneous anomaly detection
   - Integration with AnomalyDetectionService
   - Proper handling when no anomalies are detected

4. **Admin User Management**:
   - Admin user retrieval and notification
   - Handling scenarios with no admin users
   - Continuation of notifications when individual emails fail
   - Admin notification count functionality

5. **Error Handling and Resilience**:
   - Database connection failures
   - Email service exceptions
   - Null parameter handling
   - Service integration failures

**Mock Strategy:**
- Mocks GmailEmailService to verify email calls without sending
- Mocks UserRepository for admin user management
- Mocks AnomalyDetectionService for controlled anomaly scenarios

**Key Testing Principles:**
- Verifies that anomaly notifications don't break core emergency response
- Ensures proper error isolation (email failures don't stop other notifications)
- Validates risk-appropriate response levels
- Tests integration between multiple detection systems

### GroupChangeNotificationServiceTest.java ✅ COMPLETED
Comprehensive unit tests for GroupChangeNotificationService - intelligent suspicious group change detection and notification.

**Test Coverage Includes:**
1. **Suspicious Group Change Detection Logic**:
   - HIGH RISK: Changes involving Financial Emergency Group (ID: 2) or Management Emergency Group (ID: 3)
   - MEDIUM RISK: Changes involving HR Emergency Group (ID: 1) or transitions to/from HR
   - LOW RISK (Non-Suspicious): Changes involving only Logistics Emergency Group (ID: 4)

2. **Risk-Based Notification Tests**:
   - Validates that only suspicious changes trigger notifications
   - Tests proper risk level classification and email content
   - Verifies that routine Logistics-only changes are filtered out
   - Ensures no-change scenarios don't trigger alerts

3. **Group ID Resolution Tests**:
   - Tests automatic resolution of group IDs to human-readable names
   - Handles unknown group IDs gracefully
   - Manages null group IDs (new user assignments or removals)

4. **Admin Notification Management**:
   - Sends notifications to all admin users for suspicious changes
   - Handles scenarios with no admin users available
   - Continues notifications even when individual emails fail
   - Provides admin user count functionality

5. **Error Handling and Edge Cases**:
   - Database connection failures
   - Email service exceptions
   - Null parameter handling (users, tickets, groups, reasons)
   - Repository exceptions

**Security Benefits Tested:**
- Reduces alert fatigue by filtering routine changes
- Focuses admin attention on security-sensitive group transitions
- Maintains oversight of high-privilege group assignments
- Provides clear risk assessment for rapid decision-making

**Mock Strategy:**
- Mocks GmailEmailService to verify suspicious change email calls
- Mocks UserRepository for admin user management
- Tests the core suspicious change detection logic without external dependencies

## Next Steps

1. **Create TicketQueryServiceTest** - AI service testing (1,473 missed instructions)
2. **Create UserServiceTest** - Core functionality
3. **Improve existing test coverage** - Add edge cases and error scenarios
4. **Add integration points testing** - Service-to-service interactions
5. **Expand anomaly detection test scenarios** - Additional edge cases and risk scenarios

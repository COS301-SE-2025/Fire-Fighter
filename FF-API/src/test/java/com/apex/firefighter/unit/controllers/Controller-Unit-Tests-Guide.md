# Controller Unit Tests

This directory contains unit tests for REST API controllers. Controller tests focus on testing the HTTP layer, request/response handling, and controller-specific logic while mocking service dependencies.

## Current Test Files

### NotificationControllerTest.java
Tests for the NotificationController REST endpoints.

**Coverage**: Good (80% instruction coverage)
**Endpoints Tested**:
- GET `/api/notifications` - Get notifications for user
- POST `/api/notifications` - Create new notification
- PUT `/api/notifications/{id}` - Update notification
- DELETE `/api/notifications/{id}` - Delete notification

## Missing Controller Tests

The following controllers need unit tests to be created:

### High Priority
- **TicketController** - Ticket management endpoints (0% coverage)
- **UserController** - User management endpoints (0% coverage)
- **ChatbotController** - AI chatbot endpoints (0% coverage)

### Medium Priority
- **UserPreferencesController** - User preferences endpoints (0% coverage)
- **DatabaseTestController** - Database testing endpoints (0% coverage)
- **HealthController** - Health check endpoints (0% coverage)

## Running Controller Tests

```bash
# Run all controller tests
mvn test -Dtest="com.apex.firefighter.unit.controllers.**"

# Run specific controller test
mvn test -Dtest="NotificationControllerTest"
```

## Controller Testing Pattern

### Test Setup
```java
@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {
    
    @Mock
    private NotificationService notificationService;
    
    @InjectMocks
    private NotificationController notificationController;
    
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(notificationController)
            .build();
    }
}
```

### Testing GET Endpoints
```java
@Test
void shouldReturnNotifications_whenValidUserId() throws Exception {
    // Arrange
    List<Notification> notifications = Arrays.asList(
        new Notification("Message 1"),
        new Notification("Message 2")
    );
    when(notificationService.getNotificationsByUserId("user123"))
        .thenReturn(notifications);
    
    // Act & Assert
    mockMvc.perform(get("/api/notifications")
            .param("userId", "user123")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].message", is("Message 1")))
        .andExpect(jsonPath("$[1].message", is("Message 2")));
        
    verify(notificationService).getNotificationsByUserId("user123");
}
```

### Testing POST Endpoints
```java
@Test
void shouldCreateNotification_whenValidRequest() throws Exception {
    // Arrange
    NotificationRequest request = new NotificationRequest("Test message");
    Notification createdNotification = new Notification("Test message");
    createdNotification.setId(1L);
    
    when(notificationService.createNotification(any(NotificationRequest.class)))
        .thenReturn(createdNotification);
    
    // Act & Assert
    mockMvc.perform(post("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.message", is("Test message")));
        
    verify(notificationService).createNotification(any(NotificationRequest.class));
}
```

### Testing Error Scenarios
```java
@Test
void shouldReturnBadRequest_whenInvalidRequest() throws Exception {
    // Arrange
    NotificationRequest invalidRequest = new NotificationRequest(""); // Empty message
    
    // Act & Assert
    mockMvc.perform(post("/api/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
        
    verifyNoInteractions(notificationService);
}

@Test
void shouldReturnNotFound_whenNotificationNotExists() throws Exception {
    // Arrange
    when(notificationService.getNotificationById(999L))
        .thenThrow(new NotificationNotFoundException("Notification not found"));
    
    // Act & Assert
    mockMvc.perform(get("/api/notifications/999"))
        .andExpect(status().isNotFound());
}
```

## Test Guidelines

### What to Test
1. **Request Mapping**: Verify correct HTTP methods and paths
2. **Request Validation**: Test input validation and error responses
3. **Response Format**: Verify correct JSON structure and status codes
4. **Service Integration**: Verify service methods are called correctly
5. **Error Handling**: Test exception handling and error responses
6. **Security**: Test authentication and authorization (if applicable)

### What NOT to Test
1. **Business Logic**: This belongs in service tests
2. **Database Operations**: This belongs in repository tests
3. **External Service Calls**: This belongs in integration tests

### Mocking Strategy
- **Mock Services**: Always mock service layer dependencies
- **Mock Security**: Mock authentication/authorization if needed
- **Don't Mock**: Don't mock the controller itself or Spring MVC components

### Assertion Best Practices
```java
// Good: Specific assertions
.andExpect(jsonPath("$.id", is(1)))
.andExpect(jsonPath("$.message", is("Test message")))
.andExpect(jsonPath("$.createdAt", notNullValue()))

// Good: Verify service interactions
verify(notificationService).createNotification(argThat(request -> 
    request.getMessage().equals("Test message")));

// Good: Test error scenarios
.andExpect(status().isBadRequest())
.andExpect(jsonPath("$.error", is("Validation failed")))
```

## Common Test Utilities

### JSON Helper
```java
private String asJsonString(Object obj) throws JsonProcessingException {
    return objectMapper.writeValueAsString(obj);
}
```

### Request Builder Helper
```java
private MockHttpServletRequestBuilder postJson(String url, Object content) 
        throws JsonProcessingException {
    return post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(content));
}
```

## Coverage Goals

| Controller | Current Coverage | Target Coverage |
|------------|------------------|-----------------|
| NotificationController | 80% | 90% |
| TicketController | 0% | 80% |
| UserController | 0% | 80% |
| ChatbotController | 0% | 80% |
| UserPreferencesController | 0% | 80% |
| DatabaseTestController | 0% | 70% |
| HealthController | 0% | 90% |

## Next Steps

1. **Create TicketControllerTest** - Highest priority due to complexity
2. **Create UserControllerTest** - Core functionality testing
3. **Create ChatbotControllerTest** - AI integration testing
4. **Add security testing** - Authentication/authorization tests
5. **Improve error handling tests** - More comprehensive error scenarios

# Repository Unit Tests

This directory contains unit tests for repository layer components. Repository tests focus on data access operations, custom queries, and database interactions using an in-memory H2 database.

## Current Test Files

All repository tests are currently implemented and provide good coverage of data access operations:

### AccessLogRepositoryTest.java
Tests for AccessLog entity data access operations.
- **Features Tested**: CRUD operations, query methods, audit logging

### AccessRequestRepositoryTest.java  
Tests for AccessRequest entity data access operations.
- **Features Tested**: CRUD operations, status filtering, user-based queries

### AccessSessionRepositoryTest.java
Tests for AccessSession entity data access operations.
- **Features Tested**: Session management, active session queries, cleanup operations

### NotificationRepositoryTest.java
Tests for Notification entity data access operations.
- **Features Tested**: User-based queries, status filtering, pagination, bulk operations

### RoleRepositoryTest.java
Tests for Role entity data access operations.
- **Features Tested**: Role lookup, hierarchy queries, permission management

### TicketRepositoryTest.java
Tests for Ticket entity data access operations.
- **Features Tested**: Complex queries, status filtering, assignment queries, search functionality

### UserRepositoryTest.java
Tests for User entity data access operations.
- **Features Tested**: User lookup, authentication queries, profile management

## Running Repository Tests

```bash
# Run all repository tests
mvn test -Dtest="com.apex.firefighter.unit.repositories.**"

# Run specific repository test
mvn test -Dtest="NotificationRepositoryTest"

# Run with H2 console (for debugging)
mvn test -Dtest="NotificationRepositoryTest" -Dspring.h2.console.enabled=true
```

## Repository Testing Pattern

### Test Setup
```java
@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.show-sql=true"
})
class NotificationRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUsername("testuser");
        entityManager.persistAndFlush(testUser);
    }
}
```

### Testing Basic CRUD Operations
```java
@Test
void shouldSaveAndFindNotification() {
    // Arrange
    Notification notification = new Notification();
    notification.setMessage("Test message");
    notification.setUser(testUser);
    notification.setStatus(NotificationStatus.PENDING);
    
    // Act
    Notification saved = notificationRepository.save(notification);
    Optional<Notification> found = notificationRepository.findById(saved.getId());
    
    // Assert
    assertThat(found).isPresent();
    assertThat(found.get().getMessage()).isEqualTo("Test message");
    assertThat(found.get().getUser()).isEqualTo(testUser);
    assertThat(found.get().getStatus()).isEqualTo(NotificationStatus.PENDING);
}
```

### Testing Custom Query Methods
```java
@Test
void shouldFindNotificationsByUserId() {
    // Arrange
    Notification notification1 = createNotification("Message 1", testUser);
    Notification notification2 = createNotification("Message 2", testUser);
    
    User otherUser = createUser("other@example.com");
    Notification notification3 = createNotification("Message 3", otherUser);
    
    entityManager.persistAndFlush(notification1);
    entityManager.persistAndFlush(notification2);
    entityManager.persistAndFlush(notification3);
    
    // Act
    List<Notification> userNotifications = 
        notificationRepository.findByUserIdOrderByCreatedAtDesc(testUser.getId());
    
    // Assert
    assertThat(userNotifications).hasSize(2);
    assertThat(userNotifications)
        .extracting(Notification::getMessage)
        .containsExactly("Message 2", "Message 1"); // Ordered by createdAt desc
}
```

### Testing Complex Queries
```java
@Test
void shouldFindNotificationsByStatusAndDateRange() {
    // Arrange
    LocalDateTime startDate = LocalDateTime.now().minusDays(7);
    LocalDateTime endDate = LocalDateTime.now();
    
    Notification oldNotification = createNotification("Old message", testUser);
    oldNotification.setCreatedAt(LocalDateTime.now().minusDays(10));
    
    Notification recentNotification = createNotification("Recent message", testUser);
    recentNotification.setCreatedAt(LocalDateTime.now().minusDays(3));
    recentNotification.setStatus(NotificationStatus.SENT);
    
    entityManager.persistAndFlush(oldNotification);
    entityManager.persistAndFlush(recentNotification);
    
    // Act
    List<Notification> results = notificationRepository
        .findByStatusAndCreatedAtBetween(
            NotificationStatus.SENT, 
            startDate, 
            endDate
        );
    
    // Assert
    assertThat(results).hasSize(1);
    assertThat(results.get(0).getMessage()).isEqualTo("Recent message");
}
```

### Testing Pagination
```java
@Test
void shouldReturnPagedResults() {
    // Arrange
    for (int i = 1; i <= 15; i++) {
        Notification notification = createNotification("Message " + i, testUser);
        entityManager.persistAndFlush(notification);
    }
    
    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
    
    // Act
    Page<Notification> page = notificationRepository
        .findByUserId(testUser.getId(), pageable);
    
    // Assert
    assertThat(page.getContent()).hasSize(10);
    assertThat(page.getTotalElements()).isEqualTo(15);
    assertThat(page.getTotalPages()).isEqualTo(2);
    assertThat(page.isFirst()).isTrue();
    assertThat(page.hasNext()).isTrue();
}
```

### Testing Native Queries
```java
@Test
void shouldExecuteNativeQuery() {
    // Arrange
    createAndPersistNotifications(5);
    
    // Act
    List<Object[]> results = notificationRepository
        .findNotificationStatistics(testUser.getId());
    
    // Assert
    assertThat(results).hasSize(1);
    Object[] stats = results.get(0);
    assertThat(stats[0]).isEqualTo(5L); // Total count
    assertThat(stats[1]).isEqualTo(testUser.getId()); // User ID
}
```

### Testing Transactions and Rollback
```java
@Test
@Transactional
@Rollback
void shouldRollbackOnError() {
    // Arrange
    Notification notification = createNotification("Test message", testUser);
    
    // Act
    notificationRepository.save(notification);
    entityManager.flush();
    
    // Simulate error and rollback
    throw new RuntimeException("Simulated error");
    
    // This test will rollback, so the notification won't be persisted
}
```

## Test Guidelines

### What to Test
1. **CRUD Operations**: Basic save, find, update, delete operations
2. **Custom Queries**: Repository method queries and @Query annotations
3. **Relationships**: Entity associations and cascade operations
4. **Constraints**: Database constraints and validation
5. **Pagination**: Pageable queries and sorting
6. **Native Queries**: Custom SQL queries and stored procedures

### What NOT to Test
1. **JPA Framework**: Don't test Spring Data JPA functionality
2. **Database Engine**: Don't test H2/PostgreSQL specific features
3. **Connection Management**: Don't test connection pooling

### Database Setup
```java
// Use @DataJpaTest for repository testing
@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.jpa.show-sql=true",
    "spring.jpa.properties.hibernate.format_sql=true"
})
```

### Test Data Management
```java
@BeforeEach
void setUp() {
    // Create test data using TestEntityManager
    testUser = new User();
    testUser.setEmail("test@example.com");
    testUser = entityManager.persistAndFlush(testUser);
}

@AfterEach
void tearDown() {
    // Cleanup is automatic with @DataJpaTest and @Transactional
    // But you can add explicit cleanup if needed
    entityManager.clear();
}
```

## Common Test Utilities

### Entity Creation Helpers
```java
private Notification createNotification(String message, User user) {
    Notification notification = new Notification();
    notification.setMessage(message);
    notification.setUser(user);
    notification.setStatus(NotificationStatus.PENDING);
    notification.setCreatedAt(LocalDateTime.now());
    return notification;
}

private User createUser(String email) {
    User user = new User();
    user.setEmail(email);
    user.setUsername(email.split("@")[0]);
    return entityManager.persistAndFlush(user);
}
```

### Assertion Helpers
```java
private void assertNotificationEquals(Notification expected, Notification actual) {
    assertThat(actual.getMessage()).isEqualTo(expected.getMessage());
    assertThat(actual.getUser().getId()).isEqualTo(expected.getUser().getId());
    assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
}
```

## Performance Testing

### Testing Query Performance
```java
@Test
void shouldExecuteQueryEfficiently() {
    // Arrange
    createLargeDataset(1000);
    
    // Act
    long startTime = System.currentTimeMillis();
    List<Notification> results = notificationRepository
        .findByUserIdWithOptimizedQuery(testUser.getId());
    long endTime = System.currentTimeMillis();
    
    // Assert
    assertThat(results).isNotEmpty();
    assertThat(endTime - startTime).isLessThan(100); // Should complete in < 100ms
}
```

## Coverage Status

All repository tests are currently implemented with good coverage:
- **AccessLogRepositoryTest** - Complete
- **AccessRequestRepositoryTest** - Complete
- **AccessSessionRepositoryTest** - Complete
- **NotificationRepositoryTest** - Complete
- **RoleRepositoryTest** - Complete
- **TicketRepositoryTest** - Complete
- **UserRepositoryTest** - Complete

## Next Steps

1. **Review and enhance existing tests** - Add edge cases and performance tests
2. **Add integration with new entities** - When new entities are added
3. **Performance optimization testing** - Add query performance benchmarks
4. **Database migration testing** - Test schema changes and data migrations
5. **Add custom query testing** - For complex business queries

# FireFighter Platform - Database Connection Test Guide

This guide demonstrates how to interact with the PostgreSQL database using Spring Boot JPA. Use this as a reference when implementing your daemon and other database operations.

## 🗄️ Database Setup

### 1. Test Table Schema
```sql
-- Run this in PGAdmin to create the test table
CREATE TABLE IF NOT EXISTS connection_test (
    id BIGSERIAL PRIMARY KEY,
    test_name VARCHAR(100) NOT NULL,
    test_value VARCHAR(255),
    test_number INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO connection_test (test_name, test_value, test_number, is_active) VALUES 
('Database Connection', 'SUCCESS', 1, true),
('Initial Setup', 'COMPLETE', 2, true),
('Sample Data', 'LOADED', 3, false);
```

## 🚀 How to Test Database Connection

### Method 1: Run Full Application
```bash
# Start the application
mvn spring-boot:run -Dspring.profiles.active=dev

# Open browser and visit:
http://localhost:8080/api/test/run
```

### Method 2: Use API Endpoints
Once the application is running, you can test individual operations:

#### 📝 CREATE Operations
```bash
# Create a new test entry
curl -X POST "http://localhost:8080/api/test/create?testName=MyTest&testValue=SUCCESS&testNumber=123&isActive=true"
```

#### 📖 READ Operations
```bash
# Get all tests
curl http://localhost:8080/api/test/all

# Get test by ID
curl http://localhost:8080/api/test/1

# Get only active tests
curl http://localhost:8080/api/test/active

# Search tests by name
curl "http://localhost:8080/api/test/search?name=Database"

# Count active tests
curl http://localhost:8080/api/test/count/active
```

#### ✏️ UPDATE Operations
```bash
# Update a test entry
curl -X PUT "http://localhost:8080/api/test/1?testValue=UPDATED&testNumber=999"

# Toggle active status
curl -X PATCH http://localhost:8080/api/test/1/toggle
```

#### 🗑️ DELETE Operations
```bash
# Delete specific test
curl -X DELETE http://localhost:8080/api/test/1

# Delete all tests (careful!)
curl -X DELETE http://localhost:8080/api/test/all
```

## 📚 Code Examples for Your Daemon

### 1. Entity Structure (Model)
```java
@Entity
@Table(name = "your_table_name")
public class YourEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String requiredField;
    
    @Column(name = "custom_column_name")
    private String customField;
    
    // Constructors, getters, setters...
}
```

### 2. Repository Interface
```java
@Repository
public interface YourRepository extends JpaRepository<YourEntity, Long> {
    // Method naming follows Spring Data JPA conventions
    List<YourEntity> findByRequiredField(String value);
    Optional<YourEntity> findByCustomField(String customValue);
    
    // Custom queries
    @Query("SELECT e FROM YourEntity e WHERE e.field = :value")
    List<YourEntity> customQuery(@Param("value") String value);
}
```

### 3. Service Layer (Business Logic)
```java
@Service
@Transactional
public class YourService {
    
    private final YourRepository repository;
    
    @Autowired
    public YourService(YourRepository repository) {
        this.repository = repository;
    }
    
    // CREATE
    public YourEntity create(YourEntity entity) {
        return repository.save(entity);
    }
    
    // READ
    public List<YourEntity> findAll() {
        return repository.findAll();
    }
    
    public Optional<YourEntity> findById(Long id) {
        return repository.findById(id);
    }
    
    // UPDATE
    public YourEntity update(Long id, YourEntity updates) {
        Optional<YourEntity> existing = repository.findById(id);
        if (existing.isPresent()) {
            YourEntity entity = existing.get();
            // Update fields...
            return repository.save(entity);
        }
        throw new RuntimeException("Entity not found");
    }
    
    // DELETE
    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
```

## 🔧 Key Spring Boot Annotations

### Entity Annotations
- `@Entity` - Marks class as JPA entity
- `@Table(name = "table_name")` - Maps to specific table
- `@Id` - Primary key field
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` - Auto-increment
- `@Column(nullable = false, unique = true)` - Column constraints
- `@ManyToOne`, `@OneToMany` - Relationships

### Repository Annotations
- `@Repository` - Marks as Spring repository component
- `@Query("JPQL or SQL")` - Custom queries
- `@Param("paramName")` - Named parameters

### Service Annotations
- `@Service` - Marks as Spring service component
- `@Transactional` - Database transaction management
- `@Autowired` - Dependency injection

## 🎯 Best Practices for Your Daemon

### 1. Error Handling
```java
try {
    // Database operation
    result = repository.save(entity);
    logger.info("Successfully saved: {}", result);
} catch (DataAccessException e) {
    logger.error("Database error: {}", e.getMessage());
    // Handle error appropriately
}
```

### 2. Logging
```java
private static final Logger logger = LoggerFactory.getLogger(YourService.class);

logger.info("Processing entity with ID: {}", id);
logger.warn("Entity not found: {}", id);
logger.error("Failed to process: {}", e.getMessage());
```

### 3. Transaction Management
```java
@Transactional
public void complexOperation() {
    // Multiple database operations
    // All succeed or all rollback
}

@Transactional(readOnly = true)
public List<Entity> readOnlyOperation() {
    // Optimized for read-only operations
}
```

## 📋 Testing Checklist

Before implementing your daemon, verify:

- ✅ Database connection works
- ✅ Can create new records
- ✅ Can read existing records
- ✅ Can update records
- ✅ Can delete records
- ✅ Relationships work correctly
- ✅ Transactions rollback on errors
- ✅ Logging is working
- ✅ Error handling is in place

## 🆘 Troubleshooting

### Common Issues:
1. **Connection refused** - Check if PostgreSQL is running and accessible
2. **No property found** - Verify repository method names match entity fields
3. **Transaction errors** - Ensure proper `@Transactional` usage
4. **SQL errors** - Check entity annotations and column mappings

### Debug Tips:
- Set `spring.jpa.show-sql=true` to see generated SQL
- Use `logging.level.org.hibernate.SQL=DEBUG` for detailed logs
- Check database logs for connection issues
- Use the test endpoints to verify basic CRUD operations

## 📞 Support

If you encounter issues:
1. Check the console output for detailed error messages
2. Test using the provided API endpoints first
3. Verify your entity matches the database schema
4. Ensure all required dependencies are in `pom.xml`

---w
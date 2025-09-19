# Natural Language Processing Service Architecture

## Overview

The NLP Service replaces the Gemini AI Chatbot with a custom natural language processing solution for ticket management and querying. The service is designed with a modular architecture that allows users to interact with the FireFighter platform using natural language.

## Architecture Components

### 1. NLPService (Main Orchestrator)
**File:** `src/main/java/com/apex/firefighter/service/nlp/NLPService.java`

- **Purpose:** Main service that orchestrates all NLP operations
- **Responsibilities:**
  - Process user queries and admin queries
  - Coordinate between all NLP components
  - Manage user authentication and authorization
  - Provide capabilities and suggestions based on user role
  - Health checking for the entire NLP system

### 2. IntentRecognitionService
**File:** `src/main/java/com/apex/firefighter/service/nlp/IntentRecognitionService.java`

- **Purpose:** Recognize user intents from natural language queries
- **Responsibilities:**
  - Classify user queries into predefined intents (show tickets, update status, etc.)
  - Provide confidence scores for intent recognition
  - Support multiple intent recognition for complex queries
  - Validate intents against user permissions

**Supported Intent Types:**
- Query intents: `SHOW_TICKETS`, `SHOW_ACTIVE_TICKETS`, `SEARCH_TICKETS`
- Management intents: `UPDATE_TICKET_STATUS`, `ASSIGN_TICKET`, `CREATE_TICKET`
- Admin intents: `SHOW_ALL_TICKETS`, `GET_SYSTEM_STATS`, `EXPORT_TICKETS`
- Help intents: `GET_HELP`, `SHOW_CAPABILITIES`

### 3. EntityExtractionService
**File:** `src/main/java/com/apex/firefighter/service/nlp/EntityExtractionService.java`

- **Purpose:** Extract entities from natural language queries
- **Responsibilities:**
  - Identify ticket IDs, statuses, dates, user names, and other relevant data
  - Normalize extracted entities for database queries
  - Validate extracted entities against system data
  - Provide confidence scores for entity extraction

**Supported Entity Types:**
- `TICKET_ID`, `STATUS`, `DATE`, `TIME`, `USER_NAME`
- `EMERGENCY_TYPE`, `NUMBER`, `DURATION`, `PRIORITY`
- `LOCATION`, `EMAIL`, `PHONE`

### 4. QueryProcessingService
**File:** `src/main/java/com/apex/firefighter/service/nlp/QueryProcessingService.java`

- **Purpose:** Convert natural language queries into database operations
- **Responsibilities:**
  - Process queries based on recognized intents and extracted entities
  - Execute ticket queries and management operations
  - Build database query filters from extracted entities
  - Validate user permissions for operations

**Query Types:**
- `USER_TICKETS`, `ACTIVE_TICKETS`, `COMPLETED_TICKETS`
- `SEARCH_TICKETS`, `TICKET_DETAILS`, `SYSTEM_STATS`

**Operations:**
- `UPDATE_STATUS`, `ASSIGN_TICKET`, `CREATE_TICKET`
- `CLOSE_TICKET`, `ADD_COMMENT`, `UPDATE_PRIORITY`

### 5. ResponseGenerationService
**File:** `src/main/java/com/apex/firefighter/service/nlp/ResponseGenerationService.java`

- **Purpose:** Generate natural language responses from query results
- **Responsibilities:**
  - Convert structured data back into human-readable text
  - Customize responses based on user role and preferences
  - Generate error messages and help information
  - Support different response styles (professional, casual, technical, concise)

### 6. NLPController
**File:** `src/main/java/com/apex/firefighter/controller/NLPController.java`

- **Purpose:** REST API endpoints for NLP operations
- **Endpoints:**
  - `POST /api/nlp/query` - Process user natural language queries
  - `POST /api/nlp/admin/query` - Process admin queries with elevated privileges
  - `GET /api/nlp/capabilities/{userId}` - Get user capabilities
  - `GET /api/nlp/suggestions/{userId}` - Get suggested queries
  - `GET /api/nlp/health` - Health check for NLP service

### 7. NLPConfig
**File:** `src/main/java/com/apex/firefighter/config/NLPConfig.java`

- **Purpose:** Configuration management for NLP components
- **Configuration Options:**
  - Intent and entity confidence thresholds
  - Response length limits
  - Caching settings
  - Debug mode settings

## Data Flow

1. **User Input:** User submits natural language query via REST API
2. **Intent Recognition:** Query is analyzed to determine user intent
3. **Entity Extraction:** Relevant entities are extracted from the query
4. **Query Processing:** Intent and entities are converted to database operations
5. **Data Retrieval:** Database operations are executed to get results
6. **Response Generation:** Results are converted back to natural language
7. **Response Delivery:** Natural language response is returned to user

## Configuration

The NLP service can be configured through application properties:

```properties
# Intent recognition settings
nlp.intent.confidence.threshold=0.7

# Entity extraction settings  
nlp.entity.confidence.threshold=0.6

# Response settings
nlp.response.max.length=1000
nlp.query.max.length=500

# Caching settings
nlp.cache.enabled=true
nlp.cache.ttl=300

# Debug settings
nlp.debug.enabled=false
```

## Example Usage

### User Query Examples:
- "Show me my active tickets"
- "What tickets are assigned to me?"
- "Update ticket FIRE-123 status to completed"
- "How many fire emergencies are currently active?"

### Admin Query Examples:
- "Show all active tickets in the system"
- "Export all tickets from last week"
- "Get system statistics for this month"
- "Show tickets assigned to user john.doe"

## Implementation Status

✅ **Architecture Defined** - All service classes and interfaces created  
⏳ **Implementation Pending** - Core logic needs to be implemented  
⏳ **Testing Pending** - Unit and integration tests need to be created  
⏳ **Documentation Pending** - API documentation needs to be completed  

## Next Steps

1. **Implement Intent Recognition Logic** - Add pattern matching and classification algorithms
2. **Implement Entity Extraction Logic** - Add regex patterns and entity recognition
3. **Implement Query Processing Logic** - Connect to existing TicketService and database
4. **Implement Response Generation Logic** - Add response templates and formatting
5. **Add Comprehensive Testing** - Unit tests, integration tests, and API tests
6. **Add Caching Layer** - Implement query result caching for performance
7. **Add Logging and Monitoring** - Add detailed logging for debugging and monitoring

## Benefits Over Gemini AI

- **No External Dependencies** - Fully self-contained service
- **Customizable** - Tailored specifically for FireFighter platform needs
- **Cost Effective** - No API usage costs
- **Privacy** - All data processing happens locally
- **Performance** - Faster response times without external API calls
- **Reliability** - No dependency on external service availability

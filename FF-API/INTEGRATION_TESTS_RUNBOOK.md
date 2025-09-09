# FF-API Integration Tests Runbook

This runbook lists all integration tests that should exist for the FF-API service, where to place them, and how to run them locally and in CI. It is aligned with the current Maven/Spring Boot setup so you can run all integration tests together, similar to how `mvn test` runs unit tests.

---

## Test layout (convention)

- Package root for integration tests: `src/test/java/com/apex/firefighter/integration`
  - `api/` – End-to-end HTTP tests against running Spring Boot app
  - `database/` – Service/repository flows with real JPA + H2 schema
  - `externalservices/` – Tests that involve external HTTP or SMTP (stubbed)

Recommended class naming convention: `*IT.java` (e.g., `TicketControllerIT`). This makes it easy to target just integration tests via Maven patterns.

---

## What to cover (complete checklist)

Below is the comprehensive list of integration test classes and key scenarios to implement. Create these under `src/test/java/com/apex/firefighter/integration/...` as indicated.

### API (HTTP) integration tests

1) TicketControllerIT (integration/api)
   - POST /api/tickets (create)
   - GET /api/tickets (list)
   - GET /api/tickets/{id}
   - GET /api/tickets/ticket-id/{ticketId}
   - PUT /api/tickets/{id} (update fields)
   - DELETE /api/tickets/{id}
   - DELETE /api/tickets/ticket-id/{ticketId}
   - Admin endpoints:
     - GET /api/tickets/admin/active
     - GET /api/tickets/admin/history
     - GET /api/tickets/admin/status/{status}
     - PUT /api/tickets/admin/revoke/{id}
     - PUT /api/tickets/admin/revoke/ticket-id/{ticketId}
     - GET /api/tickets/admin/check/{userId}
   - POST /api/tickets/admin/export
     - With and without date range
     - Non-admin -> 403
     - Admin -> 200 and attempts to send email (stub mail sender)

2) UserControllerIT (integration/api)
   - POST /api/users/verify (happy path + invalid data)
   - GET /api/users/{firebaseUid}/authorized
   - GET /api/users/{firebaseUid}/roles/{roleName}
   - GET /api/users/{firebaseUid}
   - GET /api/users/email/{email}
   - PUT /api/users/{firebaseUid}/authorize
   - PUT /api/users/{firebaseUid}/revoke
   - POST /api/users/{firebaseUid}/roles (assign role)
   - PUT /api/users/{firebaseUid}/contact (valid + invalid)
   - GET /api/users/authorized
   - GET /api/users/department/{department}
   - GET /api/users/role/{roleName}
   - GET /api/users/authorized/role/{roleName}

3) NotificationControllerIT (integration/api)
   - GET /api/notifications?userId=
   - GET /api/notifications/unread?userId=
   - GET /api/notifications/stats?userId=
   - PUT /api/notifications/{notificationId}/read?userId=
   - PUT /api/notifications/read-all?userId=
   - DELETE /api/notifications/read?userId=
   - DELETE /api/notifications/{notificationId}?userId=
   - GET /api/notifications/{notificationId}?userId=

4) UserPreferencesControllerIT (integration/api)
   - GET /api/user-preferences/{userId}
   - PUT /api/user-preferences/{userId} (all fields)
   - PATCH /api/user-preferences/{userId}/{setting}
   - POST /api/user-preferences/{userId}/enable-all
   - POST /api/user-preferences/{userId}/disable-all
   - POST /api/user-preferences/{userId}/reset
   - GET /api/user-preferences/{userId}/check/{setting}

5) DatabaseTestControllerIT (integration/api)
   - GET /api/test/cors
   - GET /api/test/run (200 when service executes)
   - POST /api/test/create
   - GET /api/test/all
   - GET /api/test/{id}
   - GET /api/test/active
   - GET /api/test/search?name=
   - PUT /api/test/{id}
   - PATCH /api/test/{id}/toggle
   - DELETE /api/test/{id}
   - DELETE /api/test/all
   - GET /api/test/count/active
   - GET /api/test/range?start=&end=

6) HealthControllerIT (integration/api)
   - GET /api/health (200 + expected keys)
   - GET /api/health/detailed (200 + component keys)

7) ChatbotControllerIT (integration/api)
   - POST /api/chatbot/query (no API key -> returns config error as designed)
   - POST /api/chatbot/admin/query (non-admin -> 403 body)
   - POST /api/chatbot/admin/query (admin user present -> 200; stubbed context)
   - GET /api/chatbot/capabilities/{userId}
   - GET /api/chatbot/health
   - GET /api/chatbot/suggestions/{userId}

8) Swagger/OpenAPI IT (integration/api)
   - SwaggerIntegrationTest already exists; ensure `/api-docs` and `/swagger-ui.html` work and contain core tags.

### Database/service integration tests

9) TicketServiceIT (integration/database)
   - Create, update, revoke, delete flows with JPA persistence
   - Queries: getActiveTickets, getTicketHistory, getTicketsByStatus, getTicketsByDateRange

10) UserServiceIT (integration/database)
   - verifyOrCreateUser, getUserWithRoles, getUserByEmail
   - authorizeUser, revokeUserAuthorization, assignRole, hasRole, isUserAuthorized
   - Queries: getAuthorizedUsers, getUsersByDepartment, getUsersByRole, getAuthorizedUsersByRole

11) NotificationServiceIT (integration/database)
   - getNotificationsForUser, getUnreadNotificationsForUser
   - markNotificationAsRead, markAllNotificationsAsRead
   - deleteReadNotifications, deleteNotification
   - getNotificationForUser

12) UserPreferencesServiceIT (integration/database)
   - get, update (all fields), enable/disable-all, reset, individual flags getters

13) RoleServiceIT (integration/database)
   - assignRole, removeRole; finders by role and role+authorized

14) DatabaseConnectionTestServiceIT (integration/database)
   - runComprehensiveTest executes without exception
   - create/get/update/search/toggle/delete/count/range end-to-end through repo

### External services integration tests (stubbed)

15) GmailEmailServiceIT (integration/externalservices)
   - Use a test bean for `JavaMailSender` (mock/in-memory) to capture messages
   - sendTicketsCsv attaches CSV; content-type and filename assertions
   - sendTicketCreationEmail, sendTicketCompletionEmail, sendTicketRevocationEmail, sendFiveMinuteWarningEmail produce HTML

16) GeminiAIServiceIT (integration/externalservices)
   - Current URL is hardcoded; without changing code, assert `isConfigured()` behavior and that `generateResponseWithContext` degrades gracefully when key is missing
   - Future improvement: make API URL configurable and use WireMock to stub responses for full-path tests

---

## Test scaffolding tips

- Use `@SpringBootTest(webEnvironment = RANDOM_PORT)` with `TestRestTemplate` for API tests.
- Activate the test profile: `@ActiveProfiles("test")`.
- H2 database is configured for tests; schema from `src/test/resources/schema.sql` if present.
- To preserve local dev data, tests run against in-memory H2 and never touch your real Postgres.
- For mail, create a `@TestConfiguration` that provides a mock `JavaMailSender` bean.
- For AI, leave `GOOGLE_GEMINI_API_KEY` unset in tests and assert the error path, or refactor later for WireMock.

---

## How to run integration tests

Run all tests (unit + integration):

```bash
mvn test
```

Run only integration tests (by package pattern):

```bash
# All integration tests
mvn test -Dtest="com.apex.firefighter.integration.**"

# Only API integration tests
mvn test -Dtest="com.apex.firefighter.integration.api.**"

# Only database integration tests
mvn test -Dtest="com.apex.firefighter.integration.database.**"

# Only external services integration tests
mvn test -Dtest="com.apex.firefighter.integration.externalservices.**"
```

Run by naming convention (if you name classes *IT.java):

```bash
# Any class ending with IT
mvn test -Dtest="**IT"

# Multiple patterns (comma-separated)
mvn test -Dtest="**IT,**IntegrationTest"
```

Run unit and integration separately in one command (patterns):

```bash
mvn test -Dtest="com.apex.firefighter.unit.**,com.apex.firefighter.integration.**"
```

Set Spring profile explicitly:

```bash
mvn test -Dspring.profiles.active=test
```

Optional parallel test execution for speed:

```bash
# Parallelize at Maven level (1 thread per core)
mvn -T 1C test -Dtest="com.apex.firefighter.integration.**"
```

---

## CI example (GitHub Actions snippet)

```yaml
- name: Run Integration Tests
  run: mvn -B test -Dtest="com.apex.firefighter.integration.**"
  env:
    SPRING_PROFILES_ACTIVE: test
```

---

## Next steps

- Create the classes listed above and implement tests incrementally (start with controllers that have the highest business impact: tickets, users, notifications).
- Consider adding Maven Failsafe for a clean split (`mvn verify` for IT) if desired. We can supply a minimal POM snippet on request.
- Once a first pass is in, add coverage gates (JaCoCo) for integration coverage.


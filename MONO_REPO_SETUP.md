# FireFighter Mono-Repo Setup Guide

This document explains how to set up and use the mono-repo structure for the FireFighter application with integrated testing between the Angular Ionic Capacitor frontend and Spring Boot backend.

## Project Structure

```
Fire-Fighter/
├── package.json                    # Root package.json for mono-repo management
├── frontend/                       # Angular Ionic Capacitor application
│   └── FireFighter/
├── backend/                        # Spring Boot backend service
│   └── firefighter-platform/
├── integration-tests/              # Integration and E2E tests
│   ├── package.json
│   ├── jest.config.js
│   ├── cypress.config.ts
│   ├── tests/                      # API integration tests
│   └── cypress/                    # E2E tests
├── .github/workflows/              # CI/CD pipelines
└── Documentation/
```

## Prerequisites

- Node.js 18+ and npm 9+
- Java 17+
- Maven 3.6+
- Git

## Initial Setup

### 1. Install Dependencies

```bash
# Install all dependencies (frontend, backend, and integration tests)
npm install

# Or install individually:
npm run install:frontend
npm run install:backend
```

### 2. Environment Configuration

The backend supports multiple profiles:
- `default` - Production configuration with PostgreSQL
- `dev` - Development configuration
- `test` - Testing configuration with H2 in-memory database

## Development Workflow

### Starting Services

```bash
# Start both frontend and backend concurrently
npm run dev

# Or start individually:
npm run start:backend:dev    # Backend with dev profile
npm run start:frontend       # Frontend development server
```

### Building Applications

```bash
# Build both applications
npm run build:all

# Or build individually:
npm run build:frontend
npm run build:backend
```

### Running Tests

```bash
# Run all tests (unit + integration)
npm run test:all

# Run unit tests only
npm run test:frontend
npm run test:backend

# Run integration tests
npm run integration:test
```

## Integration Testing

The mono-repo includes two types of integration testing:

### 1. API Integration Tests (Jest)

Located in `integration-tests/tests/`, these tests verify API endpoints:

- **Authentication tests**: Login, registration, profile management
- **Ticket management tests**: CRUD operations, filtering, searching
- **Cross-service communication**: Frontend-backend integration

```bash
# Run API integration tests
cd integration-tests && npm test

# Run with coverage
cd integration-tests && npm run test:coverage

# Run in watch mode
cd integration-tests && npm run test:watch
```

### 2. End-to-End Tests (Cypress)

Located in `integration-tests/cypress/`, these tests verify complete user workflows:

- **Authentication flow**: Login/logout, session management
- **Ticket management**: Creating, viewing, updating tickets
- **UI interactions**: Form validation, navigation, error handling

```bash
# Run E2E tests (headless)
cd integration-tests && npm run test:e2e

# Open Cypress test runner (interactive)
cd integration-tests && npm run test:e2e:open
```

## Configuration Files

### Root package.json

Manages the entire mono-repo with scripts for:
- Installing dependencies across all projects
- Starting services concurrently
- Running tests across all projects
- Building all applications

### Integration Tests Configuration

- **jest.config.js**: Jest configuration for API tests
- **cypress.config.ts**: Cypress configuration for E2E tests
- **tests/setup.ts**: Global test setup and utilities

### Backend Test Configuration

- **application-test.properties**: Test-specific configuration
- **HealthController.java**: Health check endpoints for testing

## CI/CD Pipeline

The `.github/workflows/ci.yml` pipeline includes:

1. **Backend Tests**: Unit tests and building
2. **Frontend Tests**: Linting, unit tests, and building
3. **Integration Tests**: API and E2E tests with both services running

### Pipeline Features

- Parallel execution of backend and frontend tests
- Service health checks before running integration tests
- Artifact collection for test results and screenshots
- Caching for faster builds

## Adding New Tests

### API Integration Test

```typescript
// integration-tests/tests/api/new-feature.test.ts
import axios from 'axios';
import { config } from '../setup';

describe('New Feature API Tests', () => {
  it('should test new endpoint', async () => {
    const response = await axios.get(`${config.apiUrl}/new-endpoint`);
    expect(response.status).toBe(200);
  });
});
```

### E2E Test

```typescript
// integration-tests/cypress/e2e/new-feature.cy.ts
describe('New Feature E2E Tests', () => {
  it('should test new UI workflow', () => {
    cy.login('user@example.com', 'password');
    cy.visit('/new-page');
    cy.get('[data-cy="new-button"]').click();
    // Add assertions
  });
});
```

## Best Practices

### 1. Test Data Management

- Use unique identifiers (timestamps) for test data
- Clean up test data after tests when possible
- Use test-specific database for isolation

### 2. Service Dependencies

- Always check service health before running integration tests
- Use proper wait conditions for UI elements
- Handle network timeouts gracefully

### 3. Test Organization

- Group related tests in describe blocks
- Use descriptive test names
- Follow AAA pattern (Arrange, Act, Assert)

### 4. Environment Management

- Use environment variables for configuration
- Separate test, development, and production configurations
- Never commit sensitive data

## Troubleshooting

### Common Issues

1. **Services not starting**: Check port availability (8080, 8100)
2. **Test timeouts**: Increase timeout values in configuration
3. **Database connection issues**: Verify test profile configuration
4. **Frontend build errors**: Clear node_modules and reinstall

### Debug Commands

```bash
# Check service health
curl http://localhost:8080/api/health
curl http://localhost:8100

# View backend logs
cd backend/firefighter-platform && ./mvnw spring-boot:run -Dspring-boot.run.profiles=test

# View frontend logs
cd frontend/FireFighter && npm start

# Run specific test file
cd integration-tests && npm test -- --testNamePattern="Auth"
```

## Performance Optimization

- Use npm workspaces for better dependency management
- Cache Maven and npm dependencies in CI/CD
- Run tests in parallel when possible
- Use test-specific lightweight configurations

## Contributing

1. Follow the established project structure
2. Add tests for new features
3. Update documentation when making changes
4. Ensure all tests pass before submitting PRs
5. Use conventional commit messages

## Additional Resources

- [Jest Documentation](https://jestjs.io/docs/getting-started)
- [Cypress Documentation](https://docs.cypress.io/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [Angular Testing](https://angular.io/guide/testing) 
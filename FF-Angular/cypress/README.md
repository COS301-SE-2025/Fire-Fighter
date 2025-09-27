# Fire-Fighter E2E Tests

This directory contains End-to-End (E2E) tests for the Fire-Fighter Angular application using Cypress.

## Test Coverage

The E2E test suite covers the following critical user journeys:

### 1. Landing Page (`01-landing-page.cy.ts`)
- ✅ Page loads correctly
- ✅ Navigation elements are present
- ✅ Responsive design across different viewports

### 2. Login Flow (`02-login-flow.cy.ts`)
- ✅ Login page accessibility
- ✅ Form elements validation
- ✅ Navigation to registration
- ✅ Basic form validation

### 3. Dashboard Navigation (`03-dashboard-navigation.cy.ts`)
- ✅ Authenticated dashboard access
- ✅ Navigation menu functionality
- ✅ Page-to-page navigation
- ✅ User-specific content display

### 4. Emergency Request Flow (`04-emergency-request.cy.ts`)
- ✅ Request form accessibility
- ✅ Emergency type selection
- ✅ Form field validation
- ✅ Submit functionality

### 5. Chat Functionality (`05-chat-functionality.cy.ts`)
- ✅ Chat interface loading
- ✅ Message input functionality
- ✅ Suggested questions display
- ✅ User interaction capabilities

## Running E2E Tests

### Prerequisites
1. Ensure the Angular development server is running:
   ```bash
   npm start
   ```

2. The application should be accessible at `http://localhost:4200`

### Running Tests

#### Interactive Mode (Cypress Test Runner)
```bash
npm run e2e:open
```
This opens the Cypress Test Runner where you can:
- See tests running in real-time
- Debug test failures
- Inspect DOM elements
- Time travel through test steps

#### Headless Mode (CI/CD)
```bash
npm run e2e
# or
npm run e2e:headless
```
This runs all tests in headless mode, suitable for:
- Continuous Integration pipelines
- Automated testing
- Quick validation

## Test Strategy

### Authentication Mocking
Tests use a custom `cy.bypassAuth()` command that:
- Mocks Firebase authentication
- Sets up localStorage with test user data
- Bypasses real authentication for testing

### Responsive Testing
Tests validate the application across multiple viewports:
- Mobile: 375x667
- Tablet: 768x1024  
- Desktop: 1280x720

### Error Handling
Tests include validation for:
- Form submission without required fields
- Navigation edge cases
- Loading state handling

## Test Configuration

### Cypress Configuration (`cypress.config.ts`)
- Base URL: `http://localhost:4200`
- Viewport: 1280x720 (default)
- Timeouts: 10 seconds
- Screenshots on failure: Enabled
- Video recording: Disabled (for performance)

### Custom Commands (`cypress/support/commands.ts`)
- `cy.bypassAuth()`: Mock authentication
- `cy.clearAuth()`: Clear authentication state

## CI/CD Integration

Add to your Jenkins pipeline:

```groovy
stage('E2E Tests') {
    steps {
        dir('FF-Angular') {
            sh 'npm start &'
            sh 'sleep 10' // Wait for server to start
            sh 'npm run e2e:headless'
        }
    }
    post {
        always {
            // Archive screenshots and videos
            archiveArtifacts artifacts: 'cypress/screenshots/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'cypress/videos/**/*', allowEmptyArchive: true
        }
    }
}
```

## Best Practices

1. **Keep tests simple and focused** - Each test should verify one specific functionality
2. **Use data attributes** - Add `data-cy` attributes to elements for reliable selection
3. **Mock external dependencies** - Don't rely on real APIs or external services
4. **Test user journeys** - Focus on critical paths users take through the application
5. **Maintain test independence** - Each test should be able to run in isolation

## Troubleshooting

### Common Issues

1. **Tests failing due to timing**
   - Increase timeout values in `cypress.config.ts`
   - Add explicit waits: `cy.wait(1000)`

2. **Authentication issues**
   - Ensure `cy.bypassAuth()` is called before visiting protected routes
   - Check localStorage is properly set

3. **Element not found**
   - Use more specific selectors
   - Add `cy.get().should('exist')` to wait for elements

### Debug Mode
Run tests with debug information:
```bash
DEBUG=cypress:* npm run e2e
```

## Future Enhancements

- Add visual regression testing
- Implement API mocking with cy.intercept()
- Add accessibility testing
- Expand mobile-specific test scenarios
- Add performance testing metrics

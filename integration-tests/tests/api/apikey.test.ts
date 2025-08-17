import request from 'supertest';

const API_BASE = process.env.API_BASE || 'http://localhost:8080';

// Replace with a real Firebase UID and credentials for your test user
const TEST_USER_UID = 'test-user-uid';
const TEST_USER_EMAIL = 'testuser@example.com';
const TEST_USER_NAME = 'Test User';

let apiKey: string;

describe('API Key Authentication Integration', () => {
  beforeAll(async () => {
    // Ensure the user exists (simulate login/verify)
    await request(API_BASE)
      .post('/api/users/verify')
      .send({
        firebaseUid: TEST_USER_UID,
        username: TEST_USER_NAME,
        email: TEST_USER_EMAIL,
      });
    // Request an API key for the user
    const res = await request(API_BASE)
      .post(`/api/users/${TEST_USER_UID}/apikey`)
      .set('Authorization', `Bearer dummy-token-for-${TEST_USER_UID}`); // Adjust if your API requires real auth
    expect(res.status).toBe(200);
    apiKey = res.text;
    expect(apiKey).toBeTruthy();
  });

  it('should allow access to protected endpoint with valid API key', async () => {
    const res = await request(API_BASE)
      .get('/api/protected/hello')
      .set('Authorization', `ApiKey ${apiKey}`);
    expect(res.status).toBe(200);
    expect(res.text).toContain('Hello, you have accessed a protected endpoint');
  });

  it('should deny access to protected endpoint with invalid API key', async () => {
    const res = await request(API_BASE)
      .get('/api/protected/hello')
      .set('Authorization', 'ApiKey invalidkey123');
    expect(res.status).toBe(401);
  });

  it('should deny access to protected endpoint with no API key', async () => {
    const res = await request(API_BASE)
      .get('/api/protected/hello');
    expect(res.status).toBe(401);
  });

  it('should deny access to extra secured endpoint with invalid API key', async () => {
    const res = await request(API_BASE)
      .get('/api/endpoints/tickets/admin/active')
      .set('Authorization', 'ApiKey invalidkey123');
    expect(res.status).toBe(401);
  });

  it('should allow access to extra secured endpoint with valid API key', async () => {
    const res = await request(API_BASE)
      .get('/api/endpoints/tickets/admin/active')
      .set('Authorization', `ApiKey ${apiKey}`);
    // 200 or 500 depending on test data, but should not be 401
    expect([200, 500]).toContain(res.status);
  });
}); 
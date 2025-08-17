import axios from 'axios';
import { config } from '../setup';

describe('Health and User API Integration Tests', () => {
  const apiUrl = config.apiUrl;
  
  describe('GET /api/health', () => {
    it('should return health status', async () => {
      const response = await axios.get(`${apiUrl}/health`);
      
      expect(response.status).toBe(200);
      expect(response.data).toHaveProperty('status', 'UP');
      expect(response.data).toHaveProperty('service', 'FireFighter Backend');
      expect(response.data).toHaveProperty('version', '1.0.0');
      expect(response.data).toHaveProperty('timestamp');
    });
  });

  describe('GET /api/health/detailed', () => {
    it('should return detailed health information', async () => {
      const response = await axios.get(`${apiUrl}/health/detailed`);
      
      expect(response.status).toBe(200);
      expect(response.data).toHaveProperty('status', 'UP');
      expect(response.data).toHaveProperty('service', 'FireFighter Backend');
      expect(response.data).toHaveProperty('components');
      expect(response.data.components).toHaveProperty('database', 'UP');
      expect(response.data.components).toHaveProperty('authentication', 'UP');
      expect(response.data.components).toHaveProperty('api', 'UP');
      expect(response.data).toHaveProperty('system');
    });
  });

  describe('POST /api/users/verify', () => {
    it('should verify or create a user', async () => {
      const userData = {
        firebaseUid: `test_uid_${Date.now()}`,
        username: `testuser_${Date.now()}`,
        email: `test_${Date.now()}@example.com`,
        department: 'Testing'
      };
      
      const response = await axios.post(`${apiUrl}/users/verify`, null, {
        params: userData
      });
      
      expect(response.status).toBe(200);
      expect(response.data).toHaveProperty('userId', userData.firebaseUid);
      expect(response.data).toHaveProperty('username', userData.username);
      expect(response.data).toHaveProperty('email', userData.email);
    });
  });

  describe('GET /api/users/{firebaseUid}/authorized', () => {
    it('should check user authorization status', async () => {
      const firebaseUid = 'test_uid_for_auth_check';
      
      const response = await axios.get(`${apiUrl}/users/${firebaseUid}/authorized`);
      
      expect(response.status).toBe(200);
      expect(typeof response.data).toBe('boolean');
    });
  });

  describe('GET /api/users/authorized', () => {
    it('should get list of authorized users', async () => {
      const response = await axios.get(`${apiUrl}/users/authorized`);
      
      expect(response.status).toBe(200);
      expect(Array.isArray(response.data)).toBe(true);
    });
  });
}); 
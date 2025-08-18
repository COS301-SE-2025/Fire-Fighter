import axios from 'axios';
import { config } from '../setup';

describe('Database Test API Integration Tests', () => {
  const apiUrl = config.apiUrl;
  
  describe('GET /api/test/cors', () => {
    it('should verify CORS is working', async () => {
      const response = await axios.get(`${apiUrl}/test/cors`);
      
      expect(response.status).toBe(200);
      expect(response.data).toContain('CORS is working correctly');
    });
  });

  describe('GET /api/test/run', () => {
    it('should run comprehensive database test', async () => {
      const response = await axios.get(`${apiUrl}/test/run`);
      
      expect(response.status).toBe(200);
      expect(response.data).toContain('test completed successfully');
    });
  });

  describe('POST /api/test/create', () => {
    it('should create a new test entry', async () => {
      const testData = {
        testName: `Integration_Test_${Date.now()}`,
        testValue: 'Integration test value',
        testNumber: 42,
        isActive: true
      };
      
      const response = await axios.post(`${apiUrl}/test/create`, null, {
        params: testData
      });
      
      expect(response.status).toBe(200);
      expect(response.data).toHaveProperty('testName', testData.testName);
      expect(response.data).toHaveProperty('testValue', testData.testValue);
      expect(response.data).toHaveProperty('testNumber', testData.testNumber);
      expect(response.data).toHaveProperty('isActive', testData.isActive);
    });
  });

  describe('GET /api/test/all', () => {
    it('should get all test entries', async () => {
      const response = await axios.get(`${apiUrl}/test/all`);
      
      expect(response.status).toBe(200);
      expect(Array.isArray(response.data)).toBe(true);
    });
  });

  describe('GET /api/test/active', () => {
    it('should get only active test entries', async () => {
      const response = await axios.get(`${apiUrl}/test/active`);
      
      expect(response.status).toBe(200);
      expect(Array.isArray(response.data)).toBe(true);
      
      // Verify all returned entries are active (backend uses 'isActive' field)
      response.data.forEach((entry: any) => {
        expect(entry.isActive).toBe(true);
      });
    });
  });

  describe('GET /api/test/count/active', () => {
    it('should return count of active tests', async () => {
      const response = await axios.get(`${apiUrl}/test/count/active`);
      
      expect(response.status).toBe(200);
      expect(typeof response.data).toBe('number');
      expect(response.data).toBeGreaterThanOrEqual(0);
    });
  });

  describe('GET /api/test/search', () => {
    it('should search test entries by name', async () => {
      const searchTerm = 'Integration';
      const response = await axios.get(`${apiUrl}/test/search`, {
        params: { name: searchTerm }
      });
      
      expect(response.status).toBe(200);
      expect(Array.isArray(response.data)).toBe(true);
    });
  });

  describe('GET /api/test/range', () => {
    it('should get tests in number range', async () => {
      const response = await axios.get(`${apiUrl}/test/range`, {
        params: { start: 1, end: 100 }
      });
      
      expect(response.status).toBe(200);
      expect(Array.isArray(response.data)).toBe(true);
      
      // Verify all returned entries are within range
      response.data.forEach((entry: any) => {
        expect(entry.testNumber).toBeGreaterThanOrEqual(1);
        expect(entry.testNumber).toBeLessThanOrEqual(100);
      });
    });
  });
}); 
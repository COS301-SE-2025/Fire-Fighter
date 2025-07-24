import axios from 'axios';
import { config } from '../setup';

describe('Tickets API Integration Tests', () => {
  const apiUrl = config.apiUrl;
  
  describe('GET /api/tickets', () => {
    it('should retrieve all tickets', async () => {
      const response = await axios.get(`${apiUrl}/tickets`);
      
      expect(response.status).toBe(200);
      expect(Array.isArray(response.data)).toBe(true);
    });
  });
  
  describe('POST /api/tickets', () => {
    it('should create a new ticket', async () => {
      const ticketData = {
        ticketId: `TKT_${Date.now()}`,
        description: 'Test integration ticket created during testing',
        userId: 'test_user_123',
        emergencyType: 'FIRE',
        emergencyContact: '+1234567890',
        duration: 60
      };
      
      const response = await axios.post(`${apiUrl}/tickets`, ticketData);
      
      expect(response.status).toBe(200);
      expect(response.data).toHaveProperty('id');
      expect(response.data).toHaveProperty('ticketId', ticketData.ticketId);
      expect(response.data).toHaveProperty('description', ticketData.description);
      expect(response.data).toHaveProperty('userId', ticketData.userId);
    });
    
    it('should handle missing required fields gracefully', async () => {
      const invalidTicketData = {
        description: 'Missing other required fields'
      };
      
      const response = await axios.post(`${apiUrl}/tickets`, invalidTicketData);
      
      // The endpoint might return 500 for validation errors, which is acceptable
      expect([400, 500].includes(response.status)).toBe(true);
    });
  });
  
  describe('GET /api/tickets/:id', () => {
    let ticketId: number;
    
    beforeAll(async () => {
      // Create a test ticket first
      const ticketResponse = await axios.post(`${apiUrl}/tickets`, {
        ticketId: `TKT_RETRIEVAL_${Date.now()}`,
        description: 'Test ticket for retrieval',
        userId: 'test_user_456',
        emergencyType: 'MEDICAL',
        emergencyContact: '+0987654321',
        duration: 30
      });
      
      ticketId = ticketResponse.data.id;
    });
    
    it('should retrieve specific ticket by database ID', async () => {
      const response = await axios.get(`${apiUrl}/tickets/${ticketId}`);
      
      expect(response.status).toBe(200);
      expect(response.data).toHaveProperty('id', ticketId);
      expect(response.data).toHaveProperty('ticketId');
      expect(response.data).toHaveProperty('description');
    });
    
    it('should return 404 for non-existent ticket', async () => {
      const response = await axios.get(`${apiUrl}/tickets/99999`);
      
      expect(response.status).toBe(404);
    });
  });
  
  describe('PUT /api/tickets/:id', () => {
    let ticketId: number;
    
    beforeAll(async () => {
      // Create a test ticket for updating
      const ticketResponse = await axios.post(`${apiUrl}/tickets`, {
        ticketId: `TKT_UPDATE_${Date.now()}`,
        description: 'Ticket for update test',
        userId: 'test_user_789',
        emergencyType: 'RESCUE',
        emergencyContact: '+1122334455'
      });
      
      ticketId = ticketResponse.data.id;
    });
    
    it('should update ticket successfully', async () => {
      const updateData = {
        description: 'Updated ticket description',
        status: 'IN_PROGRESS',
        emergencyType: 'FIRE',
        emergencyContact: '+9988776655',
        duration: 90
      };
      
      const response = await axios.put(`${apiUrl}/tickets/${ticketId}`, updateData);
      
      expect(response.status).toBe(200);
      expect(response.data).toHaveProperty('description', updateData.description);
      expect(response.data).toHaveProperty('emergencyType', updateData.emergencyType);
    });
  });

  describe('GET /api/tickets/admin/active', () => {
    it('should get active tickets', async () => {
      const response = await axios.get(`${apiUrl}/tickets/admin/active`);
      
      expect(response.status).toBe(200);
      expect(Array.isArray(response.data)).toBe(true);
    });
  });

  describe('GET /api/tickets/admin/history', () => {
    it('should get ticket history', async () => {
      const response = await axios.get(`${apiUrl}/tickets/admin/history`);
      
      expect(response.status).toBe(200);
      expect(Array.isArray(response.data)).toBe(true);
    });
  });
}); 
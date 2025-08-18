import axios from 'axios';

// Global test configuration
export const config = {
  frontendUrl: process.env.FRONTEND_URL || 'http://localhost:4200',
  backendUrl: process.env.BACKEND_URL || 'http://localhost:8080',
  apiUrl: process.env.API_URL || 'http://localhost:8080/api',
  timeout: 30000
};

// Setup axios defaults
axios.defaults.timeout = config.timeout;
axios.defaults.validateStatus = () => true; // Don't throw on non-200 status codes

// Global test utilities
export const waitForServices = async (maxRetries = 30, delay = 1000) => {
  let retries = 0;
  while (retries < maxRetries) {
    try {
      // Check backend health
      const backendResponse = await axios.get(`${config.backendUrl}/api/health`);
      if (backendResponse.status === 200) {
        console.log('✅ Backend service is ready');
        return true;
      }
    } catch (error) {
      console.log(`⏳ Waiting for services... (attempt ${retries + 1}/${maxRetries})`);
    }
    
    retries++;
    await new Promise(resolve => setTimeout(resolve, delay));
  }
  
  throw new Error('Services failed to start within timeout period');
};

// Setup before all tests
beforeAll(async () => {
  console.log('🚀 Starting integration test setup...');
  await waitForServices();
  console.log('✅ All services are ready for testing');
}, 60000); 
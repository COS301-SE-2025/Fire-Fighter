import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.apex.firefighter',
  appName: 'FireFighter',
  webDir: 'www',
  server: {
    // Allow cleartext traffic for HTTP during development
    cleartext: true,
    // Use HTTP scheme instead of HTTPS to avoid mixed content issues
    androidScheme: 'http'
  },
  plugins: {
    FirebaseAuthentication: {
      skipNativeAuth: false,
      providers: ['google.com','email']
    },
    StatusBar: {
      style: 'default',
      backgroundColor: '#ffffff',
      overlaysWebView: false
    },
    CapacitorHttp: {
      enabled: false  // Disable to force use of web-based HTTP requests through interceptor
    }
  }
};

export default config;
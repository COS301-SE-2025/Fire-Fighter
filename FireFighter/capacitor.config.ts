import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.apex.firefighter',
  appName: 'FireFighter',
  webDir: 'www',

   plugins: {
    FirebaseAuthentication: {
      skipNativeAuth: false,
      providers: ['google.com','email']
    },
    StatusBar: {
      style: 'default',
      backgroundColor: '#ffffff',
      overlaysWebView: false
    }
  }
};

export default config;
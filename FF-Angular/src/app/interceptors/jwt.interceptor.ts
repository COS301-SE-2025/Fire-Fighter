import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  
  console.log('🔒 JWT INTERCEPTOR: Processing request to:', req.url);
  
  // Get the JWT token from the auth service
  const jwtToken = authService.getJwtToken();
  console.log('🔒 JWT INTERCEPTOR: JWT token available:', !!jwtToken);
  
  if (jwtToken) {
    console.log('🔒 JWT INTERCEPTOR: JWT token (first 20 chars):', jwtToken.substring(0, Math.min(20, jwtToken.length)) + '...');
  }

  // Skip adding token for authentication endpoints to avoid loops
  const isAuthEndpoint = req.url.includes('/api/auth/');
  console.log('🔒 JWT INTERCEPTOR: Is auth endpoint:', isAuthEndpoint);

  if (jwtToken && !isAuthEndpoint) {
    console.log('🔒 JWT INTERCEPTOR: Adding Authorization header');
    // Clone the request and add the Authorization header
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${jwtToken}`
      }
    });
  } else {
    console.log('🔒 JWT INTERCEPTOR: Not adding Authorization header - token missing or auth endpoint');
  }

  return next(req);
};

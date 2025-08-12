import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  
  // Get the JWT token from the auth service
  const jwtToken = authService.getJwtToken();

  // Skip adding token for authentication endpoints to avoid loops
  const isAuthEndpoint = req.url.includes('/api/auth/');

  if (jwtToken && !isAuthEndpoint) {
    // Clone the request and add the Authorization header
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${jwtToken}`
      }
    });
  }

  return next(req);
};

import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { from, switchMap } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  // Skip token refresh during registration flow (including inactive-account page)
  const isRegistrationRoute = router.url.includes('/register') || 
                              router.url.includes('/access-request') ||
                              router.url.includes('/inactive-account');
  
  if (isRegistrationRoute) {
    return next(req);
  }
  
  // Skip adding token for public endpoints
  const isPublicEndpoint = req.url.includes('/api/auth/') || 
                          req.url.includes('/api/registration/') ||
                          req.url.includes('/authorized');

  if (isPublicEndpoint) {
    return next(req);
  }

  // Check if token needs refresh before making the request
  if (authService.isTokenExpiredOrExpiringSoon(5)) {
    return from(authService.autoRefreshTokenIfNeeded()).pipe(
      switchMap(() => {
        const jwtToken = authService.getJwtToken();
        
        if (jwtToken) {
          const clonedReq = req.clone({
            setHeaders: { Authorization: `Bearer ${jwtToken}` }
          });
          return next(clonedReq);
        }
        return next(req);
      })
    );
  }
  
  // Token is still valid, proceed normally
  const jwtToken = authService.getJwtToken();
  
  if (jwtToken) {
    const clonedReq = req.clone({
      setHeaders: { Authorization: `Bearer ${jwtToken}` }
    });
    return next(clonedReq);
  }
  
  return next(req);
};

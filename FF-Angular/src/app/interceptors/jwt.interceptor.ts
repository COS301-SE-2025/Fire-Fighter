import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { from, switchMap } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  
  console.log('🔒 JWT INTERCEPTOR: Processing request to:', req.url);
  
  // Skip adding token for public endpoints (authentication and registration)
  const isPublicEndpoint = req.url.includes('/api/auth/') || 
                          req.url.includes('/api/registration/submit');
  console.log('🔒 JWT INTERCEPTOR: Is public endpoint:', isPublicEndpoint);

  if (isPublicEndpoint) {
    console.log('🔒 JWT INTERCEPTOR: Skipping token for public endpoint');
    return next(req);
  }

  // Check if token needs refresh before making the request
  if (authService.isTokenExpiredOrExpiringSoon(5)) { // 5 minutes threshold
    console.log('🔒 JWT INTERCEPTOR: Token expiring soon, refreshing...');
    
    return from(authService.autoRefreshTokenIfNeeded()).pipe(
      switchMap(() => {
        // Get the (potentially refreshed) token
        const jwtToken = authService.getJwtToken();
        console.log('🔒 JWT INTERCEPTOR: JWT token available:', !!jwtToken);
        
        if (jwtToken) {
          console.log('🔒 JWT INTERCEPTOR: JWT token (first 20 chars):', jwtToken.substring(0, Math.min(20, jwtToken.length)) + '...');
          console.log('🔒 JWT INTERCEPTOR: Adding Authorization header');
          
          // Clone the request and add the Authorization header
          const clonedReq = req.clone({
            setHeaders: {
              Authorization: `Bearer ${jwtToken}`
            }
          });
          
          return next(clonedReq);
        } else {
          console.log('🔒 JWT INTERCEPTOR: No token available after refresh attempt');
          return next(req);
        }
      })
    );
  } else {
    // Token is still valid, proceed normally
    const jwtToken = authService.getJwtToken();
    console.log('🔒 JWT INTERCEPTOR: JWT token available:', !!jwtToken);
    
    if (jwtToken) {
      console.log('🔒 JWT INTERCEPTOR: JWT token (first 20 chars):', jwtToken.substring(0, Math.min(20, jwtToken.length)) + '...');
      console.log('🔒 JWT INTERCEPTOR: Adding Authorization header');
      
      // Clone the request and add the Authorization header
      const clonedReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${jwtToken}`
        }
      });
      
      return next(clonedReq);
    } else {
      console.log('🔒 JWT INTERCEPTOR: No token available');
      return next(req);
    }
  }
};

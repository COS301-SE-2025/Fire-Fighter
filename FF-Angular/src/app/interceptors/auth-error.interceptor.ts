import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      console.log('🔒 AUTH ERROR INTERCEPTOR: Caught error:', error.status, error.message);

      if (error.status === 401) {
        const errorBody = error.error;
        
        // Check if this is a token expiration error
        if (errorBody?.error === 'TOKEN_EXPIRED' || errorBody?.requiresReauth) {
          console.log('🔒 AUTH ERROR INTERCEPTOR: Token expired, attempting refresh...');
          
          // Attempt to refresh token
          return authService.refreshJwtToken().then(success => {
            if (success) {
              console.log('✅ AUTH ERROR INTERCEPTOR: Token refreshed, retrying request');
              
              // Clone the request with new token
              const newToken = authService.getJwtToken();
              const clonedReq = req.clone({
                setHeaders: {
                  Authorization: `Bearer ${newToken}`
                }
              });
              
              // Retry the original request
              return next(clonedReq);
            } else {
              console.log('❌ AUTH ERROR INTERCEPTOR: Token refresh failed, redirecting to login');
              
              // Refresh failed, redirect to login
              authService.logout();
              router.navigate(['/login'], { 
                queryParams: { 
                  reason: 'session_expired',
                  message: 'Your session has expired. Please log in again.' 
                } 
              });
              
              return throwError(() => error);
            }
          }).catch(() => {
            console.log('❌ AUTH ERROR INTERCEPTOR: Token refresh error, redirecting to login');
            
            // Refresh failed, redirect to login
            authService.logout();
            router.navigate(['/login'], { 
              queryParams: { 
                reason: 'session_expired',
                message: 'Your session has expired. Please log in again.' 
              } 
            });
            
            return throwError(() => error);
          });
        } else {
          console.log('🔒 AUTH ERROR INTERCEPTOR: Invalid token, redirecting to login');
          
          // Invalid token, redirect to login
          authService.logout();
          router.navigate(['/login'], { 
            queryParams: { 
              reason: 'invalid_token',
              message: 'Authentication failed. Please log in again.' 
            } 
          });
        }
      }

      // For non-401 errors, just pass them through
      return throwError(() => error);
    })
  );
};
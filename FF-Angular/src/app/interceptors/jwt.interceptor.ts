import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Get the JWT token from the auth service
    const jwtToken = this.authService.getJwtToken();

    // Skip adding token for authentication endpoints to avoid loops
    const isAuthEndpoint = request.url.includes('/api/auth/');

    if (jwtToken && !isAuthEndpoint) {
      // Clone the request and add the Authorization header
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${jwtToken}`
        }
      });
    }

    return next.handle(request);
  }
}

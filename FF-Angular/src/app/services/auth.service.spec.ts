import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { testProviders } from '../../test-setup';

describe('AuthService', () => {
  let service: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [...testProviders]
    });
    service = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});

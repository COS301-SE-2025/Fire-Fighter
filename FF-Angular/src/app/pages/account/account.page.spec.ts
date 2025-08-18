import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AccountPage } from './account.page';
import { testProviders, mockAuthService } from '../../../test-setup';
import { AuthService } from '../../services/auth.service';

describe('AccountPage', () => {
  let component: AccountPage;
  let fixture: ComponentFixture<AccountPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountPage],
      providers: [
        ...testProviders,
        { provide: AuthService, useValue: mockAuthService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AccountPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

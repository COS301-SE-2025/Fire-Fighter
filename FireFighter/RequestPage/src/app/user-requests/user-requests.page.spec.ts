import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserRequestsPage } from './user-requests.page';

describe('UserRequestsPage', () => {
  let component: UserRequestsPage;
  let fixture: ComponentFixture<UserRequestsPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(UserRequestsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RequestsPage } from './requests.page';
import { testProviders } from '../../../test-setup';

describe('RequestsPage', () => {
  let component: RequestsPage;
  let fixture: ComponentFixture<RequestsPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RequestsPage],
      providers: [...testProviders]
    }).compileComponents();

    fixture = TestBed.createComponent(RequestsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

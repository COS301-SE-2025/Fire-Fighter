import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AppLoadingScreenComponent } from './app-loading-screen.component';
import { AppLoadingService } from '../../services/app-loading.service';
import { BehaviorSubject } from 'rxjs';

describe('AppLoadingScreenComponent', () => {
  let component: AppLoadingScreenComponent;
  let fixture: ComponentFixture<AppLoadingScreenComponent>;
  let mockAppLoadingService: jasmine.SpyObj<AppLoadingService>;
  let loadingStateSubject: BehaviorSubject<any>;

  beforeEach(async () => {
    loadingStateSubject = new BehaviorSubject({
      isLoading: true,
      message: 'Loading...',
      progress: 50
    });

    mockAppLoadingService = jasmine.createSpyObj('AppLoadingService', ['getCurrentState'], {
      loadingState$: loadingStateSubject.asObservable()
    });

    await TestBed.configureTestingModule({
      imports: [AppLoadingScreenComponent],
      providers: [
        { provide: AppLoadingService, useValue: mockAppLoadingService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppLoadingScreenComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display loading state', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.querySelector('.loading-message-professional')).toBeTruthy();
  });

  it('should update loading state when service emits new state', () => {
    const newState = {
      isLoading: false,
      message: 'Complete!',
      progress: 100
    };

    loadingStateSubject.next(newState);
    fixture.detectChanges();

    expect(component.loadingState).toEqual(newState);
  });

  it('should show progress bar when progress is defined', () => {
    loadingStateSubject.next({
      isLoading: true,
      message: 'Loading...',
      progress: 75
    });
    fixture.detectChanges();

    const progressBar = fixture.nativeElement.querySelector('[style*="width: 75%"]');
    expect(progressBar).toBeTruthy();
  });

  it('should hide loading screen when not loading', () => {
    loadingStateSubject.next({
      isLoading: false,
      message: 'Complete!',
      progress: 100
    });
    fixture.detectChanges();

    const loadingOverlay = fixture.nativeElement.querySelector('.opacity-0');
    expect(loadingOverlay).toBeTruthy();
  });
});

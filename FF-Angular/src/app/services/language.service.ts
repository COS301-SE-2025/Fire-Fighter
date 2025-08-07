import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Language {
  code: string;
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class LanguageService {
  private readonly STORAGE_KEY = 'selectedLanguage';
  private currentLanguageSubject = new BehaviorSubject<string>('en');
  
  public currentLanguage$: Observable<string> = this.currentLanguageSubject.asObservable();

  availableLanguages: Language[] = [
    {
      code: 'en',
      name: 'English (UK)'
    },
    {
      code: 'de',
      name: 'Deutsch (German)'
    }
  ];

  constructor(private translateService: TranslateService) {
    this.initializeLanguage();
  }

  /**
   * Initialize the language service with saved or default language
   */
  private initializeLanguage(): void {
    // Set available languages
    this.translateService.addLangs(this.availableLanguages.map(lang => lang.code));
    
    // Set default language
    this.translateService.setDefaultLang('en');
    
    // Load saved language or use default
    const savedLanguage = this.getSavedLanguage();
    const languageToUse = this.isLanguageSupported(savedLanguage) ? savedLanguage : 'en';
    
    this.setLanguage(languageToUse);
  }

  /**
   * Get the currently saved language from localStorage
   */
  private getSavedLanguage(): string {
    try {
      const saved = localStorage.getItem(this.STORAGE_KEY);
      if (saved) {
        // Handle both old format (with quotes) and new format
        const parsed = JSON.parse(saved);
        // If it's the old format with language code like "en-GB", extract the language part
        if (typeof parsed === 'string') {
          return parsed.split('-')[0]; // Convert "en-GB" to "en", "de-DE" to "de"
        }
        return parsed;
      }
    } catch (error) {
      console.warn('Error loading saved language:', error);
    }
    return 'en';
  }

  /**
   * Check if a language code is supported
   */
  private isLanguageSupported(languageCode: string): boolean {
    return this.availableLanguages.some(lang => lang.code === languageCode);
  }

  /**
   * Set the current language
   */
  setLanguage(languageCode: string): void {
    if (!this.isLanguageSupported(languageCode)) {
      console.warn(`Language ${languageCode} is not supported. Using default language.`);
      languageCode = 'en';
    }

    // Update the translation service
    this.translateService.use(languageCode);
    
    // Save to localStorage
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(languageCode));
    
    // Update the current language subject
    this.currentLanguageSubject.next(languageCode);
    
    console.log(`Language changed to: ${languageCode}`);
  }

  /**
   * Get the current language code
   */
  getCurrentLanguage(): string {
    return this.currentLanguageSubject.value;
  }

  /**
   * Get the current language object
   */
  getCurrentLanguageObject(): Language {
    const currentCode = this.getCurrentLanguage();
    return this.availableLanguages.find(lang => lang.code === currentCode) || this.availableLanguages[0];
  }

  /**
   * Get a language object by code
   */
  getLanguageByCode(code: string): Language | undefined {
    return this.availableLanguages.find(lang => lang.code === code);
  }

  /**
   * Get translation for a key
   */
  getTranslation(key: string, params?: any): Observable<string> {
    return this.translateService.get(key, params);
  }

  /**
   * Get instant translation for a key (synchronous)
   */
  getInstantTranslation(key: string, params?: any): string {
    return this.translateService.instant(key, params);
  }

  /**
   * Check if translations are loaded for current language
   */
  isLanguageLoaded(): boolean {
    const currentLang = this.getCurrentLanguage();
    return this.translateService.getLangs().includes(currentLang);
  }

  /**
   * Reload translations for current language
   */
  reloadTranslations(): void {
    const currentLang = this.getCurrentLanguage();
    this.translateService.reloadLang(currentLang);
  }
}

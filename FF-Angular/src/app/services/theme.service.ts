import { Injectable } from '@angular/core';
import { Capacitor } from '@capacitor/core';
import { StatusBar, Style } from '@capacitor/status-bar';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private isDarkMode = false;

  constructor() {
    this.initializeTheme();
  }

  private initializeTheme(): void {
    const savedTheme = localStorage.getItem('theme');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    
    this.isDarkMode = savedTheme === 'dark' || (!savedTheme && prefersDark);
    this.applyTheme();
  }

  toggleTheme(): void {
    this.isDarkMode = !this.isDarkMode;
    localStorage.setItem('theme', this.isDarkMode ? 'dark' : 'light');
    this.applyTheme();
  }

  setTheme(isDark: boolean): void {
    this.isDarkMode = isDark;
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
    this.applyTheme();
  }

  getCurrentTheme(): boolean {
    return this.isDarkMode;
  }

  private applyTheme(): void {
    this.updateHtmlClass();
    this.updateStatusBar();
  }

  private updateHtmlClass(): void {
    const html = document.documentElement;
    html.classList.toggle('dark', this.isDarkMode);
  }

  private async updateStatusBar(): Promise<void> {
    try {
      if (Capacitor.isNativePlatform()) {
        if (this.isDarkMode) {
          // Dark mode: use light content (white text) on dark background
          await StatusBar.setStyle({ style: Style.Dark });
          await StatusBar.setBackgroundColor({ color: '#111827' }); // gray-900
        } else {
          // Light mode: use dark content (dark text) on light background
          await StatusBar.setStyle({ style: Style.Light });
          await StatusBar.setBackgroundColor({ color: '#ffffff' }); // white
        }
      }
    } catch (error) {
      console.warn('Could not update status bar:', error);
    }
  }

  async setStatusBarDark(): Promise<void> {
    try {
      if (Capacitor.isNativePlatform()) {
        // Force dark status bar with light content (white text)
        await StatusBar.setStyle({ style: Style.Dark });
        await StatusBar.setBackgroundColor({ color: '#111827' }); // gray-900
      }
    } catch (error) {
      console.warn('Could not set dark status bar:', error);
    }
  }
} 
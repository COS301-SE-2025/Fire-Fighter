import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class VersionService {
  
  constructor() { }

  /**
   * Get the application version from environment
   * @returns The version string (e.g., "2.1.0")
   */
  getVersion(): string {
    return environment.appVersion;
  }

  /**
   * Get the application name from environment
   * @returns The application name
   */
  getAppName(): string {
    return environment.appName;
  }

  /**
   * Get formatted version info for display
   * @returns Formatted version string (e.g., "Version 0.0.1 • BMW IT Hub")
   */
  getFormattedVersion(): string {
    return `Version ${this.getVersion()} • BMW IT Hub`;
  }

  /**
   * Get application info object
   * @returns Object containing version, name, and formatted strings
   */
  getAppInfo() {
    return {
      version: this.getVersion(),
      name: this.getAppName(),
      formattedVersion: this.getFormattedVersion(),
      description: 'FireFighter Emergency Access Management System'
    };
  }
} 
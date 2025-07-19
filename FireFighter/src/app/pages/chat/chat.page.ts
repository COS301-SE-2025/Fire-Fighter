import { Component, OnInit, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent, IonHeader, IonTitle, IonToolbar } from '@ionic/angular/standalone';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { AuthService } from '../../services/auth.service';
import { ChatbotService, ChatbotResponse } from '../../services/chatbot.service';
import { User } from '@angular/fire/auth';
import { firstValueFrom } from 'rxjs';

export interface ChatMessage {
  id: string;
  content: string;
  isUser: boolean;
  timestamp: Date;
  isTyping?: boolean;
}

@Component({
  selector: 'app-chat',
  templateUrl: './chat.page.html',
  styleUrls: ['./chat.page.scss'],
  standalone: true,
  imports: [IonContent, IonHeader, IonTitle, IonToolbar, CommonModule, FormsModule, NavbarComponent]
})
export class ChatPage implements OnInit, AfterViewChecked {
  @ViewChild('chatContainer') chatContainer!: ElementRef;
  @ViewChild('messageInput') messageInput!: ElementRef;

  messages: ChatMessage[] = [];
  currentMessage: string = '';
  isLoading: boolean = false;
  user: User | null = null;
  apiHealthy: boolean | null = null; // null = checking, true = healthy, false = unhealthy
  apiHealthStatus: string = 'Checking...';
  showQuickActions: boolean = true;

  suggestedQuestions: string[] = [
    'Show me my tickets',
    'What elevated access do I currently have?',
    'Show my active tickets',
    'How do I create a new emergency request?'
  ];

  quickActions: string[] = [
    'Show my active tickets',
    'Show recent activity',
    'What emergency types are available?',
    'How do I request emergency access?',
    'What elevated access do I currently have?',
    'Help'
  ];

  private shouldScrollToBottom = false;

  constructor(
    private authService: AuthService,
    private chatbotService: ChatbotService
  ) { }

  ngOnInit() {
    // Check API health immediately when component loads
    this.checkAPIHealth();

    // Get current user and load suggestions
    this.authService.user$.subscribe(user => {
      this.user = user;
      if (user?.uid) {
        this.loadSuggestions(user.uid);
      }
    });
  }

  /**
   * Load personalized suggestions from the API
   */
  private loadSuggestions(userId: string): void {
    this.chatbotService.getSuggestions(userId).subscribe({
      next: (response) => {
        if (response.suggestions && response.suggestions.length > 0) {
          this.suggestedQuestions = response.suggestions;
        } else {
          // Use enhanced default suggestions if API doesn't return any
          this.setDefaultSuggestions();
        }
      },
      error: (error) => {
        console.warn('Failed to load suggestions, using enhanced defaults:', error);
        // Use enhanced default suggestions if API fails
        this.setDefaultSuggestions();
      }
    });
  }

  /**
   * Set enhanced default suggestions based on FireFighter capabilities
   */
  private setDefaultSuggestions(): void {
    const allSuggestions = [
      // Ticket Data Queries
      'Show me my tickets',
      'Show my access tickets',
      'Show my active tickets',
      'What elevated access do I currently have?',
      'Do I have any active access permissions?',
      'Show my current access',
      'Show recent access activity',
      'Show my recent activity',
      'Show my closed tickets',
      'What tickets have I completed?',

      // Emergency Type Specific
      'Do I have any security incident access?',
      'Show my critical system failure tickets',
      'What data recovery access do I have?',
      'Do I have any network outage permissions?',
      'Show my user lockout tickets',
      'What other emergency access do I have?',

      // Request Creation Guidance
      'How do I create a new emergency request?',
      'How do I request emergency access?',
      'What information do I need for a request?',
      'What emergency types are available?',
      'How long can I request access for?',

      // System Information
      'What is FireFighter?',
      'How does emergency access work?',
      'How does temporary access work?',
      'When does my access expire?'
    ];

    // Randomly select 4 suggestions to display
    const shuffled = allSuggestions.sort(() => 0.5 - Math.random());
    this.suggestedQuestions = shuffled.slice(0, 4);
  }

  /**
   * Check if the chatbot API is healthy
   */
  checkAPIHealth(): void {
    // Set initial checking state
    this.apiHealthy = null;
    this.apiHealthStatus = 'Checking...';

    this.chatbotService.getHealth().subscribe({
      next: (health) => {
        console.log('API Health Response:', health);

        if (health.status === 'healthy') {
          this.apiHealthy = true;
          this.apiHealthStatus = `Service Online (v${health.version || '1.0.0'})`;
        } else {
          this.apiHealthy = false;
          this.apiHealthStatus = 'Service Degraded';
        }
      },
      error: (error) => {
        console.warn('API health check failed:', error);
        this.apiHealthy = false;

        // Provide more specific error messages based on error type
        if (error.status === 0) {
          this.apiHealthStatus = 'Service Offline (Connection Failed)';
        } else if (error.status === 404) {
          this.apiHealthStatus = 'Service Not Found';
        } else if (error.status >= 500) {
          this.apiHealthStatus = 'Service Error';
        } else {
          this.apiHealthStatus = 'Service Unavailable';
        }
      }
    });
  }

  ngAfterViewChecked() {
    if (this.shouldScrollToBottom) {
      this.scrollToBottom();
      this.shouldScrollToBottom = false;
    }
  }

  sendMessage() {
    if (!this.currentMessage.trim() || this.isLoading) {
      return;
    }

    const userMessage: ChatMessage = {
      id: this.generateMessageId(),
      content: this.currentMessage.trim(),
      isUser: true,
      timestamp: new Date()
    };

    this.messages.push(userMessage);
    const messageContent = this.currentMessage.trim();
    this.currentMessage = '';
    this.shouldScrollToBottom = true;

    // Send to real API
    this.sendToGeminiAPI(messageContent);
  }

  sendSuggestedMessage(suggestion: string) {
    this.currentMessage = suggestion;
    this.sendMessage();
  }

  toggleQuickActions() {
    this.showQuickActions = !this.showQuickActions;
  }

  onKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  private sendToGeminiAPI(userMessage: string) {
    if (!this.user?.uid) {
      this.addErrorMessage('Please log in to use the AI assistant.');
      return;
    }

    this.isLoading = true;

    // Add typing indicator
    const typingMessage: ChatMessage = {
      id: this.generateMessageId(),
      content: '',
      isUser: false,
      timestamp: new Date(),
      isTyping: true
    };

    this.messages.push(typingMessage);
    this.shouldScrollToBottom = true;

    // Determine if this should be an admin query
    const isAdmin = this.authService.isCurrentUserAdmin();
    const shouldUseAdminEndpoint = isAdmin && this.chatbotService.isAdminQuery(userMessage);

    // Choose the appropriate API endpoint
    const apiCall = shouldUseAdminEndpoint
      ? this.chatbotService.sendAdminQuery(userMessage, this.user.uid)
      : this.chatbotService.sendQuery(userMessage, this.user.uid);

    apiCall.subscribe({
      next: (response: ChatbotResponse) => {
        // Remove typing indicator
        this.messages = this.messages.filter(msg => !msg.isTyping);

        // Add API response
        const geminiMessage: ChatMessage = {
          id: this.generateMessageId(),
          content: response.message,
          isUser: false,
          timestamp: new Date(response.timestamp)
        };

        this.messages.push(geminiMessage);
        this.isLoading = false;
        this.shouldScrollToBottom = true;
      },
      error: (errorResponse: ChatbotResponse) => {
        // Remove typing indicator
        this.messages = this.messages.filter(msg => !msg.isTyping);

        // Add error message
        this.addErrorMessage(errorResponse.message);
        this.isLoading = false;
      }
    });
  }

  /**
   * Add an error message to the chat
   */
  private addErrorMessage(message: string): void {
    const errorMessage: ChatMessage = {
      id: this.generateMessageId(),
      content: message,
      isUser: false,
      timestamp: new Date()
    };

    this.messages.push(errorMessage);
    this.shouldScrollToBottom = true;
  }



  formatTime(timestamp: Date): string {
    const now = new Date();
    const diff = now.getTime() - timestamp.getTime();
    const minutes = Math.floor(diff / 60000);

    if (minutes < 1) {
      return 'Just now';
    } else if (minutes < 60) {
      return `${minutes}m ago`;
    } else if (minutes < 1440) {
      const hours = Math.floor(minutes / 60);
      return `${hours}h ago`;
    } else {
      return timestamp.toLocaleDateString();
    }
  }

  formatMessageContent(content: string): string {
    // Convert markdown-like formatting to HTML
    return content
      .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
      .replace(/\*(.*?)\*/g, '<em>$1</em>')
      .replace(/•/g, '&bull;')
      .replace(/\n/g, '<br>');
  }

  getUserInitials(): string {
    if (!this.user) return 'U';

    const displayName = this.user.displayName;
    if (displayName) {
      const names = displayName.split(' ');
      if (names.length >= 2) {
        return (names[0][0] + names[names.length - 1][0]).toUpperCase();
      }
      return names[0][0].toUpperCase();
    }

    const email = this.user.email;
    if (email) {
      return email[0].toUpperCase();
    }

    return 'U';
  }

  trackByMessageId(_index: number, message: ChatMessage): string {
    return message.id;
  }

  private generateMessageId(): string {
    return Date.now().toString() + Math.random().toString(36).substring(2, 11);
  }

  private scrollToBottom(): void {
    try {
      if (this.chatContainer) {
        const element = this.chatContainer.nativeElement;
        element.scrollTop = element.scrollHeight;
      }
    } catch (err) {
      console.error('Error scrolling to bottom:', err);
    }
  }
}

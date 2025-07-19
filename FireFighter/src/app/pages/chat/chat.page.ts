import { Component, OnInit, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonContent, IonHeader, IonTitle, IonToolbar } from '@ionic/angular/standalone';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { AuthService } from '../../services/auth.service';
import { User } from '@angular/fire/auth';

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

  suggestedQuestions: string[] = [
    'Show me my recent tickets',
    'How do I request emergency access?',
    'What are my pending requests?',
    'Help me understand the approval process'
  ];

  quickActions: string[] = [
    'Show tickets',
    'New request',
    'Check status',
    'Help'
  ];

  private shouldScrollToBottom = false;

  constructor(private authService: AuthService) { }

  ngOnInit() {
    // Get current user
    this.authService.user$.subscribe(user => {
      this.user = user;
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

    // Simulate Gemini response
    this.simulateGeminiResponse(messageContent);
  }

  sendSuggestedMessage(suggestion: string) {
    this.currentMessage = suggestion;
    this.sendMessage();
  }

  onKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  private simulateGeminiResponse(userMessage: string) {
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

    // Simulate API delay
    setTimeout(() => {
      // Remove typing indicator
      this.messages = this.messages.filter(msg => !msg.isTyping);

      // Add actual response
      const response = this.generateGeminiResponse(userMessage);
      const geminiMessage: ChatMessage = {
        id: this.generateMessageId(),
        content: response,
        isUser: false,
        timestamp: new Date()
      };

      this.messages.push(geminiMessage);
      this.isLoading = false;
      this.shouldScrollToBottom = true;
    }, 1500 + Math.random() * 1000); // Random delay between 1.5-2.5 seconds
  }

  private generateGeminiResponse(userMessage: string): string {
    const lowerMessage = userMessage.toLowerCase();

    if (lowerMessage.includes('ticket') || lowerMessage.includes('show me')) {
      return `I can help you with your tickets! Here are some things I can do:

• **View Recent Tickets**: I can show you your most recent emergency access tickets
• **Search Tickets**: Find specific tickets by ID, date, or status
• **Ticket Details**: Get detailed information about any ticket

Would you like me to show you your recent tickets or help you search for something specific?`;
    }

    if (lowerMessage.includes('request') || lowerMessage.includes('emergency access')) {
      return `To request emergency access, I can guide you through the process:

**Emergency Access Request Steps:**
1. **Specify the system** you need access to
2. **Provide justification** for the emergency access
3. **Set duration** for how long you need access
4. **Submit for approval** to your manager or admin

Would you like me to help you start a new emergency access request?`;
    }

    if (lowerMessage.includes('pending') || lowerMessage.includes('status')) {
      return `I can help you check the status of your requests! Here's what I can show you:

• **Pending Approvals**: Requests waiting for manager approval
• **Active Access**: Currently granted emergency access
• **Recent Activity**: Latest updates on your requests

Would you like me to check your pending requests or show you active access permissions?`;
    }

    if (lowerMessage.includes('approval') || lowerMessage.includes('process')) {
      return `The **Emergency Access Approval Process** works like this:

1. **Request Submission** → You submit an emergency access request
2. **Manager Review** → Your direct manager reviews the request
3. **Admin Approval** → System admin grants the access
4. **Access Granted** → You receive temporary access
5. **Auto Revocation** → Access is automatically removed after the specified duration

**Typical Timeline**: Most requests are processed within 15-30 minutes during business hours.

Need help with a specific step in this process?`;
    }

    if (lowerMessage.includes('help') || lowerMessage.includes('how')) {
      return `I'm here to help you with the **FireFighter Emergency Access Management System**!

**What I can help you with:**
• 🎫 View and manage your tickets
• 🚨 Request emergency access
• ✅ Check approval status
• 📊 Review access history
• ❓ Answer questions about the system

**Quick Commands:**
• "Show my tickets" - View recent tickets
• "New request" - Start emergency access request
• "Check status" - See pending approvals
• "Help with [topic]" - Get specific help

What would you like to do today?`;
    }

    // Default response
    return `I'm Gemini, your AI assistant for the FireFighter Emergency Access Management System.

I can help you with:
• **Managing tickets** and emergency access requests
• **Checking status** of your pending requests
• **Understanding processes** and procedures
• **Finding information** quickly

Try asking me something like "Show me my recent tickets" or "How do I request emergency access?"

What can I help you with today?`;
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

  trackByMessageId(index: number, message: ChatMessage): string {
    return message.id;
  }

  private generateMessageId(): string {
    return Date.now().toString() + Math.random().toString(36).substr(2, 9);
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

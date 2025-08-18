# Email Notification System Implementation Guide

## Overview

This document describes the implementation of an email notification system for the FireFighter platform that sends professional HTML emails to users for ticket-related events. The system is fully integrated with user preferences and provides granular control over email notifications.

## Features Implemented

### 1. Email Notification Types
- **Ticket Creation**: Sent when a new emergency ticket is created
- **Ticket Completion**: Sent when a ticket is automatically completed/closed
- **Ticket Revocation**: Sent when an administrator revokes a ticket
- **Five-Minute Warning**: Sent 5 minutes before a ticket expires

### 2. User Preferences System
- **Master Toggle**: Enable/disable all email notifications
- **Granular Control**: Individual toggles for each notification type
- **Default Setting**: Email notifications are disabled by default
- **Database Storage**: Preferences stored in PostgreSQL database
- **Real-time Updates**: Changes are immediately saved and applied

### 3. Professional Email Templates
- **Consistent Branding**: FireFighter platform branding
- **Responsive Design**: Works on desktop and mobile email clients
- **Rich Information**: Includes ticket details, timestamps, and relevant information
- **Security Notices**: Appropriate warnings and instructions for each notification type

## Backend Implementation

### Database Schema

**Table**: `firefighter.user_preferences`
```sql
- user_id (VARCHAR, PRIMARY KEY) - Firebase UID
- email_notifications_enabled (BOOLEAN) - Master toggle
- email_ticket_creation (BOOLEAN) - Ticket creation emails
- email_ticket_completion (BOOLEAN) - Ticket completion emails  
- email_ticket_revocation (BOOLEAN) - Ticket revocation emails
- email_five_minute_warning (BOOLEAN) - Five-minute warning emails
- created_at (TIMESTAMP) - Creation timestamp
- updated_at (TIMESTAMP) - Last update timestamp
```

### New Backend Components

1. **UserPreferences Entity** (`FF-API/src/main/java/com/apex/firefighter/model/UserPreferences.java`)
   - JPA entity for user preferences
   - Utility methods for checking enabled states
   - Automatic timestamp management

2. **UserPreferencesRepository** (`FF-API/src/main/java/com/apex/firefighter/repository/UserPreferencesRepository.java`)
   - CRUD operations for user preferences
   - Query methods for checking specific notification types
   - Bulk operations for finding users with specific preferences enabled

3. **UserPreferencesService** (`FF-API/src/main/java/com/apex/firefighter/service/UserPreferencesService.java`)
   - Business logic for managing user preferences
   - Default preference creation for new users
   - Bulk enable/disable operations

4. **UserPreferencesController** (`FF-API/src/main/java/com/apex/firefighter/controller/UserPreferencesController.java`)
   - REST API endpoints for preference management
   - CRUD operations with proper error handling
   - Granular preference updates

5. **Enhanced GmailEmailService** (`FF-API/src/main/java/com/apex/firefighter/service/GmailEmailService.java`)
   - New methods for each notification type
   - Professional HTML email templates
   - Consistent styling and branding
   - Error handling and logging

6. **Enhanced NotificationService** (`FF-API/src/main/java/com/apex/firefighter/service/NotificationService.java`)
   - New methods that create both in-app and email notifications
   - Automatic preference checking before sending emails
   - Fallback handling for missing user data

### Updated Services

1. **TicketService** - Updated to use new email-enabled notification methods
2. **TicketScheduledService** - Updated for completion and warning notifications

## Frontend Implementation

### New Angular Service

**UserPreferencesService** (`FF-Angular/src/app/services/user-preferences.service.ts`)
- Manages user preferences via REST API
- Real-time preference updates
- Conversion between frontend and backend data formats
- Error handling and fallback to localStorage
- Observable-based state management

### Updated Settings Page

**Enhanced Settings Component** (`FF-Angular/src/app/pages/settings/settings.page.ts`)
- Integration with UserPreferencesService
- Real-time preference loading and saving
- Automatic change detection and saving
- Fallback to localStorage for unauthenticated users
- Improved user experience with loading states

**Updated Settings Template** (`FF-Angular/src/app/pages/settings/settings.page.html`)
- Enhanced email notification section
- Detailed information about notification types
- Visual feedback for enabled/disabled states
- Change handlers for immediate updates

## API Endpoints

### User Preferences API (`/api/user-preferences`)

- `GET /{userId}` - Get user preferences
- `PUT /{userId}` - Update user preferences
- `PATCH /{userId}/{setting}` - Update specific preference
- `POST /{userId}/enable-all` - Enable all email notifications
- `POST /{userId}/disable-all` - Disable all email notifications
- `POST /{userId}/reset` - Reset to default preferences
- `GET /{userId}/check/{setting}` - Check specific preference status

## Email Templates

### Template Features
- **Responsive Design**: Works across email clients
- **Professional Styling**: Consistent with FireFighter branding
- **Rich Content**: Includes all relevant ticket information
- **Security Notices**: Appropriate warnings for each notification type
- **Accessibility**: Proper contrast and readable fonts

### Template Types
1. **Ticket Creation**: Welcome message with ticket details
2. **Ticket Completion**: Completion confirmation with summary
3. **Ticket Revocation**: Revocation notice with reason
4. **Five-Minute Warning**: Urgent expiration warning

## Configuration

### Email Service Configuration
The system uses the existing Gmail SMTP configuration:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${GMAIL_USERNAME}
spring.mail.password=${GMAIL_APP_PASSWORD}
```

### Default Settings
- Email notifications are **disabled by default**
- Users must explicitly enable email notifications
- All notification types are disabled when email notifications are disabled
- Preferences are automatically created for new users

## Usage Instructions

### For Users
1. Navigate to Settings page
2. Toggle "Email Notifications" to enable email alerts
3. The system will automatically enable ticket-related notifications
4. Changes are saved immediately
5. Email notifications will be sent based on preferences

### For Administrators
1. Users can enable/disable their own email preferences
2. Email notifications respect user preferences
3. System continues to send in-app notifications regardless of email preferences
4. Email failures are logged but don't affect in-app notifications

## Database Migration

Run the provided SQL script to create the user preferences table:
```bash
psql -h your_host -U your_user -d firefighter -f FF-API/database/create_user_preferences_table.sql
```

## Testing

### Backend Testing
- Test all API endpoints with Postman
- Verify email sending with different preference combinations
- Test error handling and fallback scenarios

### Frontend Testing
- Test settings page functionality
- Verify real-time preference updates
- Test with and without authentication
- Verify localStorage fallback

### Email Testing
- Test all email templates in different email clients
- Verify HTML rendering and responsiveness
- Test with different ticket data scenarios

## Security Considerations

1. **User Data Protection**: Only users can modify their own preferences
2. **Email Privacy**: Emails contain only necessary ticket information
3. **Fallback Security**: System continues to work if email service fails
4. **Input Validation**: All preference updates are validated
5. **Error Handling**: Graceful degradation on failures

## Future Enhancements

1. **Email Templates**: Additional customization options
2. **Notification Scheduling**: Time-based notification preferences
3. **Email Frequency**: Digest options for multiple notifications
4. **Advanced Preferences**: More granular control options
5. **Email Analytics**: Tracking email open rates and engagement

## Troubleshooting

### Common Issues
1. **Emails not sending**: Check SMTP configuration and user preferences
2. **Preferences not saving**: Verify API connectivity and authentication
3. **Template rendering**: Check email client compatibility
4. **Database errors**: Verify table creation and foreign key constraints

### Logging
- All email operations are logged with appropriate levels
- Preference changes are tracked with timestamps
- Error conditions are logged with full context
- Success operations include confirmation messages

This implementation provides a comprehensive, user-friendly email notification system that enhances the FireFighter platform's communication capabilities while respecting user preferences and maintaining system reliability.

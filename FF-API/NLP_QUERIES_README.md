# FireFighter NLP Queries & Commands Reference

## 🔥 Overview

The FireFighter Emergency Management System includes a powerful Natural Language Processing (NLP) service called **Ada** that understands and processes natural language queries for ticket management and emergency access control.

## 🎯 Quick Start Examples

```
Show my active tickets
Create hr-emergency ticket for employee incident, duration 60 minutes, contact 0123456789
What emergency types are available?
Help
```

---

## 📋 Ticket Query Commands

### Show Tickets

#### Show My Tickets
```
show my tickets
my tickets
list my tickets
what tickets do i have
tickets for me
tickets
```

#### Show Active Tickets
```
show active tickets
active tickets
current tickets
show my active tickets
```

#### Show Completed Tickets
```
show completed tickets
completed tickets
finished tickets
show closed tickets
closed tickets
```

#### Show Rejected/Revoked Tickets
```
show rejected tickets
rejected tickets
denied tickets
show denied tickets
revoked tickets
show revoked tickets
```

#### Show All Tickets (Admin Only)
```
show all tickets
all tickets
system tickets
```

### Search & Details

#### Search Tickets
```
search for tickets
find tickets
search tickets
```

#### Get Ticket Details
```
show ticket details
ticket details
details of ticket
show details for [TICKET-ID]
```

---

## 🆕 Ticket Creation Commands

### Basic Ticket Creation
```
create ticket
new ticket
create emergency ticket
new emergency request
```

### Emergency Type Specific Tickets

#### HR Emergency
```
create hr-emergency ticket for [description]
new hr-emergency ticket for [description]
hr emergency ticket for [description]
```

#### Financial Emergency
```
create financial-emergency ticket for [description]
new financial-emergency ticket for [description]
financial emergency ticket for [description]
```

#### Management Emergency
```
create management-emergency ticket for [description]
new management-emergency ticket for [description]
management emergency ticket for [description]
```

#### Logistics Emergency
```
create logistics-emergency ticket for [description]
new logistics-emergency ticket for [description]
logistics emergency ticket for [description]
```

### Complete Ticket Creation Examples

```
create hr-emergency ticket for employee incident, duration 60 minutes, contact 0123456789
new financial-emergency ticket for budget overrun, duration 120 minutes, contact 0987654321
create management-emergency ticket for system outage, duration 30 minutes, contact 0555123456
new logistics-emergency ticket for supply shortage, duration 90 minutes, contact 0444987654
```

---

## ⚙️ Ticket Management Commands

### Update Ticket Status
```
update ticket status
change status
set status
update status of [TICKET-ID]
change status of [TICKET-ID] to [STATUS]
```

### Close Tickets
```
close ticket
end ticket
finish ticket
close ticket [TICKET-ID]
```

---

## 📊 Information & Help Commands

### System Information

#### Recent Activity
```
show recent activity
recent activity
latest activity
recent changes
```

#### Emergency Types
```
what emergency types are available
emergency types
available emergency types
list emergency types
```

#### Emergency Access Help
```
how do i request emergency access
request emergency access
emergency access help
how to request emergency access
```

#### Current Access Level
```
what elevated access do i currently have
my access level
current access
my permissions
```

### Help & Capabilities

#### General Help
```
help
how to
need help
```

#### Show Capabilities
```
what can you do
capabilities
features
show capabilities
```

---

## 🔧 Admin Commands

### System Statistics
```
system statistics
stats
system stats
```

### Export Data
```
export tickets
export
download tickets
export data
```

**📧 Email Export Functionality:**
- Exports all tickets to CSV format
- Automatically emails CSV file to your registered email address
- Includes professional email template with export details
- Supports date range filtering (if implemented in future)
- Admin-only feature for security compliance

---

## 📝 Entity Parameters

### Emergency Types
- `hr-emergency` - Human Resources emergencies
- `financial-emergency` - Financial and budget emergencies  
- `management-emergency` - Management and operational emergencies
- `logistics-emergency` - Supply chain and logistics emergencies

### Duration Options
- `15 minutes`, `30 minutes`, `45 minutes`
- `60 minutes`, `90 minutes`, `120 minutes`
- `1 hour`, `2 hours`, `3 hours`

### Contact Formats
- `contact 0123456789`
- `phone 0123456789`
- `0123456789` (10-digit numbers)
- International formats: `+27123456789`

### Ticket Statuses
- `Active` - Currently active tickets
- `Completed` - Finished tickets
- `Rejected` - Denied/revoked tickets

---

## 🎯 Usage Tips

### 1. Natural Language
Ada understands natural language, so you can phrase requests conversationally:
```
"Can you show me my active tickets?"
"I need to create an HR emergency ticket"
"What are the available emergency types?"
```

### 2. Flexible Formatting
Multiple formats work for the same command:
```
"show my tickets" = "list my tickets" = "what tickets do i have"
```

### 3. Emergency Ticket Creation
Always include these components for emergency tickets:
- **Emergency Type**: hr-emergency, financial-emergency, etc.
- **Description**: Brief reason for the emergency
- **Duration**: How long access is needed
- **Contact**: Phone number for verification

### 4. Case Insensitive
All commands work regardless of capitalization:
```
"SHOW MY TICKETS" = "show my tickets" = "Show My Tickets"
```

---

## 🚨 Emergency Access Workflow

1. **Create Emergency Ticket**
   ```
   create hr-emergency ticket for employee incident, duration 60 minutes, contact 0123456789
   ```

2. **Automatic Group Assignment**
   - HR Emergency → HR Group Access
   - Financial Emergency → Financial Group Access
   - Management Emergency → Manager Group Access
   - Logistics Emergency → Logistics Group Access

3. **Check Your Access**
   ```
   what elevated access do i currently have
   ```

4. **Monitor Activity**
   ```
   show recent activity
   ```

---

## 🔍 Troubleshooting

### Common Issues

1. **"Could not understand query"**
   - Try rephrasing using simpler language
   - Use exact phrases from this guide
   - Check spelling of emergency types

2. **"Invalid entities"**
   - Ensure emergency type is spelled correctly
   - Include required parameters (duration, contact)
   - Use supported duration formats

3. **"Insufficient permissions"**
   - Some commands require admin access
   - Check your user role and permissions

### Getting Help
```
help
what can you do
show capabilities
```

---

## 🔄 Response Examples

### Successful Ticket Creation
```
Input: "create hr-emergency ticket for employee incident, duration 60 minutes, contact 0123456789"
Output: "✅ Operation successful. Ticket [BMW-FF-12345] is now Active."
```

### Ticket List Response
```
Input: "show my active tickets"
Output: "Here are your tickets:
• [BMW-FF-12345] Active - employee incident
• [BMW-FF-12346] Active - system maintenance"
```

### Help Response
```
Input: "help"
Output: "🔥 FireFighter Emergency Management Help 🔥
Available Commands:
• Show my active tickets - View your current active tickets
• Create [emergency-type] ticket for [description] - Create new emergency ticket
..."
```

---

## 🎨 Chat Interface Quick Suggestions

The chat interface provides these quick suggestion buttons:

- **"Show my active tickets"** - View current active tickets
- **"Show recent activity"** - See latest system activity
- **"What emergency types are available?"** - List available emergency types
- **"How do I request emergency access?"** - Emergency access guide
- **"Help"** - General help information

---

## 🔐 Security & Permissions

### User Permissions
Regular users can:
- View their own tickets
- Create emergency tickets
- Get help and information
- Check their access level

### Admin Permissions
Administrators can additionally:
- View all system tickets
- Access system statistics
- Export ticket data
- Manage user operations

---

## 📊 System Integration

### Dolibarr ERP Integration
Emergency tickets automatically assign users to appropriate Dolibarr groups:
- **HR Emergency** → Group ID 7 (HR)
- **Financial Emergency** → Group ID 6 (Financials)
- **Management Emergency** → Group ID 9 (Manager)
- **Logistics Emergency** → Group ID 8 (Logistics)

### Email Notifications
- Automatic email notifications for ticket creation
- Email sent to user's registered email address
- Includes ticket ID and emergency details

### Real-time Updates
- Live status updates in the chat interface
- API health monitoring
- Automatic service status checks

---

## 📞 Support

For additional support or to report issues with NLP queries, contact your system administrator or check the application logs for detailed error messages.

---

## 📝 Version Information

- **NLP Service Version**: Latest
- **Supported Languages**: English
- **Last Updated**: September 2025
- **Compatible with**: FireFighter Emergency Management System v1.0+

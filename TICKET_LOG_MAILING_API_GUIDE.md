# 📧 Ticket Log Mailing Service - API Guide

## Overview

The FireFighter Platform provides a comprehensive ticket log mailing service that allows administrators to export ticket data as CSV files and receive them via email. This service includes admin authentication, date range filtering, and professional email formatting.

## 🔐 Authentication & Authorization

### Admin Check Endpoint
**Purpose**: Verify if a user has administrator privileges before allowing access to export functionality.

```http
GET /api/tickets/admin/check/{userId}
```

**Parameters:**
- `userId` (path): Firebase UID of the user to check

**Response:**
```json
{
  "isAdmin": true
}
```

**Example Usage:**
```javascript
// Check if user is admin before showing export options
const checkAdminStatus = async (userId) => {
  const response = await fetch(`/api/tickets/admin/check/${userId}`);
  const data = await response.json();
  return data.isAdmin;
};
```

## 📊 Ticket Export & Email Service

### Export Tickets Endpoint
**Purpose**: Export tickets to CSV format and email to administrator with optional date range filtering.

```http
POST /api/tickets/admin/export
Content-Type: application/json
```

**Request Body:**
```json
{
  "userId": "firebase-user-id",
  "startDate": "2025-01-01T00:00:00",
  "endDate": "2025-01-31T23:59:59"
}
```

**Parameters:**
- `userId` (string, required): Firebase UID of the admin user
- `startDate` (string, optional): Start date for filtering (ISO format: yyyy-MM-ddTHH:mm:ss)
- `endDate` (string, optional): End date for filtering (ISO format: yyyy-MM-ddTHH:mm:ss)

**Notes:**
- `userId` is required - the system automatically fetches the user's email from the database
- The system verifies the user exists and has admin privileges
- Date parameters are optional - if omitted, all tickets are exported

**Success Response (200 OK):**
```json
"Tickets exported and emailed successfully to admin@company.com (filtered by date range)"
```

**Error Responses:**

**400 Bad Request - Missing userId:**
```json
"Either userId or email is required."
```

**400 Bad Request - Invalid Date Format:**
```json
"Invalid date format. Use ISO format: yyyy-MM-ddTHH:mm:ss"
```

**403 Forbidden - Not Admin:**
```json
"Not authorized: Only admins can export tickets."
```

**500 Internal Server Error:**
```json
"Failed to send email: [error details]"
```

## 📅 Date Range Filtering

### Date Format
All dates must be in ISO 8601 format: `yyyy-MM-ddTHH:mm:ss`

### Examples:
- **Start of day**: `2025-01-15T00:00:00`
- **End of day**: `2025-01-15T23:59:59`
- **Specific time**: `2025-01-15T14:30:00`

### Filtering Logic:
- **Start Date Only**: All tickets from start date onwards
- **End Date Only**: All tickets up to end date
- **Both Dates**: Tickets within the specified range
- **No Dates**: All tickets in the system

## 🎨 Email Content

### Professional Email Template
The service generates professional HTML emails with:
- **FireFighter Platform branding**
- **Export summary** (date generated, ticket count, file size)
- **CSV attachment** with all ticket data
- **Professional styling** with company colors

### CSV Export Format
The CSV file includes these columns:
- `TicketId`: Unique ticket identifier
- `Description`: Ticket description/reason
- `Status`: Current ticket status
- `DateCreated`: When the ticket was created
- `UserId`: Firebase UID of the user who created the ticket
- `EmergencyType`: Type of emergency (Critical System Failure, Security Incident, etc.)
- `EmergencyContact`: Contact information provided
- `Duration`: Access duration in minutes

## 🔧 Frontend Integration Examples

### React/JavaScript Implementation

```javascript
// Check admin status and show export UI
const ExportComponent = ({ currentUserId }) => {
  const [isAdmin, setIsAdmin] = useState(false);
  const [loading, setLoading] = useState(false);
  const [dateRange, setDateRange] = useState({
    startDate: '',
    endDate: ''
  });

  useEffect(() => {
    checkAdminStatus();
  }, []);

  const checkAdminStatus = async () => {
    try {
      const response = await fetch(`/api/tickets/admin/check/${currentUserId}`);
      const data = await response.json();
      setIsAdmin(data.isAdmin);
    } catch (error) {
      console.error('Error checking admin status:', error);
    }
  };

  const exportTickets = async () => {
    if (!isAdmin) return;
    
    setLoading(true);
    try {
      const payload = {
        userId: currentUserId
      };

      // Add date range if specified
      if (dateRange.startDate) {
        payload.startDate = dateRange.startDate + 'T00:00:00';
      }
      if (dateRange.endDate) {
        payload.endDate = dateRange.endDate + 'T23:59:59';
      }

      const response = await fetch('/api/tickets/admin/export', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload)
      });

      if (response.ok) {
        const message = await response.text();
        alert('Success: ' + message);
      } else {
        const error = await response.text();
        alert('Error: ' + error);
      }
    } catch (error) {
      alert('Error: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  if (!isAdmin) {
    return <div>Access denied. Administrator privileges required.</div>;
  }

  return (
    <div className="export-component">
      <h3>Export Ticket Logs</h3>
      
      <div className="date-range">
        <label>
          Start Date:
          <input 
            type="date" 
            value={dateRange.startDate}
            onChange={(e) => setDateRange({...dateRange, startDate: e.target.value})}
          />
        </label>
        
        <label>
          End Date:
          <input 
            type="date" 
            value={dateRange.endDate}
            onChange={(e) => setDateRange({...dateRange, endDate: e.target.value})}
          />
        </label>
      </div>

      <button 
        onClick={exportTickets} 
        disabled={loading}
        className="export-button"
      >
        {loading ? 'Exporting...' : 'Export & Email Tickets'}
      </button>
    </div>
  );
};
```

### Vue.js Implementation

```vue
<template>
  <div v-if="isAdmin" class="export-component">
    <h3>Export Ticket Logs</h3>
    
    <div class="date-range">
      <label>
        Start Date:
        <input v-model="startDate" type="date" />
      </label>
      
      <label>
        End Date:
        <input v-model="endDate" type="date" />
      </label>
    </div>

    <button @click="exportTickets" :disabled="loading">
      {{ loading ? 'Exporting...' : 'Export & Email Tickets' }}
    </button>
  </div>
  
  <div v-else>
    Access denied. Administrator privileges required.
  </div>
</template>

<script>
export default {
  props: ['currentUserId'],
  data() {
    return {
      isAdmin: false,
      loading: false,
      startDate: '',
      endDate: ''
    };
  },
  
  async mounted() {
    await this.checkAdminStatus();
  },
  
  methods: {
    async checkAdminStatus() {
      try {
        const response = await fetch(`/api/tickets/admin/check/${this.currentUserId}`);
        const data = await response.json();
        this.isAdmin = data.isAdmin;
      } catch (error) {
        console.error('Error checking admin status:', error);
      }
    },
    
    async exportTickets() {
      this.loading = true;
      try {
        const payload = { userId: this.currentUserId };
        
        if (this.startDate) payload.startDate = this.startDate + 'T00:00:00';
        if (this.endDate) payload.endDate = this.endDate + 'T23:59:59';

        const response = await fetch('/api/tickets/admin/export', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });

        if (response.ok) {
          const message = await response.text();
          this.$toast.success(message);
        } else {
          const error = await response.text();
          this.$toast.error(error);
        }
      } catch (error) {
        this.$toast.error('Export failed: ' + error.message);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>
```

## 🧪 Testing Examples

### Postman/cURL Examples

**1. Check Admin Status:**
```bash
curl -X GET "https://localhost:8443/api/tickets/admin/check/firebase-user-123"
```

**2. Export All Tickets:**
```bash
curl -X POST "https://localhost:8443/api/tickets/admin/export" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "firebase-admin-123"
  }'
```

**3. Export with Date Range:**
```bash
curl -X POST "https://localhost:8443/api/tickets/admin/export" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "firebase-admin-123",
    "startDate": "2025-01-01T00:00:00",
    "endDate": "2025-01-31T23:59:59"
  }'
```

## 🔒 Security Considerations

### Admin Verification
- **Double-check**: System verifies admin status both by user lookup and database role
- **Email validation**: If email is provided, system confirms user exists and is admin
- **Secure endpoints**: All export functionality requires admin privileges

### Data Protection
- **Encrypted email**: Uses secure SMTP with authentication
- **Professional formatting**: No sensitive data exposed in email body
- **Attachment security**: CSV files are properly encoded and attached

## 📋 Error Handling

### Common Issues & Solutions

**Issue**: "Either userId or email is required"
**Solution**: Provide userId in request body (system will fetch email automatically)

**Issue**: "Invalid date format"
**Solution**: Use ISO format: `2025-01-15T14:30:00`

**Issue**: "Not authorized: Only admins can export tickets"
**Solution**: Ensure user has admin privileges in the system

**Issue**: "Failed to send email"
**Solution**: Check email configuration and SMTP settings

## 🎯 Best Practices

### Frontend Implementation
1. **Always check admin status** before showing export UI
2. **Provide date range options** for better user experience
3. **Show loading states** during export operations
4. **Handle errors gracefully** with user-friendly messages
5. **Validate date inputs** before sending to API

### Date Range Usage
1. **Default to recent data** (e.g., last 30 days) for performance
2. **Limit large exports** to prevent system overload
3. **Provide preset options** (Today, This Week, This Month, etc.)
4. **Validate date ranges** (start date before end date)

---

**📧 FireFighter Platform - Ticket Log Mailing Service**  
*Professional Email Export System for Emergency Access Management*

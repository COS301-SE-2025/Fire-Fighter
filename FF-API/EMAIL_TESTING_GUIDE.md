# FireFighter Platform - Email Testing System

## Overview

This comprehensive email testing system allows you to test all email notification functionality in the FireFighter platform using mock data and real email configuration. The system sends actual emails to all admin users in the database, allowing you to see exactly how the emails will look and function in production.

## 🚀 Quick Start

### Test All Email Types (Recommended)
```bash
POST /api/test/master-email-test/run-comprehensive-test
```

This single endpoint tests **all email types** including:
- **Anomaly Detection Emails** (3 types)
- **Group Change Notification Emails** (3 risk levels)  
- **Ticket Notification Emails** (4 types)

## 📧 Email Types Tested

### 1. Anomaly Detection Emails
Tests behavioral anomaly detection notifications sent to admins:

| Anomaly Type | Risk Level | Description |
|--------------|------------|-------------|
| `FREQUENT_REQUESTS` | MEDIUM | User exceeded request frequency thresholds |
| `DORMANT_USER_ACTIVITY` | HIGH | Previously inactive user suddenly active |
| `OFF_HOURS_ACTIVITY` | LOW | System access outside business hours |

### 2. Group Change Notification Emails
Tests suspicious group change alerts based on the implemented risk assessment:

| Risk Level | Groups | Description |
|------------|--------|-------------|
| **HIGH** | Financial, Management | Always suspicious - immediate investigation |
| **MEDIUM** | HR, HR transfers | Potentially suspicious - review required |
| **LOW** | Logistics | Generally not suspicious - standard monitoring |

### 3. Ticket Notification Emails
Tests standard ticket lifecycle notifications:

| Notification Type | Description |
|-------------------|-------------|
| `TICKET_CREATION` | New emergency ticket created |
| `TICKET_COMPLETION` | Emergency ticket completed |
| `TICKET_REVOCATION` | Emergency ticket revoked by admin |
| `TICKET_WARNING` | Emergency ticket expiring soon (5-minute warning) |

## 🎯 Available Endpoints

### Master Test Controller
**Base URL:** `/api/test/master-email-test`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/run-comprehensive-test` | POST | **Test ALL email types** (recommended) |
| `/test-anomaly-emails` | POST | Test only anomaly detection emails |
| `/test-group-change-emails` | POST | Test only group change emails |
| `/test-ticket-emails` | POST | Test only ticket notification emails |
| `/admin-info` | GET | Get info about admin users who receive emails |
| `/test-info` | GET | Get detailed information about all tests |

### Specific Test Controllers

#### Anomaly Email Testing
**Base URL:** `/api/test/anomaly-emails`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/send-all-anomaly-types` | POST | Send all 3 anomaly types to all admins |
| `/send-specific-anomaly/{type}` | POST | Send specific anomaly type |
| `/send-to-specific-admin` | POST | Send to one admin (params: adminEmail, anomalyType) |
| `/admin-info` | GET | Get admin user information |

#### Group Change Email Testing  
**Base URL:** `/api/test/group-change-emails`

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/send-all-risk-levels` | POST | Send all risk level scenarios |
| `/send-specific-risk/{riskLevel}` | POST | Send specific risk level (HIGH/MEDIUM/LOW) |
| `/send-custom-scenario` | POST | Custom scenario (params: oldGroup, newGroup, reason, riskLevel) |
| `/risk-assessment-info` | GET | Get risk assessment logic information |

## 📊 Response Format

All test endpoints return detailed JSON responses:

```json
{
  "success": true,
  "message": "All email types tested successfully",
  "adminCount": 3,
  "totalEmailsSent": 30,
  "testResults": {
    "anomalyEmails": {
      "success": true,
      "emailsSent": 9,
      "details": {
        "FREQUENT_REQUESTS": 3,
        "DORMANT_USER_ACTIVITY": 3,
        "OFF_HOURS_ACTIVITY": 3
      }
    },
    "groupChangeEmails": {
      "success": true,
      "emailsSent": 9,
      "details": {
        "HIGH_RISK_FINANCIAL": 3,
        "HIGH_RISK_MANAGEMENT": 3,
        "MEDIUM_RISK_HR": 3
      }
    },
    "ticketEmails": {
      "success": true,
      "emailsSent": 12,
      "details": {
        "TICKET_CREATION": 3,
        "TICKET_COMPLETION": 3,
        "TICKET_REVOCATION": 3,
        "TICKET_WARNING": 3
      }
    }
  }
}
```

## 🧪 Mock Data Used

### Mock Users
- **Username:** Various test usernames (anomaly.test, financial.test, etc.)
- **Email:** test.username@company.com
- **Department:** Varies by test (Security, Finance, HR, etc.)
- **Dolibarr ID:** Random 5-digit number
- **Role:** Employee (non-admin)

### Mock Tickets
- **Ticket ID:** Descriptive prefixes (ANOMALY-TEST, TICKET-FIN-TEST, etc.)
- **Emergency Type:** Fire Emergency
- **Description:** Realistic emergency scenarios
- **Status:** ACTIVE (or appropriate for test type)
- **Duration:** 60 minutes
- **Created:** Recent timestamp (5 minutes ago)

## 🔧 Prerequisites

1. **Admin Users Required:** The system needs at least one admin user in the database
2. **Email Configuration:** Gmail email service must be properly configured
3. **Database Access:** System needs access to UserRepository for admin lookup

## 📝 Usage Examples

### Test Everything (Recommended)
```bash
curl -X POST http://localhost:8080/api/test/master-email-test/run-comprehensive-test
```

### Test Only Anomaly Detection
```bash
curl -X POST http://localhost:8080/api/test/master-email-test/test-anomaly-emails
```

### Test Specific Anomaly Type
```bash
curl -X POST http://localhost:8080/api/test/anomaly-emails/send-specific-anomaly/DORMANT_USER_ACTIVITY
```

### Test Custom Group Change Scenario
```bash
curl -X POST "http://localhost:8080/api/test/group-change-emails/send-custom-scenario?oldGroup=None&newGroup=Financial Emergency Group&reason=Emergency access required&riskLevel=HIGH"
```

### Get Admin Information
```bash
curl -X GET http://localhost:8080/api/test/master-email-test/admin-info
```

## 🔍 Monitoring and Logging

The system provides comprehensive logging:

- **✅ Success Messages:** Show successful email sends to each admin
- **❌ Error Messages:** Show failed email sends with error details
- **📊 Summary Statistics:** Total emails sent, admin count, success rates
- **🔍 Test Progress:** Real-time logging of test execution

Example console output:
```
🚀 MASTER EMAIL TEST: Starting comprehensive email system test...
✅ TEST EMAIL: Sent FREQUENT_REQUESTS anomaly email to admin@company.com
✅ TEST EMAIL: Sent HIGH risk group change email to admin@company.com
✅ MASTER EMAIL TEST: All email types tested successfully!
```

## ⚠️ Important Notes

1. **Real Emails:** This system sends **actual emails** using your configured email service
2. **Admin Recipients:** All emails go to **real admin users** in your database
3. **Production Use:** Be cautious when running in production environments
4. **Rate Limits:** Consider email service rate limits when testing extensively
5. **Mock Data:** All user and ticket data is mock/test data - no real user data is used

## 🛠️ Troubleshooting

### No Emails Sent
- **Check:** Admin users exist in database (`isAdmin = true`)
- **Check:** Email service configuration is correct
- **Check:** Network connectivity for email sending

### Partial Email Failures
- **Check:** Individual admin email addresses are valid
- **Check:** Email service rate limits
- **Review:** Console logs for specific error messages

### Database Errors
- **Check:** Database connectivity
- **Check:** UserRepository is properly configured
- **Check:** User table has required fields

## 🔗 Related Components

This testing system integrates with:
- `GmailEmailService` - Email sending functionality
- `AnomalyDetectionService` - Anomaly detection logic
- `AnomalyNotificationService` - Anomaly notification handling
- `UserRepository` - Admin user lookup
- Email templates and styling from existing email services

## 📈 Expected Results

When running the comprehensive test with 3 admin users, you should expect:
- **30 total emails** sent (10 email types × 3 admins each)
- **Professional HTML emails** with FireFighter branding
- **Risk-level color coding** for group change and anomaly emails
- **Detailed information** about users, tickets, and detected issues
- **Security notices** with appropriate action guidance

The emails will demonstrate the full functionality of the FireFighter email notification system and allow you to verify that all templates, styling, and content are working correctly.

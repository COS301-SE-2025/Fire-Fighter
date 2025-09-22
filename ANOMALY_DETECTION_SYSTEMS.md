# FireFighter Platform - Anomaly Detection Systems

## Overview

The FireFighter Emergency Response Platform implements multiple sophisticated anomaly detection systems to identify potentially suspicious activities and security threats. These systems are designed to maintain the integrity and security of the emergency response infrastructure while minimizing false positives that could overwhelm administrators.

## 1. Suspicious Group Change Detection System

### Purpose
Monitors user group assignments in the Dolibarr ERP system and alerts administrators only when group changes are deemed suspicious based on security sensitivity and risk assessment.

### How It Works

#### Detection Logic
The system evaluates every group change against a risk-based classification system:

1. **Initial Assessment**: Checks if an actual change occurred (no alerts for no-op changes)
2. **Risk Classification**: Categorizes changes into HIGH, MEDIUM, and LOW risk levels
3. **Notification Decision**: Only sends alerts for changes classified as suspicious
4. **Enhanced Alerting**: Provides detailed risk information and action guidance

#### Risk Classification Framework

##### HIGH RISK (Always Suspicious)
- **Financial Emergency Group (ID: 2)**: Any addition to or removal from this group
- **Management Emergency Group (ID: 3)**: Any addition to or removal from this group

**Rationale**: These groups typically have access to:
- Financial systems and sensitive monetary data
- Executive-level emergency protocols
- Critical infrastructure controls
- High-privilege system access
- Strategic decision-making capabilities

##### MEDIUM RISK (Suspicious)
- **HR Emergency Group (ID: 1)**: Any changes involving this group
- **Cross-HR Changes**: Moving from HR to any other group or vice versa

**Rationale**: HR groups have access to:
- Personal employee information (PII)
- Emergency contact databases
- Personnel security clearances
- Organizational structure data
- Privacy-sensitive emergency protocols

##### LOW RISK (Not Suspicious)
- **Logistics Emergency Group (ID: 4)**: Changes involving only this group
- **Routine Operations**: Standard operational group assignments

**Rationale**: Logistics groups typically handle:
- Equipment and supply management
- Non-sensitive operational data
- Standard emergency response coordination
- Lower-privilege system access

### Thought Process and Risk Assessment

#### Why This Approach?

1. **Noise Reduction**: Traditional systems that alert on every change create notification fatigue, causing administrators to ignore important alerts. By filtering based on risk, we ensure critical alerts receive proper attention.

2. **Security Prioritization**: Different emergency groups have vastly different security implications:
   - **Financial groups** can authorize emergency expenditures and access monetary systems
   - **Management groups** can override safety protocols and make critical decisions
   - **HR groups** have access to personal data that could be exploited
   - **Logistics groups** primarily handle operational tasks with limited security impact

3. **Threat Modeling**: The system is designed around common attack vectors:
   - **Privilege Escalation**: Attackers often try to gain access to high-privilege groups
   - **Data Exfiltration**: HR and Financial groups are prime targets for data theft
   - **Insider Threats**: Unusual group changes may indicate compromised accounts or malicious insiders

4. **Operational Balance**: The system maintains security oversight while allowing normal operations to proceed without excessive administrative burden.

### Technical Implementation

#### Key Components
- `GroupChangeNotificationService.isSuspiciousGroupChange()`: Core detection logic
- `GroupChangeNotificationService.getSuspicionLevel()`: Risk classification
- `GmailEmailService.sendSuspiciousGroupChangeNotificationEmail()`: Enhanced alerting

#### Email Features
- **Color-coded risk levels**: Visual indicators (Red=HIGH, Orange=MEDIUM, Yellow=LOW)
- **Enhanced subject lines**: Clear indication of suspicious activity and risk level
- **Detailed security notices**: Specific action guidance for administrators
- **Professional formatting**: Ensures alerts are taken seriously

---

## 2. Dormant User Anomaly Detection System

### Purpose
Identifies potentially compromised accounts by detecting when users who have been inactive for extended periods suddenly log in and perform actions quickly, which may indicate unauthorized access or account takeover.

### How It Works

#### Detection Logic
The system monitors user activity patterns and flags anomalous behavior:

1. **Dormancy Assessment**: Identifies users inactive for 30+ days (configurable)
2. **Login Monitoring**: Tracks recent login events within a 15-minute window (configurable)
3. **Activity Analysis**: Counts actions performed after the recent login
4. **Anomaly Flagging**: Alerts when dormant users perform quick actions after login

#### Technical Process

1. **Recent Login Detection**: 
   - Finds the most recent login within the quick action threshold (15 minutes)
   - Uses `AccessLogRepository.findMostRecentLoginByUser()`

2. **Historical Activity Check**:
   - Searches for previous activity before the dormant threshold period (30 days)
   - Uses `AccessSessionRepository.findByUserIdBeforeDate()`

3. **Dormancy Verification**:
   - Confirms the user had no sessions or logins for 30+ days
   - Ensures the user was truly dormant, not just less active

4. **Action Counting**:
   - Counts actions performed since the recent login
   - Uses `AccessLogRepository.countActionsByUserSince()`

5. **Anomaly Decision**:
   - Flags as anomalous if dormant user made actions after recent login
   - Provides detailed reporting through `getDormantUserAnomalyDetails()`

### Thought Process and Risk Assessment

#### Why This Matters?

1. **Account Takeover Detection**: Legitimate users who haven't accessed the system for weeks/months are unlikely to suddenly become very active. This pattern often indicates:
   - Compromised credentials
   - Account sharing or unauthorized access
   - Automated attacks using stolen credentials

2. **Behavioral Analysis**: The system recognizes that normal user behavior includes:
   - Gradual re-engagement after periods of inactivity
   - Slower initial actions as users reacquaint themselves with the system
   - Typical usage patterns that don't involve rapid-fire actions immediately after login

3. **Emergency System Context**: In emergency response systems, dormant account activity is particularly concerning because:
   - Emergency systems often have elevated privileges
   - Unauthorized access during emergencies could have life-safety implications
   - Compromised accounts could disrupt critical emergency response operations

#### Configuration Parameters

- **DORMANT_THRESHOLD_DAYS = 30**: Balances detection sensitivity with normal usage patterns
- **QUICK_ACTION_THRESHOLD_MINUTES = 15**: Captures rapid actions that are suspicious for dormant users

### Technical Implementation

#### Key Components
- `AnomalyDetectionService.isDormantUserLoginAnomaly()`: Core detection logic
- `AnomalyDetectionService.getDormantUserAnomalyDetails()`: Detailed reporting
- Enhanced repository methods for historical data analysis

#### Integration
- Integrated into `checkForAnomalousTicketCreation()` for comprehensive anomaly detection
- Preserves all original functionality while adding new detection capabilities

---

## 3. Anomaly Email Notification System

### Purpose
Provides comprehensive email notifications to administrators when any type of anomalous behavior is detected, ensuring rapid response to potential security threats.

### How It Works

#### Notification Architecture
The system uses a centralized notification service that:

1. **Detects Multiple Anomaly Types**: Monitors for frequent requests, dormant user activity, and off-hours access
2. **Risk Assessment**: Classifies each anomaly by risk level (HIGH, MEDIUM, LOW)
3. **Targeted Notifications**: Sends detailed email alerts only to admin users
4. **Comprehensive Reporting**: Provides specific details and recommended actions for each anomaly type

#### Email Notification Features

##### Professional Email Templates
- **Color-coded risk levels**: Visual indicators for quick assessment
- **Detailed anomaly information**: User details, ticket information, and specific anomaly data
- **Contextual security notices**: Tailored guidance based on anomaly type and risk level
- **Professional formatting**: Ensures alerts are taken seriously and contain all necessary information

##### Risk-Based Content
- **HIGH Risk (Dormant User Activity)**: Immediate identity verification and security breach investigation
- **MEDIUM Risk (Frequent Requests)**: Activity review and potential access restrictions
- **LOW Risk (Off-Hours Activity)**: Verification of authorized access

#### Technical Implementation

##### Key Components
- `AnomalyNotificationService`: Central orchestration of anomaly detection and notification
- `GmailEmailService.sendAnomalyDetectionNotificationEmail()`: Email delivery with rich formatting
- `TicketService` integration: Automatic anomaly checking during ticket creation

##### Notification Process
1. **Ticket Creation Trigger**: Anomaly detection runs automatically when tickets are created
2. **Multi-Type Detection**: Checks for all anomaly types simultaneously
3. **Risk Assessment**: Determines appropriate risk level for each detected anomaly
4. **Admin Notification**: Sends targeted emails to all admin users
5. **Comprehensive Logging**: Records all detection and notification activities

### Integration with Ticket Creation

The anomaly notification system is seamlessly integrated into the ticket creation process:

```java
// Automatic anomaly detection and notification
anomalyNotificationService.checkAndNotifyAnomalies(user, savedTicket);
```

This ensures that any suspicious behavior is immediately flagged and reported to administrators without disrupting normal emergency response operations.

---

## 4. System Integration and Benefits

### Comprehensive Security Approach

The combination of these anomaly detection systems provides:

1. **Multi-layered Protection**: Different types of threats are detected through different mechanisms
2. **Behavioral Analysis**: Systems learn normal patterns and flag deviations
3. **Risk-based Alerting**: Reduces noise while maintaining security oversight
4. **Actionable Intelligence**: Provides administrators with specific, actionable information

### Operational Benefits

1. **Reduced Alert Fatigue**: By filtering low-risk activities, administrators can focus on genuine threats
2. **Faster Response Times**: Clear risk classification enables rapid decision-making
3. **Audit Trail**: Comprehensive logging of all decisions and risk assessments
4. **Scalable Security**: Systems adapt to organizational growth and changing threat landscapes

### Future Enhancements

Potential areas for expansion:
- **Machine Learning Integration**: Adaptive thresholds based on historical patterns
- **Cross-system Correlation**: Combining multiple anomaly types for enhanced detection
- **Automated Response**: Configurable automatic actions for high-risk scenarios
- **Advanced Reporting**: Trend analysis and security metrics dashboards

---

## Configuration and Maintenance

### Tuning Parameters

Both systems include configurable parameters that can be adjusted based on organizational needs:

- **Group Change Detection**: Risk classifications can be modified based on organizational structure
- **Dormant User Detection**: Thresholds can be adjusted based on typical user behavior patterns

### Monitoring and Metrics

Regular review of system effectiveness should include:
- False positive rates
- Detection accuracy
- Response times to alerts
- Overall security posture improvements

### Best Practices

1. **Regular Review**: Periodically assess risk classifications and thresholds
2. **Staff Training**: Ensure administrators understand the systems and response procedures
3. **Documentation**: Maintain current documentation of all configurations and procedures
4. **Testing**: Regular testing to ensure systems function correctly under various scenarios

---

## Conclusion

The FireFighter platform's anomaly detection systems represent a sophisticated, multi-layered approach to security monitoring. By combining risk-based group change detection with behavioral analysis of user activity patterns, the system provides comprehensive protection against various threat vectors while maintaining operational efficiency.

The key to these systems' effectiveness lies in their ability to distinguish between normal operational activities and genuinely suspicious behavior, ensuring that security teams can focus their attention where it's most needed while maintaining the rapid response capabilities essential to emergency management systems.

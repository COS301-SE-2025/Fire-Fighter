# 🚀 Postman Setup Guide - AI Chatbot Testing

## 📥 Import Collection

### Step 1: Import the Collection
1. Open Postman
2. Click **"Import"** button
3. Select **"Upload Files"**
4. Choose `FireFighter_Chatbot_Collection.json`
5. Click **"Import"**

### Step 2: Create Environment
1. Click **"Environments"** in the sidebar
2. Click **"Create Environment"**
3. Name it: `FireFighter Local`
4. Add these variables:

| Variable | Initial Value | Current Value |
|----------|---------------|---------------|
| `base_url` | `http://localhost:8080` | `http://localhost:8080` |
| `test_user_id` | `your-firebase-user-id` | `your-firebase-user-id` |
| `admin_user_id` | `admin-firebase-user-id` | `admin-firebase-user-id` |

5. Click **"Save"**

### Step 3: Select Environment
1. In the top-right corner, select **"FireFighter Local"** environment
2. Ensure all variables show green checkmarks

## 🔧 Configuration

### Update User IDs
Before testing, you need real Firebase user IDs from your database:

1. **Get Test User ID**:
   - Check your database for a regular user
   - Copy their `firebaseUid` field
   - Update `test_user_id` variable in Postman

2. **Get Admin User ID**:
   - Check your database for an admin user (`isAdmin = true`)
   - Copy their `firebaseUid` field  
   - Update `admin_user_id` variable in Postman

### SSL Certificate (HTTPS)
If using HTTPS (`https://localhost:8443`):
1. In Postman, go to **Settings** → **General**
2. Turn **OFF** "SSL certificate verification"
3. Or use HTTP: change `base_url` to `http://localhost:8080`

## 🧪 Testing Workflow

### 1. Start Application
```bash
cd firefighter-platform
mvn spring-boot:run
```

### 2. Verify Health
Run: **"Health Check"** request
- Should return `200 OK` with `"status": "healthy"`

### 3. Test Basic Functionality
Run these requests in order:
1. **"Basic User Query - Show My Tickets"**
2. **"Active Tickets Query"**
3. **"Get User Capabilities"**

### 4. Test Security
Run error test cases:
1. **"Error Test - Empty Query"** (should return 400)
2. **"Error Test - Missing User ID"** (should return 400)
3. **"Error Test - Invalid User ID"** (should handle gracefully)

## 📊 Expected Results

### ✅ Success Cases
- **Status Code**: `200 OK`
- **Response Structure**:
```json
{
  "message": "AI response here...",
  "success": true,
  "userRole": "User" or "Administrator",
  "timestamp": "2025-01-18T18:30:00",
  "formattedTimestamp": "2025-01-18 18:30:00"
}
```

### ❌ Error Cases
- **Empty Query**: `400 Bad Request`
- **Missing User ID**: `400 Bad Request`
- **Invalid User**: `200 OK` with `"success": false`
- **Non-Admin Access**: `403 Forbidden`

## 🔍 Troubleshooting

### Common Issues

#### 1. Connection Refused
**Problem**: `Error: connect ECONNREFUSED`
**Solution**: 
- Ensure application is running (`mvn spring-boot:run`)
- Check if port 8443/8080 is correct
- Try HTTP instead of HTTPS

#### 2. SSL Certificate Error
**Problem**: SSL handshake failed
**Solution**:
- Disable SSL verification in Postman settings
- Or use HTTP endpoint (`http://localhost:8080`)

#### 3. User Not Found
**Problem**: `"User not found"` response
**Solution**:
- Verify user exists in database
- Check Firebase UID is correct
- Ensure user is properly authenticated

#### 4. AI Service Not Configured
**Problem**: `"AI service is not properly configured"`
**Solution**:
- Check `.env` file has `GOOGLE_GEMINI_API_KEY`
- Restart application after adding API key
- Verify API key is valid

#### 5. Empty Responses
**Problem**: AI returns generic error messages
**Solution**:
- Check console logs for detailed errors
- Verify Gemini API key is working
- Test with simpler queries first

### Debug Steps

1. **Check Application Logs**:
   - Look for startup errors
   - Check for API key validation messages
   - Monitor request/response logs

2. **Test Health Endpoint**:
   ```http
   GET {{base_url}}/api/chatbot/health
   ```

3. **Verify User Exists**:
   ```http
   GET {{base_url}}/api/users
   ```

4. **Check Capabilities**:
   ```http
   GET {{base_url}}/api/chatbot/capabilities/{{test_user_id}}
   ```

## 📝 Test Scenarios

### Basic User Tests
- [ ] Health check passes
- [ ] Show my tickets works
- [ ] Active tickets filtering works
- [ ] Emergency type filtering works
- [ ] Recent tickets works
- [ ] Closed tickets works
- [ ] User capabilities returned correctly
- [ ] User suggestions returned correctly

### Security Tests
- [ ] Empty queries rejected
- [ ] Missing user ID rejected
- [ ] Invalid user ID handled gracefully
- [ ] Non-admin cannot access admin endpoints
- [ ] Users only see their own tickets

### Admin Tests (Future)
- [ ] Admin capabilities show full access
- [ ] Admin queries work (when implemented)
- [ ] Admin suggestions include system-wide options

## 🎯 Success Criteria

Your chatbot is working correctly if:
1. ✅ Health check returns healthy status
2. ✅ Basic queries return AI-generated responses
3. ✅ User-specific ticket data is returned
4. ✅ Security validations work properly
5. ✅ Error cases are handled gracefully

---

**🤖 Happy Testing!**  
*FireFighter Platform - AI Chatbot Integration*

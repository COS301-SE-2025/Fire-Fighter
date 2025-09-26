# FireFighter API Load Testing with Apache JMeter

This directory contains a complete JMeter setup for load testing your FireFighter Emergency Management API.

## 📁 Directory Structure

```
jmeter-testing/
├── apache-jmeter-5.6.3/          # JMeter installation
├── ff-api-tests/
│   ├── test-plans/               # JMeter test plan files (.jmx)
│   ├── test-data/                # CSV files with test data
│   ├── scripts/                  # Shell scripts to run tests
│   └── reports/                  # Test results and dashboards
└── README.md                     # This file
```

## 🚀 Quick Start

### 1. Prerequisites

- Your FF-API server must be running (default: `http://localhost:8080`)
- Java 8+ installed (required for JMeter)
- `curl` and `jq` for health checks (optional)

### 2. Test Authentication Flow

```bash
cd ff-api-tests/scripts
./test-auth-flow.sh
```

This verifies that JWT authentication works properly before running load tests.

### 3. Basic Health Check

```bash
./quick-health-check.sh
```

### 4. Run Load Test (Light Load)

```bash
./run-load-test.sh
```

Default configuration:
- **10 concurrent users**
- **30 seconds ramp-up**
- **5 minutes duration**

### 5. Run Load Test (Custom Configuration)

```bash
./run-load-test.sh [threads] [ramp-up] [duration] [server] [port]

# Examples:
./run-load-test.sh 25 60 600                    # 25 users, 1min ramp-up, 10min test
./run-load-test.sh 50 120 900 localhost 8082    # 50 users, test server on port 8082
```

### 6. Run Stress Test (High Load)

⚠️ **Warning**: This generates high load on your system!

```bash
./run-stress-test.sh
```

Default stress configuration:
- **100 concurrent users**
- **2 minutes ramp-up**
- **10 minutes duration**

## 📊 Test Plans

### FF-API-Load-Test.jmx
**Purpose**: General load testing for normal usage patterns

**What it tests**:
- ✅ Health check endpoints (`/api/health`)
- ✅ Authentication endpoints (`/api/auth/simple-test`)
- ✅ Ticket creation (`POST /api/tickets`)
- ✅ Ticket retrieval (`GET /api/tickets`)
- ✅ NLP service health (`/api/nlp/health`)
- ✅ Chatbot service health (`/api/chatbot/health`)

**Authentication Flow**:
1. **Development Login** - Gets JWT token via `/api/auth/dev-login`
2. **Token Extraction** - Extracts JWT from login response
3. **Authenticated Requests** - Uses `Authorization: Bearer {token}` header

**Thread Groups**:
1. **Health Check Load** (5 users, continuous)
2. **API Load Test** (configurable users, realistic scenarios with auth)

### FF-API-Stress-Test.jmx
**Purpose**: High-load stress testing to find breaking points

**What it tests**:
- 🔥 Health endpoints under extreme load
- 🔥 System stability with many concurrent users
- 🔥 Response times under stress

## 📈 Test Data

### users.csv
Contains test user credentials and emergency types:
- 10 regular test users
- 2 admin users
- Various emergency types (fire, medical, rescue, hazmat)

### tickets.csv
Contains realistic emergency ticket scenarios:
- 20 different emergency descriptions
- Various emergency types and durations
- Realistic contact information

### nlp-queries.csv
Contains natural language queries for testing:
- Regular user queries
- Admin-level queries
- Various question types

## 📊 Understanding Results

### Key Metrics to Monitor

1. **Response Time**
   - ✅ Good: < 500ms for health checks
   - ✅ Good: < 2s for business operations
   - ⚠️ Warning: > 5s response times

2. **Throughput**
   - Requests per second your API can handle
   - Should remain stable under load

3. **Error Rate**
   - ✅ Good: < 1% error rate
   - ⚠️ Warning: > 5% error rate
   - ❌ Critical: > 10% error rate

4. **Resource Usage**
   - Monitor CPU, memory, database connections
   - Watch for connection pool exhaustion

### Report Files

After running tests, you'll find:

```
reports/
├── load-test-results-TIMESTAMP.jtl      # Raw test results
├── load-test-dashboard-TIMESTAMP/       # HTML dashboard
│   └── index.html                       # Open this in browser
├── stress-test-results-TIMESTAMP.jtl    # Stress test results
└── stress-test-dashboard-TIMESTAMP/     # Stress test dashboard
```

## 🔧 Customization

### Modify Test Parameters

Edit the test plan variables in JMeter GUI:
```bash
cd apache-jmeter-5.6.3/bin
./jmeter.sh
```

Open test plans from `ff-api-tests/test-plans/`

### Add New Endpoints

1. Open test plan in JMeter GUI
2. Add new HTTP Request samplers
3. Configure assertions and extractors
4. Save the test plan

### Modify Test Data

Edit CSV files in `ff-api-tests/test-data/`:
- Add more users, tickets, or queries
- Ensure CSV format is maintained
- Update variable names if needed

## 🚨 Troubleshooting

### Common Issues

1. **Connection Refused**
   ```
   Solution: Ensure FF-API is running on the specified port
   Check: ./quick-health-check.sh
   ```

2. **High Error Rates**
   ```
   Possible causes:
   - Database connection pool exhaustion
   - Memory issues
   - Network timeouts
   
   Solutions:
   - Reduce concurrent users
   - Increase timeouts in test plan
   - Monitor server resources
   ```

3. **JMeter Not Found**
   ```
   Solution: Verify JMeter path in scripts
   Check: ls apache-jmeter-5.6.3/bin/jmeter
   ```

### Performance Tuning

For your FF-API application:

1. **Database Optimization**
   - Monitor HikariCP connection pool
   - Check for slow queries
   - Ensure proper indexing

2. **JVM Tuning**
   - Increase heap size if needed
   - Monitor garbage collection

3. **Application Tuning**
   - Review Spring Boot actuator metrics
   - Check for memory leaks
   - Optimize heavy operations

## 📞 Emergency Response Testing

This setup is specifically designed for your FireFighter Emergency Management system:

- **Realistic Scenarios**: Tests emergency ticket creation and management
- **Authentication Flow**: Validates JWT and Firebase integration
- **NLP Service**: Tests natural language processing endpoints
- **Notification System**: Validates notification delivery
- **Admin Operations**: Tests administrative functions

## 🎯 Recommended Testing Strategy

1. **Start Small**: Begin with 5-10 users
2. **Baseline**: Establish performance baseline
3. **Gradual Increase**: Slowly increase load
4. **Monitor**: Watch for degradation points
5. **Stress Test**: Find breaking point
6. **Optimize**: Address bottlenecks
7. **Repeat**: Re-test after optimizations

---

**Happy Load Testing! 🚒🔥**

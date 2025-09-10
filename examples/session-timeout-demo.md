# Session Timeout Configuration Demo

This document demonstrates how the new session timeout configuration works in Operaton.

## How It Works

### 1. Configuration Loading
When the application starts, the `SessionCookieProperties` class loads configuration from:
- `application.yml` or `application.properties`
- Environment variables
- System properties

```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 1800      # 30 minutes
      warning-time-in-seconds: 300  # 5 minutes warning
```

### 2. Backend Session Configuration
The `OperatonBpmWebappInitializer` applies the configuration to the servlet context:

```java
// Configures session timeout at the container level
servletContext.setSessionTimeout(timeoutInMinutes);
servletContext.getSessionCookieConfig().setMaxAge(timeoutInSeconds);
```

The `SessionCookieFilter` applies the timeout to individual HTTP sessions:

```java
// Apply timeout to each session
session.setMaxInactiveInterval(sessionTimeoutInSeconds);
```

### 3. Frontend Session Monitoring
The `SessionTimeoutService` JavaScript service:

1. **Gets configuration** from HTTP headers or API calls
2. **Monitors user activity** (mouse, keyboard, scroll, clicks)
3. **Shows warnings** before session expires
4. **Automatically extends** sessions on activity
5. **Handles logout** when session expires

### 4. User Experience Flow

1. **User logs in** → Session timers start
2. **User is active** → Timers reset automatically
3. **User is idle** → Warning appears before expiry
4. **User clicks "Extend"** → Session is extended
5. **User ignores warning** → Automatic logout

## Configuration Examples

### Development (Long Sessions)
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 3600    # 1 hour
      warning-time-in-seconds: 600 # 10 minutes
```

### Production (Balanced)
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 1800    # 30 minutes
      warning-time-in-seconds: 300 # 5 minutes
```

### High Security (Short Sessions)
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 900     # 15 minutes
      warning-time-in-seconds: 120 # 2 minutes
```

### Administrative Tasks (Extended)
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 7200    # 2 hours
      warning-time-in-seconds: 900 # 15 minutes
```

### Disabled (For Debugging)
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 0       # Never expire
      warning-time-in-seconds: 0  # No warnings
```

## Environment Variable Examples

```bash
# Docker/Kubernetes deployment
export OPERATON_WEBAPP_SESSION_COOKIE_TIMEOUT_IN_SECONDS=1800
export OPERATON_WEBAPP_SESSION_COOKIE_WARNING_TIME_IN_SECONDS=300

# Alternative shorter names (if implemented)
export OPERATON_SESSION_TIMEOUT=1800
export OPERATON_SESSION_WARNING_TIME=300
```

## Testing the Configuration

### 1. Check Configuration Loading
Look for log messages during startup:
```
INFO: Session timeout set to 30 minutes (1800 seconds)
```

### 2. Verify Session Timeout in Browser
1. Log into Operaton
2. Check browser developer tools → Application → Cookies
3. Look for session cookie with appropriate MaxAge

### 3. Test Activity Detection
1. Stay active in the application
2. Verify that no warning appears
3. Stop all activity and wait
4. Confirm warning appears at the configured time

### 4. Test Session Extension
1. Wait for session warning to appear
2. Click "Extend Session" button
3. Verify warning disappears and session continues

### 5. Test Automatic Logout
1. Wait for session warning to appear
2. Do not click "Extend Session"
3. Wait for timeout period to complete
4. Verify automatic redirect to login page

## Configuration Validation

The configuration includes validation to ensure sensible values:

- `timeout-in-seconds` must be ≥ 0 (0 = disabled, -1 = container default)
- `warning-time-in-seconds` must be ≥ 0 (0 = no warnings)
- Warning time should be less than total timeout time
- Values are logged during startup for verification

## Integration Points

### Spring Boot Applications
The configuration automatically integrates with Spring Boot's configuration system:

```java
@Autowired
private OperatonBpmProperties properties;

// Access session timeout settings
int timeout = properties.getWebapp().getSessionCookie().getTimeoutInSeconds();
```

### Traditional WAR Deployments
Configuration can be applied via system properties or environment variables:

```xml
<!-- web.xml context parameters -->
<context-param>
  <param-name>sessionTimeoutInSeconds</param-name>
  <param-value>1800</param-value>
</context-param>
```

### Container Deployments
Works with all major servlet containers:
- Tomcat
- Jetty
- Undertow
- WildFly

## Backward Compatibility

The implementation is fully backward compatible:
- Existing deployments continue to work without changes
- Container-level session timeout settings still work
- New configuration takes precedence when specified
- Default values match typical production requirements (30 min / 5 min warning)
# Session Timeout Configuration - Feature Implementation

## Overview
This implementation adds configurable session timeout functionality to Operaton Cockpit, Admin, and Tasklist web applications. Users can now configure session timeouts through Spring Boot properties, environment variables, or system properties.

## New Features

### 🔧 Backend Configuration
- **SessionCookieProperties**: New configuration properties for timeout and warning settings
- **Servlet Integration**: Automatic application of timeout settings to HTTP sessions
- **Spring Boot Integration**: Full integration with Spring Boot configuration system
- **Environment Variable Support**: Support for containerized deployments

### 🎯 Frontend Session Management
- **SessionTimeoutService**: JavaScript service for session monitoring and management
- **Activity Detection**: Automatic session extension on user activity
- **Warning System**: Configurable warnings before session expiry
- **Manual Extension**: User option to manually extend sessions

### 📝 Configuration Options

#### Spring Boot Properties (application.yml)
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 1800      # 30 minutes (default)
      warning-time-in-seconds: 300  # 5 minutes warning (default)
```

#### Environment Variables
```bash
OPERATON_WEBAPP_SESSION_COOKIE_TIMEOUT_IN_SECONDS=1800
OPERATON_WEBAPP_SESSION_COOKIE_WARNING_TIME_IN_SECONDS=300
```

## Implementation Details

### Backend Changes

#### 1. SessionCookieProperties.java
- Added `timeoutInSeconds` property (default: 1800 = 30 minutes)
- Added `warningTimeInSeconds` property (default: 300 = 5 minutes)
- Enhanced `getInitParams()` to include timeout parameters
- Updated `toString()` for better logging

#### 2. OperatonBpmWebappInitializer.java
- Added `configureSessionTimeout()` method
- Integrated session timeout configuration with servlet context
- Added logging for configuration verification

#### 3. SessionCookieFilter.java
- Enhanced to parse session timeout parameters from filter config
- Applied timeout settings to individual HTTP sessions
- Added support for disabled timeouts (0 = never expire, -1 = container default)

### Frontend Changes

#### 1. SessionTimeoutService.js
- Complete session timeout monitoring service
- Activity detection (mouse, keyboard, scroll, touch, click events)
- Configurable warning system with user notifications
- Automatic and manual session extension capabilities
- Integration with existing authentication system

#### 2. Authentication Integration
- Enhanced auth/index.js to bootstrap SessionTimeoutService
- Integration with existing authentication events
- Proper cleanup on logout

## Configuration Examples

### Development Environment
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 3600    # 1 hour for long development sessions
      warning-time-in-seconds: 600 # 10 minutes warning
```

### Production Environment
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 1800    # 30 minutes for security
      warning-time-in-seconds: 300 # 5 minutes warning
```

### High-Security Environment
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 900     # 15 minutes for high security
      warning-time-in-seconds: 120 # 2 minutes warning
```

### Administrative Tasks
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 7200    # 2 hours for complex tasks
      warning-time-in-seconds: 900 # 15 minutes warning
```

### Disabled Timeout (Debugging)
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 0       # Never expire
      warning-time-in-seconds: 0  # No warnings
```

## Docker/Kubernetes Support

### Docker Compose
```yaml
services:
  operaton:
    image: operaton/operaton-bpm-platform:latest
    environment:
      - OPERATON_WEBAPP_SESSION_COOKIE_TIMEOUT_IN_SECONDS=1800
      - OPERATON_WEBAPP_SESSION_COOKIE_WARNING_TIME_IN_SECONDS=300
```

### Kubernetes ConfigMap
```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: operaton-session-config
data:
  OPERATON_WEBAPP_SESSION_COOKIE_TIMEOUT_IN_SECONDS: "1800"
  OPERATON_WEBAPP_SESSION_COOKIE_WARNING_TIME_IN_SECONDS: "300"
```

## User Experience

### Session Warning Flow
1. User works normally with automatic session extension on activity
2. When idle, warning appears 5 minutes (configurable) before expiry
3. User can click "Extend Session" to continue working
4. If ignored, automatic logout occurs when session expires
5. User is redirected to login page with appropriate messaging

### Activity Detection
Sessions are automatically extended when users:
- Move the mouse within the application
- Click any UI element
- Use keyboard shortcuts or input
- Scroll pages
- Touch the screen (mobile/tablet)

## Testing

### Unit Tests
- `SessionCookiePropertiesTest.java`: Tests configuration property handling
- `SessionTimeoutConfigurationTest.java`: Tests Spring Boot configuration loading

### Manual Testing
1. Configure different timeout values
2. Verify session warnings appear at correct times
3. Test activity detection keeps sessions alive
4. Confirm automatic logout on timeout
5. Validate configuration loading from different sources

## Migration Guide

### From Container-Level Configuration
```xml
<!-- Old: web.xml -->
<session-config>
  <session-timeout>30</session-timeout>
</session-config>
```

```yaml
# New: application.yml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 1800  # 30 minutes * 60 seconds
```

### From Environment-Specific Configurations
Replace container-specific session timeout configurations with unified Operaton configuration.

## Backward Compatibility

- ✅ Existing deployments continue to work without changes
- ✅ Container-level session configurations still respected
- ✅ New configuration takes precedence when specified
- ✅ Sensible defaults (30 min timeout, 5 min warning)
- ✅ No breaking changes to existing APIs

## Security Considerations

- **Shorter timeouts** improve security but may impact productivity
- **Activity detection** prevents premature logouts during active use
- **Warning system** gives users control over session extension
- **Configurable by environment** allows different policies per deployment
- **Audit trail** preserved through existing authentication logging

## Documentation

- `docs/session-timeout-configuration.md`: Complete configuration guide
- `examples/session-timeout-example.yml`: Configuration examples
- `examples/session-timeout-env-example.sh`: Environment variable examples
- `examples/session-timeout-demo.md`: Implementation demonstration

## Files Changed

### Backend
- `spring-boot-starter/starter/src/main/java/org/operaton/bpm/spring/boot/starter/property/SessionCookieProperties.java`
- `spring-boot-starter/starter-webapp-core/src/main/java/org/operaton/bpm/spring/boot/starter/webapp/OperatonBpmWebappInitializer.java`
- `webapps/assembly/src/main/java/org/operaton/bpm/webapp/impl/security/filter/SessionCookieFilter.java`

### Frontend
- `webapps/frontend/operaton-commons-ui/lib/services/SessionTimeoutService.js` (new)
- `webapps/frontend/operaton-commons-ui/lib/services/index.js`
- `webapps/frontend/operaton-commons-ui/lib/auth/index.js`

### Testing
- `spring-boot-starter/starter/src/test/java/org/operaton/bpm/spring/boot/starter/property/SessionCookiePropertiesTest.java` (new)
- `spring-boot-starter/starter/src/test/java/org/operaton/bpm/spring/boot/starter/property/SessionTimeoutConfigurationTest.java` (new)

### Documentation & Examples
- `docs/session-timeout-configuration.md` (new)
- `examples/session-timeout-example.yml` (new)
- `examples/session-timeout-env-example.sh` (new)
- `examples/session-timeout-demo.md` (new)
- `examples/test-session-timeout.properties` (new)

This implementation provides a comprehensive solution for configurable session timeouts that addresses all requirements from the original issue while maintaining backward compatibility and following Operaton's architectural patterns.
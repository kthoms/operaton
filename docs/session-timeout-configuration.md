# Session Timeout Configuration

This document describes how to configure session timeouts for Operaton web applications (Cockpit, Admin, Tasklist).

## Overview

The Operaton web applications now support configurable session timeouts with the following features:

- **Configurable timeout duration** via Spring Boot properties or environment variables
- **Session expiry warnings** shown to users before their session expires
- **Automatic session extension** on user activity
- **Manual session extension** through warning dialogs
- **Different configurations** for different environments

## Configuration Methods

### 1. Spring Boot Configuration (application.yml)

```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 1800      # 30 minutes (default)
      warning-time-in-seconds: 300  # 5 minutes warning (default)
```

### 2. Spring Boot Configuration (application.properties)

```properties
operaton.webapp.session-cookie.timeout-in-seconds=1800
operaton.webapp.session-cookie.warning-time-in-seconds=300
```

### 3. Environment Variables

```bash
export OPERATON_WEBAPP_SESSION_COOKIE_TIMEOUT_IN_SECONDS=1800
export OPERATON_WEBAPP_SESSION_COOKIE_WARNING_TIME_IN_SECONDS=300
```

### 4. Java System Properties

```bash
-Doperaton.webapp.session-cookie.timeout-in-seconds=1800
-Doperaton.webapp.session-cookie.warning-time-in-seconds=300
```

## Configuration Options

| Property | Description | Default | Special Values |
|----------|-------------|---------|----------------|
| `timeout-in-seconds` | Session timeout in seconds | 1800 (30 min) | `0` = Never expire, `-1` = Use container default |
| `warning-time-in-seconds` | Warning time before expiry | 300 (5 min) | `0` = No warnings |

## Environment-Specific Examples

### Development Environment
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 3600    # 1 hour
      warning-time-in-seconds: 600 # 10 minutes
```

### Production Environment
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 1800    # 30 minutes
      warning-time-in-seconds: 300 # 5 minutes
```

### High-Security Environment
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 900     # 15 minutes
      warning-time-in-seconds: 120 # 2 minutes
```

### Administrative Tasks
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 7200    # 2 hours
      warning-time-in-seconds: 900 # 15 minutes
```

### Disable Session Timeout (for debugging)
```yaml
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 0       # Never expire
      warning-time-in-seconds: 0  # No warnings
```

## Docker Configuration

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
  name: operaton-config
data:
  OPERATON_WEBAPP_SESSION_COOKIE_TIMEOUT_IN_SECONDS: "1800"
  OPERATON_WEBAPP_SESSION_COOKIE_WARNING_TIME_IN_SECONDS: "300"
```

## User Experience

### Session Warning Dialog
When a user's session is about to expire, they will see a warning dialog with:
- Time remaining until session expires
- Option to extend the session
- Automatic dismissal when session is extended

### Automatic Session Extension
Sessions are automatically extended when users:
- Move the mouse
- Click anywhere in the application
- Use the keyboard
- Scroll the page
- Interact with any UI element

### Manual Session Extension
Users can manually extend their session by:
- Clicking "Extend Session" in the warning dialog
- Making any API call or navigation within the application

## Security Considerations

- **Shorter sessions** improve security but may impact user productivity
- **Longer sessions** improve usability but increase security risk
- Consider your organization's security policies when setting timeout values
- Use shorter timeouts for high-privilege operations
- Consider different timeouts for different user roles (if implemented)

## Migration from Previous Versions

If you were previously using container-specific session timeout configurations:

### From Tomcat web.xml
```xml
<!-- Old method -->
<session-config>
  <session-timeout>30</session-timeout>
</session-config>
```

### To Operaton Configuration
```yaml
# New method
operaton:
  webapp:
    session-cookie:
      timeout-in-seconds: 1800  # 30 minutes * 60 seconds
```

## Troubleshooting

### Session Timeout Not Working
1. Check that the configuration is correctly set
2. Verify that the Spring Boot application context includes the webapp configuration
3. Check application logs for session timeout configuration messages

### Warning Not Appearing
1. Ensure `warning-time-in-seconds` is greater than 0
2. Check that the frontend JavaScript is loaded correctly
3. Verify that notifications are enabled in the UI

### Sessions Expiring Too Quickly
1. Check for conflicting container-level session timeout settings
2. Verify that activity detection is working (check browser console for errors)
3. Ensure system time is synchronized across all components

## Examples

See the `examples/` directory for complete configuration examples:
- `session-timeout-example.yml` - Complete YAML configuration examples
- `session-timeout-env-example.sh` - Environment variable examples
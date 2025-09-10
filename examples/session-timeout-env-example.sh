#!/bin/bash
# Example environment variables for configuring Operaton session timeout

# Session timeout configuration via environment variables
# These can be set in Docker environments, Kubernetes, or CI/CD pipelines

# Basic session timeout configuration
export OPERATON_WEBAPP_SESSION_COOKIE_TIMEOUT_IN_SECONDS=1800  # 30 minutes
export OPERATON_WEBAPP_SESSION_COOKIE_WARNING_TIME_IN_SECONDS=300  # 5 minutes

# Alternative short names (if supported)
export OPERATON_SESSION_TIMEOUT=1800
export OPERATON_SESSION_WARNING_TIME=300

# Examples for different environments:

# Development environment
# export OPERATON_WEBAPP_SESSION_COOKIE_TIMEOUT_IN_SECONDS=3600  # 1 hour
# export OPERATON_WEBAPP_SESSION_COOKIE_WARNING_TIME_IN_SECONDS=600  # 10 minutes

# Production environment  
# export OPERATON_WEBAPP_SESSION_COOKIE_TIMEOUT_IN_SECONDS=1800  # 30 minutes
# export OPERATON_WEBAPP_SESSION_COOKIE_WARNING_TIME_IN_SECONDS=300  # 5 minutes

# High-security environment
# export OPERATON_WEBAPP_SESSION_COOKIE_TIMEOUT_IN_SECONDS=900   # 15 minutes
# export OPERATON_WEBAPP_SESSION_COOKIE_WARNING_TIME_IN_SECONDS=120  # 2 minutes

# Administrative tasks environment
# export OPERATON_WEBAPP_SESSION_COOKIE_TIMEOUT_IN_SECONDS=7200  # 2 hours
# export OPERATON_WEBAPP_SESSION_COOKIE_WARNING_TIME_IN_SECONDS=900  # 15 minutes

# Disable session timeout (for debugging)
# export OPERATON_WEBAPP_SESSION_COOKIE_TIMEOUT_IN_SECONDS=0     # Never expire
# export OPERATON_WEBAPP_SESSION_COOKIE_WARNING_TIME_IN_SECONDS=0  # No warnings

echo "Session timeout configured:"
echo "  Timeout: ${OPERATON_WEBAPP_SESSION_COOKIE_TIMEOUT_IN_SECONDS} seconds"
echo "  Warning: ${OPERATON_WEBAPP_SESSION_COOKIE_WARNING_TIME_IN_SECONDS} seconds"
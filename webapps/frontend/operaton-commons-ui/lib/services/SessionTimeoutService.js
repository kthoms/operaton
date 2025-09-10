/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

/**
 * Session Timeout Service
 * @name SessionTimeoutService
 * @memberof cam.common.services
 * @type angular.service
 */
module.exports = [
  '$rootScope',
  '$timeout',
  '$interval',
  '$http',
  'Uri',
  '$translate',
  'Notifications',
  function(
    $rootScope,
    $timeout,
    $interval,
    $http,
    Uri,
    $translate,
    Notifications
  ) {
    var sessionTimeoutInSeconds = 1800; // 30 minutes default
    var warningTimeInSeconds = 300; // 5 minutes default
    var warningTimer;
    var logoutTimer;
    var activityTimer;
    var warningDisplayed = false;
    var intervalCheck;

    // Configuration endpoint to get session timeout settings
    var configPromise = $http.get(Uri.appUri('engine://engine/'))
      .then(function(response) {
        var headers = response.headers();
        if (headers['x-session-timeout']) {
          sessionTimeoutInSeconds = parseInt(headers['x-session-timeout'], 10);
        }
        if (headers['x-session-warning-time']) {
          warningTimeInSeconds = parseInt(headers['x-session-warning-time'], 10);
        }
      })
      .catch(function() {
        // Use defaults if configuration fails
      });

    function resetTimers() {
      if (warningTimer) {
        $timeout.cancel(warningTimer);
        warningTimer = null;
      }
      if (logoutTimer) {
        $timeout.cancel(logoutTimer);
        logoutTimer = null;
      }
      if (activityTimer) {
        $timeout.cancel(activityTimer);
        activityTimer = null;
      }
      warningDisplayed = false;
      
      // Clear any existing session warning notifications
      if (Notifications && Notifications.clearByType) {
        Notifications.clearByType('session-warning');
      }
    }

    function startTimers() {
      if (sessionTimeoutInSeconds <= 0 || warningTimeInSeconds <= 0) {
        return; // Timeout disabled
      }

      resetTimers();

      var warningTime = (sessionTimeoutInSeconds - warningTimeInSeconds) * 1000;
      var logoutTime = sessionTimeoutInSeconds * 1000;

      // Set warning timer
      if (warningTime > 0) {
        warningTimer = $timeout(function() {
          showSessionWarning();
        }, warningTime);
      }

      // Set logout timer
      logoutTimer = $timeout(function() {
        performLogout();
      }, logoutTime);
    }

    function showSessionWarning() {
      if (warningDisplayed) {
        return;
      }
      warningDisplayed = true;

      var remainingMinutes = Math.ceil(warningTimeInSeconds / 60);

      if (Notifications) {
        Notifications.addMessage({
          status: $translate.instant('SESSION_WARNING_TITLE', {}, 'Session Warning'),
          message: $translate.instant(
            'SESSION_WARNING_MESSAGE',
            { minutes: remainingMinutes },
            'Your session will expire in ' + remainingMinutes + ' minutes. Click here to extend your session.'
          ),
          type: 'session-warning',
          duration: warningTimeInSeconds * 1000,
          actions: [
            {
              label: $translate.instant('SESSION_EXTEND', {}, 'Extend Session'),
              action: function() {
                extendSession();
              }
            }
          ]
        });
      }
    }

    function extendSession() {
      // Make a lightweight API call to extend the session
      $http.get(Uri.appUri('engine://engine/'))
        .then(function() {
          resetTimers();
          startTimers();
          if (Notifications) {
            Notifications.addMessage({
              status: $translate.instant('SESSION_EXTENDED', {}, 'Session Extended'),
              message: $translate.instant(
                'SESSION_EXTENDED_MESSAGE',
                {},
                'Your session has been extended.'
              ),
              type: 'success',
              duration: 3000
            });
          }
        })
        .catch(function() {
          // Session might have already expired
          performLogout();
        });
    }

    function performLogout() {
      resetTimers();
      $rootScope.$broadcast('session.timeout');
      $rootScope.$broadcast('authentication.login.required');
    }

    function onUserActivity() {
      if (activityTimer) {
        $timeout.cancel(activityTimer);
      }
      
      // Debounce activity detection to avoid too many timer resets
      activityTimer = $timeout(function() {
        resetTimers();
        startTimers();
      }, 1000);
    }

    // Activity detection
    function initializeActivityDetection() {
      var events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click'];
      
      angular.forEach(events, function(event) {
        document.addEventListener(event, onUserActivity, true);
      });
    }

    // Cleanup function
    function cleanup() {
      resetTimers();
      if (intervalCheck) {
        $interval.cancel(intervalCheck);
      }
      var events = ['mousedown', 'mousemove', 'keypress', 'scroll', 'touchstart', 'click'];
      angular.forEach(events, function(event) {
        document.removeEventListener(event, onUserActivity, true);
      });
    }

    // Initialize the service
    configPromise.then(function() {
      initializeActivityDetection();
      startTimers();
    });

    // Listen for authentication events
    $rootScope.$on('authentication.login.success', function() {
      resetTimers();
      startTimers();
    });

    $rootScope.$on('authentication.logout.success', function() {
      cleanup();
    });

    // Cleanup on scope destroy
    $rootScope.$on('$destroy', cleanup);

    // Service API
    return {
      /**
       * Get current session timeout in seconds
       */
      getSessionTimeout: function() {
        return sessionTimeoutInSeconds;
      },

      /**
       * Get warning time in seconds
       */
      getWarningTime: function() {
        return warningTimeInSeconds;
      },

      /**
       * Manually extend the session
       */
      extendSession: extendSession,

      /**
       * Reset and restart session timers
       */
      resetSession: function() {
        resetTimers();
        startTimers();
      },

      /**
       * Stop session timeout monitoring
       */
      stopMonitoring: function() {
        cleanup();
      },

      /**
       * Start session timeout monitoring
       */
      startMonitoring: function() {
        initializeActivityDetection();
        startTimers();
      }
    };
  }
];
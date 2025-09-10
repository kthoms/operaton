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
package org.operaton.bpm.spring.boot.starter.property;

import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SessionCookiePropertiesTest {

  @Test
  void shouldHaveDefaultValues() {
    SessionCookieProperties properties = new SessionCookieProperties();
    
    assertThat(properties.getTimeoutInSeconds()).isEqualTo(1800); // 30 minutes
    assertThat(properties.getWarningTimeInSeconds()).isEqualTo(300); // 5 minutes
    assertThat(properties.isEnableSameSiteCookie()).isTrue();
    assertThat(properties.isEnableSecureCookie()).isFalse();
  }

  @Test
  void shouldAllowCustomTimeoutValues() {
    SessionCookieProperties properties = new SessionCookieProperties();
    
    properties.setTimeoutInSeconds(3600); // 1 hour
    properties.setWarningTimeInSeconds(600); // 10 minutes
    
    assertThat(properties.getTimeoutInSeconds()).isEqualTo(3600);
    assertThat(properties.getWarningTimeInSeconds()).isEqualTo(600);
  }

  @Test
  void shouldAllowDisabledTimeout() {
    SessionCookieProperties properties = new SessionCookieProperties();
    
    properties.setTimeoutInSeconds(0); // Disabled
    properties.setWarningTimeInSeconds(0); // Disabled
    
    assertThat(properties.getTimeoutInSeconds()).isEqualTo(0);
    assertThat(properties.getWarningTimeInSeconds()).isEqualTo(0);
  }

  @Test
  void shouldIncludeTimeoutInInitParams() {
    SessionCookieProperties properties = new SessionCookieProperties();
    properties.setTimeoutInSeconds(3600); // Different from default
    properties.setWarningTimeInSeconds(600); // Different from default
    
    Map<String, String> initParams = properties.getInitParams();
    
    assertThat(initParams).containsEntry("sessionTimeoutInSeconds", "3600");
    assertThat(initParams).containsEntry("sessionWarningTimeInSeconds", "600");
  }

  @Test
  void shouldNotIncludeDefaultTimeoutInInitParams() {
    SessionCookieProperties properties = new SessionCookieProperties();
    // Use default values
    
    Map<String, String> initParams = properties.getInitParams();
    
    assertThat(initParams).doesNotContainKey("sessionTimeoutInSeconds");
    assertThat(initParams).doesNotContainKey("sessionWarningTimeInSeconds");
  }

  @Test
  void shouldIncludeTimeoutInToString() {
    SessionCookieProperties properties = new SessionCookieProperties();
    properties.setTimeoutInSeconds(7200);
    properties.setWarningTimeInSeconds(900);
    
    String toString = properties.toString();
    
    assertThat(toString).contains("timeoutInSeconds=7200");
    assertThat(toString).contains("warningTimeInSeconds=900");
  }
}
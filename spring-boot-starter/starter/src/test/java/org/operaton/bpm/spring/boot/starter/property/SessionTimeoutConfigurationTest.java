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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringBootConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SessionTimeoutConfigurationTest.TestConfig.class)
@TestPropertySource(properties = {
    "operaton.webapp.session-cookie.timeout-in-seconds=3600",
    "operaton.webapp.session-cookie.warning-time-in-seconds=600"
})
class SessionTimeoutConfigurationTest {

  @Autowired
  private OperatonBpmProperties properties;

  @Test
  void shouldLoadSessionTimeoutConfiguration() {
    SessionCookieProperties sessionCookie = properties.getWebapp().getSessionCookie();
    
    assertThat(sessionCookie.getTimeoutInSeconds()).isEqualTo(3600);
    assertThat(sessionCookie.getWarningTimeInSeconds()).isEqualTo(600);
  }

  @Test
  void shouldIncludeTimeoutInInitParams() {
    SessionCookieProperties sessionCookie = properties.getWebapp().getSessionCookie();
    
    var initParams = sessionCookie.getInitParams();
    
    assertThat(initParams).containsEntry("sessionTimeoutInSeconds", "3600");
    assertThat(initParams).containsEntry("sessionWarningTimeInSeconds", "600");
  }

  @SpringBootConfiguration
  @EnableConfigurationProperties(OperatonBpmProperties.class)
  static class TestConfig {
    // Test configuration
  }
}
/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.operaton.bpm.dmn.engine.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.operaton.bpm.dmn.engine.DmnEngine;
import org.operaton.bpm.dmn.engine.DmnEngineConfiguration;
import org.operaton.bpm.dmn.engine.impl.DefaultDmnEngine;
import org.operaton.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;

public class DmnEngineExtensionTest {

  @RegisterExtension
  public DmnEngineExtension dmnEngineExtension = new DmnEngineExtension();

  @RegisterExtension
  public DmnEngineExtension nullEngineExtension = new DmnEngineExtension(null);

  @RegisterExtension
  public DmnEngineExtension customEngineExtension = new DmnEngineExtension(initConfiguration());

  public DmnEngineConfiguration customConfiguration;

  public DmnEngineConfiguration initConfiguration() {
    customConfiguration = new DefaultDmnEngineConfiguration();
    return customConfiguration;
  }

  @Test
  public void shouldCreateDefaultDmnEngineWithoutConfiguration() {
    DmnEngine dmnEngine = dmnEngineExtension.getDmnEngine();
    assertThat(dmnEngine)
      .isInstanceOf(DefaultDmnEngine.class)
      .isNotNull();
  }

  @Test
  public void shouldCreateDefaultDmnEngineWithNullConfiguration() {
    DmnEngine dmnEngine = nullEngineExtension.getDmnEngine();
    assertThat(dmnEngine)
      .isInstanceOf(DefaultDmnEngine.class)
      .isNotNull();
  }

  @Test
  public void shouldCreateEngineFromCustomConfiguration() {
    DmnEngine dmnEngine = customEngineExtension.getDmnEngine();
    assertThat(dmnEngine)
      .isNotNull();

    assertThat(dmnEngine.getConfiguration())
      .isEqualTo(customConfiguration);
  }

}

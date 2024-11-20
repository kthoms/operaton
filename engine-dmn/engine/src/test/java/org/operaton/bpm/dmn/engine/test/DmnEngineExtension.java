/*
 * Copyright itemis AG and/or licensed to itemis AG
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. itemis AG licenses this file to you under the Apache License,
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

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.operaton.bpm.dmn.engine.DmnEngine;
import org.operaton.bpm.dmn.engine.DmnEngineConfiguration;

/**
 * JUnit 5 extension for {@link DmnEngine} initialization.
 * <p>
 * Usage:
 * </p>
 *
 * <pre>
 *   class YourDmnTest {
 *     DmnEngine dmnEngine;
 *
 *     &#64;RegisterExtension
 *     private static DmnEngineExtension dmnEngineExtension = new DmnEngineExtension();
 *
 *     &#64;BeforeAll
 *     static void init() {
 *       dmnEngine = engine;
 *     }
 *
 *     &#64;Test
 *     void testDecision() {
 *       assertThat(dmnEngine).isNotNull();
 *       // your test logic here
 *     }
 *   }
 * </pre>
 *
 * <p>
 * The DMN engine is provided to the test class via the DmnEngineExtension. The DMN engine
 * is initialized with the default DMN engine configuration. To specify a custom
 * configuration, provide a {@link DmnEngineConfiguration} via the constructor
 * of {@link DmnEngineExtension}.
 * </p>
 */
public class DmnEngineExtension implements BeforeAllCallback, BeforeEachCallback {

  private final DmnEngineConfiguration configuration;
  private DmnEngine dmnEngine;

  /**
   * Creates a {@link DmnEngine} with the default {@link DmnEngineConfiguration}.
   */
  public DmnEngineExtension() {
    this(DmnEngineConfiguration.createDefaultDmnEngineConfiguration());
  }

  /**
   * Creates a {@link DmnEngine} with the given {@link DmnEngineConfiguration}.
   *
   * @param configuration the custom DMN engine configuration
   */
  public DmnEngineExtension(DmnEngineConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Provides the {@link DmnEngine} instance.
   *
   * @return the initialized {@link DmnEngine}
   */
  public DmnEngine getDmnEngine() {
    if (dmnEngine == null) {
      dmnEngine = configuration.buildEngine();
    }
    return dmnEngine;
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    if (dmnEngine == null) {
      dmnEngine = configuration.buildEngine();
    }
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    // Ensures the engine is ready before each test
    if (dmnEngine == null) {
      dmnEngine = configuration.buildEngine();
    }
  }
}


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
package org.operaton.bpm.dmn.engine.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;

import org.operaton.bpm.dmn.engine.DmnEngine;
import org.operaton.bpm.dmn.engine.delegate.DmnDecisionEvaluationEvent;
import org.operaton.bpm.dmn.engine.delegate.DmnDecisionEvaluationListener;
import org.operaton.bpm.dmn.engine.delegate.DmnDecisionTableEvaluationListener;
import org.operaton.bpm.dmn.engine.impl.DefaultDmnEngine;
import org.operaton.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.operaton.bpm.dmn.engine.impl.el.DefaultScriptEngineResolver;
import org.operaton.bpm.dmn.engine.impl.el.JuelElProvider;
import org.operaton.bpm.dmn.engine.impl.metrics.DefaultEngineMetricCollector;
import org.operaton.bpm.dmn.engine.impl.spi.el.ElProvider;
import org.operaton.bpm.dmn.engine.impl.spi.transform.DmnTransformer;
import org.operaton.bpm.dmn.engine.impl.transform.DefaultDmnTransformer;
import org.operaton.bpm.dmn.feel.impl.FeelEngineFactory;
import org.operaton.bpm.dmn.feel.impl.juel.FeelEngineFactoryImpl;
import org.operaton.bpm.dmn.feel.impl.juel.FeelEngineImpl;
import org.junit.Before;
import org.junit.Test;

public class DefaultDmnEngineConfigurationApiTest {

  protected DefaultDmnEngineConfiguration configuration;

  @Before
  public void initConfiguration() {
    configuration = new DefaultDmnEngineConfiguration();
  }

  @Test
  public void shouldSetEngineMetricCollector() {
    configuration.setEngineMetricCollector(null);
    assertThat(configuration.getEngineMetricCollector())
      .isNull();

    DefaultEngineMetricCollector metricCollector = new DefaultEngineMetricCollector();
    configuration.setEngineMetricCollector(metricCollector);

    assertThat(configuration.getEngineMetricCollector())
      .isEqualTo(metricCollector);
    }

  @Test
  public void shouldSetFluentEngineMetricCollector() {
    configuration.engineMetricCollector(null);
    assertThat(configuration.getEngineMetricCollector())
      .isNull();

    DefaultEngineMetricCollector metricCollector = new DefaultEngineMetricCollector();
    configuration.engineMetricCollector(metricCollector);

    assertThat(configuration.getEngineMetricCollector())
      .isEqualTo(metricCollector);
  }

  @Test
  public void shouldSetCustomPreDecisionTableEvaluationListeners() {
    configuration.setCustomPreDecisionTableEvaluationListeners(null);
    assertThat(configuration.getCustomPreDecisionTableEvaluationListeners())
      .isNull();

    configuration.setCustomPreDecisionTableEvaluationListeners(Collections.<DmnDecisionTableEvaluationListener>emptyList());
    assertThat(configuration.getCustomPreDecisionTableEvaluationListeners())
      .isEmpty();

    ArrayList<DmnDecisionTableEvaluationListener> listeners = new ArrayList<>();
    listeners.add(new DefaultEngineMetricCollector());
    listeners.add(new DefaultEngineMetricCollector());

    configuration.setCustomPreDecisionTableEvaluationListeners(listeners);
    assertThat(configuration.getCustomPreDecisionTableEvaluationListeners())
      .containsExactlyElementsOf(listeners);
  }

  @Test
  public void shouldSetFluentCustomPreDecisionTableEvaluationListeners() {
    configuration.customPreDecisionTableEvaluationListeners(null);
    assertThat(configuration.getCustomPreDecisionTableEvaluationListeners())
      .isNull();

    configuration.customPreDecisionTableEvaluationListeners(Collections.<DmnDecisionTableEvaluationListener>emptyList());
    assertThat(configuration.getCustomPreDecisionTableEvaluationListeners())
      .isEmpty();

    ArrayList<DmnDecisionTableEvaluationListener> listeners = new ArrayList<>();
    listeners.add(new DefaultEngineMetricCollector());
    listeners.add(new DefaultEngineMetricCollector());

    configuration.customPreDecisionTableEvaluationListeners(listeners);
    assertThat(configuration.getCustomPreDecisionTableEvaluationListeners())
      .containsExactlyElementsOf(listeners);
  }

  @Test
  public void shouldSetFluentCustomPreDecisionEvaluationListeners() {
    configuration.customPreDecisionEvaluationListeners(null);
    assertThat(configuration.getCustomPreDecisionEvaluationListeners())
      .isNull();

    configuration.customPreDecisionEvaluationListeners(Collections.<DmnDecisionEvaluationListener>emptyList());
    assertThat(configuration.getCustomPreDecisionEvaluationListeners())
      .isEmpty();

    ArrayList<DmnDecisionEvaluationListener> listeners = new ArrayList<>();
    listeners.add(new TestDecisionEvaluationListener());
    listeners.add(new TestDecisionEvaluationListener());

    configuration.customPreDecisionEvaluationListeners(listeners);
    assertThat(configuration.getCustomPreDecisionEvaluationListeners())
      .containsExactlyElementsOf(listeners);
  }

  @Test
  public void shouldSetCustomPostDecisionTableEvaluationListeners() {
    configuration.setCustomPostDecisionTableEvaluationListeners(null);
    assertThat(configuration.getCustomPostDecisionTableEvaluationListeners())
      .isNull();

    configuration.setCustomPostDecisionTableEvaluationListeners(Collections.<DmnDecisionTableEvaluationListener>emptyList());
    assertThat(configuration.getCustomPostDecisionTableEvaluationListeners())
      .isEmpty();

    ArrayList<DmnDecisionTableEvaluationListener> listeners = new ArrayList<>();
    listeners.add(new DefaultEngineMetricCollector());
    listeners.add(new DefaultEngineMetricCollector());

    configuration.setCustomPostDecisionTableEvaluationListeners(listeners);
    assertThat(configuration.getCustomPostDecisionTableEvaluationListeners())
      .containsExactlyElementsOf(listeners);
  }

  @Test
  public void shouldSetCustomPreDecisionEvaluationListeners() {
    configuration.setCustomPreDecisionEvaluationListeners(null);
    assertThat(configuration.getCustomPreDecisionEvaluationListeners())
      .isNull();

    configuration.setCustomPreDecisionEvaluationListeners(Collections.<DmnDecisionEvaluationListener>emptyList());
    assertThat(configuration.getCustomPreDecisionEvaluationListeners())
      .isEmpty();

    ArrayList<DmnDecisionEvaluationListener> listeners = new ArrayList<>();
    listeners.add(new TestDecisionEvaluationListener());
    listeners.add(new TestDecisionEvaluationListener());

    configuration.setCustomPreDecisionEvaluationListeners(listeners);
    assertThat(configuration.getCustomPreDecisionEvaluationListeners())
      .containsExactlyElementsOf(listeners);
  }

  @Test
  public void shouldSetCustomPostDecisionEvaluationListeners() {
    configuration.setCustomPostDecisionEvaluationListeners(null);
    assertThat(configuration.getCustomPostDecisionEvaluationListeners())
      .isNull();

    configuration.setCustomPostDecisionEvaluationListeners(Collections.<DmnDecisionEvaluationListener>emptyList());
    assertThat(configuration.getCustomPostDecisionEvaluationListeners())
      .isEmpty();

    ArrayList<DmnDecisionEvaluationListener> listeners = new ArrayList<>();
    listeners.add(new TestDecisionEvaluationListener());
    listeners.add(new TestDecisionEvaluationListener());

    configuration.setCustomPostDecisionEvaluationListeners(listeners);
    assertThat(configuration.getCustomPostDecisionEvaluationListeners())
      .containsExactlyElementsOf(listeners);
  }

  @Test
  public void shouldSetFluentCustomPostDecisionTableEvaluationListeners() {
    configuration.customPostDecisionTableEvaluationListeners(null);
    assertThat(configuration.getCustomPostDecisionTableEvaluationListeners())
      .isNull();

    configuration.customPostDecisionTableEvaluationListeners(Collections.<DmnDecisionTableEvaluationListener>emptyList());
    assertThat(configuration.getCustomPostDecisionTableEvaluationListeners())
      .isEmpty();

    ArrayList<DmnDecisionTableEvaluationListener> listeners = new ArrayList<>();
    listeners.add(new DefaultEngineMetricCollector());
    listeners.add(new DefaultEngineMetricCollector());

    configuration.customPostDecisionTableEvaluationListeners(listeners);
    assertThat(configuration.getCustomPostDecisionTableEvaluationListeners())
      .containsExactlyElementsOf(listeners);
  }

  @Test
  public void shouldSetFluentCustomPostDecisionEvaluationListeners() {
    configuration.customPostDecisionEvaluationListeners(null);
    assertThat(configuration.getCustomPostDecisionEvaluationListeners())
      .isNull();

    configuration.customPostDecisionEvaluationListeners(Collections.<DmnDecisionEvaluationListener>emptyList());
    assertThat(configuration.getCustomPostDecisionEvaluationListeners())
      .isEmpty();

    ArrayList<DmnDecisionEvaluationListener> listeners = new ArrayList<>();
    listeners.add(new TestDecisionEvaluationListener());
    listeners.add(new TestDecisionEvaluationListener());

    configuration.customPostDecisionEvaluationListeners(listeners);
    assertThat(configuration.getCustomPostDecisionEvaluationListeners())
      .containsExactlyElementsOf(listeners);
  }

  @Test
  public void shouldSetScriptEngineResolver() {
    configuration.setScriptEngineResolver(null);
    assertThat(configuration.getScriptEngineResolver())
      .isNull();

    DefaultScriptEngineResolver scriptEngineResolver = new DefaultScriptEngineResolver();

    configuration.setScriptEngineResolver(scriptEngineResolver);
    assertThat(configuration.getScriptEngineResolver())
      .isEqualTo(scriptEngineResolver);
  }

  @Test
  public void shouldSetFluentScriptEngineResolver() {
    configuration.scriptEngineResolver(null);
    assertThat(configuration.getScriptEngineResolver())
      .isNull();

    DefaultScriptEngineResolver scriptEngineResolver = new DefaultScriptEngineResolver();

    configuration.scriptEngineResolver(scriptEngineResolver);
    assertThat(configuration.getScriptEngineResolver())
      .isEqualTo(scriptEngineResolver);
  }

  @Test
  public void shouldSetElProvider() {
    configuration.setElProvider(null);
    assertThat(configuration.getElProvider())
      .isNull();

    ElProvider elProvider = new JuelElProvider();

    configuration.setElProvider(elProvider);
    assertThat(configuration.getElProvider())
      .isEqualTo(elProvider);
  }

  @Test
  public void shouldSetFluentElProvider() {
    configuration.elProvider(null);
    assertThat(configuration.getElProvider())
      .isNull();

    ElProvider elProvider = new JuelElProvider();

    configuration.elProvider(elProvider);
    assertThat(configuration.getElProvider())
      .isEqualTo(elProvider);
  }

  @Test
  public void shouldSetFeelEngineFactory() {
    configuration.setFeelEngineFactory(null);
    assertThat(configuration.getFeelEngineFactory())
      .isNull();

    FeelEngineFactory feelEngineFactory = new FeelEngineFactoryImpl();

    configuration.setFeelEngineFactory(feelEngineFactory);
    assertThat(configuration.getFeelEngineFactory())
      .isEqualTo(feelEngineFactory);
  }

  @Test
  public void shouldSetFluentFeelEngineFactory() {
    configuration.feelEngineFactory(null);
    assertThat(configuration.getFeelEngineFactory())
      .isNull();

    FeelEngineFactory feelEngineFactory = new FeelEngineFactoryImpl();

    configuration.feelEngineFactory(feelEngineFactory);
    assertThat(configuration.getFeelEngineFactory())
      .isEqualTo(feelEngineFactory);
  }

  @Test
  public void shouldSetDefaultInputExpressionExpressionLanguage() {
    configuration.setDefaultInputExpressionExpressionLanguage(null);
    assertThat(configuration.getDefaultInputExpressionExpressionLanguage())
      .isNull();

    configuration.setDefaultInputExpressionExpressionLanguage("operaton");
    assertThat(configuration.getDefaultInputExpressionExpressionLanguage())
      .isEqualTo("operaton");
  }

  @Test
  public void shouldSetFluentDefaultInputExpressionExpressionLanguage() {
    configuration.defaultInputExpressionExpressionLanguage(null);
    assertThat(configuration.getDefaultInputExpressionExpressionLanguage())
      .isNull();

    configuration.defaultInputExpressionExpressionLanguage("operaton");
    assertThat(configuration.getDefaultInputExpressionExpressionLanguage())
      .isEqualTo("operaton");
  }

  @Test
  public void shouldSetDefaultInputEntryExpressionLanguage() {
    configuration.setDefaultInputEntryExpressionLanguage(null);
    assertThat(configuration.getDefaultInputEntryExpressionLanguage())
      .isNull();

    configuration.setDefaultInputEntryExpressionLanguage("operaton");
    assertThat(configuration.getDefaultInputEntryExpressionLanguage())
      .isEqualTo("operaton");
  }

  @Test
  public void shouldSetFluentDefaultInputEntryExpressionLanguage() {
    configuration.defaultInputEntryExpressionLanguage(null);
    assertThat(configuration.getDefaultInputEntryExpressionLanguage())
      .isNull();

    configuration.defaultInputEntryExpressionLanguage("operaton");
    assertThat(configuration.getDefaultInputEntryExpressionLanguage())
      .isEqualTo("operaton");
  }

  @Test
  public void shouldSetDefaultOutputEntryExpressionLanguage() {
    configuration.setDefaultOutputEntryExpressionLanguage(null);
    assertThat(configuration.getDefaultOutputEntryExpressionLanguage())
      .isNull();

    configuration.setDefaultOutputEntryExpressionLanguage("operaton");
    assertThat(configuration.getDefaultOutputEntryExpressionLanguage())
      .isEqualTo("operaton");
  }

  @Test
  public void shouldSetFluentDefaultOutputEntryExpressionLanguage() {
    configuration.defaultOutputEntryExpressionLanguage(null);
    assertThat(configuration.getDefaultOutputEntryExpressionLanguage())
      .isNull();

    configuration.defaultOutputEntryExpressionLanguage("operaton");
    assertThat(configuration.getDefaultOutputEntryExpressionLanguage())
      .isEqualTo("operaton");
  }

  @Test
  public void shouldSetDefaultLiteralExpressionLanguage() {
    configuration.setDefaultLiteralExpressionLanguage(null);
    assertThat(configuration.getDefaultLiteralExpressionLanguage())
      .isNull();

    configuration.setDefaultLiteralExpressionLanguage("operaton");
    assertThat(configuration.getDefaultLiteralExpressionLanguage())
      .isEqualTo("operaton");
  }

  @Test
  public void shouldSetFluentDefaultLiteralExpressionLanguage() {
    configuration.defaultLiteralExpressionLanguage(null);
    assertThat(configuration.getDefaultLiteralExpressionLanguage())
      .isNull();

    configuration.defaultLiteralExpressionLanguage("operaton");
    assertThat(configuration.getDefaultLiteralExpressionLanguage())
      .isEqualTo("operaton");
  }

  @Test
  public void shouldSetTransformer() {
    configuration.setTransformer(null);
    assertThat(configuration.getTransformer())
      .isNull();

    DmnTransformer transformer = new DefaultDmnTransformer();

    configuration.setTransformer(transformer);
    assertThat(configuration.getTransformer())
      .isEqualTo(transformer);
  }

  @Test
  public void shouldSetFluentTransformer() {
    configuration.transformer(null);
    assertThat(configuration.getTransformer())
      .isNull();

    DmnTransformer transformer = new DefaultDmnTransformer();

    configuration.transformer(transformer);
    assertThat(configuration.getTransformer())
      .isEqualTo(transformer);
  }

  @Test
  public void shouldBeFluentConfigurable() {
    DefaultEngineMetricCollector metricCollector = new DefaultEngineMetricCollector();
    ArrayList<DmnDecisionTableEvaluationListener> preListeners = new ArrayList<>();
    preListeners.add(new DefaultEngineMetricCollector());
    ArrayList<DmnDecisionTableEvaluationListener> postListeners = new ArrayList<>();
    preListeners.add(new DefaultEngineMetricCollector());
    ArrayList<DmnDecisionEvaluationListener> preDecisionListeners = new ArrayList<>();
    preDecisionListeners.add(new TestDecisionEvaluationListener());
    ArrayList<DmnDecisionEvaluationListener> postDecisionListeners = new ArrayList<>();
    postDecisionListeners.add(new TestDecisionEvaluationListener());
    DefaultScriptEngineResolver scriptEngineResolver = new DefaultScriptEngineResolver();
    ElProvider elProvider = new JuelElProvider();
    FeelEngineFactory feelEngineFactory = new FeelEngineFactoryImpl();
    DmnTransformer transformer = new DefaultDmnTransformer();

    DmnEngine engine = configuration
      .engineMetricCollector(metricCollector)
      .customPreDecisionTableEvaluationListeners(preListeners)
      .customPostDecisionTableEvaluationListeners(postListeners)
      .customPreDecisionEvaluationListeners(preDecisionListeners)
      .customPostDecisionEvaluationListeners(postDecisionListeners)
      .scriptEngineResolver(scriptEngineResolver)
      .elProvider(elProvider)
      .feelEngineFactory(feelEngineFactory)
      .defaultInputExpressionExpressionLanguage("operaton")
      .defaultInputEntryExpressionLanguage("operaton")
      .defaultOutputEntryExpressionLanguage("operaton")
      .transformer(transformer)
      .buildEngine();

    configuration = (DefaultDmnEngineConfiguration) engine.getConfiguration();
    assertThat(configuration.getEngineMetricCollector())
      .isEqualTo(metricCollector);
    assertThat(configuration.getCustomPreDecisionTableEvaluationListeners())
      .containsExactlyElementsOf(preListeners);
    assertThat(configuration.getCustomPostDecisionTableEvaluationListeners())
      .containsExactlyElementsOf(postListeners);
    assertThat(configuration.getCustomPreDecisionEvaluationListeners())
      .containsExactlyElementsOf(preDecisionListeners);
    assertThat(configuration.getCustomPostDecisionEvaluationListeners())
      .containsExactlyElementsOf(postDecisionListeners);
    assertThat(configuration.getScriptEngineResolver())
      .isEqualTo(scriptEngineResolver);
    assertThat(configuration.getElProvider())
      .isEqualTo(elProvider);
    assertThat(configuration.getFeelEngineFactory())
      .isEqualTo(feelEngineFactory);
    assertThat(configuration.getDefaultInputExpressionExpressionLanguage())
      .isEqualTo("operaton");
    assertThat(configuration.getDefaultInputEntryExpressionLanguage())
      .isEqualTo("operaton");
    assertThat(configuration.getDefaultOutputEntryExpressionLanguage())
      .isEqualTo("operaton");
    assertThat(configuration.getTransformer())
      .isEqualTo(transformer);
  }

  @Test
  public void shouldInitDecisionTableEvaluationListeners() {
    ArrayList<DmnDecisionTableEvaluationListener> preListeners = new ArrayList<>();
    preListeners.add(new DefaultEngineMetricCollector());
    ArrayList<DmnDecisionTableEvaluationListener> postListeners = new ArrayList<>();
    postListeners.add(new DefaultEngineMetricCollector());

    configuration
      .customPreDecisionTableEvaluationListeners(preListeners)
      .customPostDecisionTableEvaluationListeners(postListeners)
      .buildEngine();

    ArrayList<DmnDecisionTableEvaluationListener> expectedListeners = new ArrayList<>();
    expectedListeners.addAll(preListeners);
    expectedListeners.addAll(postListeners);

    assertThat(configuration.getDecisionTableEvaluationListeners())
      .containsExactlyElementsOf(expectedListeners);
  }

  @Test
  public void shouldInitDecisionEvaluationListeners() {
    ArrayList<DmnDecisionEvaluationListener> preListeners = new ArrayList<>();
    preListeners.add(new TestDecisionEvaluationListener());
    ArrayList<DmnDecisionEvaluationListener> postListeners = new ArrayList<>();
    postListeners.add(new TestDecisionEvaluationListener());

    configuration
      .customPreDecisionEvaluationListeners(preListeners)
      .customPostDecisionEvaluationListeners(postListeners)
      .buildEngine();

    ArrayList<DmnDecisionEvaluationListener> expectedListeners = new ArrayList<>();
    expectedListeners.addAll(preListeners);
    expectedListeners.add((DefaultEngineMetricCollector) configuration.getEngineMetricCollector());
    expectedListeners.addAll(postListeners);

    assertThat(configuration.getDecisionEvaluationListeners())
      .containsExactlyElementsOf(expectedListeners);
  }

  @Test
  public void shouldInitFeelEngine() {
    FeelEngineFactory feelEngineFactory = new FeelEngineFactoryImpl();
    configuration.setFeelEngineFactory(feelEngineFactory);

    configuration.buildEngine();

    assertThat(configuration.getFeelEngine())
      .isInstanceOf(FeelEngineImpl.class)
      .isNotNull();
  }

  @Test
  public void shouldBuildDefaultDmnEngine() {
    DmnEngine engine = configuration.buildEngine();
    assertThat(engine)
      .isInstanceOf(DefaultDmnEngine.class)
      .isNotNull();
  }

  // helper
  public static class TestDecisionEvaluationListener implements DmnDecisionEvaluationListener {

    public DmnDecisionEvaluationEvent evaluationEvent;

    @Override
    public void notify(DmnDecisionEvaluationEvent evaluationEvent) {
      this.evaluationEvent = evaluationEvent;
    }

    public DmnDecisionEvaluationEvent getEvaluationEvent() {
      return evaluationEvent;
    }
  }
}

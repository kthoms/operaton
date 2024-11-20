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
package org.operaton.bpm.dmn.engine.feel.helper;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.operaton.bpm.dmn.engine.feel.function.helper.FunctionProvider;
import org.operaton.bpm.dmn.feel.impl.scala.ScalaFeelEngine;
import org.operaton.bpm.dmn.feel.impl.scala.function.FeelCustomFunctionProvider;
import org.operaton.bpm.engine.variable.Variables;
import org.operaton.bpm.engine.variable.context.VariableContext;

public class FeelExtension implements AfterEachCallback {

  protected FunctionProvider functionProvider;
  protected ScalaFeelEngine feelEngine;

  protected FeelExtension(FunctionProvider functionProvider) {
    this.functionProvider = functionProvider;
  }

  protected FeelExtension() {
    feelEngine = new ScalaFeelEngine(null);
  }

  public static FeelExtension buildWithFunctionProvider() {
    FunctionProvider functionProvider = new FunctionProvider();
    return new FeelExtension(functionProvider);
  }

  public static FeelExtension build() {
    return new FeelExtension();
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    if (functionProvider != null) {
      functionProvider.clear();
    }
  }

  public <T> T evaluateExpression(String expression) {
    return evaluateExpression(expression, null);
  }

  public <T> T evaluateExpression(String expression, Object value) {
    if (functionProvider != null) {
      List<FeelCustomFunctionProvider> functionProviders =
        Collections.singletonList(functionProvider);

      feelEngine = new ScalaFeelEngine(functionProviders);
    }

    VariableContext variableCtx = Variables.putValue("variable", value).asVariableContext();
    return feelEngine.evaluateSimpleExpression(expression, variableCtx);
  }

  public FunctionProvider getFunctionProvider() {
    return functionProvider;
  }
}

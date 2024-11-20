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
package org.operaton.bpm.dmn.engine.feel.function;

import org.operaton.bpm.dmn.engine.feel.helper.FeelRule;
import org.operaton.bpm.dmn.feel.impl.FeelException;


import org.junit.Rule;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;

public class ExternalFunctionTest {

  protected FeelRule feelRule = FeelRule.build();

  @Rule
  public RuleChain ruleChain = RuleChain.outerRule(feelRule).around(thrown);

  @Test
  public void shouldFailWhenUsingExternalFunction() {
    Throwable exception = assertThrows(FeelException.class, () ->

      // when
      feelRule.evaluateExpression("{ \n" +
        "  foo: function(x, y) external { \n" +
        "    java: { \n" +
        "        class: \"java.lang.Math\", \n" +
        "        method signature: \"addExact(int, int)\" \n" +
        "    } \n" +
        "  },\n" +
        "  bar: foo(5, 5)\n" +
        "}.bar"));
    assertTrue(exception.getMessage().contains("External functions are disabled"));
  }

}

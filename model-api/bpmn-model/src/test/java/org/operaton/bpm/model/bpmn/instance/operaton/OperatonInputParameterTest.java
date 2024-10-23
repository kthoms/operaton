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
package org.operaton.bpm.model.bpmn.instance.operaton;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.operaton.bpm.model.bpmn.instance.BpmnModelElementInstanceTest;

import static org.operaton.bpm.model.bpmn.impl.BpmnModelConstants.OPERATON_NS;

/**
 * @author Sebastian Menski
 */
public class OperatonInputParameterTest extends BpmnModelElementInstanceTest {

  public TypeAssumption getTypeAssumption() {
    return new TypeAssumption(OPERATON_NS, false);
  }

  public Collection<ChildElementAssumption> getChildElementAssumptions() {
    return null;
  }

  public Collection<AttributeAssumption> getAttributesAssumptions() {
    return Arrays.asList(
      new AttributeAssumption(OPERATON_NS, "name", false, true)
    );
  }

  @Disabled("Test ignored. CAM-9441: Bug fix needed")
  @Test
  void intputParameterScriptChildAssignment() {
    Assertions.assertDoesNotThrow(() -> {
      OperatonInputParameter inputParamElement = modelInstance.newInstance(OperatonInputParameter.class);
      inputParamElement.setOperatonName("aVariable");

      OperatonScript scriptElement = modelInstance.newInstance(OperatonScript.class);
      scriptElement.setOperatonScriptFormat("juel");
      scriptElement.setTextContent("${'a script'}");

      inputParamElement.addChildElement(scriptElement);
    }, "OperatonScript should be accepted as a child element of OperatonInputParameter. Error: ");
  }

  @Disabled("Test ignored. CAM-9441: Bug fix needed")
  @Test
  void inputParameterListChildAssignment() {
    Assertions.assertDoesNotThrow(() -> {
      OperatonInputParameter inputParamElement = modelInstance.newInstance(OperatonInputParameter.class);
      inputParamElement.setOperatonName("aVariable");

      OperatonList listElement = modelInstance.newInstance(OperatonList.class);

      inputParamElement.addChildElement(listElement);
    }, "OperatonList should be accepted as a child element of OperatonInputParameter. Error: ");
  }

  @Disabled("Test ignored. CAM-9441: Bug fix needed")
  @Test
  void inputParameterMapChildAssignment() {
    Assertions.assertDoesNotThrow(() -> {
      OperatonInputParameter inputParamElement = modelInstance.newInstance(OperatonInputParameter.class);
      inputParamElement.setOperatonName("aVariable");

      OperatonMap listElement = modelInstance.newInstance(OperatonMap.class);

      inputParamElement.addChildElement(listElement);
    }, "OperatonMap should be accepted as a child element of OperatonInputParameter. Error: ");
  }
}

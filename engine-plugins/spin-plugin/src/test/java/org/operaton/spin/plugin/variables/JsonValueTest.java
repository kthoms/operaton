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
package org.operaton.spin.plugin.variables;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.operaton.spin.DataFormats.json;
import static org.operaton.spin.plugin.variable.SpinValues.jsonValue;
import static org.operaton.spin.plugin.variable.type.SpinValueType.JSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.operaton.bpm.engine.ProcessEngineException;
import org.operaton.bpm.engine.impl.test.PluggableProcessEngineTestCase;
import org.operaton.bpm.engine.runtime.VariableInstance;
import org.operaton.bpm.engine.task.Task;
import org.operaton.bpm.engine.test.Deployment;
import org.operaton.bpm.engine.variable.VariableMap;
import org.operaton.bpm.engine.variable.Variables;
import org.operaton.bpm.engine.variable.type.ValueType;
import org.operaton.bpm.model.bpmn.Bpmn;
import org.operaton.bpm.model.bpmn.BpmnModelInstance;
import org.operaton.spin.DataFormats;
import org.operaton.spin.SpinRuntimeException;
import org.operaton.spin.json.SpinJsonNode;
import org.operaton.spin.plugin.variable.type.SpinValueType;
import org.operaton.spin.plugin.variable.value.JsonValue;
import org.operaton.spin.plugin.variable.value.builder.JsonValueBuilder;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * @author Roman Smirnov
 *
 */
class JsonValueTest extends PluggableProcessEngineTestCase {

  protected static final String ONE_TASK_PROCESS = "org/operaton/spin/plugin/oneTaskProcess.bpmn20.xml";
  protected static final String JSON_FORMAT_NAME = DataFormats.JSON_DATAFORMAT_NAME;

  protected static final String ONE_TASK_PROCESS_KEY = "oneTaskProcess";

  protected String jsonString = "{\"foo\": \"bar\"}";
  protected String brokenJsonString = "{\"foo: \"bar\"}";

  protected String variableName = "x";

  @Deployment(resources = ONE_TASK_PROCESS)
  @Test
  void getUntypedJsonValue() throws JSONException {
    // given
    JsonValue jsonValue = jsonValue(jsonString).create();
    VariableMap variables = Variables.createVariables().putValueTyped(variableName, jsonValue);

    String processInstanceId = runtimeService.startProcessInstanceByKey(ONE_TASK_PROCESS_KEY, variables).getId();

    // when
    SpinJsonNode value = (SpinJsonNode) runtimeService.getVariable(processInstanceId, variableName);

    // then
    JSONAssert.assertEquals(jsonString, value.toString(), true);
    assertEquals(json().getName(), value.getDataFormatName());
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  @Test
  void getNullJsonValue() {
    // given
    JsonValue jsonValue = jsonValue((String) null).create();
    VariableMap variables = Variables.createVariables().putValueTyped(variableName, jsonValue);

    String processInstanceId = runtimeService.startProcessInstanceByKey(ONE_TASK_PROCESS_KEY, variables).getId();

    // when
    SpinJsonNode value = (SpinJsonNode) runtimeService.getVariable(processInstanceId, variableName);

    // then
    assertThat(value).isNull();
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  @Test
  void getTypedJsonValue() throws JSONException {
    // given
    JsonValue jsonValue = jsonValue(jsonString).create();
    VariableMap variables = Variables.createVariables().putValueTyped(variableName, jsonValue);

    String processInstanceId = runtimeService.startProcessInstanceByKey(ONE_TASK_PROCESS_KEY, variables).getId();

    // when
    JsonValue typedValue = runtimeService.getVariableTyped(processInstanceId, variableName);

    // then
    SpinJsonNode value = typedValue.getValue();
    JSONAssert.assertEquals(jsonString, value.toString(), true);

    assertTrue(typedValue.isDeserialized());
    assertEquals(JSON, typedValue.getType());
    assertEquals(JSON_FORMAT_NAME, typedValue.getSerializationDataFormat());
    JSONAssert.assertEquals(jsonString, typedValue.getValueSerialized(), true);
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  @Test
  void brokenJsonSerialization() {
    // given
    JsonValue value = jsonValue(brokenJsonString).create();

    String processInstanceId = runtimeService.startProcessInstanceByKey(ONE_TASK_PROCESS_KEY).getId();

    Assertions.assertDoesNotThrow(() -> {
      // when
      runtimeService.setVariable(processInstanceId, variableName, value);
    }, "no exception expected");
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  @Test
  void failingDeserialization() {
    // given
    JsonValue value = jsonValue(brokenJsonString).create();

    String processInstanceId = runtimeService.startProcessInstanceByKey(ONE_TASK_PROCESS_KEY).getId();
    runtimeService.setVariable(processInstanceId, variableName, value);

    try {
      // when
      runtimeService.getVariable(processInstanceId, variableName);
      fail("exception expected");
    } catch (ProcessEngineException e) {
      // happy path
    }

    try {
      runtimeService.getVariableTyped(processInstanceId, variableName);
      fail("exception expected");
    } catch(ProcessEngineException e) {
      // happy path
    }

    // However, I can access the serialized value
    JsonValue jsonValue = runtimeService.getVariableTyped(processInstanceId, variableName, false);
    assertFalse(jsonValue.isDeserialized());
    assertEquals(brokenJsonString, jsonValue.getValueSerialized());

    // but not the deserialized properties
    try {
      jsonValue.getValue();
      fail("exception expected");
    } catch(SpinRuntimeException e) {
    }
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  @Test
  void failForNonExistingSerializationFormat() {
    // given
    JsonValueBuilder builder = jsonValue(jsonString).serializationDataFormat("non existing data format");
    String processInstanceId = runtimeService.startProcessInstanceByKey(ONE_TASK_PROCESS_KEY).getId();

    try {
      // when (1)
      runtimeService.setVariable(processInstanceId, variableName, builder);
      fail("Exception expected");
    } catch (ProcessEngineException e) {
      // then (1)
      assertTextPresent("Cannot find serializer for value", e.getMessage());
      // happy path
    }

    try {
      // when (2)
      runtimeService.setVariable(processInstanceId, variableName, builder.create());
      fail("Exception expected");
    } catch (ProcessEngineException e) {
      // then (2)
      assertTextPresent("Cannot find serializer for value", e.getMessage());
      // happy path
    }
  }

  @Deployment(resources = "org/operaton/spin/plugin/jsonConditionProcess.bpmn20.xml")
  @Test
  void jsonValueInCondition() {
    // given
    String jsonString = "{\"age\": 22 }";
    JsonValue value = jsonValue(jsonString).create();
    VariableMap variables = Variables.createVariables().putValueTyped("customer", value);

    // when
    runtimeService.startProcessInstanceByKey("process", variables);

    // then
    Task task = taskService.createTaskQuery().singleResult();
    assertEquals("task1", task.getTaskDefinitionKey());
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  @Test
  void transientJsonValueFluent() {
    // given
    JsonValue jsonValue = jsonValue(jsonString).setTransient(true).create();
    VariableMap variables = Variables.createVariables().putValueTyped(variableName, jsonValue);

    // when
    runtimeService.startProcessInstanceByKey(ONE_TASK_PROCESS_KEY, variables).getId();

    // then
    List<VariableInstance> variableInstances = runtimeService.createVariableInstanceQuery().list();
    assertEquals(0, variableInstances.size());
  }

  @Deployment(resources = ONE_TASK_PROCESS)
  @Test
  void transientJsonValue() {
    // given
    JsonValue jsonValue = jsonValue(jsonString, true).create();
    VariableMap variables = Variables.createVariables().putValueTyped(variableName, jsonValue);

    // when
    runtimeService.startProcessInstanceByKey(ONE_TASK_PROCESS_KEY, variables).getId();

    // then
    List<VariableInstance> variableInstances = runtimeService.createVariableInstanceQuery().list();
    assertEquals(0, variableInstances.size());
  }

  @Test
  void applyValueInfoFromSerializedValue() {
    // given
    Map<String, Object> valueInfo = new HashMap<>();
    valueInfo.put(ValueType.VALUE_INFO_TRANSIENT, true);

    // when
    JsonValue jsonValue = (JsonValue) SpinValueType.JSON.createValueFromSerialized(jsonString, valueInfo);

    // then
    assertTrue(jsonValue.isTransient());
    Map<String, Object> returnedValueInfo = SpinValueType.JSON.getValueInfo(jsonValue);
    assertEquals(true, returnedValueInfo.get(ValueType.VALUE_INFO_TRANSIENT));
  }

  /**
   * See https://app.camunda.com/jira/browse/CAM-9932
   */
  @Test
  void transientJsonSpinVariables() {
    // given
    BpmnModelInstance modelInstance = Bpmn.createExecutableProcess("aProcess")
      .startEvent()
      .serviceTask()
        .operatonClass(JsonDelegate.class)
      .userTask()
      .endEvent()
      .done();
    deployment(modelInstance);

    // when
    String processInstanceId = runtimeService.startProcessInstanceByKey("aProcess").getId();

    // then
    Object value = runtimeService.getVariable(processInstanceId, "jsonVariable");
    assertThat(value).isNull();
  }

  @Test
  void deserializeTransientJsonValue() {
    // given
    BpmnModelInstance modelInstance = Bpmn.createExecutableProcess("foo")
        .startEvent()
        .exclusiveGateway("gtw")
          .sequenceFlowId("flow1")
          .condition("cond", "${S(" + variableName + ").prop(\"foo\").stringValue() == \"bar\"}")
          .userTask("userTask1")
          .endEvent()
        .moveToLastGateway()
          .sequenceFlowId("flow2")
          .userTask("userTask2")
          .endEvent()
        .done();

    deployment(modelInstance);

    JsonValue jsonValue = jsonValue(jsonString, true).create();
    VariableMap variables = Variables.createVariables().putValueTyped(variableName, jsonValue);

    // when
    runtimeService.startProcessInstanceByKey("foo", variables);

    // then
    List<VariableInstance> variableInstances = runtimeService.createVariableInstanceQuery().list();
    assertEquals(0, variableInstances.size());

    Task task = taskService.createTaskQuery().singleResult();
    assertNotNull(task);
    assertEquals("userTask1", task.getTaskDefinitionKey());
  }

}

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
package org.operaton.bpm.qa.performance.engine.junit;

import static org.operaton.bpm.engine.authorization.Authorization.ANY;
import static org.operaton.bpm.engine.authorization.Authorization.AUTH_TYPE_GRANT;

import java.util.List;

import org.operaton.bpm.engine.AuthorizationService;
import org.operaton.bpm.engine.HistoryService;
import org.operaton.bpm.engine.ProcessEngine;
import org.operaton.bpm.engine.RepositoryService;
import org.operaton.bpm.engine.RuntimeService;
import org.operaton.bpm.engine.TaskService;
import org.operaton.bpm.engine.authorization.Authorization;
import org.operaton.bpm.engine.authorization.Permission;
import org.operaton.bpm.engine.authorization.Resource;
import org.operaton.bpm.engine.test.ProcessEngineRule;
import org.operaton.bpm.qa.performance.engine.framework.PerfTestBuilder;
import org.operaton.bpm.qa.performance.engine.framework.PerfTestConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;

/**
 * @author Daniel Meyer
 *
 */
public abstract class AuthorizationPerformanceTestCase {

  public PerfTestConfigurationRule testConfigurationRule = new PerfTestConfigurationRule();

  public PerfTestResultRecorderRule resultRecorderRule = new PerfTestResultRecorderRule();

  @Rule
  public RuleChain ruleChain = RuleChain
    .outerRule(testConfigurationRule)
    .around(resultRecorderRule);

  protected ProcessEngine engine;
  protected TaskService taskService;
  protected HistoryService historyService;
  protected RuntimeService runtimeService;
  protected RepositoryService repositoryService;

  @Before
  public void setup() {
    engine = PerfTestProcessEngine.getInstance();
    taskService = engine.getTaskService();
    historyService = engine.getHistoryService();
    runtimeService = engine.getRuntimeService();
    repositoryService = engine.getRepositoryService();
  }

  public PerfTestBuilder performanceTest() {
    PerfTestConfiguration configuration = testConfigurationRule.getPerformanceTestConfiguration();
    configuration.setPlatform("operaton BPM");
    return new PerfTestBuilder(configuration, resultRecorderRule);
  }


  protected void grouptGrant(String groupId, Resource resource, Permission... perms) {

    AuthorizationService authorizationService = engine.getAuthorizationService();
    Authorization groupGrant = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
    groupGrant.setResource(resource);
    groupGrant.setResourceId(ANY);
    for (Permission permission : perms) {
      groupGrant.addPermission(permission);
    }
    groupGrant.setGroupId(groupId);
    authorizationService.saveAuthorization(groupGrant);
  }

  protected void userGrant(String userId, Resource resource, Permission... perms) {

    AuthorizationService authorizationService = engine.getAuthorizationService();
    Authorization groupGrant = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
    groupGrant.setResource(resource);
    groupGrant.setResourceId(ANY);
    for (Permission permission : perms) {
      groupGrant.addPermission(permission);
    }
    groupGrant.setUserId(userId);
    authorizationService.saveAuthorization(groupGrant);
  }

}
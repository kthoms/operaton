/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.engine.test.api.authorization.history;

import static org.camunda.bpm.engine.authorization.Authorization.ANY;
import static org.camunda.bpm.engine.authorization.Permissions.READ_HISTORY;
import static org.camunda.bpm.engine.authorization.Resources.PROCESS_DEFINITION;

import java.util.Date;
import java.util.List;

import org.camunda.bpm.engine.history.HistoricIncident;
import org.camunda.bpm.engine.history.HistoricIncidentQuery;
import org.camunda.bpm.engine.impl.AbstractQuery;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.interceptor.Command;
import org.camunda.bpm.engine.impl.interceptor.CommandContext;
import org.camunda.bpm.engine.impl.interceptor.CommandExecutor;
import org.camunda.bpm.engine.impl.jobexecutor.TimerSuspendProcessDefinitionHandler;
import org.camunda.bpm.engine.impl.persistence.entity.HistoricIncidentEntity;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.test.api.authorization.AuthorizationTest;

/**
 * @author Roman Smirnov
 *
 */
public class HistoricIncidentAuthorizationTest extends AuthorizationTest {

  protected static final String TIMER_START_PROCESS_KEY = "timerStartProcess";
  protected static final String ONE_INCIDENT_PROCESS_KEY = "process";
  protected static final String ANOTHER_ONE_INCIDENT_PROCESS_KEY = "anotherOneIncidentProcess";

  protected String deploymentId;

  @Override
  public void setUp() throws Exception {
    deploymentId = createDeployment(null,
        "org/camunda/bpm/engine/test/api/authorization/timerStartEventProcess.bpmn20.xml",
        "org/camunda/bpm/engine/test/api/authorization/oneIncidentProcess.bpmn20.xml",
        "org/camunda/bpm/engine/test/api/authorization/anotherOneIncidentProcess.bpmn20.xml").getId();
    super.setUp();
  }

  @Override
  public void tearDown() {
    super.tearDown();
    deleteDeployment(deploymentId);
  }

  // historic incident query (standalone) //////////////////////////////

  public void testQueryForStandaloneHistoricIncidents() {
    // given
    disableAuthorization();
    repositoryService.suspendProcessDefinitionByKey(ONE_INCIDENT_PROCESS_KEY, true, new Date());
    String jobId = null;
    List<Job> jobs = managementService.createJobQuery().list();
    for (Job job : jobs) {
      if (job.getProcessDefinitionKey() == null) {
        jobId = job.getId();
        break;
      }
    }
    managementService.setJobRetries(jobId, 0);
    enableAuthorization();

    // when
    HistoricIncidentQuery query = historyService.createHistoricIncidentQuery();

    // then
    verifyQueryResults(query, 1);

    disableAuthorization();
    managementService.deleteJob(jobId);
    enableAuthorization();

    clearDatabase();
  }

  // historic incident query (start timer job incident) //////////////////////////////

  public void testStartTimerJobIncidentQueryWithoutAuthorization() {
    // given
    disableAuthorization();
    String jobId = managementService.createJobQuery().singleResult().getId();
    managementService.setJobRetries(jobId, 0);
    enableAuthorization();

    // when
    HistoricIncidentQuery query = historyService.createHistoricIncidentQuery();

    // then
    verifyQueryResults(query, 0);
  }

  public void testStartTimerJobIncidentQueryWithReadHistoryPermissionOnProcessDefinition() {
    // given
    disableAuthorization();
    String jobId = managementService.createJobQuery().singleResult().getId();
    managementService.setJobRetries(jobId, 0);
    enableAuthorization();

    createGrantAuthorization(PROCESS_DEFINITION, TIMER_START_PROCESS_KEY, userId, READ_HISTORY);

    // when
    HistoricIncidentQuery query = historyService.createHistoricIncidentQuery();

    // then
    verifyQueryResults(query, 1);
  }

  public void testStartTimerJobIncidentQueryWithReadHistoryPermissionOnAnyProcessDefinition() {
    // given
    disableAuthorization();
    String jobId = managementService.createJobQuery().singleResult().getId();
    managementService.setJobRetries(jobId, 0);
    enableAuthorization();

    createGrantAuthorization(PROCESS_DEFINITION, ANY, userId, READ_HISTORY);

    // when
    HistoricIncidentQuery query = historyService.createHistoricIncidentQuery();

    // then
    verifyQueryResults(query, 1);
  }

  public void testStartTimerJobIncidentQueryWithReadInstancePermissionOnAnyProcessDefinition() {
    // given
    disableAuthorization();
    String jobId = managementService.createJobQuery().singleResult().getId();
    managementService.setJobRetries(jobId, 0);
    enableAuthorization();

    createGrantAuthorization(PROCESS_DEFINITION, ANY, userId, READ_HISTORY);

    // when
    HistoricIncidentQuery query = historyService.createHistoricIncidentQuery();

    // then
    verifyQueryResults(query, 1);
  }

  // historic incident query ///////////////////////////////////////////

  public void testSimpleQueryWithoutAuthorization() {
    // given
    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);

    // when
    HistoricIncidentQuery query = historyService.createHistoricIncidentQuery();

    // then
    verifyQueryResults(query, 0);
  }

  public void testSimpleQueryWithReadHistoryPermissionOnProcessDefinition() {
    // given
    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);
    createGrantAuthorization(PROCESS_DEFINITION, ONE_INCIDENT_PROCESS_KEY, userId, READ_HISTORY);

    // when
    HistoricIncidentQuery query = historyService.createHistoricIncidentQuery();

    // then
    verifyQueryResults(query, 1);
  }

  public void testSimpleQueryWithReadHistoryPermissionOnAnyProcessDefinition() {
    // given
    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);
    createGrantAuthorization(PROCESS_DEFINITION, ANY, userId, READ_HISTORY);

    // when
    HistoricIncidentQuery query = historyService.createHistoricIncidentQuery();

    // then
    verifyQueryResults(query, 1);
  }

  public void testSimpleQueryWithMultiple() {
    // given
    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);
    createGrantAuthorization(PROCESS_DEFINITION, ONE_INCIDENT_PROCESS_KEY, userId, READ_HISTORY);
    createGrantAuthorization(PROCESS_DEFINITION, ANY, userId, READ_HISTORY);

    // when
    HistoricIncidentQuery query = historyService.createHistoricIncidentQuery();

    // then
    verifyQueryResults(query, 1);
  }

  // historic incident query (multiple incidents ) ///////////////////////////////////////////

  public void testQueryWithoutAuthorization() {
    // given
    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);

    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);

    // when
    HistoricIncidentQuery query = historyService.createHistoricIncidentQuery();

    // then
    verifyQueryResults(query, 0);
  }

  public void testQueryWithReadHistoryPermissionOnProcessDefinition() {
    // given
    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);

    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);

    createGrantAuthorization(PROCESS_DEFINITION, ONE_INCIDENT_PROCESS_KEY, userId, READ_HISTORY);

    // when
    HistoricIncidentQuery query = historyService.createHistoricIncidentQuery();

    // then
    verifyQueryResults(query, 2);
  }

  public void testQueryWithReadHistoryPermissionOnAnyProcessDefinition() {
    // given
    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);

    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);

    createGrantAuthorization(PROCESS_DEFINITION, ANY, userId, READ_HISTORY);

    // when
    HistoricIncidentQuery query = historyService.createHistoricIncidentQuery();

    // then
    verifyQueryResults(query, 5);
  }

  // historic job log (mixed) //////////////////////////////////////////

  public void testMixedQueryWithoutAuthorization() {
    // given
    disableAuthorization();
    repositoryService.suspendProcessDefinitionByKey(ONE_INCIDENT_PROCESS_KEY, true, new Date());
    String firstJobId = null;
    List<Job> jobs = managementService.createJobQuery().withRetriesLeft().list();
    for (Job job : jobs) {
      if (job.getProcessDefinitionKey() == null) {
        firstJobId = job.getId();
        break;
      }
    }
    managementService.setJobRetries(firstJobId, 0);

    repositoryService.suspendProcessDefinitionByKey(ONE_INCIDENT_PROCESS_KEY, true, new Date());
    String secondJobId = null;
    jobs = managementService.createJobQuery().withRetriesLeft().list();
    for (Job job : jobs) {
      if (job.getProcessDefinitionKey() == null) {
        secondJobId = job.getId();
        break;
      }
    }
    managementService.setJobRetries(secondJobId, 0);
    enableAuthorization();

    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);

    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);

    // when
    HistoricIncidentQuery query = historyService.createHistoricIncidentQuery();

    // then
    verifyQueryResults(query, 2);

    disableAuthorization();
    managementService.deleteJob(firstJobId);
    managementService.deleteJob(secondJobId);
    enableAuthorization();

    clearDatabase();
  }

  public void testMixedQueryWithReadHistoryPermissionOnProcessDefinition() {
    // given
    disableAuthorization();
    repositoryService.suspendProcessDefinitionByKey(ONE_INCIDENT_PROCESS_KEY, true, new Date());
    String firstJobId = null;
    List<Job> jobs = managementService.createJobQuery().withRetriesLeft().list();
    for (Job job : jobs) {
      if (job.getProcessDefinitionKey() == null) {
        firstJobId = job.getId();
        break;
      }
    }
    managementService.setJobRetries(firstJobId, 0);

    repositoryService.suspendProcessDefinitionByKey(ONE_INCIDENT_PROCESS_KEY, true, new Date());
    String secondJobId = null;
    jobs = managementService.createJobQuery().withRetriesLeft().list();
    for (Job job : jobs) {
      if (job.getProcessDefinitionKey() == null) {
        secondJobId = job.getId();
        break;
      }
    }
    managementService.setJobRetries(secondJobId, 0);
    enableAuthorization();

    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);

    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);

    createGrantAuthorization(PROCESS_DEFINITION, ONE_INCIDENT_PROCESS_KEY, userId, READ_HISTORY);

    // when
    HistoricIncidentQuery query = historyService.createHistoricIncidentQuery();

    // then
    verifyQueryResults(query, 4);

    disableAuthorization();
    managementService.deleteJob(firstJobId);
    managementService.deleteJob(secondJobId);
    enableAuthorization();

    clearDatabase();
  }

  public void testMixedQueryWithReadHistoryPermissionOnAnyProcessDefinition() {
    // given
    disableAuthorization();
    repositoryService.suspendProcessDefinitionByKey(ONE_INCIDENT_PROCESS_KEY, true, new Date());
    String firstJobId = null;
    List<Job> jobs = managementService.createJobQuery().withRetriesLeft().list();
    for (Job job : jobs) {
      if (job.getProcessDefinitionKey() == null) {
        firstJobId = job.getId();
        break;
      }
    }
    managementService.setJobRetries(firstJobId, 0);

    repositoryService.suspendProcessDefinitionByKey(ONE_INCIDENT_PROCESS_KEY, true, new Date());
    String secondJobId = null;
    jobs = managementService.createJobQuery().withRetriesLeft().list();
    for (Job job : jobs) {
      if (job.getProcessDefinitionKey() == null) {
        secondJobId = job.getId();
        break;
      }
    }
    managementService.setJobRetries(secondJobId, 0);
    enableAuthorization();

    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ONE_INCIDENT_PROCESS_KEY);

    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);
    startProcessAndExecuteJob(ANOTHER_ONE_INCIDENT_PROCESS_KEY);

    createGrantAuthorization(PROCESS_DEFINITION, ANY, userId, READ_HISTORY);

    // when
    HistoricIncidentQuery query = historyService.createHistoricIncidentQuery();

    // then
    verifyQueryResults(query, 7);

    disableAuthorization();
    managementService.deleteJob(firstJobId);
    managementService.deleteJob(secondJobId);
    enableAuthorization();

    clearDatabase();
  }

  // helper ////////////////////////////////////////////////////////////

  protected void verifyQueryResults(HistoricIncidentQuery query, int countExpected) {
    verifyQueryResults((AbstractQuery<?, ?>) query, countExpected);
  }

  protected void clearDatabase() {
    CommandExecutor commandExecutor = processEngineConfiguration.getCommandExecutorTxRequired();
    commandExecutor.execute(new Command<Object>() {
      public Object execute(CommandContext commandContext) {
        commandContext.getHistoricJobLogManager().deleteHistoricJobLogsByHandlerType(TimerSuspendProcessDefinitionHandler.TYPE);
        List<HistoricIncident> incidents = Context.getProcessEngineConfiguration().getHistoryService().createHistoricIncidentQuery().list();
        for (HistoricIncident incident : incidents) {
          commandContext.getHistoricIncidentManager().delete((HistoricIncidentEntity) incident);
        }
        return null;
      }
    });
  }

}

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
package org.operaton.bpm.identity.ldap.util;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class LdapTestEnvironmentExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

  private LdapTestEnvironment ldapTestEnvironment;

  private int additionalNumberOfUsers = 0;
  private int additionnalNumberOfGroups = 0;
  private int additionalNumberOfRoles = 0;
  private boolean posix = false;

  @Override
  public void beforeTestExecution(ExtensionContext context) throws Exception {
    if (posix) {
      setupPosix();
    } else {
      setupLdap();
    }
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    if (ldapTestEnvironment != null) {
      ldapTestEnvironment.shutdown();
      ldapTestEnvironment = null;
    }
  }

  private void setupLdap() throws Exception {
    ldapTestEnvironment = new LdapTestEnvironment();
    ldapTestEnvironment.init(additionalNumberOfUsers, additionnalNumberOfGroups, additionalNumberOfRoles);
  }

  private void setupPosix() throws Exception {
    ldapTestEnvironment = new LdapPosixTestEnvironment();
    ldapTestEnvironment.init();
  }

  public LdapTestEnvironmentExtension additionalNumberOfUsers(int additionalNumberOfUsers) {
    this.additionalNumberOfUsers = additionalNumberOfUsers;
    return this;
  }

  public LdapTestEnvironmentExtension additionnalNumberOfGroups(int additionnalNumberOfGroups) {
    this.additionnalNumberOfGroups = additionnalNumberOfGroups;
    return this;
  }

  public LdapTestEnvironmentExtension additionalNumberOfRoles(int additionalNumberOfRoles) {
    this.additionalNumberOfRoles = additionalNumberOfRoles;
    return this;
  }

  public LdapTestEnvironmentExtension posix(boolean posix) {
    this.posix = posix;
    return this;
  }

  public LdapTestEnvironment getLdapTestEnvironment() {
    return ldapTestEnvironment;
  }

}

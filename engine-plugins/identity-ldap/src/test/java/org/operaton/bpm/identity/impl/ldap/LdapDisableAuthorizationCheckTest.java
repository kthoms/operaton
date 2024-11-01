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
package org.operaton.bpm.identity.impl.ldap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.operaton.bpm.engine.AuthorizationService;
import org.operaton.bpm.engine.IdentityService;
import org.operaton.bpm.engine.ProcessEngineConfiguration;
import org.operaton.bpm.engine.authorization.Authorization;
import org.operaton.bpm.engine.authorization.Permission;
import org.operaton.bpm.engine.authorization.Resource;
import org.operaton.bpm.engine.test.junit5.ProcessEngineExtension;
import org.operaton.bpm.identity.ldap.util.LdapTestEnvironment;
import org.operaton.bpm.identity.ldap.util.LdapTestEnvironmentRule;
import org.operaton.bpm.identity.ldap.util.LdapTestUtilities;

import static org.operaton.bpm.engine.authorization.Authorization.AUTH_TYPE_GRANT;
import static org.operaton.bpm.engine.authorization.Permissions.READ;
import static org.operaton.bpm.engine.authorization.Resources.GROUP;
import static org.operaton.bpm.engine.authorization.Resources.USER;
import static org.operaton.bpm.identity.ldap.util.LdapTestUtilities.testGroupPaging;
import static org.operaton.bpm.identity.ldap.util.LdapTestUtilities.testUserPaging;

/**
 * @author Roman Smirnov
 *
 */
@ExtendWith(LdapTestEnvironmentRule.class)
public class LdapDisableAuthorizationCheckTest {

  @RegisterExtension
  static LdapTestEnvironmentRule ldapRule = new LdapTestEnvironmentRule();
  @RegisterExtension
  static ProcessEngineExtension engineRule = new ProcessEngineExtension().configurationResource("operaton.ldap.disable.authorization.check.cfg.xml");

  ProcessEngineConfiguration processEngineConfiguration;
  IdentityService identityService;
  AuthorizationService authorizationService;
  LdapTestEnvironment ldapTestEnvironment;

  @BeforeEach
  void setup() {
    processEngineConfiguration = engineRule.getProcessEngineConfiguration();
    identityService = engineRule.getIdentityService();
    authorizationService = engineRule.getAuthorizationService();
    ldapTestEnvironment = ldapRule.getLdapTestEnvironment();
  }

  @Test
  void userQueryPagination() {
    LdapTestUtilities.testUserPaging(identityService, ldapTestEnvironment);
  }

  @Test
  void userQueryPaginationWithAuthenticatedUserWithoutAuthorizations() {
    try {
      processEngineConfiguration.setAuthorizationEnabled(true);

      identityService.setAuthenticatedUserId("oscar");
      testUserPaging(identityService, ldapTestEnvironment);

    } finally {
      processEngineConfiguration.setAuthorizationEnabled(false);
      identityService.clearAuthentication();
    }
  }

  @Test
  void userQueryPaginationWithAuthenticatedUserWithAuthorizations() {
    createGrantAuthorization(USER, "roman", "oscar", READ);
    createGrantAuthorization(USER, "daniel", "oscar", READ);
    createGrantAuthorization(USER, "monster", "oscar", READ);
    createGrantAuthorization(USER, "ruecker", "oscar", READ);

    try {
      processEngineConfiguration.setAuthorizationEnabled(true);

      identityService.setAuthenticatedUserId("oscar");
      testUserPaging(identityService, ldapTestEnvironment);

    } finally {
      processEngineConfiguration.setAuthorizationEnabled(false);
      identityService.clearAuthentication();

      for (Authorization authorization : authorizationService.createAuthorizationQuery().list()) {
        authorizationService.deleteAuthorization(authorization.getId());
      }

    }
  }

  @Test
  void groupQueryPagination() {
    testGroupPaging(identityService);
  }

  @Test
  void groupQueryPaginationWithAuthenticatedUserWithoutAuthorizations() {
    try {
      processEngineConfiguration.setAuthorizationEnabled(true);

      identityService.setAuthenticatedUserId("oscar");
      testGroupPaging(identityService);

    } finally {
      processEngineConfiguration.setAuthorizationEnabled(false);
      identityService.clearAuthentication();
    }
  }

  @Test
  void groupQueryPaginationWithAuthenticatedUserWithAuthorizations() {
    createGrantAuthorization(GROUP, "management", "oscar", READ);
    createGrantAuthorization(GROUP, "consulting", "oscar", READ);
    createGrantAuthorization(GROUP, "external", "oscar", READ);

    try {
      processEngineConfiguration.setAuthorizationEnabled(true);

      identityService.setAuthenticatedUserId("oscar");
      testGroupPaging(identityService);

    } finally {
      processEngineConfiguration.setAuthorizationEnabled(false);
      identityService.clearAuthentication();

      for (Authorization authorization : authorizationService.createAuthorizationQuery().list()) {
        authorizationService.deleteAuthorization(authorization.getId());
      }

    }
  }

  protected void createGrantAuthorization(Resource resource, String resourceId, String userId, Permission... permissions) {
    Authorization authorization = createAuthorization(AUTH_TYPE_GRANT, resource, resourceId);
    authorization.setUserId(userId);
    for (Permission permission : permissions) {
      authorization.addPermission(permission);
    }
    authorizationService.saveAuthorization(authorization);
  }

  protected Authorization createAuthorization(int type, Resource resource, String resourceId) {
    Authorization authorization = authorizationService.createNewAuthorization(type);

    authorization.setResource(resource);
    if (resourceId != null) {
      authorization.setResourceId(resourceId);
    }

    return authorization;
  }

}

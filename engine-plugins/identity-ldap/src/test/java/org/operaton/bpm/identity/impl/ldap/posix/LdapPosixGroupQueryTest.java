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
package org.operaton.bpm.identity.impl.ldap.posix;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.operaton.bpm.engine.IdentityService;
import org.operaton.bpm.engine.identity.Group;
import org.operaton.bpm.engine.identity.User;
import org.operaton.bpm.engine.test.ProcessEngineRule;
import org.operaton.bpm.identity.ldap.util.LdapTestEnvironment;
import org.operaton.bpm.identity.ldap.util.LdapTestEnvironmentRule;
import org.junit.ClassRule;
import org.junit.Rule;


public class LdapPosixGroupQueryTest {

  @ClassRule
  public static LdapTestEnvironmentRule ldapRule = new LdapTestEnvironmentRule().posix(true);
  @Rule
  public ProcessEngineRule engineRule = new ProcessEngineRule("posix.operaton.cfg.xml");

  IdentityService identityService;
  LdapTestEnvironment ldapTestEnvironment;

  @BeforeEach
  void setup() {
    identityService = engineRule.getIdentityService();
    ldapTestEnvironment = ldapRule.getLdapTestEnvironment();
  }

  @Test
  void shouldFindGroupFilterByGroupIdWithoutMembers() {
    // given

    // when
    Group group = identityService.createGroupQuery().groupId("posix-group-without-members").singleResult();

    // then
    assertThat(group).isNotNull();
    assertThat(group.getId()).isEqualTo("posix-group-without-members");
  }

  @Test
  void shouldFindGroupFilterByGroupIdWithMembers() {
    // given

    // when
    Group group = identityService.createGroupQuery().groupId("posix-group-with-members").singleResult();

    // then
    assertThat(group).isNotNull();
    assertThat(group.getId()).isEqualTo("posix-group-with-members");
  }

  @Test
  void shouldFindUserFilterByMemberOfGroupWithoutMember() {
    // given

    // when
    List<User> result = identityService.createUserQuery().memberOfGroup("posix-group-without-members").list();

    // then
    assertThat(result).hasSize(0);
  }

  @Test
  void shouldFindUserFilterByMemberOfGroupWithMember() {
    // given

    // when
    List<User> result = identityService.createUserQuery().memberOfGroup("posix-group-with-members").list();

    // then
    assertThat(result).hasSize(3);
    assertThat(result).extracting("id").containsOnly("fozzie", "monster", "ruecker");
  }

}

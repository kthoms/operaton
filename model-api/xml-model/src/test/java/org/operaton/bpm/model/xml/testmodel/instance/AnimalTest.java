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
package org.operaton.bpm.model.xml.testmodel.instance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.operaton.bpm.model.xml.testmodel.TestModelConstants.MODEL_NAMESPACE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.operaton.bpm.model.xml.ModelInstance;
import org.operaton.bpm.model.xml.ModelValidationException;
import org.operaton.bpm.model.xml.impl.parser.AbstractModelParser;
import org.operaton.bpm.model.xml.testmodel.Gender;
import org.operaton.bpm.model.xml.testmodel.TestModelParser;
import org.operaton.bpm.model.xml.testmodel.TestModelTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Sebastian Menski
 */
public class AnimalTest extends TestModelTest {

  private Animal tweety;
  private Animal hedwig;
  private Animal birdo;
  private Animal plucky;
  private Animal fiffy;
  private Animal timmy;
  private Animal daisy;
  private RelationshipDefinition hedwigRelationship;
  private RelationshipDefinition birdoRelationship;
  private RelationshipDefinition pluckyRelationship;
  private RelationshipDefinition fiffyRelationship;
  private RelationshipDefinition timmyRelationship;
  private RelationshipDefinition daisyRelationship;

  public AnimalTest(String testName, ModelInstance testModelInstance, AbstractModelParser modelParser) {
    super(testName, testModelInstance, modelParser);
  }


  @Parameters(name="Model {0}")
  public static Collection<Object[]> models() {
    Object[][] models = {createModel(), parseModel(AnimalTest.class)};
    return Arrays.asList(models);
  }

  public static Object[] createModel() {
    TestModelParser modelParser = new TestModelParser();
    ModelInstance modelInstance = modelParser.getEmptyModel();

    Animals animals = modelInstance.newInstance(Animals.class);
    modelInstance.setDocumentElement(animals);

    // add a tns namespace prefix for QName testing
    animals.getDomElement().registerNamespace("tns", MODEL_NAMESPACE);

    Animal tweety = createBird(modelInstance, "tweety", Gender.Female);
    Animal hedwig = createBird(modelInstance, "hedwig", Gender.Male);
    Animal birdo = createBird(modelInstance, "birdo", Gender.Female);
    Animal plucky = createBird(modelInstance, "plucky", Gender.Unknown);
    Animal fiffy = createBird(modelInstance, "fiffy", Gender.Female);
    createBird(modelInstance, "timmy", Gender.Male);
    createBird(modelInstance, "daisy", Gender.Female);

    // create and add some relationships
    RelationshipDefinition hedwigRelationship = createRelationshipDefinition(modelInstance, hedwig, ChildRelationshipDefinition.class);
    addRelationshipDefinition(tweety, hedwigRelationship);
    RelationshipDefinition birdoRelationship = createRelationshipDefinition(modelInstance, birdo, ChildRelationshipDefinition.class);
    addRelationshipDefinition(tweety, birdoRelationship);
    RelationshipDefinition pluckyRelationship = createRelationshipDefinition(modelInstance, plucky, FriendRelationshipDefinition.class);
    addRelationshipDefinition(tweety, pluckyRelationship);
    RelationshipDefinition fiffyRelationship = createRelationshipDefinition(modelInstance, fiffy, FriendRelationshipDefinition.class);
    addRelationshipDefinition(tweety, fiffyRelationship);

    tweety.getRelationshipDefinitionRefs().add(hedwigRelationship);
    tweety.getRelationshipDefinitionRefs().add(birdoRelationship);
    tweety.getRelationshipDefinitionRefs().add(pluckyRelationship);
    tweety.getRelationshipDefinitionRefs().add(fiffyRelationship);

    tweety.getBestFriends().add(birdo);
    tweety.getBestFriends().add(plucky);

    return new Object[]{"created", modelInstance, modelParser};
  }

  @BeforeEach
  void copyModelInstance() {
    modelInstance = cloneModelInstance();

    tweety = modelInstance.getModelElementById("tweety");
    hedwig = modelInstance.getModelElementById("hedwig");
    birdo = modelInstance.getModelElementById("birdo");
    plucky = modelInstance.getModelElementById("plucky");
    fiffy = modelInstance.getModelElementById("fiffy");
    timmy = modelInstance.getModelElementById("timmy");
    daisy = modelInstance.getModelElementById("daisy");

    hedwigRelationship = modelInstance.getModelElementById("tweety-hedwig");
    birdoRelationship = modelInstance.getModelElementById("tweety-birdo");
    pluckyRelationship = modelInstance.getModelElementById("tweety-plucky");
    fiffyRelationship = modelInstance.getModelElementById("tweety-fiffy");

    timmyRelationship = createRelationshipDefinition(modelInstance, timmy, FriendRelationshipDefinition.class);
    daisyRelationship = createRelationshipDefinition(modelInstance, daisy, ChildRelationshipDefinition.class);
  }

  @Test
  void setIdAttributeByHelper() {
    String newId = "new-" + tweety.getId();
    tweety.setId(newId);
    assertThat(tweety.getId()).isEqualTo(newId);
  }

  @Test
  void setIdAttributeByAttributeName() {
    tweety.setAttributeValue("id", "duffy", true);
    assertThat(tweety.getId()).isEqualTo("duffy");
  }

  @Test
  void removeIdAttribute() {
    tweety.removeAttribute("id");
    assertThat(tweety.getId()).isNull();
  }

  @Test
  void setNameAttributeByHelper() {
    tweety.setName("tweety");
    assertThat(tweety.getName()).isEqualTo("tweety");
  }

  @Test
  void setNameAttributeByAttributeName() {
    tweety.setAttributeValue("name", "daisy");
    assertThat(tweety.getName()).isEqualTo("daisy");
  }

  @Test
  void removeNameAttribute() {
    tweety.removeAttribute("name");
    assertThat(tweety.getName()).isNull();
  }

  @Test
  void setFatherAttributeByHelper() {
    tweety.setFather(timmy);
    assertThat(tweety.getFather()).isEqualTo(timmy);
  }

  @Test
  void setFatherAttributeByAttributeName() {
    tweety.setAttributeValue("father", timmy.getId());
    assertThat(tweety.getFather()).isEqualTo(timmy);
  }

  @Test
  void setFatherAttributeByAttributeNameWithNamespace() {
    tweety.setAttributeValue("father", "tns:hedwig");
    assertThat(tweety.getFather()).isEqualTo(hedwig);
  }

  @Test
  void removeFatherAttribute() {
    tweety.setFather(timmy);
    assertThat(tweety.getFather()).isEqualTo(timmy);
    tweety.removeAttribute("father");
    assertThat(tweety.getFather()).isNull();
  }

  @Test
  void changeIdAttributeOfFatherReference() {
    tweety.setFather(timmy);
    assertThat(tweety.getFather()).isEqualTo(timmy);
    timmy.setId("new-" + timmy.getId());
    assertThat(tweety.getFather()).isEqualTo(timmy);
  }

  @Test
  void replaceFatherReferenceWithNewAnimal() {
    tweety.setFather(timmy);
    assertThat(tweety.getFather()).isEqualTo(timmy);
    timmy.replaceWithElement(plucky);
    assertThat(tweety.getFather()).isEqualTo(plucky);
  }

  @Test
  void setMotherAttributeByHelper() {
    tweety.setMother(daisy);
    assertThat(tweety.getMother()).isEqualTo(daisy);
  }

  @Test
  void setMotherAttributeByAttributeName() {
    tweety.setAttributeValue("mother", fiffy.getId());
    assertThat(tweety.getMother()).isEqualTo(fiffy);
  }

  @Test
  void removeMotherAttribute() {
    tweety.setMother(daisy);
    assertThat(tweety.getMother()).isEqualTo(daisy);
    tweety.removeAttribute("mother");
    assertThat(tweety.getMother()).isNull();
  }

  @Test
  void replaceMotherReferenceWithNewAnimal() {
    tweety.setMother(daisy);
    assertThat(tweety.getMother()).isEqualTo(daisy);
    daisy.replaceWithElement(birdo);
    assertThat(tweety.getMother()).isEqualTo(birdo);
  }

  @Test
  void changeIdAttributeOfMotherReference() {
    tweety.setMother(daisy);
    assertThat(tweety.getMother()).isEqualTo(daisy);
    daisy.setId("new-" + daisy.getId());
    assertThat(tweety.getMother()).isEqualTo(daisy);
  }

  @Test
  void setIsEndangeredAttributeByHelper() {
    tweety.setIsEndangered(true);
    assertThat(tweety.isEndangered()).isTrue();
  }

  @Test
  void setIsEndangeredAttributeByAttributeName() {
    tweety.setAttributeValue("isEndangered", "false");
    assertThat(tweety.isEndangered()).isFalse();
  }

  @Test
  void removeIsEndangeredAttribute() {
    tweety.removeAttribute("isEndangered");
    // default value of isEndangered: false
    assertThat(tweety.isEndangered()).isFalse();
  }

  @Test
  void setGenderAttributeByHelper() {
    tweety.setGender(Gender.Male);
    assertThat(tweety.getGender()).isEqualTo(Gender.Male);
  }

  @Test
  void setGenderAttributeByAttributeName() {
    tweety.setAttributeValue("gender", Gender.Unknown.toString());
    assertThat(tweety.getGender()).isEqualTo(Gender.Unknown);
  }

  @Test
  void removeGenderAttribute() {
    tweety.removeAttribute("gender");
    assertThat(tweety.getGender()).isNull();

    // gender is required, so the model is invalid without
    try {
      validateModel();
      fail("The model is invalid cause the gender of an animal is a required attribute.");
    }
    catch (Exception e) {
      assertThat(e).isInstanceOf(ModelValidationException.class);
    }

    // add gender to make model valid
    tweety.setGender(Gender.Female);
  }

  @Test
  void setAgeAttributeByHelper() {
    tweety.setAge(13);
    assertThat(tweety.getAge()).isEqualTo(13);
  }

  @Test
  void setAgeAttributeByAttributeName() {
    tweety.setAttributeValue("age", "23");
    assertThat(tweety.getAge()).isEqualTo(23);
  }

  @Test
  void removeAgeAttribute() {
    tweety.removeAttribute("age");
    assertThat(tweety.getAge()).isNull();
  }

  @Test
  void addRelationshipDefinitionsByHelper() {
    assertThat(tweety.getRelationshipDefinitions())
      .isNotEmpty()
      .hasSize(4)
      .containsOnly(hedwigRelationship, birdoRelationship, pluckyRelationship, fiffyRelationship);

    tweety.getRelationshipDefinitions().add(timmyRelationship);
    tweety.getRelationshipDefinitions().add(daisyRelationship);

    assertThat(tweety.getRelationshipDefinitions())
      .hasSize(6)
      .containsOnly(hedwigRelationship, birdoRelationship, pluckyRelationship, fiffyRelationship, timmyRelationship, daisyRelationship);
  }

  @Test
  void updateRelationshipDefinitionsByIdByHelper() {
    hedwigRelationship.setId("new-" + hedwigRelationship.getId());
    pluckyRelationship.setId("new-" + pluckyRelationship.getId());
    assertThat(tweety.getRelationshipDefinitions())
      .hasSize(4)
      .containsOnly(hedwigRelationship, birdoRelationship, pluckyRelationship, fiffyRelationship);
  }

  @Test
  void updateRelationshipDefinitionsByIdByAttributeName() {
    birdoRelationship.setAttributeValue("id", "new-" + birdoRelationship.getId(), true);
    fiffyRelationship.setAttributeValue("id", "new-" + fiffyRelationship.getId(), true);
    assertThat(tweety.getRelationshipDefinitions())
      .hasSize(4)
      .containsOnly(hedwigRelationship, birdoRelationship, pluckyRelationship, fiffyRelationship);
  }

  @Test
  void updateRelationshipDefinitionsByReplaceElements() {
    hedwigRelationship.replaceWithElement(timmyRelationship);
    pluckyRelationship.replaceWithElement(daisyRelationship);
    assertThat(tweety.getRelationshipDefinitions())
      .hasSize(4)
      .containsOnly(birdoRelationship, fiffyRelationship, timmyRelationship, daisyRelationship);
  }

  @Test
  void updateRelationshipDefinitionsByRemoveElements() {
    tweety.getRelationshipDefinitions().remove(birdoRelationship);
    tweety.getRelationshipDefinitions().remove(fiffyRelationship);
    assertThat(tweety.getRelationshipDefinitions())
      .hasSize(2)
      .containsOnly(hedwigRelationship, pluckyRelationship);
  }

  @Test
  void clearRelationshipDefinitions() {
    tweety.getRelationshipDefinitions().clear();
    assertThat(tweety.getRelationshipDefinitions()).isEmpty();
  }

  @Test
  void addRelationsDefinitionRefsByHelper() {
    assertThat(tweety.getRelationshipDefinitionRefs())
      .isNotEmpty()
      .hasSize(4)
      .containsOnly(hedwigRelationship, birdoRelationship, pluckyRelationship, fiffyRelationship);

    addRelationshipDefinition(tweety, timmyRelationship);
    addRelationshipDefinition(tweety, daisyRelationship);
    tweety.getRelationshipDefinitionRefs().add(timmyRelationship);
    tweety.getRelationshipDefinitionRefs().add(daisyRelationship);

    assertThat(tweety.getRelationshipDefinitionRefs())
      .isNotEmpty()
      .hasSize(6)
      .containsOnly(hedwigRelationship, birdoRelationship, pluckyRelationship, fiffyRelationship, timmyRelationship, daisyRelationship);
  }

  @Test
  void updateRelationshipDefinitionRefsByIdByHelper() {
    hedwigRelationship.setId("child-relationship");
    pluckyRelationship.setId("friend-relationship");
    assertThat(tweety.getRelationshipDefinitionRefs())
      .hasSize(4)
      .containsOnly(hedwigRelationship, birdoRelationship, pluckyRelationship, fiffyRelationship);
  }

  @Test
  void updateRelationshipDefinitionRefsByIdByAttributeName() {
    birdoRelationship.setAttributeValue("id", "birdo-relationship", true);
    fiffyRelationship.setAttributeValue("id", "fiffy-relationship", true);
    assertThat(tweety.getRelationshipDefinitionRefs())
      .hasSize(4)
      .containsOnly(hedwigRelationship, birdoRelationship, pluckyRelationship, fiffyRelationship);
  }

  @Test
  void updateRelationshipDefinitionRefsByReplaceElements() {
    hedwigRelationship.replaceWithElement(timmyRelationship);
    pluckyRelationship.replaceWithElement(daisyRelationship);
    assertThat(tweety.getRelationshipDefinitionRefs())
      .hasSize(4)
      .containsOnly(birdoRelationship, fiffyRelationship, timmyRelationship, daisyRelationship);
  }

  @Test
  void updateRelationshipDefinitionRefsByRemoveElements() {
    tweety.getRelationshipDefinitions().remove(birdoRelationship);
    tweety.getRelationshipDefinitions().remove(fiffyRelationship);
    assertThat(tweety.getRelationshipDefinitionRefs())
      .hasSize(2)
      .containsOnly(hedwigRelationship, pluckyRelationship);
  }

  @Test
  void updateRelationshipDefinitionRefsByRemoveIdAttribute() {
    birdoRelationship.removeAttribute("id");
    pluckyRelationship.removeAttribute("id");
    assertThat(tweety.getRelationshipDefinitionRefs())
      .hasSize(2)
      .containsOnly(hedwigRelationship, fiffyRelationship);
  }

  @Test
  void clearRelationshipDefinitionsRefs() {
    tweety.getRelationshipDefinitionRefs().clear();
    assertThat(tweety.getRelationshipDefinitionRefs()).isEmpty();
    // should not affect animal relationship definitions
    assertThat(tweety.getRelationshipDefinitions()).hasSize(4);
  }

  @Test
  void clearRelationshipDefinitionRefsByClearRelationshipDefinitions() {
    assertThat(tweety.getRelationshipDefinitionRefs()).isNotEmpty();
    tweety.getRelationshipDefinitions().clear();
    assertThat(tweety.getRelationshipDefinitions()).isEmpty();
    // should affect animal relationship definition refs
    assertThat(tweety.getRelationshipDefinitionRefs()).isEmpty();
  }

  @Test
  void addRelationshipDefinitionRefElementsByHelper() {
    assertThat(tweety.getRelationshipDefinitionRefElements())
      .isNotEmpty()
      .hasSize(4);

    addRelationshipDefinition(tweety, timmyRelationship);
    RelationshipDefinitionRef timmyRelationshipDefinitionRef = modelInstance.newInstance(RelationshipDefinitionRef.class);
    timmyRelationshipDefinitionRef.setTextContent(timmyRelationship.getId());
    tweety.getRelationshipDefinitionRefElements().add(timmyRelationshipDefinitionRef);

    addRelationshipDefinition(tweety, daisyRelationship);
    RelationshipDefinitionRef daisyRelationshipDefinitionRef = modelInstance.newInstance(RelationshipDefinitionRef.class);
    daisyRelationshipDefinitionRef.setTextContent(daisyRelationship.getId());
    tweety.getRelationshipDefinitionRefElements().add(daisyRelationshipDefinitionRef);

    assertThat(tweety.getRelationshipDefinitionRefElements())
      .isNotEmpty()
      .hasSize(6)
      .contains(timmyRelationshipDefinitionRef, daisyRelationshipDefinitionRef);
  }

  @Test
  void relationshipDefinitionRefElementsByTextContent() {
    Collection<RelationshipDefinitionRef> relationshipDefinitionRefElements = tweety.getRelationshipDefinitionRefElements();
    Collection<String> textContents = new ArrayList<String>();
    for (RelationshipDefinitionRef relationshipDefinitionRef : relationshipDefinitionRefElements) {
      String textContent = relationshipDefinitionRef.getTextContent();
      assertThat(textContent).isNotEmpty();
      textContents.add(textContent);
    }
    assertThat(textContents)
      .isNotEmpty()
      .hasSize(4)
      .containsOnly(hedwigRelationship.getId(), birdoRelationship.getId(), pluckyRelationship.getId(), fiffyRelationship.getId());
  }

  @Test
  void updateRelationshipDefinitionRefElementsByTextContent() {
    List<RelationshipDefinitionRef> relationshipDefinitionRefs = new ArrayList<RelationshipDefinitionRef>(tweety.getRelationshipDefinitionRefElements());

    addRelationshipDefinition(tweety, timmyRelationship);
    relationshipDefinitionRefs.get(0).setTextContent(timmyRelationship.getId());

    addRelationshipDefinition(daisy, daisyRelationship);
    relationshipDefinitionRefs.get(2).setTextContent(daisyRelationship.getId());

    assertThat(tweety.getRelationshipDefinitionRefs())
      .hasSize(4)
      .containsOnly(birdoRelationship, fiffyRelationship, timmyRelationship, daisyRelationship);
  }

  @Test
  void updateRelationshipDefinitionRefElementsByTextContentWithNamespace() {
    List<RelationshipDefinitionRef> relationshipDefinitionRefs = new ArrayList<RelationshipDefinitionRef>(tweety.getRelationshipDefinitionRefElements());

    addRelationshipDefinition(tweety, timmyRelationship);
    relationshipDefinitionRefs.get(0).setTextContent("tns:" + timmyRelationship.getId());

    addRelationshipDefinition(daisy, daisyRelationship);
    relationshipDefinitionRefs.get(2).setTextContent("tns:" + daisyRelationship.getId());

    assertThat(tweety.getRelationshipDefinitionRefs())
      .hasSize(4)
      .containsOnly(birdoRelationship, fiffyRelationship, timmyRelationship, daisyRelationship);
  }

  @Test
  void updateRelationshipDefinitionRefElementsByRemoveElements() {
    List<RelationshipDefinitionRef> relationshipDefinitionRefs = new ArrayList<RelationshipDefinitionRef>(tweety.getRelationshipDefinitionRefElements());
    tweety.getRelationshipDefinitionRefElements().remove(relationshipDefinitionRefs.get(1));
    tweety.getRelationshipDefinitionRefElements().remove(relationshipDefinitionRefs.get(3));
    assertThat(tweety.getRelationshipDefinitionRefs())
      .hasSize(2)
      .containsOnly(hedwigRelationship, pluckyRelationship);
  }

  @Test
  void clearRelationshipDefinitionRefElements() {
    tweety.getRelationshipDefinitionRefElements().clear();
    assertThat(tweety.getRelationshipDefinitionRefElements()).isEmpty();
    assertThat(tweety.getRelationshipDefinitionRefs()).isEmpty();
    // should not affect animal relationship definitions
    assertThat(tweety.getRelationshipDefinitions())
      .isNotEmpty()
      .hasSize(4);
  }

  @Test
  void clearRelationshipDefinitionRefElementsByClearRelationshipDefinitionRefs() {
    tweety.getRelationshipDefinitionRefs().clear();
    assertThat(tweety.getRelationshipDefinitionRefs()).isEmpty();
    assertThat(tweety.getRelationshipDefinitionRefElements()).isEmpty();
    // should not affect animal relationship definitions
    assertThat(tweety.getRelationshipDefinitions())
      .isNotEmpty()
      .hasSize(4);
  }

  @Test
  void clearRelationshipDefinitionRefElementsByClearRelationshipDefinitions() {
    tweety.getRelationshipDefinitions().clear();
    assertThat(tweety.getRelationshipDefinitionRefs()).isEmpty();
    assertThat(tweety.getRelationshipDefinitionRefElements()).isEmpty();
    // should affect animal relationship definitions
    assertThat(tweety.getRelationshipDefinitions()).isEmpty();
  }

  @Test
  void getBestFriends() {
    Collection<Animal> bestFriends = tweety.getBestFriends();

    assertThat(bestFriends)
      .isNotEmpty()
      .hasSize(2)
      .containsOnly(birdo, plucky);
  }

  @Test
  void addBestFriend() {
    tweety.getBestFriends().add(daisy);

    Collection<Animal> bestFriends = tweety.getBestFriends();

    assertThat(bestFriends)
      .isNotEmpty()
      .hasSize(3)
      .containsOnly(birdo, plucky, daisy);
  }

  @Test
  void removeBestFriendRef() {
    tweety.getBestFriends().remove(plucky);

    Collection<Animal> bestFriends = tweety.getBestFriends();

    assertThat(bestFriends)
      .isNotEmpty()
      .hasSize(1)
      .containsOnly(birdo);
  }

  @Test
  void clearBestFriendRef() {
    tweety.getBestFriends().clear();

    Collection<Animal> bestFriends = tweety.getBestFriends();

    assertThat(bestFriends)
      .isEmpty();
  }

  @Test
  void clearAndAddBestFriendRef() {
    tweety.getBestFriends().clear();

    Collection<Animal> bestFriends = tweety.getBestFriends();

    assertThat(bestFriends)
      .isEmpty();

    bestFriends.add(daisy);

    assertThat(bestFriends)
      .hasSize(1)
      .containsOnly(daisy);
  }
}

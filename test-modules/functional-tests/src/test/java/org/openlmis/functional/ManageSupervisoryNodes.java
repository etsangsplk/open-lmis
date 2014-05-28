/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.functional;

import org.openlmis.UiUtils.TestCaseHelper;
import org.openlmis.pageobjects.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static com.thoughtworks.selenium.SeleneseTestBase.assertFalse;
import static com.thoughtworks.selenium.SeleneseTestBase.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

public class ManageSupervisoryNodes extends TestCaseHelper {

  LoginPage loginPage;
  SupervisoryNodesPage supervisoryNodesPage;

  public static final String ADMIN = "admin";
  public static final String PASSWORD = "password";

  public Map<String, String> testData = new HashMap<String, String>() {{
    put(PASSWORD, "Admin123");
    put(ADMIN, "Admin123");
  }};

  @BeforeMethod(groups = {"admin"})
  public void setUp() throws InterruptedException, SQLException, IOException {
    super.setup();
    dbWrapper.insertFacilities("F10", "F11");
    loginPage = PageObjectFactory.getLoginPage(testWebDriver, baseUrlGlobal);
    supervisoryNodesPage = PageObjectFactory.getSupervisoryNodesPage(testWebDriver);
  }

  @Test(groups = {"admin"})
  public void testRightsNotPresent() throws SQLException {
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Node2", null);
    dbWrapper.insertSupervisoryNode("F10", "N3", "Node3", "N2");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateToUser();
    assertFalse(homePage.isSupervisoryNodeTabDisplayed());
    homePage.logout();

    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    homePage.navigateToUser();
    assertTrue(homePage.isSupervisoryNodeTabDisplayed());
    supervisoryNodesPage = homePage.navigateToSupervisoryNodes();

    assertEquals("Search supervisory node", supervisoryNodesPage.getSearchSupervisoryNodeLabel());
    assertTrue(supervisoryNodesPage.isAddNewButtonDisplayed());
    assertEquals("Supervisory node", supervisoryNodesPage.getSelectedSearchOption());
    assertTrue(supervisoryNodesPage.isSearchIconDisplayed());
  }

  @Test(groups = {"admin"})
  public void testSupervisoryNodeSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Node2", null);
    dbWrapper.insertSupervisoryNode("F10", "N3", "Node3", "N2");

    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    supervisoryNodesPage = homePage.navigateToSupervisoryNodes();

    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadSupervisoryNodes("QA_supervisoryNodes21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    homePage.navigateToSupervisoryNodes();
    assertEquals("Search supervisory node", supervisoryNodesPage.getSearchSupervisoryNodeLabel());
    searchNode("Approval Point");
    assertEquals("19 matches found for 'Approval Point'", supervisoryNodesPage.getNResultsMessage());
    searchNode("Ap");
    assertEquals("21 matches found for 'Ap'", supervisoryNodesPage.getNResultsMessage());

    verifyNumberOFPageLinksDisplayed(21, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyParentNameOrderOnPage(new String[]{"Approval Point 10", "Approval Point 3", "Approval Point 3", "approval Point 4", "approval Point 4",
      "Approval Point 5", "Approval Point 5", "Approval Point 5", "Approval Point 6", "approval Point 7"});
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Approval Point 13", "Approval Point 12", "Approval Point 21", "Approval Point 13",
      "Approval Point 5", "Approval Point 15", "Approval Point 20", "Approval Point 6", "Approval Point 8", "Approval Point 9"});

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyParentNameOrderOnPage(new String[]{"Approval Point 8", "Approval Point 8", "Approval Point 9", "Node1", "Node1", "Node1",
      "Node1", "Node1", "Node1", "Node3"});
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Approval Point 11", "Approval Point 13", "Approval Point 13", "Approval Point 10",
      "Approval Point 13", "Approval Point 3", "approval Point 7", "Aproval Point 3", "Aproval Point 4", "Approval Point 13"});

    navigateToNextPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(1);
    verifyParentNameOrderOnPage(new String[]{"Node3"});
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Approval Point 13"});

    navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyParentNameOrderOnPage(new String[]{"Approval Point 10", "Approval Point 3", "Approval Point 3", "approval Point 4", "approval Point 4",
      "Approval Point 5", "Approval Point 5", "Approval Point 5", "Approval Point 6", "approval Point 7"});
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Approval Point 13", "Approval Point 12", "Approval Point 21", "Approval Point 13",
      "Approval Point 5", "Approval Point 15", "Approval Point 20", "Approval Point 6", "Approval Point 8", "Approval Point 9"});

    navigateToLastPage();
    verifyPageNumberSelected(3);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(1);

    navigateToPreviousPage();
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(10);

    supervisoryNodesPage.closeSearchResults();
    assertFalse(supervisoryNodesPage.isSupervisoryNodeHeaderPresent());
  }

  @Test(groups = {"admin"})
  public void testSupervisoryNodeParentSearchSortAndPagination() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Node2", null);
    dbWrapper.insertSupervisoryNode("F10", "N3", "Node3", "N2");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    UploadPage uploadPage = homePage.navigateUploads();
    uploadPage.uploadSupervisoryNodes("QA_supervisoryNodes21.csv");
    uploadPage.verifySuccessMessageOnUploadScreen();

    supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
    assertEquals("Supervisory node", supervisoryNodesPage.getSelectedSearchOption());
    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeParentAsSearchOption();
    assertEquals("Supervisory node parent", supervisoryNodesPage.getSelectedSearchOption());

    searchNode("Approval Point");
    assertEquals("14 matches found for 'Approval Point'", supervisoryNodesPage.getNResultsMessage());
    searchNode("Ap");
    assertEquals("14 matches found for 'Ap'", supervisoryNodesPage.getNResultsMessage());

    verifyNumberOFPageLinksDisplayed(16, 10);
    verifyPageNumberLinksDisplayed();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyParentNameOrderOnPage(new String[]{"Approval Point 10", "Approval Point 3", "Approval Point 3", "Approval Point 3", "approval Point 4",
      "approval Point 4", "Approval Point 5", "Approval Point 5", "Approval Point 5", "Approval Point 6"});
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Approval Point 13", "Approval Point 12", "Approval Point 21", "Node3", "Approval Point 13",
      "Approval Point 5", "Approval Point 15", "Approval Point 20", "Approval Point 6", "Approval Point 8"});

    navigateToPage(2);
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(4);
    verifyParentNameOrderOnPage(new String[]{"approval Point 7", "Approval Point 8", "Approval Point 8", "Approval Point 9"});
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Approval Point 9", "Approval Point 11", "Approval Point 13", "Approval Point 13"});

    navigateToFirstPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
    verifyParentNameOrderOnPage(new String[]{"Approval Point 10", "Approval Point 3", "Approval Point 3", "Approval Point 3", "approval Point 4",
      "approval Point 4", "Approval Point 5", "Approval Point 5", "Approval Point 5", "Approval Point 6"});
    verifySupervisoryNodeNameOrderOnPage(new String[]{"Approval Point 13", "Approval Point 12", "Approval Point 21", "Node3", "Approval Point 13",
      "Approval Point 5", "Approval Point 15", "Approval Point 20", "Approval Point 6", "Approval Point 8"});

    navigateToLastPage();
    verifyPageNumberSelected(2);
    verifyNextAndLastPageLinksDisabled();
    verifyPreviousAndFirstPageLinksEnabled();
    verifyNumberOfLineItemsVisibleOnPage(4);

    navigateToPreviousPage();
    verifyPageNumberSelected(1);
    verifyNextAndLastPageLinksEnabled();
    verifyPreviousAndFirstPageLinksDisabled();
    verifyNumberOfLineItemsVisibleOnPage(10);
  }

  @Test(groups = {"admin"})
  public void testSupervisoryNodeParentSearchWhenNoResults() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    supervisoryNodesPage = homePage.navigateToSupervisoryNodes();

    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeParentAsSearchOption();
    searchNode("nod");
    assertTrue(supervisoryNodesPage.isNoResultMessageDisplayed());

    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeAsSearchOption();
    assertTrue(supervisoryNodesPage.isNoResultMessageDisplayed());

    dbWrapper.insertSupervisoryNode("F10", "N2", "Node2", null);
    supervisoryNodesPage.clickSearchIcon();
    assertTrue(supervisoryNodesPage.isOneResultMessageDisplayed());

    assertEquals("Supervisory Node Name", supervisoryNodesPage.getSupervisoryNodeHeader());
    assertEquals("Code", supervisoryNodesPage.getCodeHeader());
    assertEquals("Facility", supervisoryNodesPage.getFacilityHeader());
    assertEquals("Parent", supervisoryNodesPage.getParentHeader());

    assertEquals("Node2", supervisoryNodesPage.getSupervisoryNodeName(1));
    assertEquals("N2", supervisoryNodesPage.getSupervisoryNodeCode(1));
    assertEquals("Village Dispensary", supervisoryNodesPage.getFacility(1));
    assertEquals("", supervisoryNodesPage.getParent(1));

    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeParentAsSearchOption();
    supervisoryNodesPage.clickSearchIcon();
    testWebDriver.waitForAjax();
    assertTrue(supervisoryNodesPage.isNoResultMessageDisplayed());
  }

  @Test(groups = {"admin"})
  public void testAddNewSupervisoryNode() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Node2", null);
    dbWrapper.insertSupervisoryNode("F10", "N3", "Node3", "N2");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    supervisoryNodesPage = homePage.navigateToSupervisoryNodes();

    supervisoryNodesPage.clickAddNewButton();
    supervisoryNodesPage.enterSupervisoryNodeCodeValue("N4");
    supervisoryNodesPage.enterSupervisoryNodeNameValues("Node 4");
    supervisoryNodesPage.enterSupervisoryNodeDescriptionValue("This is Node 4");
    supervisoryNodesPage.enterSearchParentNodeParameter("Nod");

    supervisoryNodesPage.isSearchListDisplayed();
    assertEquals("Node1", supervisoryNodesPage.getParentNodeResult(1));
    assertEquals("Node2", supervisoryNodesPage.getParentNodeResult(2));
    assertEquals("Node3", supervisoryNodesPage.getParentNodeResult(3));
    supervisoryNodesPage.clickOnFirstFacilitySearchResultLink();

    assertTrue(supervisoryNodesPage.isClearSearchButtonIsVisible());
    supervisoryNodesPage.clickOnClearSearchResultButton();
    supervisoryNodesPage.enterSearchParentNodeParameter("Nod");
    supervisoryNodesPage.clickOnFirstParentSearchResult();

    searchAssociatedFacility("F10");
    supervisoryNodesPage.selectFirstFacilityToBeAssociated();

    supervisoryNodesPage.clickSaveButton();
    assertEquals("Supervisory Node \"Node 4\" created successfully", supervisoryNodesPage.getSuccessMessage());
    supervisoryNodesPage.clickViewHereLink();
    assertTrue(supervisoryNodesPage.isEditPageHeaderDisplayed());
    supervisoryNodesPage.clickCancelButton();

    searchNode("Node 4");
    assertTrue(supervisoryNodesPage.isOneResultMessageDisplayed());
    assertEquals("Node 4", supervisoryNodesPage.getSupervisoryNodeName(1));
    assertEquals("N4", supervisoryNodesPage.getSupervisoryNodeCode(1));
    assertEquals("Village Dispensary", supervisoryNodesPage.getFacility(1));

    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeParentAsSearchOption();
    supervisoryNodesPage.clickSearchIcon();
    assertTrue(supervisoryNodesPage.isNoResultMessageDisplayed());

    supervisoryNodesPage.clickAddNewButton();
    enterSupervisoryNodeDetails("N5", "Node 5", "This is Node 5", "Node 4", "F10");
    supervisoryNodesPage.clickSaveButton();
    testWebDriver.refresh();

    supervisoryNodesPage.clickSearchOptionButton();
    supervisoryNodesPage.selectSupervisoryNodeParentAsSearchOption();
    searchNode("Node 4");
    assertEquals("Node 5", supervisoryNodesPage.getSupervisoryNodeName(1));
    assertEquals("N5", supervisoryNodesPage.getSupervisoryNodeCode(1));
    assertEquals("Village Dispensary", supervisoryNodesPage.getFacility(1));
  }

  //@Test(groups = {"admin"})
  public void testValidationsOnAddNewSupervisoryNode() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Node1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Node2", null);
    dbWrapper.insertSupervisoryNode("F10", "N3", "Node3", "N2");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    supervisoryNodesPage = homePage.navigateToSupervisoryNodes();

    supervisoryNodesPage.clickAddNewButton();
    enterSupervisoryNodeDetails("N1", "Node4", "This is Node 4", "Node2", "F10");
    supervisoryNodesPage.clickSaveButton();
    assertEquals("Duplicate Supervisory Node Code", supervisoryNodesPage.getSaveMessage());

    supervisoryNodesPage.enterSupervisoryNodeCodeValue("N4");
    supervisoryNodesPage.enterSearchParentNodeParameter("parent");
    supervisoryNodesPage.clickSaveButton();
    supervisoryNodesPage.clickViewHereLink();
    assertEquals("", supervisoryNodesPage.getParentNode());
  }

  @Test(groups = {"admin"})
  public void testSaveWithoutEnteringValuesInSupervisoryNode() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    supervisoryNodesPage = homePage.navigateToSupervisoryNodes();

    supervisoryNodesPage.clickAddNewButton();
    supervisoryNodesPage.clickSaveButton();
    assertEquals("There are some errors in the form. Please resolve them.", supervisoryNodesPage.getSaveMessage());
    supervisoryNodesPage.clickCancelButton();

    supervisoryNodesPage.isAddNewButtonDisplayed();
    supervisoryNodesPage.isSearchIconDisplayed();
  }

  @Test(groups = {"admin"})
  public void testDisabledFacilityNotVisible() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));

    supervisoryNodesPage = homePage.navigateToSupervisoryNodes();
    supervisoryNodesPage.clickAddNewButton();
    dbWrapper.updateFieldValue("facilities", "enabled", "false", "code", "F10");

    searchAssociatedFacility("F10");
    assertEquals("No matches found for 'F10'", supervisoryNodesPage.getNoFacilityResultMessage());
    supervisoryNodesPage.closeSearchResults();
    assertEquals("", supervisoryNodesPage.getSearchFacilityText());
  }

  @Test(groups = {"admin"})
  public void testUpdateSupervisoryNode() throws SQLException {
    dbWrapper.assignRight("Admin", "MANAGE_SUPERVISORY_NODE");
    dbWrapper.insertSupervisoryNode("F10", "N1", "Super1", null);
    dbWrapper.insertSupervisoryNode("F11", "N2", "Super2", null);
    HomePage homePage = loginPage.loginAs(testData.get(ADMIN), testData.get(PASSWORD));
    supervisoryNodesPage = homePage.navigateToSupervisoryNodes();

    searchNode("sup");
    supervisoryNodesPage.clickOnNode(1);
    enterSupervisoryNodeDetails("N1", "Node1", "node 1", "sup", "Village Dispensary");
    supervisoryNodesPage.clickSaveButton();
    assertEquals("Supervisory Node \"Node1\" updated successfully", supervisoryNodesPage.getSuccessMessage());
    supervisoryNodesPage.clickViewHereLink();
    assertTrue(supervisoryNodesPage.isEditPageHeaderDisplayed());
    supervisoryNodesPage.clickCancelButton();

    searchNode("super1");
    assertTrue(supervisoryNodesPage.isNoResultMessageDisplayed());

    searchNode("Node1");
    assertTrue(supervisoryNodesPage.isOneResultMessageDisplayed());
    assertEquals("Super2", supervisoryNodesPage.getParent(1));
    assertEquals("Village Dispensary", supervisoryNodesPage.getFacility(1));

    supervisoryNodesPage.closeSearchResults();
    assertEquals("", supervisoryNodesPage.getSearchSupervisoryNodeText());
  }

  public void searchNode(String searchParameter) {
    SupervisoryNodesPage supervisoryNodesPage = PageObjectFactory.getSupervisoryNodesPage(testWebDriver);
    supervisoryNodesPage.enterSearchParameter(searchParameter);
    supervisoryNodesPage.clickSearchIcon();
    testWebDriver.waitForAjax();
  }

  public void enterSupervisoryNodeDetails(String code, String name, String description, String parentNode, String facilityCodeOrName) {
    supervisoryNodesPage.enterSupervisoryNodeCodeValue(code);
    supervisoryNodesPage.enterSupervisoryNodeNameValues(name);
    supervisoryNodesPage.enterSupervisoryNodeDescriptionValue(description);
    supervisoryNodesPage.enterSearchParentNodeParameter(parentNode);
    supervisoryNodesPage.clickOnFirstFacilitySearchResultLink();
    searchAssociatedFacility(facilityCodeOrName);
    supervisoryNodesPage.selectFirstFacilityToBeAssociated();
  }


  public void searchAssociatedFacility(String facilityName) {
    supervisoryNodesPage.clickAssociatedFacilityField();
    supervisoryNodesPage.searchFacilityToBeAssociated(facilityName);
    supervisoryNodesPage.clickSearchIcon();
  }

  private void verifySupervisoryNodeNameOrderOnPage(String[] nodeNames) {
    for (int i = 1; i < nodeNames.length; i++) {
      assertEquals(nodeNames[i - 1], supervisoryNodesPage.getSupervisoryNodeName(i));
    }
  }

  private void verifyParentNameOrderOnPage(String[] parentNames) {
    for (int i = 1; i < parentNames.length; i++) {
      assertEquals(parentNames[i - 1], supervisoryNodesPage.getParent(i));
    }
  }

  private void verifyNumberOfLineItemsVisibleOnPage(int numberOfLineItems) {
    assertEquals(numberOfLineItems, testWebDriver.getElementsSizeByXpath("//table[@id='supervisoryNodesTable']/tbody/tr"));
  }

  @AfterMethod(groups = {"admin"})
  public void tearDown() throws SQLException {
    HomePage homePage = PageObjectFactory.getHomePage(testWebDriver);
    homePage.logout(baseUrlGlobal);
    dbWrapper.deleteData();
    dbWrapper.closeConnection();
  }
}
import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testng.keyword.TestNGBuiltinKeywords as TestNGKW
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.keyword.internal.WebUIAbstractKeyword
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows

import generalactions.generalActions
import internal.GlobalVariable
import mid.framework.generalAction
import utility.Utility
import mid.framework.filter
import mid.framework.FilterScopes
import mid.framework.FilterType

import org.openqa.selenium.Keys as Keys

/**************************
 * Test Case Pre-Conditions
 **************************/
def GeneralAction = new generalAction()
def midFilter = new filter()
def randomString = GeneralAction.generateRandomString(8,false) //CustomKeywords.'mid.framework.GeneralAction.generateRandomString'()
def randomPhoneNumber = ('5' + (Math.random() * 90000000 + 10000000).toLong())
def utility = new Utility()
WebUI.callTestCase(findTestCase('BE/MID/Customers/All Customers/Open Customer Page'), [:], FailureHandling.STOP_ON_FAILURE)

/**************************
 * Test Case Steps
 **************************/

GeneralAction.clickOnDependOnName('Add New Customer')

GeneralAction.verifyHeaderText('Add New Customer')

GeneralAction.setTextToInputFieldDependOnName('first name', randomString )
//
GeneralAction.setTextToInputFieldDependOnName('email', randomString+"@example.com")
//
GeneralAction.setTextToInputFieldDependOnName('last name', randomString)
//
GeneralAction.setTextToInputFieldDependOnName('Mobile Number', randomPhoneNumber)

GeneralAction.setDate('Date of Birth','1986-05-10')

GeneralAction.toggleFieldDependOnName('Allow remote shopping assistance')

GeneralAction.toggleFieldDependOnName('Sales Notification')

GeneralAction.toggleFieldDependOnName('New Arrivals Notification')

GeneralAction.toggleFieldDependOnName('Delivery Status Changes Notification')

GeneralAction.selectOptionDependOnName('Associate to Website','Snaptec')

GeneralAction.selectOptionDependOnName('Group','New Group')

GeneralAction.selectOptionDependOnName('Send Welcome Email From','Select store view')

GeneralAction.selectOptionDependOnName('Gender','Male')

GeneralAction.clickOnDependOnName('Save Customer')

/**************************
 * Test Case Verifications
 **************************/
CustomKeywords.'generalactions.generalActions.waiteSpinnerToHide'()
List<Map<String, Object>> filters = [
	['filterName': 'Customer Name', 'value': randomString, 'filterType':FilterType.TEXT_INPUT],
	['filterName': 'Email', 'value': randomString+"@example.com",'filterType':FilterType.TEXT_INPUT],
	['filterName': 'Phone', 'value': randomPhoneNumber,'filterType':FilterType.TEXT_INPUT]
]
midFilter.applyFilter(filters)
//TODO Verification filter results

List<Map<String, Object>> filterResults = [
	['filterScope':FilterScopes.FILTER_SELECTED_ITEMS, 'value':[randomString,randomString+"@example.com",randomPhoneNumber]],
	['filterScope':FilterScopes.FILTER_SELECTED_ITEMS, 'value':[randomString,randomString+"@example.com",randomPhoneNumber]]
	]
midFilter.verifyFiltersApplied(filterResults)
//WebUI.delay(10)

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
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys
import mid.framework.generalAction
import mid.framework.item
import utility.Utility
import mid.framework.filter
import mid.framework.FilterScopes
//import mid.framework.FilterScopes
import mid.framework.FilterType

def GeneralAction = new generalAction()
def midFilter = new filter()
def utility = new Utility()
def items = new item()
WebUI.callTestCase(findTestCase('BE/MID/Sales/Orders/Open Order Page'), [:], FailureHandling.STOP_ON_FAILURE)

List<Map<String, Object>> filters = [
	['filterName': 'Status', 'value': 'Pending', 'filterType':FilterType.MULTISELECT]
]
midFilter.applyFilter(filters)


List<Map<String, Object>> filterResults = [
	['filterScope':FilterScopes.FILTER_RESULTS, 'value':""]
	]
midFilter.verifyFiltersApplied(filterResults)

items.editItem(false, true, 0)

GeneralAction.selectTabDependOnName("Invoices")

if(midFilter.verifyNoResults()) {
	items.selectMoreActionsOption("Invoice")
	//GeneralAction.clickOnDependOnName('Update Qty\'s')
	//GeneralAction.verifyMessagePresent("Update total success")
	GeneralAction.clickOnDependOnName("Submit Invoice")
	
	//check all invoice headersand sub-headers
	def invoiceTitle = WebUI.getText(findTestObject("Object Repository/Mid/titles"), FailureHandling.OPTIONAL)
	WebUI.verifyMatch(invoiceTitle, /Invoice # \d+/, true)
	GeneralAction.verifyHeaderText('Order & Account Information')
	GeneralAction.verifyHeaderText('Invoice Total')
	GeneralAction.verifyHeaderText('Address Information')
	GeneralAction.verifyHeaderText('Invoice Comments')
	GeneralAction.verifyHeaderText('Billing Address')
	GeneralAction.verifyHeaderText('Shipping Address')
	GeneralAction.verifyHeaderText('Payment & Shipping Method')
	GeneralAction.verifyHeaderText('Payment Information')
	GeneralAction.verifyHeaderText('Shipping & Handling Information')
	
}
WebUI.delay(20)
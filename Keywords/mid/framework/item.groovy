package mid.framework

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import org.openqa.selenium.WebElement

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.webui.keyword.internal.WebUIAbstractKeyword
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.utils.CustomLogger

import internal.GlobalVariable
import mid.framework.generalAction
import mid.framework.pagination
import utility.Utility


public class item {
	def gnUtility = new Utility()
	def p = new pagination()
	@Keyword
	def editItem(boolean isView = false, boolean isRandom = false, int incrementID = 0) {
		def itemsList = generalAction.createTestObject("//div[contains(@class,'row-table-custom')]/div[starts-with(@class,'styles_list__')]//div[starts-with(@class,'styles_content__')]")
		def itemNumberTitle = generalAction.createTestObject("//span[contains(@class, 'styles_title') and contains(text(),'#')]")
		def itemTitle = generalAction.createTestObject("//span[contains(@class, 'styles_title')]")
		def item
		WebUI.waitForElementVisible(itemsList, 10)

		if (isRandom) {
			int randomNumber = (Math.random() * 20).toInteger() + 1
			item = generalAction.createTestObject("//div[@id='table-with-scroll']/div[contains(@class, 'row-table-custom')]["+randomNumber+"]//*[contains(@class, 'feather-edit')] | //div[@id='table-with-scroll']/div[contains(@class,'styles_container')]["+randomNumber+"]//*[contains(@class, 'feather-edit')]")
		} else {
			item = generalAction.createTestObject("//div[@id='table-with-scroll']/div[contains(@class, 'row-table-custom')]["+incrementID+"]//*[contains(@class, 'feather-edit')] | //div[@id='table-with-scroll']/div[contains(@class,'styles_container')]["+incrementID+"]//*[contains(@class, 'feather-edit')]")
		}

		WebUI.waitForElementVisible(item, 10)
		WebUI.click(item)
		boolean isItemNumberTitlePresent = WebUI.verifyElementPresent(itemNumberTitle, 10, FailureHandling.OPTIONAL)
		boolean isItemTitlePresent = WebUI.verifyElementPresent(itemTitle, 10, FailureHandling.OPTIONAL)

		if(isItemNumberTitlePresent) {
			// Wait for the itemNumberTitle to be visible
			WebUI.waitForElementVisible(itemNumberTitle, 10)
			// Retrieve the text from the element
			def itemNumberText = WebUI.getText(itemNumberTitle)
			// Check if the text matches the expected format
			def myRegex = ~/^\w+ # \d+$/
			if (!myRegex.matcher(itemNumberText).matches()) {
				println("Item number title did not match the expected format.")
			}
		}else if (isItemTitlePresent) {
			WebUI.waitForElementVisible(itemTitle, 10)
		}else {
		}
	}

	@Keyword
	def selectItem(boolean isRandom = false, List<Integer> incrementID = []) {
		def itemsList = generalAction.createTestObject("//div[contains(@class,'row-table-custom')]/div[starts-with(@class,'styles_list__')]//div[starts-with(@class,'styles_content__')]")
		def itemNumberTitle = generalAction.createTestObject("//span[contains(@class, 'styles_title') and contains(text(),'#')]")
		def item
		WebUI.waitForElementVisible(itemsList, 10)

		def ItemsNum = p.getItemsNum()
		if (isRandom) {
			while (ItemsNum > 0) {
				item = generalAction.createTestObject("//div[@id='table-with-scroll']/div[contains(@class, 'row-table-custom')]["+ItemsNum+"]//*[@type = 'checkbox']")
				WebUI.waitForElementVisible(item, 10)
				WebUI.click(item)
				ItemsNum-=2
			}
		} else if(incrementID.size() > 0) {
			incrementID.each { id ->
				item = generalAction.createTestObject("//div[@id='table-with-scroll']/div[contains(@class, 'row-table-custom')][" + id + "]//*[@type = 'checkbox']")
				WebUI.waitForElementVisible(item, 10)
				WebUI.click(item)
			}
		}
	}

	@Keyword
	def exportItems(String text = 'CSV') {
		//WebUI.click(findTestObject('Object Repository/Mid/items/Export Button'))
		CustomLogger.logPassed("Starting the export cycle")
		TestObject exportBtn = clickExportButton()
		if(WebUI.waitForElementPresent(gnUtility.extendTestObjectXPath(exportBtn, '/following-sibling::div[@data-toggle="dropdown"]'), 1, FailureHandling.OPTIONAL)) {
			CustomLogger.logPassed("Start Exporting type: ${text}")
			def exportType = generalAction.createTestObject("//span[text()='"+text+"']")
			CustomLogger.logPassed("Clicking on exporting type ${text}")
			WebUI.click(exportType)
		}
		CustomLogger.logPassed("Exporting finished")
	}

	@Keyword
	static TestObject clickExportButton() {
		/**
		 * Click Export button - works across all pages
		 */
		TestObject exportButton = findTestObject('Object Repository/Mid/items/Export Button')
		CustomLogger.logPassed("Waiting Export Element to be clickable")
		WebUI.waitForElementClickable(exportButton, 2)
		CustomLogger.logPassed("Clicking on Export Button")
		WebUI.click(exportButton)
		CustomLogger.logPassed("Clicked Export button successfully")
		return exportButton
	}

	@Keyword
	static TestObject clickMoreActionsButton() {
		/**
		 * Click More Actions button to open dropdown
		 */
		TestObject moreActionsButton = findTestObject("Object Repository/Mid/items/More Actions btn")
		CustomLogger.logPassed("Clicking on More Actions Button")
		WebUI.click(moreActionsButton)
		CustomLogger.logPassed("Clicked successfully and returning the object")
		return moreActionsButton
	}

	@Keyword
	def selectMoreActionsOption(String optionText) {
		/**
		 * Select an option from More Actions dropdown
		 * @param optionText - The text of the option to select
		 */
		// First click to open dropdown
		TestObject moreActions = clickMoreActionsButton();
		if(WebUI.waitForElementPresent(gnUtility.extendTestObjectXPath(moreActions, '/following-sibling::div[@data-toggle="dropdown"]'), 1, FailureHandling.OPTIONAL)) {
			CustomLogger.logPassed("Start Action type: ${optionText}")
			def moreActionType = gnUtility.extendTestObjectXPath(moreActions, "/following-sibling::div[@data-toggle='dropdown']/div/span[text()='${optionText}']")
			CustomLogger.logPassed("Clicking on Action type ${optionText}")
			WebUI.click(moreActionType)
			CustomLogger.logPassed("Clicking on action type ${optionText} is finished")
		}else {
			CustomLogger.logError("No more options to click")
		}
	}

	@Keyword
	def checkItemListAppeared(def timeOut = 10) {
		CustomLogger.logInfo("Checking the Item list is appearing or not")
		def itemsList = generalAction.createTestObject("//div[contains(@class,'row-table-custom')]/div[starts-with(@class,'styles_list__')]//div[starts-with(@class,'styles_content__')]")
		def noResultsFound = generalAction.createTestObject("//div[@id='table-with-scroll']/span[starts-with(@class,'styles_noRecords__')]")
		if(WebUI.waitForElementVisible(itemsList, timeOut)) {
			CustomLogger.logPassed("Item List are appeared")
		} else if(WebUI.waitForElementVisible(noResultsFound, timeOut)) {
			CustomLogger.logWarning("There is no results found")
		} else {
			CustomLogger.logError("error on results")
		}
	}
}

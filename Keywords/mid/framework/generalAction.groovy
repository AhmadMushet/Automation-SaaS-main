package mid.framework;

import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import static com.kms.katalon.core.testobject.ObjectRepository.findWindowsObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.checkpoint.Checkpoint
import com.kms.katalon.core.cucumber.keyword.CucumberBuiltinKeywords as CucumberKW
import com.kms.katalon.core.exception.StepFailedException
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testcase.TestCase
import com.kms.katalon.core.testdata.TestData
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.common.WebUiCommonHelper
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.utils.CustomLogger

import generalactions.generalActions

import com.kms.katalon.core.testobject.ConditionType
import internal.GlobalVariable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

import org.openqa.selenium.WebElement

public class generalAction {
	// Constants for timeout and retry configuration
	private static final int DEFAULT_TIMEOUT = 10
	private static final int ELEMENT_WAIT_TIMEOUT = 5
	private static final int MAX_RETRY_ATTEMPTS = 3
	def generalActions = new generalActions()
	@Keyword
	def setTextToInputFieldDependOnName(String fieldName, String fieldData,boolean isItTo=false) {
		CustomLogger.logInfo("Attempting to set text '${fieldData}' to field '${fieldName}'")

		if (!fieldName || !fieldData) {
			CustomLogger.logError("Field name or field data is null/empty. FieldName: '${fieldName}', FieldData: '${fieldData}'")
			throw new IllegalArgumentException("Field name and field data cannot be null or empty")
		}

		//def inputField = generalAction.createTestObject("//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '"+fieldName.toLowerCase()+"']/parent::div//input | //*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '"+fieldName.toLowerCase()+"']/parent::span/parent::*//input")
		//WebUI.setText(inputField, fieldData)

		try {
			int result = isItTo ? 2 : 1
			def inputField = createTestObjectWithMultipleSelectors(fieldName.toLowerCase(), [
				"//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '${fieldName.toLowerCase()}']/parent::div//input[${result}]",
				"//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '${fieldName.toLowerCase()}']/parent::span/parent::*//input",
				"//*[@placeholder and translate(@placeholder, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '${fieldName.toLowerCase()}']",
				"//label[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '${fieldName.toLowerCase()}']/following-sibling::input"
			])

			WebUI.waitForElementVisible(inputField, ELEMENT_WAIT_TIMEOUT)
			WebUI.clearText(inputField)
			WebUI.setText(inputField, fieldData)

			// Verify text was set correctly
			String actualText = WebUI.getAttribute(inputField, 'value')
			if (actualText == fieldData) {
				CustomLogger.logPassed("Successfully set text '${fieldData}' to field '${fieldName}'")
			} else {
				CustomLogger.logWarning("Text verification failed. Expected: '${fieldData}', Actual: '${actualText}'")
			}
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to set text to field '${fieldName}': ${e.getMessage()}")
			throw new StepFailedException("Failed to set text to field '${fieldName}': ${e.getMessage()}")
		}
	}

	@Keyword
	def setDate(String fieldName,String dateValue=LocalDate.now().toString(),boolean isItTo=false) {
		//		String script = "document.evaluate('//input[@name=\"dob\"]', document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.value = '2024-12-30';"
		//		WebUI.executeJavaScript(script, null)
		//def inputField = generalAction.createTestObject("//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = 'date of birth']/parent::div//input")
		//WebUI.sendKeys(inputField, dateValue)

		CustomLogger.logInfo("Attempting to set date: ${dateValue}")

		try {
			// Validate date format
			if (!isValidDateFormat(dateValue)) {
				CustomLogger.logWarning("Invalid date format detected: ${dateValue}. Converting to standard format.")
				dateValue = convertToStandardDateFormat(dateValue)
			}
			int result = isItTo ? 2 : 1
			TestObject inputField = createTestObjectWithMultipleSelectors(fieldName.toLowerCase(), [
				"(//span[text() = '${fieldName}']/parent::div//child::input[@type='date'])[${result.toString()}]"
			])

			WebUI.waitForElementVisible(inputField, ELEMENT_WAIT_TIMEOUT)
			//WebUI.clearText(inputField)
			//WebUI.setText(inputField, dateValue)
			WebElement dateElement = WebUiCommonHelper.findWebElement(inputField, ELEMENT_WAIT_TIMEOUT)
			WebUI.executeJavaScript('''
					var element = arguments[0];
					element.value = "''' + dateValue + '''";
					element.dispatchEvent(new Event("input", { bubbles: true }));
					element.dispatchEvent(new Event("change", { bubbles: true }));
					element.dispatchEvent(new Event("blur", { bubbles: true }));
					''', Arrays.asList(dateElement))

			CustomLogger.logPassed("Successfully set date: ${dateValue}")
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to set date '${dateValue}': ${e.getMessage()}")
			throw new StepFailedException("Failed to set date: ${e.getMessage()}")
		}
	}

	@Keyword
	def clickOnDependOnName(String fieldName) {
		//def fieldObject = generalAction.createTestObject("//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '"+fieldName.toLowerCase()+"']")
		//WebUI.click(fieldObject)

		CustomLogger.logInfo("Attempting to click on element with name: '${fieldName}'")

		if (!fieldName) {
			CustomLogger.logError("Field name is null or empty")
			throw new IllegalArgumentException("Field name cannot be null or empty")
		}

		def retryCount = 0
		def success = false

		while (retryCount < MAX_RETRY_ATTEMPTS && !success) {
			try {
				def fieldObject = createTestObjectWithMultipleSelectors(fieldName.toLowerCase(), [
					"//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '${fieldName.toLowerCase()}']",
					"//button[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '${fieldName.toLowerCase()}']",
					"//a[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '${fieldName.toLowerCase()}']",
					"//*[@title and translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '${fieldName.toLowerCase()}']"
				])

				WebUI.waitForElementClickable(fieldObject, ELEMENT_WAIT_TIMEOUT)
				WebUI.scrollToElement(fieldObject, ELEMENT_WAIT_TIMEOUT)
				WebUI.click(fieldObject)
				generalActions.waiteSpinnerToHide()
				success = true
				CustomLogger.logPassed("Successfully clicked on element '${fieldName}'")
			} catch (Exception e) {
				retryCount++
				CustomLogger.logWarning("Attempt ${retryCount} failed to click '${fieldName}': ${e.getMessage()}")

				if (retryCount >= MAX_RETRY_ATTEMPTS) {
					CustomLogger.logFailed("Failed to click on element '${fieldName}' after ${MAX_RETRY_ATTEMPTS} attempts")
					throw new StepFailedException("Failed to click on element '${fieldName}': ${e.getMessage()}")
				}

				WebUI.delay(0.5) // Wait 0.5 second before retry
			}
		}
	}

	@Keyword
	def toggleFieldDependOnName(String fieldName, boolean enable = true) {
		//def fieldObject = generalAction.createTestObject("//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '"+fieldName.toLowerCase()+"']/parent::div//label")
		//WebUI.click(fieldObject)

		String action = enable ? "enable" : "disable"
		CustomLogger.logInfo("Attempting to ${action} field: '${fieldName}'")

		try {
			def fieldObject = createTestObjectWithMultipleSelectors(fieldName.toLowerCase(), [
				"//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '${fieldName.toLowerCase()}']/parent::div//label",
				"//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '${fieldName.toLowerCase()}']/parent::*//input[@type='checkbox']",
				"//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '${fieldName.toLowerCase()}']/parent::*//input[@type='radio']"
			])

			WebUI.waitForElementVisible(fieldObject, ELEMENT_WAIT_TIMEOUT)

			// Check current state
			boolean isCurrentlyChecked = WebUI.getAttribute(fieldObject, 'checked') != null

			if ((enable && !isCurrentlyChecked) || (!enable && isCurrentlyChecked)) {
				WebUI.click(fieldObject)
				CustomLogger.logPassed("Successfully ${action}d field '${fieldName}'")
			} else {
				CustomLogger.logInfo("Field '${fieldName}' is already in desired state (${enable ? 'enabled' : 'disabled'})")
			}
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to ${action} field '${fieldName}': ${e.getMessage()}")
			throw new StepFailedException("Failed to ${action} field '${fieldName}': ${e.getMessage()}")
		}
	}

	@Keyword
	def selectOptionDependOnName(String fieldName, String optionLabel) {
		//def fieldObject = generalAction.createTestObject("//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '"+fieldName.toLowerCase()+"']/../..//select")
		//WebUI.selectOptionByLabel(fieldObject, OptionLabel, false)

		CustomLogger.logInfo("Attempting to select option '${optionLabel}' from field '${fieldName}'")

		if (!fieldName || !optionLabel) {
			CustomLogger.logError("Field name or option label is null/empty")
			throw new IllegalArgumentException("Field name and option label cannot be null or empty")
		}

		try {
			def fieldObject = createTestObjectWithMultipleSelectors(fieldName.toLowerCase(), [
				"//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '${fieldName.toLowerCase()}']/../..//select",
				"//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '${fieldName.toLowerCase()}']/parent::*//select",
				"//label[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '${fieldName.toLowerCase()}']/following-sibling::select"
			])

			WebUI.waitForElementVisible(fieldObject, ELEMENT_WAIT_TIMEOUT)
			WebUI.selectOptionByLabel(fieldObject, optionLabel, false)

			// Verify selection
			String selectedOption = WebUI.getAttribute(fieldObject, 'value')
			CustomLogger.logPassed("Successfully selected option '${optionLabel}' from field '${fieldName}'. Selected value: '${selectedOption}'")
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to select option '${optionLabel}' from field '${fieldName}': ${e.getMessage()}")
			throw new StepFailedException("Failed to select option from dropdown: ${e.getMessage()}")
		}
	}
	@Keyword
	def selectTabDependOnName(String fieldName) {
		//def fieldObject = generalAction.createTestObject("//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '"+fieldName.toLowerCase()+"']/../..//select")
		//WebUI.selectOptionByLabel(fieldObject, OptionLabel, false)

		CustomLogger.logInfo("Attempting to select tab -> '${fieldName}'")

		if (!fieldName) {
			CustomLogger.logError("Tab Name is null/empty")
			throw new IllegalArgumentException("Field name and option label cannot be null or empty")
		}

		try {
			def tabObject = createTestObjectWithMultipleSelectors(fieldName.toLowerCase(), [
				"//*[translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') = '${fieldName.toLowerCase()}']/parent::div[starts-with(@class,'styles_titleTab__')]/parent::div[starts-with(@class,'styles_tabClass__')]"
			])

			WebUI.waitForElementVisible(tabObject, ELEMENT_WAIT_TIMEOUT)
			WebUI.enhancedClick(tabObject)

			CustomLogger.logPassed("Successfully selected tab -> '${fieldName}'")
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to select tab -> '${fieldName}': ${e.getMessage()}")
			throw new StepFailedException("Failed to select option from dropdown: ${e.getMessage()}")
		}
	}
	@Keyword
	def verifyHeaderText(String expectedText) {
		//def headerObject = generalAction.createTestObject("//*[local-name() = 'h1'or local-name() = 'h2'or local-name() = 'h3'or local-name() = 'h4' or local-name() = 'span'][text()='"+expectedText+"']")
		//WebUI.verifyElementText(headerObject, expectedText)

		CustomLogger.logInfo("Verifying header text: '${expectedText}'")

		try {
			def headerObject = createTestObjectWithMultipleSelectors(expectedText, [
				"//*[local-name() = 'h1' or local-name() = 'h2' or local-name() = 'h3' or local-name() = 'h4' or local-name() = 'h5' or local-name() = 'h6'][text()='${expectedText}']",
				"//*[local-name() = 'span' or local-name() = 'div'][contains(@class, 'header') or contains(@class, 'title')][text()='${expectedText}']",
				"//*[contains(@class, 'heading')][text()='${expectedText}']"
			])

			WebUI.waitForElementVisible(headerObject, ELEMENT_WAIT_TIMEOUT)
			WebUI.verifyElementText(headerObject, expectedText)

			CustomLogger.logPassed("Successfully verified header text: '${expectedText}'")
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to verify header text '${expectedText}': ${e.getMessage()}")
			throw new StepFailedException("Header verification failed: ${e.getMessage()}")
		}
	}

	@Keyword
	def verifyMessagePresent(String text, int timeoutSeconds = 5) {
		//def itemNumberTitle = generalAction.createTestObject("//*[contains(@class,'toast-notifications') and text()='"+text+"'] | //*[contains(@class,'styles_message')]//*[text()='"+text+"']")
		//WebUI.verifyElementPresent(itemNumberTitle, 5)

		CustomLogger.logInfo("Verifying message is present: '${text}' (timeout: ${timeoutSeconds}s)")

		try {
			def messageObject = createTestObjectWithMultipleSelectors(text, [
				"//*[contains(@class,'toast-notifications') and text()='${text}']",
				"//*[contains(@class,'styles_message')]//*[text()='${text}']",
				"//*[contains(@class,'alert') or contains(@class,'notification') or contains(@class,'message')]//*[text()='${text}']",
				"//*[contains(@class,'success') or contains(@class,'error') or contains(@class,'warning')]//*[text()='${text}']"
			])

			boolean isPresent = WebUI.waitForElementPresent(messageObject, timeoutSeconds, FailureHandling.OPTIONAL)

			if (isPresent) {
				CustomLogger.logPassed("Successfully verified message is present: '${text}'")
			} else {
				CustomLogger.logFailed("Message not found within ${timeoutSeconds} seconds: '${text}'")
				throw new StepFailedException("Expected message not found: '${text}'")
			}
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to verify message presence '${text}': ${e.getMessage()}")
			throw new StepFailedException("Message verification failed: ${e.getMessage()}")
		}
	}

	@Keyword
	def generateRandomString(int length = 8, boolean includeNumbers = true /*, boolean includeSpecialChars = false*/) {
		//def randomString = UUID.randomUUID().toString().substring(0, 8)
		//return randomString

		CustomLogger.logInfo("Generating random string with length: ${length}")

		try {
			String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			if (includeNumbers) chars += "0123456789"
			//if (includeSpecialChars) chars += "!@#$%^&*"

			Random random = new Random()
			StringBuilder result = new StringBuilder()

			for (int i = 0; i < length; i++) {
				result.append(chars.charAt(random.nextInt(chars.length())))
			}

			String randomString = result.toString()
			CustomLogger.logInfo("Generated random string: ${randomString}")
			return randomString
		} catch (Exception e) {
			CustomLogger.logError("Failed to generate random string: ${e.getMessage()}")
			return UUID.randomUUID().toString().substring(0, length) // Fallback
		}
	}

	public static TestObject createTestObject(String xpath) {
		//TestObject testObject = new TestObject("dynamicTestObject_" + xpath.hashCode())
		TestObject testObject = new TestObject("dynamicTestObject_" + Math.abs(xpath.hashCode()))
		testObject.addProperty("xpath", ConditionType.EQUALS, xpath)
		return testObject
	}

	/**
	 * Wait for page to load completely
	 */
	@Keyword
	def waitForPageLoad(int timeoutSeconds = 30) {
		CustomLogger.logInfo("Waiting for page to load completely (timeout: ${timeoutSeconds}s)")

		try {
			WebUI.waitForPageLoad(timeoutSeconds)
			CustomLogger.logPassed("Page loaded successfully")
		} catch (Exception e) {
			CustomLogger.logWarning("Page load timeout after ${timeoutSeconds} seconds: ${e.getMessage()}")
		}
	}

	/**
	 * Take screenshot with custom name
	 */
	@Keyword
	def takeScreenshot(String screenshotName = null) {
		try {
			String fileName = screenshotName ?: "screenshot_${System.currentTimeMillis()}"
			WebUI.takeScreenshot(fileName)
			CustomLogger.logInfo("Screenshot taken: ${fileName}")
		} catch (Exception e) {
			CustomLogger.logError("Failed to take screenshot: ${e.getMessage()}")
		}
	}

	/**
	 * Creates test object with multiple selector fallbacks
	 */
	private static TestObject createTestObjectWithMultipleSelectors(String identifier, List<String> xpaths) {
		for (String xpath in xpaths) {
			try {
				TestObject testObject = createTestObject(xpath)
				if (WebUI.waitForElementPresent(testObject, 2, FailureHandling.OPTIONAL)) {
					CustomLogger.logInfo("Element found using xpath: ${xpath}")
					return testObject
				}
			} catch (Exception e) {
				// Continue to next xpath
			}
		}

		// Return the first xpath as fallback
		CustomLogger.logWarning("Element not found with any selector for identifier: ${identifier}. Using first selector as fallback.")
		return createTestObject(xpaths[0])
	}

	/**
	 * Validates date format
	 */
	private boolean isValidDateFormat(String dateString) {
		try {
			LocalDate.parse(dateString)
			return true
		} catch (DateTimeParseException e) {
			return false
		}
	}

	/**
	 * Converts various date formats to standard ISO format
	 */
	private String convertToStandardDateFormat(String dateString) {
		List<DateTimeFormatter> formatters = [
			DateTimeFormatter.ofPattern("dd/MM/yyyy"),
			DateTimeFormatter.ofPattern("MM/dd/yyyy"),
			DateTimeFormatter.ofPattern("dd-MM-yyyy"),
			DateTimeFormatter.ofPattern("MM-dd-yyyy"),
			DateTimeFormatter.ofPattern("yyyy/MM/dd"),
			DateTimeFormatter.ofPattern("yyyy-MM-dd")
		]

		for (DateTimeFormatter formatter : formatters) {
			try {
				LocalDate date = LocalDate.parse(dateString, formatter)
				return date.toString() // Returns in ISO format (yyyy-MM-dd)
			} catch (DateTimeParseException e) {
				// Continue to next formatter
			}
		}

		// If no format matches, return current date
		CustomLogger.logWarning("Could not parse date '${dateString}', using current date")
		return LocalDate.now().toString()
	}
}

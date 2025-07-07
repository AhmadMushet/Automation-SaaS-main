package mid.framework

import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject

import com.kms.katalon.core.annotation.Keyword
import com.kms.katalon.core.model.FailureHandling
import com.kms.katalon.core.testobject.ConditionType
import com.kms.katalon.core.testobject.TestObject
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.exception.StepFailedException
import com.utils.CustomLogger

import utility.Utility

public class MultiSelectHandler {

	// Constants for timeout configuration
	private static final int DEFAULT_TIMEOUT = 10
	private static final int QUICK_TIMEOUT = 3
	private static final String SEARCH_INPUT = "/ancestor::div[starts-with(@class,'styles_multiSelectRow__')]/child::div[contains(@class,'styles_inputContainer')]"
	private static final String MULTI_DROPDOWN_OPTIONS = "//div[@class='optionListContainer displayBlock']"
	private static final String IS_MULTI_DROPDOWN_OPTIONS_CLOSED = "//div[@class='optionListContainer displayNone']"
	def gnUtility = new Utility()

	/**
	 * Handles multi-select dropdown operations
	 * @param containerId The ID of the multi-select container (e.g., "genders-multi-select")
	 * @param action The action to perform: "random", "byName", "searchAndSelect"
	 * @param options Map containing action-specific parameters
	 *   - For random: ["count": number] (optional, defaults to 1)
	 *   - For byName: ["names": List<String>] or ["name": String]
	 *   - For searchAndSelect: ["searchText": String, "selectMode": "random"|"byText", "selectText": String (optional), "count": number (optional)]
	 */
	@Keyword
	def handleMultiSelect(String containerId, String action, Map<String, Object> options = [:]) {
		CustomLogger.logInfo("Starting multi-select operation - Container: '${containerId}', Action: '${action}'")

		if (!containerId || !action) {
			CustomLogger.logError("Container ID and action are required parameters")
			throw new IllegalArgumentException("Container ID and action cannot be null or empty")
		}

		try {
			// Open the dropdown
			openDropdown(containerId)

			// Perform the requested action
			switch (action.toLowerCase()) {
				case "random":
					selectRandomOptions(containerId, options.get("count", 1) as Integer)
					break
				case "byname":
					def names = options.get("names") ?: [options.get("name")]
					selectOptionsByName(containerId, names as List<String>)
					break
				case "searchandselect":
					searchAndSelect(containerId, options)
					break
				default:
					throw new IllegalArgumentException("Unknown action: ${action}. Supported actions: random, byName, searchAndSelect")
			}

			// Close the dropdown
			closeDropdown(containerId)

			CustomLogger.logPassed("Multi-select operation completed successfully")
		} catch (Exception e) {
			CustomLogger.logFailed("Multi-select operation failed: ${e.getMessage()}")
			throw new StepFailedException("Multi-select handling failed: ${e.getMessage()}")
		}
	}

	/**
	 * Opens the multi-select dropdown
	 */
	def openDropdown(String containerId) {
		CustomLogger.logInfo("Opening dropdown for container: '${containerId}'")

		try {
			// Try clicking the search input to open dropdown
			TestObject searchInput = createTestObject("//span[text() = '${containerId}']${SEARCH_INPUT}")
			TestObject multiSelectDropDownClosed = gnUtility.extendTestObjectXPath(searchInput, IS_MULTI_DROPDOWN_OPTIONS_CLOSED)
			boolean isClosed = WebUI.waitForElementPresent(multiSelectDropDownClosed, QUICK_TIMEOUT)
			if (isClosed) {
				WebUI.enhancedClick(searchInput)
				CustomLogger.logInfo("Clicked search input to open dropdown")
			}

			// Wait for options to become visible
			//TestObject optionsContainer = createTestObject("//div[@id='${containerId}']//div[contains(@class,'optionListContainer')]")
			TestObject multiSelectDropDown = gnUtility.extendTestObjectXPath(searchInput, MULTI_DROPDOWN_OPTIONS)
			WebUI.waitForElementVisible(multiSelectDropDown, DEFAULT_TIMEOUT)

			CustomLogger.logPassed("Dropdown opened successfully")
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to open dropdown: ${e.getMessage()}")
			throw e
		}
	}

	/**
	 * Closes the multi-select dropdown
	 */
	def closeDropdown(String containerId) {
		CustomLogger.logInfo("Closing dropdown for container: '${containerId}'")

		try {
			// Click outside the dropdown or press Escape
			WebUI.sendKeys(null, "\\ue00b") // Escape key
			WebUI.delay(1)

			CustomLogger.logPassed("Dropdown closed successfully")
		} catch (Exception e) {
			CustomLogger.logWarning("Could not close dropdown explicitly: ${e.getMessage()}")
		}
	}

	/**
	 * Selects random options from the dropdown
	 */
	def selectRandomOptions(String containerId, int count=1) {
		CustomLogger.logInfo("Selecting ${count} random options from dropdown")

		try {
			// Get all available options (excluding "All" if present)
			List<TestObject> options = getAllOptions(containerId, true)

			if (options.isEmpty()) {
				throw new Exception("No options available to select")
			}

			// Adjust count if it's more than available options
			int actualCount = Math.min(count, options.size())
			CustomLogger.logInfo("Available options: ${options.size()}, Selecting: ${actualCount}")

			// Randomly select options
			Random random = new Random()
			Set<Integer> selectedIndices = new HashSet<>()

			while (selectedIndices.size() < actualCount) {
				selectedIndices.add(random.nextInt(options.size()))
			}

			selectedIndices.each { index ->
				try {
					WebUI.click(options[index])
					String optionText = WebUI.getText(options[index])
					CustomLogger.logInfo("Selected random option: '${optionText}'")
				} catch (Exception e) {
					CustomLogger.logWarning("Failed to select option at index ${index}: ${e.getMessage()}")
				}
			}

			CustomLogger.logPassed("Successfully selected ${selectedIndices.size()} random options")
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to select random options: ${e.getMessage()}")
			throw e
		}
	}

	/**
	 * Selects options by their display names
	 */
	def selectOptionsByName(String containerId, List<String> names) {
		CustomLogger.logInfo("Selecting options by names: ${names}")

		if (!names || names.isEmpty()) {
			CustomLogger.logWarning("No option names provided")
			return
		}

		try {
			int selectedCount = 0

			names.each { name ->
				if (name) {
					try {
						TestObject option = createTestObject("//span[text() = '${containerId}']${SEARCH_INPUT}${MULTI_DROPDOWN_OPTIONS}//li[contains(@class,'option')]//text()[normalize-space()='${name.trim()}']/parent::*")

						if (WebUI.waitForElementPresent(option, QUICK_TIMEOUT)) {
							WebUI.click(option)
							selectedCount++
							CustomLogger.logInfo("Selected option: '${name}'")
						} else {
							CustomLogger.logWarning("Option '${name}' not found")
						}
					} catch (Exception e) {
						CustomLogger.logWarning("Failed to select option '${name}': ${e.getMessage()}")
					}
				}
			}

			CustomLogger.logPassed("Successfully selected ${selectedCount} out of ${names.size()} requested options")
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to select options by name: ${e.getMessage()}")
			throw e
		}
	}

	/**
	 * Searches for text and then selects options
	 */
	def searchAndSelect(String containerId, Map<String, Object> options) {
		String searchText = options.get("searchText") as String
		String selectMode = options.get("selectMode", "random") as String
		String selectText = options.get("selectText") as String
		int count = options.get("count", 1) as Integer

		CustomLogger.logInfo("Search and select - Search: '${searchText}', Mode: '${selectMode}', Count: ${count}")

		if (!searchText) {
			throw new IllegalArgumentException("Search text is required for searchAndSelect action")
		}

		try {
			// Perform search
			performSearch(containerId, searchText)

			// Wait for filtered results
			WebUI.delay(1)

			// Select based on mode
			if (selectMode.toLowerCase() == "random") {
				selectRandomOptions(containerId, count)
			} else if (selectMode.toLowerCase() == "bytext") {
				if (selectText) {
					selectOptionsByName(containerId, [selectText])
				} else {
					CustomLogger.logWarning("No select text provided for byText mode, defaulting to random selection")
					selectRandomOptions(containerId, count)
				}
			} else {
				throw new IllegalArgumentException("Invalid select mode: ${selectMode}. Use 'random' or 'byText'")
			}

			CustomLogger.logPassed("Search and select operation completed successfully")
		} catch (Exception e) {
			CustomLogger.logFailed("Search and select operation failed: ${e.getMessage()}")
			throw e
		}
	}

	/**
	 * Performs search in the dropdown
	 */
	def performSearch(String containerId, String searchText) {
		CustomLogger.logInfo("Performing search with text: '${searchText}'")

		try {
			TestObject searchInput = createTestObject("//span[text() = '${containerId}']${SEARCH_INPUT}")

			if (WebUI.waitForElementPresent(searchInput, DEFAULT_TIMEOUT)) {
				WebUI.clearText(searchInput)
				WebUI.setText(searchInput, searchText)
				CustomLogger.logPassed("Search text entered successfully")
			} else {
				throw new Exception("Search input not found")
			}
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to perform search: ${e.getMessage()}")
			throw e
		}
	}

	/**
	 * Gets all available options from the dropdown
	 */
	List<TestObject> getAllOptions(String containerId, boolean excludeAll = true) {
		CustomLogger.logInfo("Getting all available options from dropdown")

		try {
			List<TestObject> options = []

			// Get all option elements
			String xpath = "//span[text() = '${containerId}']${SEARCH_INPUT}${MULTI_DROPDOWN_OPTIONS}//li[contains(@class,'option')]"

			if (excludeAll) {
				xpath += "[not(contains(text(),'All'))]"
			}

			// Find all matching elements
			for (int i = 1; i <= 20; i++) {
				// Limit to prevent infinite loop
				try {
					TestObject option = createTestObject("(${xpath})[${i}]")
					if (WebUI.waitForElementPresent(option, 1)) {
						options.add(option)
					} else {
						break
					}
				} catch (Exception e) {
					break
				}
			}

			CustomLogger.logInfo("Found ${options.size()} available options")
			return options
		} catch (Exception e) {
			CustomLogger.logWarning("Failed to get all options: ${e.getMessage()}")
			return []
		}
	}

	/**
	 * Verifies that specified options are selected
	 */
	@Keyword
	def verifyOptionsSelected(String containerId, List<String> expectedOptions) {
		CustomLogger.logInfo("Verifying selected options: ${expectedOptions}")

		try {
			List<String> selectedOptions = getSelectedOptions(containerId)
			CustomLogger.logInfo("Currently selected options: ${selectedOptions}")

			boolean allFound = true
			expectedOptions.each { expected ->
				if (!selectedOptions.contains(expected)) {
					CustomLogger.logWarning("Expected option '${expected}' is not selected")
					allFound = false
				}
			}

			if (allFound) {
				CustomLogger.logPassed("All expected options are selected")
				return true
			} else {
				CustomLogger.logFailed("Not all expected options are selected")
				return false
			}
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to verify selected options: ${e.getMessage()}")
			return false
		}
	}

	/**
	 * Gets currently selected options
	 */
	List<String> getSelectedOptions(String containerId) {
		List<String> selectedOptions = []

		try {
			// Look for selected checkboxes or selected indicators
			String xpath = "//span[text() = '${containerId}']${SEARCH_INPUT}${MULTI_DROPDOWN_OPTIONS}//li[contains(@class,'option selected')]"

			for (int i = 1; i <= 10; i++) {
				try {
					TestObject selectedOption = createTestObject("(${xpath})[${i}]")
					if (WebUI.waitForElementPresent(selectedOption, 1)) {
						String text = WebUI.getText(selectedOption).trim()
						if (text && !text.isEmpty()) {
							selectedOptions.add(text)
						}
					} else {
						break
					}
				} catch (Exception e) {
					break
				}
			}
		} catch (Exception e) {
			CustomLogger.logWarning("Failed to get selected options: ${e.getMessage()}")
		}

		return selectedOptions
	}

	/**
	 * Clears all selected options
	 */
	@Keyword
	def clearAllSelections(String containerId) {
		CustomLogger.logInfo("Clearing all selections from dropdown: '${containerId}'")

		try {
			openDropdown(containerId)

			// Look for "All" option to uncheck if it exists and is checked
			TestObject allOption = createTestObject("//span[text() = '${containerId}']${SEARCH_INPUT}${MULTI_DROPDOWN_OPTIONS}//li[contains(@class,'option')][contains(text(),'All')]")

			if (WebUI.waitForElementPresent(allOption, QUICK_TIMEOUT)) {
				WebUI.click(allOption)
				CustomLogger.logInfo("Clicked 'All' option to clear selections")
			} else {
				// Manually uncheck all selected options
				List<String> selectedOptions = getSelectedOptions(containerId)
				selectedOptions.each { option ->
					selectOptionsByName(containerId, [option]) // Click again to unselect
				}
			}

			closeDropdown(containerId)
			CustomLogger.logPassed("All selections cleared successfully")
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to clear selections: ${e.getMessage()}")
			throw new StepFailedException("Failed to clear selections: ${e.getMessage()}")
		}
	}

	/**
	 * Helper method to create TestObject with XPath
	 */
	static TestObject createTestObject(String xpath) {
		TestObject testObject = new TestObject("multiSelect_" + Math.abs(xpath.hashCode()))
		testObject.addProperty("xpath", ConditionType.EQUALS, xpath)
		return testObject
	}

	// ==== CONVENIENCE METHODS FOR COMMON SCENARIOS ====

	/**
	 * Quick method to select random options
	 */
	@Keyword
	def selectRandom(String containerId, int count = 1) {
		handleMultiSelect(containerId, "random", ["count": count])
	}

	/**
	 * Quick method to select by name
	 */
	@Keyword
	def selectByName(String containerId, String name) {
		handleMultiSelect(containerId, "byName", ["name": name])
	}

	/**
	 * Quick method to select multiple by names
	 */
	@Keyword
	def selectByNames(String containerId, List<String> names) {
		handleMultiSelect(containerId, "byName", ["names": names])
	}

	/**
	 * Quick method to search and select randomly
	 */
	@Keyword
	def searchAndSelectRandom(String containerId, String searchText, int count = 1) {
		handleMultiSelect(containerId, "searchAndSelect", [
			"searchText": searchText,
			"selectMode": "random",
			"count": count
		])
	}

	/**
	 * Quick method to search and select by text
	 */
	@Keyword
	def searchAndSelectByText(String containerId, String searchText, String selectText) {
		handleMultiSelect(containerId, "searchAndSelect", [
			"searchText": searchText,
			"selectMode": "byText",
			"selectText": selectText
		])
	}
}
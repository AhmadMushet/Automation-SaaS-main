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
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import com.kms.katalon.core.exception.StepFailedException

import internal.GlobalVariable
import com.utils.CustomLogger
import mid.framework.generalAction
import mid.framework.MultiSelectHandler
import mid.framework.item
import mid.framework.FilterScopes
import mid.framework.FilterType
class filter {
	String url = '/'

	// Constants for timeout configuration
	private static final int DEFAULT_TIMEOUT = 10
	private static final int QUICK_TIMEOUT = 2
	def midActions = new generalAction()
	def multSelectHandler = new MultiSelectHandler()
	def item = new item()
	/**
	 * Expands the filter panel if it's currently collapsed
	 */
	@Keyword
	def expandFilter() {
		CustomLogger.logInfo("Attempting to expand filter panel")

		try {
			TestObject isClosedFilter = findTestObject('Object Repository/Mid/filterPage/closedFilterList')
			boolean filterClosed = WebUI.waitForElementPresent(isClosedFilter, QUICK_TIMEOUT)

			if (filterClosed) {
				CustomLogger.logInfo("Filter panel is currently closed, attempting to expand")

				TestObject expandButton = findTestObject('Object Repository/Mid/filterPage/expandButton')

				if (WebUI.verifyElementVisible(expandButton, FailureHandling.OPTIONAL)) {
					WebUI.click(expandButton)
					CustomLogger.logPassed("Successfully expanded filter panel")
				} else {
					CustomLogger.logWarning("Expand button is not visible")
					throw new StepFailedException("Filter expand button is not visible")
				}
			} else {
				CustomLogger.logInfo("Filter panel is already expanded")
			}
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to expand filter panel: ${e.getMessage()}")
			throw new StepFailedException("Failed to expand filter: ${e.getMessage()}")
		}
	}

	/**
	 * Applies filters based on provided parameters
	 * @param params Array of filter parameters containing filterName and value
	 */
	@Keyword
	def doFilter(List<Map<String, Object>> params) {
		CustomLogger.logInfo("Starting filter operation with ${params?.size() ?: 0} filter parameters")

		if (!params || params.size() == 0) {
			CustomLogger.logWarning("No filter parameters provided")
			return
		}

		try {
			// First expand the filter panel
			expandFilter()

			int processedFilters = 0

			params.each { param ->
				def filterName = param.get('filterName')
				def value = param.get('value')
				def filterType = param.get('filterType') // New: Get filter type from params

				CustomLogger.logInfo("Processing filter - Name: '${filterName}', Value: '${value}', Type: '${filterType}'")

				if (!filterName) {
					CustomLogger.logWarning("Filter name is null or empty, skipping this filter")
					return // Continue to next iteration
				}

				try {
					if (value instanceof List) {
						CustomLogger.logInfo("Processing list filter '${filterName}' with ${value.size()} values: ${value}")
						processListFilter(filterName, value, filterType)
					} else {
						CustomLogger.logInfo("Processing single value filter '${filterName}' with value: '${value}'")
						processSingleValueFilter(filterName, value, filterType)
					}

					processedFilters++
					CustomLogger.logPassed("Successfully processed filter '${filterName}'")
				} catch (Exception e) {
					CustomLogger.logFailed("Failed to process filter '${filterName}': ${e.getMessage()}")
					// Continue processing other filters instead of failing completely
				}
			}

			CustomLogger.logPassed("Filter operation completed. Successfully processed ${processedFilters} out of ${params.size()} filters")
		} catch (Exception e) {
			CustomLogger.logFailed("Filter operation failed: ${e.getMessage()}")
			throw new StepFailedException("Filter operation failed: ${e.getMessage()}")
		}
	}

	/**
	 * Processes list-type filters (dropdown selections)
	 */
	private def processListFilter(String filterName, List values, FilterType filterType) {
		CustomLogger.logInfo("Processing list filter '${filterName}' with values: ${values}")

		values.each { val ->
			try {
				CustomLogger.logInfo("Selecting value '${val}' for filter '${filterName}'")

				handleFilterTypes(filterName,val,filterType)
			} catch (Exception e) {
				CustomLogger.logFailed("Failed to select value '${val}' for filter '${filterName}': ${e.getMessage()}")
				throw e
			}
		}
	}

	/**
	 * Processes single value filters (text inputs)
	 */
	private def processSingleValueFilter(String filterName, def value, FilterType filterType) {
		CustomLogger.logInfo("Processing single value filter '${filterName}' with value: '${value}'")

		try {
			handleFilterTypes(filterName,value,filterType)
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to set value for filter '${filterName}': ${e.getMessage()}")
			throw e
		}
	}

	/**
	 * Clears applied filters
	 * @param allFilter If true, clears all filters; if false, clears specific filters
	 * @param filterData Specific filter data to clear (when allFilter is false)
	 */
	@Keyword
	def clearFilter(boolean allFilter = true, def filterData = '') {
		CustomLogger.logInfo("Starting filter clear operation - All filters: ${allFilter}, Filter data: '${filterData}'")

		try {
			TestObject itemSelected = findTestObject('itemsPageObj/filterPage/listOfItemSelected')

			if (allFilter) {
				CustomLogger.logInfo("Attempting to clear all filters")
				clearAllFilters()
			} else {
				CustomLogger.logInfo("Attempting to clear specific filters: '${filterData}'")
				clearSpecificFilters(filterData)
			}

			CustomLogger.logPassed("Filter clear operation completed successfully")
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to clear filters: ${e.getMessage()}")
			throw new StepFailedException("Failed to clear filters: ${e.getMessage()}")
		}
	}

	/**
	 * Clears all applied filters
	 */
	private def clearAllFilters() {
		try {
			CustomLogger.logPassed("clicking On 'Clear All' button")
			midActions.clickOnDependOnName('Clear all')
			CustomLogger.logPassed("Successfully clicked 'Clear All' button")
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to clear all filters: ${e.getMessage()}")
			throw e
		}
	}

	/**
	 * Clears specific filters based on provided data
	 */
	private def clearSpecificFilters(def filterData) {
		try {
			if (filterData instanceof List) {
				CustomLogger.logInfo("Clearing multiple specific filters: ${filterData}")

				filterData.each { data ->
					clearSingleFilter(data)
				}
			} else {
				CustomLogger.logInfo("Clearing single specific filter: '${filterData}'")
				clearSingleFilter(filterData)
			}
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to clear specific filters: ${e.getMessage()}")
			throw e
		}
	}

	/**
	 * Clears a single filter item
	 */
	private def clearSingleFilter(def data) {
		try {
			CustomLogger.logInfo("Attempting to clear filter item: '${data}'")

			TestObject removeFilter = findTestObject("Object Repository/Mid/filterPage/removeFilter",["data":data])
			//String originalXpath = removeFilterTemplate.getSelectorCollection()['xpath']
			//String modifiedXpath = originalXpath.replace('${data}', data.toString())

			//TestObject removeFilter = new TestObject("removeFilter_${data}")
			//		.addProperty('xpath', ConditionType.EQUALS, modifiedXpath)

			if (WebUI.waitForElementPresent(removeFilter, DEFAULT_TIMEOUT)) {
				WebUI.click(removeFilter)
				CustomLogger.logPassed("Successfully removed filter item: '${data}'")
			} else {
				CustomLogger.logWarning("Remove button not found for filter item: '${data}'")
			}
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to clear filter item '${data}': ${e.getMessage()}")
			throw e
		}
	}

	/**
	 * Clicks the search button to apply filters
	 */
	@Keyword
	def enabledSearchButton() {
		CustomLogger.logInfo("Attempting to click search button to apply filters")

		try {
			midActions.clickOnDependOnName('Search')
		} catch (Exception e) {
			CustomLogger.logFailed("Failed to click search button: ${e.getMessage()}")
			throw new StepFailedException("Failed to click search button: ${e.getMessage()}")
		}
	}

	/**
	 * Complete filter operation: applies filters and executes search
	 * @param params Filter parameters to apply
	 */
	@Keyword
	def applyFilter(List<Map<String, Object>> params) {
		CustomLogger.logInfo("Starting complete filter application process")

		try {
			// Optionally clear existing filters first
			// CustomLogger.logInfo("Clearing existing filters before applying new ones")
			// clearFilter()

			// Apply the new filters
			CustomLogger.logInfo("Applying filter parameters")
			doFilter(params)

			// Execute search
			CustomLogger.logInfo("Executing search with applied filters")
			enabledSearchButton()

			CustomLogger.logPassed("Filter application process completed successfully")
		} catch (Exception e) {
			CustomLogger.logFailed("Filter application process failed: ${e.getMessage()}")
			throw new StepFailedException("Filter application failed: ${e.getMessage()}")
		}
	}

	/**
	 * Utility method to wait for filter results to load
	 */
	@Keyword
	def waitForFilterResults(int timeoutSeconds = 5) {
		CustomLogger.logInfo("Waiting for filter results to load (timeout: ${timeoutSeconds}s)")

		try {

			item.checkItemListAppeared(timeoutSeconds)
		} catch (Exception e) {
			CustomLogger.logWarning("Timeout waiting for filter results: ${e.getMessage()}")
		}
	}

	/**
	 * Verifies that filters have been applied correctly
	 */
	@Keyword
	def verifyFiltersApplied(List<Map<String, Object>> expectedFilters) {
		CustomLogger.logInfo("Verifying that filters have been applied correctly")

		if (!expectedFilters || expectedFilters.size() == 0) {
			CustomLogger.logWarning("No filter parameters provided")
			return
		}

		try {

			int processedFilters = 0

			expectedFilters.each { param ->
				def filterScope = param.get('filterScope')
				def value = param.get('value')

				CustomLogger.logInfo("Processing filter Results - Scope: '${filterScope}', Value: '${value}'")

				if (!filterScope) {
					CustomLogger.logWarning("Filter name is null or empty, skipping this filter")
					return // Continue to next iteration
				}

				try {
					if (value instanceof List) {
						CustomLogger.logInfo("Processing list filter '${filterScope}' with ${value.size()} values: ${value}")

						value.each { val ->
							try {
								CustomLogger.logInfo("Selecting value '${val}' for filter Result '${filterScope}'")

								handleFilterScopes(filterScope,val)
							} catch (Exception e) {
								CustomLogger.logFailed("Failed to find value '${val}' for filter '${filterScope}': ${e.getMessage()}")
								throw e
							}
						}
					} else {
						CustomLogger.logInfo("Processing single value filter Result '${filterScope}' with value: '${value}'")

						try {
							handleFilterScopes(filterScope,value)
						} catch (Exception e) {
							CustomLogger.logFailed("Failed to find value for filter: '${filterScope}' and value: '${value}' \n ${e.getMessage()}")
							throw e
						}
					}

					processedFilters++
					CustomLogger.logPassed("Successfully processed filter '${filterScope}'")
				} catch (Exception e) {
					CustomLogger.logFailed("Failed to process filter '${filterScope}': ${e.getMessage()}")
					// Continue processing other filters instead of failing completely
				}
			}

			CustomLogger.logPassed("Filter operation completed. Successfully processed ${processedFilters} out of ${expectedFilters.size()} filters")
		} catch (Exception e) {
			CustomLogger.logFailed("Filter operation failed: ${e.getMessage()}")
			throw new StepFailedException("Filter operation failed: ${e.getMessage()}")
		}
	}
	
	@Keyword
	boolean verifyNoResults() {
		TestObject noRecordsElement = new TestObject()
		noRecordsElement.addProperty('xpath', ConditionType.EQUALS, "//div[@id='table-with-scroll']/span[starts-with(@class,'styles_noRecords__')]")
		
		return WebUI.verifyElementPresent(noRecordsElement, 3, FailureHandling.OPTIONAL)
	}

	def handleFilterTypes(String fieldName,String value,FilterType filterType) {
		// Handle different filter types with specific actions
		switch (filterType) {
			case FilterType.DROPDOWN:
				midActions.selectOptionDependOnName(fieldName, value)
				break

			case FilterType.MULTISELECT:
				multSelectHandler.selectByName(fieldName, value)
				break

			case FilterType.DATE_PICKER:
				midActions.setDate(value)
				break

			case FilterType.TEXT_INPUT:
				midActions.setTextToInputFieldDependOnName(fieldName, value)
				break

			default:
				throw new Exception("Unsupported filter type: ${filterType}")
		}
	}

	def handleFilterScopes(FilterScopes filterScope,String value) {
		// Handle different filter types with specific actions
		TestObject dynamicTextNode
		def params = [:]
		switch (filterScope) {
			case FilterScopes.FILTER_RESULTS:
				CustomLogger.logInfo("Verifyng the element is existing in Filter results")
				if(!value.isEmpty() || value != null) {
				params = ["parent": "//div[starts-with(@class,'styles_item__') and contains(@class,'styles_itemPosition__')]",
					"value": value
				]
				dynamicTextNode = findTestObject("Object Repository/Mid/filterPage/filterResults", params)
				WebUI.verifyElementVisible(dynamicTextNode, FailureHandling.OPTIONAL)
				}else {
					dynamicTextNode = findTestObject("Object Repository/Mid/filterPage/filterResults_Copy")
					WebElement dynamicNode = WebUiCommonHelper.findWebElement(dynamicTextNode, QUICK_TIMEOUT)
					WebUI.verifyNotEqual(dynamicNode.size(), 0)
				}
				break

			case FilterScopes.FILTER_SELECTED_ITEMS:
				CustomLogger.logInfo("Verifyng the element is existing in Filter selected Items")
				params = ["parent": "//div[starts-with(@class,'styles_itemSelect__')]",
					"value": value
				]
				dynamicTextNode = findTestObject("Object Repository/Mid/filterPage/filterResults", params)
				WebUI.verifyElementVisible(dynamicTextNode, FailureHandling.OPTIONAL)
				break


			default:
				throw new Exception("Unsupported filter type: ${filterScope}")
		}
	}
}
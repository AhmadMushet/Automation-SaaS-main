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

//WebUI.callTestCase(findTestCase('null'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('Test Cases/FE/Search/Verification/Verify elemnts for the search'), [:], FailureHandling.OPTIONAL)

switch (GlobalVariable.searchMode) {

case 'Normal': 
//	println(InvalidProduct + ",,,,,,,,,,,,,,,,,,,,,,,,,,,,")

    WebUI.setText(findTestObject('Object Repository/Search contents/Search'), "hjnmnddjk")

    WebUI.verifyElementVisible(findTestObject('Object Repository/Search contents/Search box/No results found'))
	break
 
case 'Non-Normal':
//	println(InvalidProduct + ",,,,,,,,,,,,,,,,,,,,,,,,,,,,")
    WebUI.setText(findTestObject('Object Repository/Search contents/input'), "hjnmnddjk")

    WebUI.verifyElementVisible(findTestObject('Object Repository/Search contents/Search box/No results found'))
	break
  //  WebUI.callTestCase(findTestCase('null'), [:], FailureHandling.STOP_ON_FAILURE)

}

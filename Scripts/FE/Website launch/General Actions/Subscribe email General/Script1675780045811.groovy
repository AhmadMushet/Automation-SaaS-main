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
import com.kms.katalon.core.util.KeywordUtil as KeywordUtil
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import com.kms.katalon.core.windows.keyword.WindowsBuiltinKeywords as Windows
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys


WebUI.callTestCase(findTestCase('FE/Scrolling/scrollingAtTheBottom'), [:], FailureHandling.OPTIONAL)
        

           // WebUI.verifyElementVisible(findTestObject('Headers and Footers/Header contents/Lets stay in touch field'), FailureHandling.STOP_ON_FAILURE)

            if (WebUI.verifyElementVisible(findTestObject('Subscribe/Subcribe Footer field'), FailureHandling.OPTIONAL)) {
                WebUI.setText(findTestObject('Subscribe/Subcribe Footer field'), CustomKeywords.'generalactions.generalStrings.generatRandomEmail'(), FailureHandling.OPTIONAL)
                WebUI.click(findTestObject('Subscribe/Subscribe Footer Button'), FailureHandling.OPTIONAL)
				verifyTheMessage()
            } 
			if(WebUI.verifyElementVisible(findTestObject('Headers and Footers/Header contents/Lets stay in touch field'), FailureHandling.OPTIONAL)) {
                WebUI.setText(findTestObject('Headers and Footers/Header contents/Lets stay in touch field'), CustomKeywords.'generalactions.generalStrings.generatRandomEmail'(), FailureHandling.OPTIONAL)
                WebUI.click(findTestObject('Subscribe/Subcribe Button on touch'), FailureHandling.OPTIONAL)
				verifyTheMessage()
            }

def verifyTheMessage() {
	if(GlobalVariable.languageMode.toString().equalsIgnoreCase('ar')) {
		WebUI.verifyEqual(CustomKeywords.'generalactions.notificationsObject.getMessageText'(), 'تم تحديث اشتراكك.', FailureHandling.OPTIONAL)
	} else {
		WebUI.verifyEqual(CustomKeywords.'generalactions.notificationsObject.getMessageText'(), 'Your subscription have been updated.', FailureHandling.OPTIONAL)
	}
}


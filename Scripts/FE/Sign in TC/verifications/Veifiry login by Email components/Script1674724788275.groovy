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
import internal.GlobalVariable
import utility.CustomWebUI

import org.openqa.selenium.Keys as Keys

WebUI.callTestCase(findTestCase('FE/Sign in TC/verifications/veifiry Login page components'), [:], FailureHandling.STOP_ON_FAILURE)

WebUI.callTestCase(findTestCase('FE/Sign in TC/validations/General Actions/Navigate sign in By email'), [:], FailureHandling.STOP_ON_FAILURE)

CustomWebUI.verifyElementVisibleWithTimeout(findTestObject('login page/email page/email field'),5,FailureHandling.CONTINUE_ON_FAILURE)

CustomWebUI.verifyElementVisibleWithTimeout(findTestObject('login page/email page/password field'),5,FailureHandling.CONTINUE_ON_FAILURE)

CustomWebUI.verifyElementVisibleWithTimeout(findTestObject('login page/email page/Forget password context'),5,FailureHandling.CONTINUE_ON_FAILURE)

CustomWebUI.verifyElementVisibleWithTimeout(findTestObject('login page/email page/login in Button Email page'),5,FailureHandling.CONTINUE_ON_FAILURE)


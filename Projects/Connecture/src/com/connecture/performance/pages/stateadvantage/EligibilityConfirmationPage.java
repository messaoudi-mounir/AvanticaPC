package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class EligibilityConfirmationPage extends BasePage{
	
	public EligibilityConfirmationPage(WebDriver driver) {
		super(driver);		
	}
	
	public static EligibilityConfirmationPage getPage(WebDriver driver, Logger logHandler) {
		EligibilityConfirmationPage  page = PageFactory.initElements(driver, EligibilityConfirmationPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
	@FindBy (xpath="//input[contains(@name, 'attestEmployeeCountCheck')]")
	public WebElement attestEmployeeCountCheck;
	
	public void checkAttestEmployeeCountCheckToBeClickable(){
		logInfo("Checking Next button to be clickable");
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(attestEmployeeCountCheck));
        wait.until(ExpectedConditions.elementToBeClickable(attestEmployeeCountCheck));
        logInfo("Next button is now clickable");
	}
	
	@FindBy (xpath="//a[contains(@class, 'buttonDone')]")
	public WebElement buttonDone;
	
	public void checkSubmitButtonToBeClickable(){
		logInfo("Checking Next button to be clickable");
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(attestEmployeeCountCheck));
        wait.until(ExpectedConditions.elementToBeClickable(attestEmployeeCountCheck));
        logInfo("Next button is now clickable");
	}	
	
	public EmployerAccountOverviewPage goToEmployerAccountOverviewPage(){        
        logInfo("Clicking submit Button...");        
        alternativeClick(buttonDone);
        logInfo("Submit button clicked...");
        return EmployerAccountOverviewPage.getPage(driver, logHandler);
    }
	
	
	
	/*****************************************************************************************************/
	
	@FindBy (xpath="//input[contains(@name, 'attestInformation')]")
	public WebElement attestInformation;
	
	@FindBy (xpath="//input[contains(@name, 'agreeToTerms')]")
	public WebElement agreeToTerms;
	
	@FindBy (xpath="//input[contains(@name, 'firstName')]")
	public WebElement firstName;
	
	@FindBy (xpath="//input[contains(@name, 'lastName')]")
	public WebElement lastName;
	
	@FindBy (xpath="//input[contains(@name, 'attestDate')]")
	public WebElement attestDate;
	
	public void fillForm(String firstNameText, String lastNameText, String date){
		sleepFor(10);
		attestEmployeeCountCheck.click();
		attestInformation.click();
		agreeToTerms.click();
		
		firstName.sendKeys(firstNameText);
		lastName.sendKeys(lastNameText);
		attestDate.clear();
		attestDate.sendKeys(date);
	}
	
	/*****************************************************************************************************/
	
	
	
}

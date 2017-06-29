package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class EnrollmentConfirmationPage extends BasePage{

	public EnrollmentConfirmationPage(WebDriver driver) {
		super(driver);		
	}
	
	public static EnrollmentConfirmationPage getPage(WebDriver driver, Logger logHandler) {
		EnrollmentConfirmationPage  page = PageFactory.initElements(driver, EnrollmentConfirmationPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
	public void waitForPopupToDisappear(){        
        logInfo("Checking Continue button to be clickable");        
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='actionindicator']")));
        logInfo("Continue button is clickable now");
    }
	
	@FindBy (xpath="//a[contains(@class, 'buttonNext')]")
	public WebElement continueButton;
		
	public void checkContinueButtonToBeClickable(){
		logInfo("Checking Continue button to be clickable");
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(continueButton));
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        logInfo("Continue button is now clickable");
	}
	
	public EnrollmentConfirmationPage goToEnrollmentConfirmationPage(){        
        logInfo("Clicking Continue Button...");            
        continueButton.click();
        waitForPopupToDisappear();
        logInfo("Continue button clicked...");
        return EnrollmentConfirmationPage.getPage(driver, logHandler);
    }	
	
	@FindBy (xpath="//a[contains(@class, 'buttonDone')]")
	public WebElement submitButton;
		
	public void checkSubmitButtonToBeClickable(){
		logInfo("Checking submit button to be clickable");
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(submitButton));
        wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        logInfo("Submit button is now clickable");
	}
	
	public EnrollmentConfirmationPage goToEmployerAccountOverviewPage(){        
        logInfo("Clicking Continue Button...");            
        submitButton.click();
        waitForPopupToDisappear();
        logInfo("Continue button clicked...");
        return EnrollmentConfirmationPage.getPage(driver, logHandler);
    }
	
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
		agreeToTerms.click();
		
		firstName.sendKeys(firstNameText);
		lastName.sendKeys(lastNameText);
		attestDate.clear();
		attestDate.sendKeys(date);
	}
	
}

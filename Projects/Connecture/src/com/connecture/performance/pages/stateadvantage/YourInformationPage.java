package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;


	public class YourInformationPage extends BasePage {
		
		public YourInformationPage(WebDriver driver){
		
		super (driver);
}
	

public static YourInformationPage getPage(WebDriver driver, Logger logHandler) {
	YourInformationPage  page = PageFactory.initElements(driver, YourInformationPage.class);
    page.setLogHandler(logHandler);
    return page;
}

	 
@FindBy (xpath="//a[@class = 'buttonNext']")
public WebElement ContinueButton;

public void checkContinueButtonClickable(){		
	logInfo("Checking Continue Button to be clickable");		
    WebDriverWait wait = new WebDriverWait(driver, timeOut);
    wait.until(ExpectedConditions.visibilityOf(ContinueButton));
    wait.until(ExpectedConditions.elementToBeClickable(ContinueButton));
    logInfo("Continue button is clickable now");
}

public ConfirmationAndSignaturePage goToConfirmationAndSignaturePage(){
	logInfo("Clicking Continue Button...");    
	ContinueButton.click();
    logInfo("Continue button clicked...");
    return ConfirmationAndSignaturePage.getPage(driver, logHandler);
}









}

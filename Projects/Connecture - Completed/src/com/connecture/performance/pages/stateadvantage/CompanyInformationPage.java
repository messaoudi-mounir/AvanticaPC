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

public class CompanyInformationPage extends BasePage{
	
	public CompanyInformationPage(WebDriver driver) {
		super(driver);	
	}
	
	private final static String BLOCK_UI_XPATH_STRING = "//div[@class='blockUI blockOverlay']";
	
	public void waitForBlockUIToAppearAndDisappear() {
        logInfo("In method waitForBlockUIToAppearAndDisappear...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        logInfo("Waiting for blockui to appear...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(BLOCK_UI_XPATH_STRING)));
        logInfo("BlockUI is here!, waiting for it to disappear");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(BLOCK_UI_XPATH_STRING)));
        logInfo("BlockUI is gone...");        
    }
	
    public void waitForBlockUIToDisappear() {
        logInfo("In method waitForBlockUIToDisappear...");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        logInfo("Waiting for blockui to disappear");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(BLOCK_UI_XPATH_STRING)));
        logInfo("BlockUI is gone...");        
    }
    
	public static CompanyInformationPage getPage(WebDriver driver, Logger logHandler) {
		CompanyInformationPage  page = PageFactory.initElements(driver, CompanyInformationPage.class);
	    page.setLogHandler(logHandler);
	    return page;	    
	}
	
	@FindBy(xpath="//a[contains(text(), 'Check Eligibility')]")
    public WebElement checkEligibility;
	
	public void checkCheckEligibilityButtonClickable(){
		
		logInfo("Checking check eligibility button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(checkEligibility));
        wait.until(ExpectedConditions.elementToBeClickable(checkEligibility));
        logInfo("Check eligibility button is clickable now");
	}
	
	@FindBy(xpath="//a[contains(text(), 'Continue')]")
    public WebElement continueButton;
	
	public void checkContinueButtonClickable(){
		logInfo("Checking Continue button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        //waitForBlockUIToAppearAndDisappear();
        wait.until(ExpectedConditions.visibilityOf(continueButton));
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        logInfo("Continue button is clickable now");
	}
	
	public TellUsAboutYourCompanyPage goToTellUsAboutYourCompanyPage(){
		logInfo("Clicking Continue Button..."); 
		waitForBlockUIToDisappear();
		checkContinueButtonClickable();
        continueButton.click();
        logInfo("Continue button clicked...");
        return TellUsAboutYourCompanyPage.getPage(driver, logHandler);        
	}
	
	

}

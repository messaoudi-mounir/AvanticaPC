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

public class TellUsAboutYourCompanyPage extends BasePage{
	
	public TellUsAboutYourCompanyPage(WebDriver driver) {
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
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        logInfo("Waiting for blockui to disappear");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(BLOCK_UI_XPATH_STRING)));
        logInfo("BlockUI is gone...");        
    }
	
	public static TellUsAboutYourCompanyPage getPage(WebDriver driver, Logger logHandler) {
		TellUsAboutYourCompanyPage  page = PageFactory.initElements(driver, TellUsAboutYourCompanyPage.class);
	    page.setLogHandler(logHandler);
	    return page;	    
	}
	
	@FindBy(xpath="//input[@id = 'companyname']")
    public WebElement companyNameField;
	
	public void checkCompanyNameFieldVisible(){
		logInfo("Checking Company name to be visible...");
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(companyNameField));
        logInfo("Company name is now visible...");
	}
	
	public void fillCompanyName(String companyname){
		logInfo("Filling company name...");
        checkCompanyNameFieldVisible();
        companyNameField.sendKeys(companyname);
        
        if(!companyNameField.getAttribute("value").equals(companyname)){
        	companyNameField.clear();
        	fillCompanyName(companyname);
        }
        
	}
	
	
	@FindBy(xpath="//input[@id = 'zipcode_0']")
    public WebElement zipCodeField;
	
	public void fillZipCode(String zipCode){
		logInfo("Filling zip code...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(zipCodeField));
        zipCodeField.clear();
        zipCodeField.sendKeys(zipCode);
        
        if(!zipCodeField.getAttribute("value").equals(zipCode)){
        	zipCodeField.clear();
        	fillZipCode(zipCode);
        }
	}
	
	public void fillForm(String companyname, String zipCode){
		logInfo("Filling Full Form...");
		fillCompanyName(companyname);
		fillZipCode(zipCode);
	}
	
	@FindBy(xpath="//a[@id = 'companyLocationNextButton']")
    public WebElement continueButton;
	
	public void checkContinueButtonClickable(){	
		//sleepFor(sleepTime);
		logInfo("Checking Continue button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);        
        wait.until(ExpectedConditions.visibilityOf(continueButton));
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        logInfo("Continue button is clickable now");
	}
	
	public EmployeeDetailsPage goToEmployeeDetailsPage(){
		logInfo("Clicking Continue Button..."); 		
		checkContinueButtonClickable();
        continueButton.click();
        logInfo("Continue button clicked...");
        return EmployeeDetailsPage.getPage(driver, logHandler);        
	}
	


}

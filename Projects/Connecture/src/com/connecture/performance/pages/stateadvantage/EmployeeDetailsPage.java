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

public class EmployeeDetailsPage extends BasePage{

	public EmployeeDetailsPage(WebDriver driver) {
		super(driver);		
	}

	public static EmployeeDetailsPage getPage(WebDriver driver, Logger logHandler) {
		EmployeeDetailsPage  page = PageFactory.initElements(driver, EmployeeDetailsPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
	@FindBy (xpath="//a[text()='Import Census']")
	public WebElement importCensusLink;
	
	public void checkImportCensusLinkClickable(){
		//sleepFor(sleepTime);
		logInfo("Checking Import Census link to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(importCensusLink));
        wait.until(ExpectedConditions.elementToBeClickable(importCensusLink));
        logInfo("Import Census link is clickable now");
	}
	
	public void openImportCensusPopUp(){
		logInfo("Clicking Import Census link...");   
		checkImportCensusLinkClickable();
		importCensusLink.click();
        logInfo("Import Census link clicked");        
	}
	
	@FindBy (xpath="//input[@id='censusImportFile']")
	public WebElement importCensusPathField;
	
	public void importCensusFile(String path){
		openImportCensusPopUp();		
		logInfo("Sending File path to the importCensus element...");
		logInfo("Path: "+path);
		importCensusPathField.sendKeys(path);
        logInfo("Path sent..."); 
        waitForImportCensusButtonClickable();
        importCensusButton.click();
        logInfo("Import Census Button clicked, Census attached.");
	}

	@FindBy (xpath="//button[@id='importCensusButton']")
	public WebElement importCensusButton;
	
	public void waitForImportCensusButtonClickable() {
		logInfo("Checking Import Census button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(importCensusButton));
        wait.until(ExpectedConditions.elementToBeClickable(importCensusButton));
        logInfo("Import Census button is clickable now");		
	}
	
	@FindBy (xpath="//a[contains(text(), 'Continue') and @class = 'buttonDone']")
	public WebElement continueButton;
	
	public void checkContinueButtonClickable(){		
		logInfo("Checking Continue button to be clickable");
		//sleepFor(sleepTime);
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(continueButton));
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        logInfo("Continue button is clickable now");
	}
	
	public void waitForPopUpToDisappear(){		
		logInfo("Waiting for Import Popup to disappear...");
		//sleepFor(sleepTime);
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//input[@id='censusImportFile']")));
        logInfo("Import Popup is now gone...");
	}
	
	public void waitForImportingPopupToDisappear(){		
		logInfo("Waiting for Import Popup to disappear...");
		//sleepFor(sleepTime);
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='actionindicator']")));
        logInfo("Import Popup is now gone...");
	}
	
	public HowWouldYouLikeToShopForPlansPage goToHowWouldYouLikeToShopForPlansPage(){
		logInfo("Clicking Continue Button...");		
		checkContinueButtonClickable();
        continueButton.click();
        logInfo("Continue button clicked...");
        return HowWouldYouLikeToShopForPlansPage.getPage(driver, logHandler);        
	}
}

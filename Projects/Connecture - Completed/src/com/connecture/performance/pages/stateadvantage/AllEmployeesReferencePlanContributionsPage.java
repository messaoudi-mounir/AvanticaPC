package com.connecture.performance.pages.stateadvantage;


import org.apache.log.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class AllEmployeesReferencePlanContributionsPage extends BasePage {

	public AllEmployeesReferencePlanContributionsPage(WebDriver driver) {
		super(driver);		
	}
	
	public void waitForLoadingPlansPopupToDisappear(){		
		logInfo("Waiting for actionindicator to disappear...");		
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='actionindicator']")));
        logInfo("actionindicator is now gone...");
	}

	public static AllEmployeesReferencePlanContributionsPage getPage(WebDriver driver, Logger logHandler) {
		AllEmployeesReferencePlanContributionsPage  page = PageFactory.initElements(driver, AllEmployeesReferencePlanContributionsPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
	@FindBy (xpath="//a[contains(@class, 'continueSummary')]")
	public WebElement continueButton;
	
	public void checkContinueButtonClickable(){		
		logInfo("Checking Continue button to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(continueButton));
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        logInfo("Continue button is clickable now");
	}
	
	public CompanyProfileSummaryPage goToCompanyProfileSummaryPage(){
		logInfo("Clicking Add Contributions Button...");    
		continueButton.click();
        logInfo("Add Contributions button clicked...");
        return CompanyProfileSummaryPage.getPage(driver, logHandler);
	}
	
	@FindBy (xpath="//input[contains(@id,'contributionEE_DC')]")
	public WebElement contributionEE_DC;
	
	public void waitForPerEmployeeFieldToBeVisible(){		
		logInfo("Checking Per Employee field to be visible");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(contributionEE_DC));
        logInfo("Per Employee field is visible now");
	}
	
	public void fillPerEmployeeField(){
		logInfo("Filling Per Employee field...");
		contributionEE_DC.clear();
		contributionEE_DC.sendKeys("50");
        logInfo("Per Employee field filled...");
	}
	
	@FindBy (xpath="//a[contains(text(), 'Select Reference Plan')]")
	public WebElement referencePlanLink;
	
	public void checkReferencePlanLinkClickable(){		
		logInfo("Checking reference plan link to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(referencePlanLink));
        wait.until(ExpectedConditions.elementToBeClickable(referencePlanLink));
        logInfo("Reference plan link is clickable now");
	}
	
	@FindBy (xpath="//a[contains(text(), 'Select this plan')]")
	public WebElement selectThisPlanButton;
	
	public void checkSelectThisPlanButtonToBeClickable(){
		logInfo("Checking select this plan button to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(selectThisPlanButton));
        wait.until(ExpectedConditions.elementToBeClickable(selectThisPlanButton));
        logInfo("Select this plan button is clickable now");
	}
	
	public void selectReferencePlan(){
		logInfo("Selecting Reference Plan...");
		logInfo("Clicking reference plan link...");		
		alternativeClick(referencePlanLink);
		logInfo("Reference plan link clicked...");
		logInfo("Clicking Select this plan button...");
		checkSelectThisPlanButtonToBeClickable();
		alternativeClick(selectThisPlanButton);
        logInfo("Select this plan button clicked...");
	}
	
	public boolean alternativeClick(WebElement element){
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeOut);
			   wait.until(ExpectedConditions.elementToBeClickable(element));
			   JavascriptExecutor js = (JavascriptExecutor) driver;
			   js.executeScript(
			     "var evt = document.createEvent('MouseEvents');"
			       + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"
			       + "arguments[0].dispatchEvent(evt);", element);
			   return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}

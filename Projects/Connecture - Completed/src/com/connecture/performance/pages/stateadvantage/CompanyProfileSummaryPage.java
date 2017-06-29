package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class CompanyProfileSummaryPage extends BasePage{

	public CompanyProfileSummaryPage(WebDriver driver) {
		super(driver);
	}

	public static CompanyProfileSummaryPage getPage(WebDriver driver, Logger logHandler) {
		CompanyProfileSummaryPage  page = PageFactory.initElements(driver, CompanyProfileSummaryPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
	@FindBy (xpath="//a[contains(@class, '_enrollEr')]")
	public WebElement eligibilityApplicationButton;
	
	public void checkContinueToEligibilityApplicationButtonClickable(){		
		logInfo("Checking continue to eligibility application button to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(eligibilityApplicationButton));
        wait.until(ExpectedConditions.elementToBeClickable(eligibilityApplicationButton));
        logInfo("Continue to eligibility application button is clickable now");
	}
	
	public EligibilityGetStartedPage goToEligibilityGetStartedPage(){
		logInfo("Clicking continue to eligibility application button...");    
		eligibilityApplicationButton.click();
        logInfo("Continue to eligibility application button clicked...");
        return EligibilityGetStartedPage.getPage(driver, logHandler);
	}
	
	
}

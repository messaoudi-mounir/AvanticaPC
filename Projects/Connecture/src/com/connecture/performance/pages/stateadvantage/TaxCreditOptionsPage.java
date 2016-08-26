package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class TaxCreditOptionsPage extends BasePage {
	
	public TaxCreditOptionsPage(WebDriver driver) {
		super (driver);
	}

	
	public static TaxCreditOptionsPage getPage(WebDriver driver, Logger logHandler) {
		TaxCreditOptionsPage  page = PageFactory.initElements(driver, TaxCreditOptionsPage.class);
        page.setLogHandler(logHandler);
        return page;
    }

	
	
	
	@FindBy (xpath="//a[@class = 'buttonNext']")
	public WebElement NextButton;
	
	public void checkNextButtonClickable(){		
		logInfo("Checking Next Button to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(NextButton));
        wait.until(ExpectedConditions.elementToBeClickable(NextButton));
        logInfo("Next button is clickable now");
	}
	
	public PlanSummaryPage goToPlanSummaryPage(){
		logInfo("Clicking Next Button...");    
		NextButton.click();
        logInfo("Next button clicked...");
        return PlanSummaryPage.getPage(driver, logHandler);
	}
	
	
}

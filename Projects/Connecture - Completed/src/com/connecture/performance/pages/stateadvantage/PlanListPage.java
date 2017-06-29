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

public class PlanListPage extends BasePage{

	public PlanListPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	public static PlanListPage getPage(WebDriver driver, Logger logHandler) {
		PlanListPage  page = PageFactory.initElements(driver, PlanListPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
    public void waitForLoadingPlansPopupToDisappear(){        
        logInfo("Checking Continue button to be clickable");        
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='actionindicator']")));
        logInfo("Continue button is clickable now");
    }
    
    @FindBy (xpath="//a[contains(@class, 'continueContribution')]")
    public WebElement continueButton;
    
    public void checkContinueButtonClickable(){    
        logInfo("Checking Continue button to be clickable");        
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(continueButton));
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        logInfo("Continue button is clickable now");
    }
    
    public PlanSummaryPage goToPlanSummaryPage(){
        checkContinueButtonClickable();
        logInfo("Clicking Continue Button...");    
        continueButton.click();
        logInfo("Continue button clicked...");
        return PlanSummaryPage.getPage(driver, logHandler);
    }

}

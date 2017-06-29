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

public class CoverageSummaryPage extends BasePage {

	public CoverageSummaryPage(WebDriver driver){
		super (driver);
		
	}

	public static CoverageSummaryPage getPage(WebDriver driver, Logger logHandler) {
		CoverageSummaryPage  page = PageFactory.initElements(driver, CoverageSummaryPage.class);
	    page.setLogHandler(logHandler);
	    return page;	
}
	public void waitForLoadingPlansPopupToDisappear(){		
		logInfo("Checking Continue button to be clickable");		
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='actionindicator']")));
        logInfo("Start Button is clickable now");
	}
	
/***************************************************Methods**************************************************/
	
	
	
	
	@FindBy(xpath="//a[@class = 'buttonInline _shophealthPlan']")
    public WebElement shopForAHealthPlanButton;
	
	public void shopForAHealthPlanButtonnClickable(){
		logInfo("Checking Shop For A Health Plan button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(shopForAHealthPlanButton));
        wait.until(ExpectedConditions.elementToBeClickable(shopForAHealthPlanButton));
        logInfo("Shop For A Health Plan Button button is clickable now");
	}
    
	public PlanAdvisorPage goToPlanAdvisorPage(){
		logInfo("Clicking Shop For A Health Plan Button...");    	
		shopForAHealthPlanButton.click();
        logInfo("Shop For A Health Plan  Button clicked");
        return PlanAdvisorPage.getPage(driver, logHandler);
	}
	
	
	@FindBy(xpath="//a[@class = 'buttonPrimary _continue_employee_enrollment _has_tooltip']")
    public WebElement continueToEnrollmentButton;
	
	public void continueToEnrollmentButtonClickable(){
		logInfo("Checking Continue To Enrollment button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(continueToEnrollmentButton));
        wait.until(ExpectedConditions.elementToBeClickable(continueToEnrollmentButton));
        logInfo("Continue To Enrollment Button button is clickable now");
	}
    
	public MemberCostSummaryPage goToMemberCostSummaryPage(){
		logInfo("Clicking Continue To Enrollment Button...");    	
		continueToEnrollmentButton.click();
        logInfo("Continue To Enrollment  Button clicked");
        return  MemberCostSummaryPage.getPage(driver, logHandler);
	}
	
}



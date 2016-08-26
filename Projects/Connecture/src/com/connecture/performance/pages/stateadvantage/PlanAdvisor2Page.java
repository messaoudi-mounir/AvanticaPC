package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class PlanAdvisor2Page extends BasePage {

	public PlanAdvisor2Page(WebDriver driver){
		super (driver);
	}

	
	public static PlanAdvisor2Page getPage(WebDriver driver, Logger logHandler) {
		PlanAdvisor2Page  page = PageFactory.initElements(driver, PlanAdvisor2Page.class);
	    page.setLogHandler(logHandler);
	    return page;	
}

	
/**********************************************Methods**************************************************************/	
	
	
	@FindBy(xpath="//a[@class = 'buttonNext _navigateForward _viewPlans']")
    public WebElement viewPlansButton;
	
	public void viewPlansButtonClickable(){
		logInfo("Checking View Plans button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(viewPlansButton));
        wait.until(ExpectedConditions.elementToBeClickable(viewPlansButton));
        logInfo("View Plans Button button is clickable now");
	}
    
	public PlansPage goToPlansPage(){
		logInfo("Clicking View Plans Button...");    	
		viewPlansButton.click();
        logInfo("View Plans Button clicked");
        return PlansPage.getPage(driver, logHandler);
	}
	
	
	
}

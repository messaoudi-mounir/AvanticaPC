package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class PlanAdvisorPage extends BasePage {

	public PlanAdvisorPage(WebDriver driver){
		super (driver);
	}

	
	public static PlanAdvisorPage getPage(WebDriver driver, Logger logHandler) {
		PlanAdvisorPage  page = PageFactory.initElements(driver, PlanAdvisorPage.class);
	    page.setLogHandler(logHandler);
	    return page;	
}

	
/**********************************************Methods**************************************************************/	
	
	
	@FindBy(xpath="//a[@class = 'buttonNext _navigateForward _navigate']")
    public WebElement nextQuestionButton;
	
	public void nextQuestionButtonClickable(){
		logInfo("Checking Next Question button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(nextQuestionButton));
        wait.until(ExpectedConditions.elementToBeClickable(nextQuestionButton));
        logInfo("Next Question Button button is clickable now");
	}
    
	public PlanAdvisor2Page goToPlanAdvisor2Page(){
		logInfo("Clicking Next Question Button...");    	
		nextQuestionButton.click();
        logInfo("Next Question Button clicked");
        return PlanAdvisor2Page.getPage(driver, logHandler);
	}
	
	
	
	
	
}




package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class WhichCarrierPlansWouldYouLikeToOfferToEmployeesPage extends BasePage{

	public WhichCarrierPlansWouldYouLikeToOfferToEmployeesPage(WebDriver driver) {
		super(driver);
	}
	
	public static WhichCarrierPlansWouldYouLikeToOfferToEmployeesPage getPage(WebDriver driver, Logger logHandler) {
		WhichCarrierPlansWouldYouLikeToOfferToEmployeesPage  page = PageFactory.initElements(driver, WhichCarrierPlansWouldYouLikeToOfferToEmployeesPage.class);
        page.setLogHandler(logHandler);
        return page;
    }

	@FindBy (xpath="//a[contains(text(), 'View Plans')]")
	public WebElement viewPlansButton;
	
	public void checkViewPlansButtonClickable(){		
		logInfo("Checking Next Question button to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(viewPlansButton));
        wait.until(ExpectedConditions.elementToBeClickable(viewPlansButton));
        logInfo("Next Question button is clickable now");
	}
	
	@FindBy (xpath="//label[contains(text(),'LancerTest')]")
	public WebElement carrierPlansRadioButton;

	public void selectCarrierWithTheMostPlans(){
		logInfo("Selecting carrier with the most plans");
		carrierPlansRadioButton.click();
		logInfo("Carrier with most plans selected");		
		
	}
	
	public PlanListPage goToPlanListPage(){
		logInfo("Clicking View Plans Button...");    
		viewPlansButton.click();
        logInfo("View plans button clicked...");
        return PlanListPage.getPage(driver, logHandler);
	}
	
}

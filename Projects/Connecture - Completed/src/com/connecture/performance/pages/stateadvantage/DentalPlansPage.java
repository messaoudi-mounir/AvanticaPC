package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.connecture.performance.pages.BasePage;

public class DentalPlansPage extends BasePage {
	

	public DentalPlansPage(WebDriver driver){
		super (driver);
	}
	
	public static DentalPlansPage getPage(WebDriver driver, Logger logHandler) {
		DentalPlansPage  page = PageFactory.initElements(driver, DentalPlansPage.class);
        page.setLogHandler(logHandler);
        return page;
    }

	
	
	
	
	@FindBy(xpath="(//a[@class = 'buttonPrimary _enroll'])[1]")
	 public WebElement selectDentalPlanButton;
	
	
	public PlanSummaryPage goToPlanSummaryPage(){
		logInfo("Clicking Dental Plan Button...");    	
		selectDentalPlanButton.click();
      logInfo("View Dental Plan Button clicked");
      return PlanSummaryPage.getPage(driver, logHandler);
	}
	
	
}

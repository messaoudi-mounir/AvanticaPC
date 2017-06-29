package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class AnnualDeductiblePage extends BasePage{

	public AnnualDeductiblePage(WebDriver driver) {
		super(driver);
	}

	public static AnnualDeductiblePage getPage(WebDriver driver, Logger logHandler) {
		AnnualDeductiblePage  page = PageFactory.initElements(driver, AnnualDeductiblePage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
	@FindBy(xpath="//a[contains(@class, 'buttonNext _navigateForward _viewPlans')]")
    public WebElement viewPlansButton;
	
	public void checkViewPlansButtonClickable(){
		logInfo("Checking View Plans Button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(viewPlansButton));
        wait.until(ExpectedConditions.elementToBeClickable(viewPlansButton));
        logInfo("View Plans Button is clickable now");
	}
	
	public PlansPage goToPlansPage(){
		logInfo("Clicking View Plans Button...");    	
		viewPlansButton.click();
        logInfo("View Plans Button clicked");
        return PlansPage.getPage(driver, logHandler);
	}
}

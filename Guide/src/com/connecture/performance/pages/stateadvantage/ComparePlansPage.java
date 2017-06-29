package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class ComparePlansPage extends BasePage{

	public ComparePlansPage(WebDriver driver) {
		super(driver);
	}

	public static ComparePlansPage getPage(WebDriver driver, Logger logHandler) {
		ComparePlansPage  page = PageFactory.initElements(driver, ComparePlansPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
	@FindBy(xpath="//a[@id = 'FamilyCostBreakdown1']")
    public WebElement familyCostLink;
    
	
	@FindBy(xpath="//a[@class = 'returnToPlans buttonPrimary _backToPlans']")
	public WebElement returnToPlansButton;
	
	public void checkFamilyCostLinkClickable(){
		logInfo("Checking Family Cost Link to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(familyCostLink));
        wait.until(ExpectedConditions.elementToBeClickable(familyCostLink));
        logInfo("Family Cost Link is clickable now");
	}
	
	public AnnualDeductiblePage goToAnnualDeductiblePage(){
		logInfo("Clicking Next Question Button...");    	
		familyCostLink.click();
        logInfo("Next Question Button clicked");
        return AnnualDeductiblePage.getPage(driver, logHandler);
	}
	
	
	public PlansPage goToPlansPage(){
	logInfo ("Clicking Return To Plans Page..");
	returnToPlansButton.click();
	logInfo("Return To Plans clicked");
	return PlansPage.getPage(driver, logHandler);
}
}
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

public class MemberCostSummaryPage extends BasePage {
	public MemberCostSummaryPage(WebDriver driver){
		super (driver);
		
	}

	public static MemberCostSummaryPage getPage(WebDriver driver, Logger logHandler) {
		MemberCostSummaryPage  page = PageFactory.initElements(driver, MemberCostSummaryPage.class);
	    page.setLogHandler(logHandler);
	    return page;	
}
	public void waitForLoadingPopupToDisappear(){		
		logInfo("Checking Continue button to be clickable");		
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='actionindicator']")));
        logInfo("Start Button is clickable now");
	}

/*********************************************Merthods**************************************************/
	
	@FindBy(xpath="//a[@class = 'buttonNext']")
	    public WebElement nextButton;
		
		public void nextButtonClickable(){
			logInfo("Checking Next button to be clickable");
	        WebDriverWait wait = new WebDriverWait(driver, timeOut);
	        wait.until(ExpectedConditions.visibilityOf(nextButton));
	        wait.until(ExpectedConditions.elementToBeClickable(nextButton));
	        logInfo("Next button is clickable now");
		}
	    
		public ReviewPage goToReviewPage(){
			logInfo("Clicking Next Button...");    	
			nextButton.click();
	        logInfo("Next Button clicked");
	        return ReviewPage.getPage(driver, logHandler);
		}
	

	
	
	
}

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

public class ReviewPage extends BasePage {
	public ReviewPage(WebDriver driver){
		super (driver);
		
	}

	public static ReviewPage getPage(WebDriver driver, Logger logHandler) {
		ReviewPage  page = PageFactory.initElements(driver, ReviewPage.class);
	    page.setLogHandler(logHandler);
	    return page;	
}
	public void waitForLoadingPopupToDisappear(){		
		logInfo("Checking Continue button to be clickable");		
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='actionindicator']")));
        logInfo("Start Button is clickable now");
	}
	
	

/*********************************************Methods**************************************************/
	
	@FindBy(xpath="//a[@class = 'buttonNext']")
	    public WebElement buttonNext;
		
		public void checkButtonNextClickable(){
			logInfo("Checking Next button to be clickable");
	        WebDriverWait wait = new WebDriverWait(driver, timeOut);
	        wait.until(ExpectedConditions.visibilityOf(buttonNext));
	        wait.until(ExpectedConditions.elementToBeClickable(buttonNext));
	        logInfo("Next button is clickable now");
		}
	    
		public ConfirmationAndSignaturePage goToConfirmationAndSignaturePage(){
			logInfo("Clicking Next Button...");    	
			buttonNext.click();
			//alternativeClick(buttonNext);
	        logInfo("Next Button clicked");
	        return ConfirmationAndSignaturePage.getPage(driver, logHandler);
		}
	
	
	
	
	
}


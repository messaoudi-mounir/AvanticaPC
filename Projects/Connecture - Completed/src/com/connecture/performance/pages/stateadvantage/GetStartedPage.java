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

public class GetStartedPage extends BasePage{
	
	public GetStartedPage(WebDriver driver){
		super(driver);

}
	
	public static GetStartedPage getPage(WebDriver driver, Logger logHandler) {
		GetStartedPage  page = PageFactory.initElements(driver, GetStartedPage.class);
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
	
	@FindBy(xpath="//a[@class = 'buttonNext _startPA']")
	    public WebElement startButton;
		
		public void startButtonClickable(){
			logInfo("Checking Login button to be clickable");
	        WebDriverWait wait = new WebDriverWait(driver, timeOut);
	        wait.until(ExpectedConditions.visibilityOf(startButton));
	        wait.until(ExpectedConditions.elementToBeClickable(startButton));
	        logInfo("Start button is clickable now");
		}
	    
		public CoverageSummaryPage goToCoverageSummaryPage(){
			logInfo("Clicking Start Button...");    	
			startButton.click();
	        logInfo("Start Button clicked");
	        return CoverageSummaryPage.getPage(driver, logHandler);
		}
	

}

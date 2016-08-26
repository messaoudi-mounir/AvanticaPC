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

public class CuramTestPage extends BasePage{

	public CuramTestPage(WebDriver driver) {
		super(driver);
	}
	
	public static CuramTestPage getPage(WebDriver driver, Logger logHandler) {
		CuramTestPage  page = PageFactory.initElements(driver, CuramTestPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
	public void waitForLoadingPopupToDisappear(){		
		logInfo("Checking Continue button to be clickable");		
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='actionindicator']")));
        logInfo("Start Button is clickable now");
	}
	
	
	
	/*06/10/2015 WORKAROUND for Non English configured systems******************************/	

	@FindBy (xpath="//a[text()='English']")
    public WebElement englishButton;
    
    public void checkEnglishButtonClickable() {
    	WebDriverWait wait = new WebDriverWait(driver, timeOut);
        logInfo("Clicking English button"); 
        wait.until(ExpectedConditions.visibilityOf(englishButton));        
        wait.until(ExpectedConditions.elementToBeClickable(englishButton));
        logInfo("English button clicked, REMOVE THIS BEFORE THE RUN FOR RESULTS");        
    }      
    public void clickEnglishButton(){
    	checkEnglishButtonClickable();
        englishButton.click();              
    }    
    /***************************************************************************************/
    
    @FindBy(xpath="//a[@id = 'ahbeCommercial']")
    public WebElement ahbeCommercialLink;
	
	public void checkAHBEComercialLinkClickable(){
		logInfo("Checking AHBE Commercial Link to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(ahbeCommercialLink));
        wait.until(ExpectedConditions.elementToBeClickable(ahbeCommercialLink));
        logInfo("AHBE Commercial Link is clickable now");
	}
	
	public YourPreferencePage goToYourPreferencePage(){
		logInfo("Clicking AHBE Commercial Link...");    	
		ahbeCommercialLink.click();
        logInfo("AHBE Commercial Link clicked");
        return YourPreferencePage.getPage(driver, logHandler);
	}
	
	
	 @FindBy(xpath="//a[contains(text(),'Employee Test')]")
	    public WebElement employeeTestLink;
		
		public void employeeTestLinkClickable(){
			logInfo("Checking Employee Test Link to be clickable");
	        WebDriverWait wait = new WebDriverWait(driver, timeOut);
	        wait.until(ExpectedConditions.visibilityOf(employeeTestLink));
	        wait.until(ExpectedConditions.elementToBeClickable(employeeTestLink));
	        logInfo("Employee Test Link is clickable now");
		}
		
		public RunOpenEnrollmentPage goToRunOpenEnrollmentPage(){
			logInfo("Clicking Employee Test Link...");    	
			employeeTestLink.click();
	        logInfo("Employee Test Link clicked");
	        return RunOpenEnrollmentPage.getPage(driver, logHandler);
		}
	
		
		
		
}

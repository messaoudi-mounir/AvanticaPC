package com.connecture.performance.pages;

import java.text.SimpleDateFormat;
import java.util.Date;






import org.apache.log.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.uhg.PortalPage;




public class BasePage {
	
	public final int timeOut = 600;
	public WebDriver driver;
	//public final int sleepTime = 20;
	public Logger logHandler;
	
	public BasePage(WebDriver driver){
		this.driver = driver;
	}
	

	
	
	protected static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	/************************************METHODS****************************/	
	
	public void setLogHandler(Logger logHandler) {
	    this.logHandler = logHandler;
	}
	
	public void logTimedInfo(String msg) {
	    logInfo(msg, true);
	}
	
	public void logInfo(String msg) {
	    logInfo(msg, false);
	}
	public void logInfo(String msg, boolean includeCurrentDate) {
	    String messageStr = null;
	    if (includeCurrentDate) {
	        messageStr = dateFormat.format(new Date()) + " - " + msg;	        
	    }
	    else {
	        messageStr = msg;
	    }
	    
	    // Use external log handler if provided
	    if (logHandler != null) {
	        logHandler.info(messageStr);
	    }
	    else {
	        System.out.println("INFO: " + messageStr);
	    }	    
	}
	
	// Sleeps for a fixed amount of seconds
	public void sleepFor(int seconds) {
	    logInfo("Sleeping for " + seconds  + " seconds...");
        try {
            Thread.sleep(seconds * 1000);
        }
        catch (InterruptedException e) {};
        logInfo("Awake now!");
	}
	
	public boolean alternativeClick(WebElement element){
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeOut);
			   wait.until(ExpectedConditions.elementToBeClickable(element));
			   JavascriptExecutor js = (JavascriptExecutor) driver;
			   js.executeScript(
			     "var evt = document.createEvent('MouseEvents');"
			       + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"
			       + "arguments[0].dispatchEvent(evt);", element);
			   return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@FindBy (xpath="//span[@class = 'welcomeName']")
    public WebElement welcomeMenu;
    
    @FindBy (xpath="//a[contains(@class,'_logout')]")
    public WebElement logoutButton;    
    
    public PortalPage doLogout() {    
        	logInfo("goToLogoutMenu - start");
        	WebDriverWait wait = new WebDriverWait(driver, 600);
        	logInfo("Checking welcome menu button visibility");
        	wait.until(ExpectedConditions.visibilityOf(welcomeMenu));
        	logInfo("Checking welcome menu button to be clickable");
        	wait.until(ExpectedConditions.elementToBeClickable(welcomeMenu));
    		logInfo("Clicking welcome menu Button...");
    		welcomeMenu.click();
    		logInfo("Welcome menu button clicked...");
    		logInfo("Checking logout button visibility");
        	wait.until(ExpectedConditions.visibilityOf(logoutButton));
        	logInfo("Checking welcome menu button to be clickable");
        	wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
    		logInfo("Clicking logout button...");
    		logoutButton.click();
    		logInfo("Logout button clicked...");			
    		return PortalPage.getPage(driver, logHandler);        
    }
	
}

package com.connecture.performance.pages.uhg;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class PortalPage extends BasePage {   

    /**
     * Constructor has to be public to be used by Page Factory, but we should not call this constructor externally
     */
    public PortalPage(WebDriver driver) {
        super(driver);
    }
    
    // Factory method
    public static PortalPage getPage(WebDriver driver, Logger logHandler) {
        PortalPage  page = PageFactory.initElements(driver, PortalPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
    
    @FindBy (xpath="//*[@id='rLogin']")
    public WebElement userLoginLink;
        
    public void checkUserLoginClickable() {
    	WebDriverWait wait = new WebDriverWait(driver, timeOut);
        logInfo("checking login button to be visible..."); 
        wait.until(ExpectedConditions.visibilityOf(userLoginLink));
        logInfo("login button is visible now");    
        logInfo("checking login button to be clickable...");
        wait.until(ExpectedConditions.elementToBeClickable(userLoginLink));
        logInfo("login button is clickable now");        
        
    }
    
    public LoginPage goToLoginPage() {
        logInfo("going to login page...");
        userLoginLink.click();
        return LoginPage.getPage(driver, logHandler);                
    }
   

}

package com.connecture.performance.pages.stateadvantage;

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
    
    @FindBy (xpath="//a[text()='Get Started']")
    public WebElement getStartedButton;
        
    public void checkGetStartedButtonClickable() {
    	WebDriverWait wait = new WebDriverWait(driver, timeOut);
        logInfo("checking get started button to be visible..."); 
        wait.until(ExpectedConditions.visibilityOf(getStartedButton));
        logInfo("Get started button is visible now");    
        logInfo("checking get started button to be clickable...");
        wait.until(ExpectedConditions.elementToBeClickable(getStartedButton));
        logInfo("get started button is clickable now");
        
    }   
    
    public ContactInformationPage goToContactInformationPage(){
    	logInfo("Clicking Portal link...");    	
        getStartedButton.click();
        logInfo("Portal link clicked");
        return ContactInformationPage.getPage(driver, logHandler);
    }

    
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
}

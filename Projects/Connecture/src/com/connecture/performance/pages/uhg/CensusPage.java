package com.connecture.performance.pages.uhg;

import org.apache.log.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class CensusPage extends BasePage{
    
    // Factory method
    public static CensusPage getPage(WebDriver driver, Logger logHandler) {
        CensusPage page = PageFactory.initElements(driver, CensusPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
        
    public CensusPage(WebDriver driver) {
        super(driver);
    }    
        

    @FindBy(xpath="//a [@id = 'addEmployeeBtn']")
    public WebElement addEmployeeButton;
    
    public void checkAddEmployeeButtonClickable() {
        logInfo("Checking add employee button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.elementToBeClickable(addEmployeeButton));
        logInfo("Add employee button is clickable now");                                      
    }
    
    
    
    public AddEmployeePopup goToAddEmployee() {
        logInfo("Clicking add employee...");
        addEmployeeButton.click();
        logInfo("Add employee clicked");
        return AddEmployeePopup.getPage(driver, logHandler);        
    }
    
    @FindBy (xpath="//a[contains(text() , 'Continue')]")
    public WebElement continueButton;

    private final static String BLOCK_UI_XPATH_STRING = "//div[@class='ui-widget-overlay ui-front']";
    
    public void checkContinueButtonClickable() {
    	logInfo("Checking continue button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, 300);
     
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(BLOCK_UI_XPATH_STRING)));
        logInfo("Overlay is gone!");
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        logInfo("Continue button is clickable now"); 
    }

    
    public PlanSelectionPage goToPlanSelection() {
    	logInfo("Clicking Continue button...");    	
        continueButton.click();
        logInfo("Continue button clicked");
        return PlanSelectionPage.getPage(driver, logHandler);
    }
    
}

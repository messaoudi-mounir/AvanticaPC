package com.connecture.performance.pages.uhg;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class LocationPage extends BasePage{
    
    
    // Factory method
    public static LocationPage getPage(WebDriver driver, Logger logHandler) {
        LocationPage page = PageFactory.initElements(driver, LocationPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
        
    public LocationPage(WebDriver driver) {
        super(driver);
    }    
    
    
    @FindBy (xpath="//a[contains(text(), 'Continue')]")
    public WebElement continueButton;
    
    
    public void checkContinueButtonClickable() {
        logInfo("Checking continue button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        logInfo("Continue button is clickable now");                
    }
            
    
    
    public void fillOutLocationForm() {
        sleepFor(1);
        fillOutDentalPriorCoverageField();
        fillOutLifeAndDisabilityProducts();
        sleepFor(1);
    }
    
    
    @FindBy(xpath="//input [@name = 'custom.dentalPriorCoverage']")
    public WebElement dentalPriorCoverageField;    
    
    public void fillOutDentalPriorCoverageField() {
        logInfo("Filling dental prior coverage...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(dentalPriorCoverageField));
        dentalPriorCoverageField.click();        
    }
    
    
    @FindBy(xpath="//input [@name = 'custom.lifeAndDisabilityProducts']")
    public WebElement lifeAndDisabilityProductsField;
    
    @FindBy(xpath="//input [@id = 'LTD']")
    public WebElement ltdField;
    
    @FindBy(xpath="//input [@id = 'STD']")
    public WebElement stdField;    
    
    @FindBy(xpath="//input [@id = 'LIFE_AD_D']")
    public WebElement lifeAddField;    
    
    
    public void fillOutLifeAndDisabilityProducts() {
        logInfo("Filling life and disability products...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(lifeAndDisabilityProductsField));
        lifeAndDisabilityProductsField.click();
        wait.until(ExpectedConditions.visibilityOf(lifeAddField));
        //ltdField.click();
        //stdField.click();
        lifeAddField.click();
    }
    public CensusPage goToCensusPage() {
        logInfo("Clicking continue...");
        continueButton.click();
        logInfo("continue clicked");
        return CensusPage.getPage(driver, logHandler);        
    }
    
}

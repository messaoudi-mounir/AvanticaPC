package com.connecture.performance.pages.ibc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class PriorToApplyingPage extends BaseIBCPage {
    
    // Singleton instance
    private static PriorToApplyingPage page = null;

    public PriorToApplyingPage(WebDriver driver) {
        super(driver);
    }
    
    public static PriorToApplyingPage getPage(WebDriver driver) {
        if (page == null) {
          page = PageFactory.initElements(driver, PriorToApplyingPage.class);
        }
        return page;
    }
    
    @FindBy (xpath="//a[contains(text(),'Continue')]")
    public WebElement continueButton;
       
    public void checkContinueButtonClickable() {
        System.out.println("DEBUG - checking continueButton to be clickable");        
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        System.out.println("DEBUG - continueButton is clickable now");
    }
    

}

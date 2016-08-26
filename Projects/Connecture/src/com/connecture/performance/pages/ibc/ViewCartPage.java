package com.connecture.performance.pages.ibc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class ViewCartPage extends BaseIBCPage {
    
    // Singleton instance
    private static ViewCartPage page = null;

    public ViewCartPage(WebDriver driver) {
        super(driver);
    }
    
    public static ViewCartPage getPage(WebDriver driver) {
        if (page == null) {
          page = PageFactory.initElements(driver, ViewCartPage.class);
        }
        return page;
    }
    
    @FindBy (xpath="//a[contains(text(),'Enroll Now')]")
    public WebElement enrollNowButton;
    
    
    public void checkEnrollNowButtonClickable() {
        System.out.println("DEBUG - checking enrollNowButton to be clickable");        
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.elementToBeClickable(enrollNowButton));
        System.out.println("DEBUG - enrollNowButton is clickable now");
    }
    
    public PriorToApplyingPage gotoPriorToApplyingPage() {
        System.out.println("DEBUG - going to Prior to Applying Page....");
        enrollNowButton.click();
        System.out.println("DEBUG - enrollNowButton clicked...");
        return PriorToApplyingPage.getPage(driver);
        
    }

    
}

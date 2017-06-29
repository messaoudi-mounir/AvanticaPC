package com.connecture.performance.pages.ibc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class AddToCartPopup extends BaseIBCPage {
    
    // Singleton instance
    private static AddToCartPopup page = null;

    public AddToCartPopup(WebDriver driver) {
        super(driver);
    }
    
    public static AddToCartPopup getPage(WebDriver driver) {
        if (page == null) {
          page = PageFactory.initElements(driver, AddToCartPopup.class);
        }
        return page;
    }
    
    @FindBy (xpath="//a[contains(text(),'View Cart & Enroll')]")
    public WebElement viewCartAndEnrollButton;
    
    public void checkViewCartAndEnrollButtonButtonClickable() {
        System.out.println("DEBUG - checking viewCartAndEnrollButton to be clickable");        
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.elementToBeClickable(viewCartAndEnrollButton));
        System.out.println("DEBUG - viewCartAndEnrollButton is clickable now");
    }
    
    public ViewCartPage gotoViewCartPage() {
        System.out.println("DEBUG - going to View Cart Page....");
        viewCartAndEnrollButton.click();
        System.out.println("DEBUG - viewCartAndEnrollButton clicked...");
        return ViewCartPage.getPage(driver);
    }
    

}

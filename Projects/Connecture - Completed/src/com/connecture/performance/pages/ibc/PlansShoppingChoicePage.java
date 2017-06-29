package com.connecture.performance.pages.ibc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class PlansShoppingChoicePage extends BaseIBCPage {
    
    // Singleton instance
    private static PlansShoppingChoicePage page = null;

    public PlansShoppingChoicePage(WebDriver driver) {
        super(driver);
    }
    
    public static PlansShoppingChoicePage getPage(WebDriver driver) {
        if (page == null) {
          page = PageFactory.initElements(driver, PlansShoppingChoicePage.class);
        }
        return page;
    }
    
    @FindBy (xpath="//a[contains(text(),'Browse')]")
    public WebElement browsePlansButton;
    
    public void checkBrowsePlansButtonClickable() {
        System.out.println("DEBUG - checking browsePlansButton to be clickable");        
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.elementToBeClickable(browsePlansButton));
        System.out.println("DEBUG - browsePlansButton is clickable now");
    }
    
    public ViewPlansPage gotoViewPlansPage() {
        System.out.println("DEBUG - going to View Plans Page....");
        browsePlansButton.click();
        System.out.println("DEBUG - continueButton clicked...");
        return ViewPlansPage.getPage(driver);
    }
    

}

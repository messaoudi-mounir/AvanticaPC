package com.connecture.performance.pages.ibc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class PortalPage extends BaseIBCPage {
    
    // Singleton instance
    private static PortalPage page = null;

    public PortalPage(WebDriver driver) {
        super(driver);
    }
    
    public static PortalPage getPage(WebDriver driver) {
        if (page == null) {
          page = PageFactory.initElements(driver, PortalPage.class);
        }
        return page;
    }
    
    @FindBy (xpath="//a[contains(text(),'Shop IBC Plans')]")
    public WebElement shopIBCPlansButton;
    
    public void checkShopIBCPlansButtonClickable() {        
        System.out.println("DEBUG - checking shopIBCPlansButton to be clickable");        
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.elementToBeClickable(shopIBCPlansButton));
        System.out.println("DEBUG - shopIBCPlansButton is clickable now");
    }
    
    public ShopProfilePage goToShopIBCPlansPage() {
        System.out.println("DEBUG - going to shop IBC Plans Page....");
        shopIBCPlansButton.click();
        return ShopProfilePage.getPage(driver);
    }
      

}

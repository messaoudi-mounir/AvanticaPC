package com.connecture.performance.pages.ibc;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class ViewPlansPage extends BaseIBCPage {
    
    // Singleton instance
    private static ViewPlansPage page = null;

    public ViewPlansPage(WebDriver driver) {
        super(driver);
    }
    
    public static ViewPlansPage getPage(WebDriver driver) {
        if (page == null) {
          page = PageFactory.initElements(driver, ViewPlansPage.class);
        }
        return page;
    }
    
    @FindBy (xpath="(//a[contains(text(),'Add to Cart')])[1]")
    public WebElement addToCartButton;
    
    private final static String BLOCK_UI_XPATH_STRING = "//div[@class='blockUI blockOverlay']";    
    
    public void checkAddToCartButtonClickable() {
        System.out.println("DEBUG - checking addToCartButton to be clickable");        
        WebDriverWait wait = new WebDriverWait(driver, 300);
        // Wait for the block UI to appear 
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(BLOCK_UI_XPATH_STRING)));
        System.out.println("DEBUG - overlay is here!, waiting for it to disappear");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(BLOCK_UI_XPATH_STRING)));
        System.out.println("DEBUG - overlay is gone...");
        wait.until(ExpectedConditions.elementToBeClickable(addToCartButton));
        System.out.println("DEBUG - addToCartButton is clickable now");
    }
    
    public AddToCartPopup addToCart() {
        System.out.println("DEBUG - adding to cart ....");
        addToCartButton.click();
        System.out.println("DEBUG - addToCart clicked...");
        return AddToCartPopup.getPage(driver);        
    }

}

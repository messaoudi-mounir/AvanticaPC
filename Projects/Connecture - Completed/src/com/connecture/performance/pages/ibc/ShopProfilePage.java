package com.connecture.performance.pages.ibc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;


public class ShopProfilePage extends BaseIBCPage {
    
    // Singleton instance
    private static ShopProfilePage page = null;

    public ShopProfilePage(WebDriver driver) {
        super(driver);
    }
    
    public static ShopProfilePage getPage(WebDriver driver) {
        if (page == null) {
          page = PageFactory.initElements(driver, ShopProfilePage.class);
        }
        return page;
    }
    
    @FindBy (xpath="//a[contains(text(),'Save & Continue')]")
    public WebElement continueButton;
    
    @FindBy (xpath="//input[@name='zipCode']")
    public WebElement zipCodeField;
    
    @FindBy (xpath="//*[@id='default-name.first']")
    public WebElement firstName1Field;
    
    @FindBy (xpath="//*[@id='default-birthDate']")
    public WebElement birthDate1Field;
    
    @FindBy (xpath="//select[@name='gender' and @data-id='0']")
    public WebElement gender1Field;
    
    @FindBy (xpath="//select[@name='isSmoker' and @data-id='0']")
    public WebElement tobacco1Field;
    
    @FindBy (xpath="//a[contains(text(),'Add Dependent')]")
    public WebElement addDependentButton;
    
    @FindBy (xpath="//*[@name='name.first' and @data-id='1']")
    public WebElement firstName2Field;
    
    @FindBy (xpath="//*[@name='birthDate' and @data-id='1']")
    public WebElement birthDate2Field;
    
    @FindBy (xpath="//select[@name='memberRelationship' and @data-id='1']")
    public WebElement relationship2Field;
    
    @FindBy (xpath="//select[@name='gender' and @data-id='1']")
    public WebElement gender2Field;
    
    @FindBy (xpath="//select[@name='isSmoker' and @data-id='1']")
    public WebElement tobacco2Field;
    
    public void checkContinueButtonClickable() {
        System.out.println("DEBUG - checking continueButton to be clickable");        
        WebDriverWait wait = new WebDriverWait(driver, getWaitTimeot());
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        System.out.println("DEBUG - continueButton is clickable now");
    }
    
    public void fillOutProfileForm(String zipCode, String firstName1, String birthDate1, String gender1, String tobacco1,
                                                   String firstName2, String birthDate2, String relationship2, String gender2, String tobacco2) {
        WebDriverWait wait = new WebDriverWait (driver, getWaitTimeot());
        
        System.out.println("DEBUG - Filling out profile form");
        zipCodeField.sendKeys(zipCode);
        
        
        System.out.println("DEBUG - Waiting for 'firstName1Field' to be clickable");
        wait.until(ExpectedConditions.elementToBeClickable(firstName1Field));
        firstName1Field.sendKeys(firstName1);
        setDateField(driver, birthDate1Field, birthDate1);
        
        Select selectField = new Select(gender1Field);
        selectField.selectByValue(gender1);
        
        selectField = new Select(tobacco1Field);
        selectField.selectByValue(tobacco1);
        
        System.out.println("DEBUG - Filling out next fields, person two");
        wait.until(ExpectedConditions.elementToBeClickable(addDependentButton));
        addDependentButton.click();
        
        wait.until(ExpectedConditions.elementToBeClickable(firstName2Field));
        firstName2Field.sendKeys(firstName2);
        setDateField(driver, birthDate2Field, birthDate2);
        
        selectField = new Select(relationship2Field);
        selectField.selectByValue(relationship2);
        
        selectField = new Select(gender2Field);
        selectField.selectByValue(gender2);
        
        selectField = new Select(tobacco2Field);
        selectField.selectByValue(tobacco2);
        
    }
    
    public PlansShoppingChoicePage gotoPlansShoppingChoicePage() {
        System.out.println("DEBUG - going to Plans Shopping Choice Page....");
        continueButton.click();
        System.out.println("DEBUG - continueButton clicked...");
        return PlansShoppingChoicePage.getPage(driver);
    }
      

}

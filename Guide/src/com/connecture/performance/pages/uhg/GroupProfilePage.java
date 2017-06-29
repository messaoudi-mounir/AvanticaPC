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

public class GroupProfilePage extends BasePage  {
    
    // Factory method
    public static GroupProfilePage getPage(WebDriver driver, Logger logHandler) {
        GroupProfilePage page = PageFactory.initElements(driver, GroupProfilePage.class);
        page.setLogHandler(logHandler);
        return page;
    }
    
    public GroupProfilePage(WebDriver driver) {
        super(driver);
    }    
    /**************************************************FILL FORM********************************************/
    
    @FindBy (xpath="//a[contains(text(), 'Continue')]")
    public WebElement continueButton;
    
    
    public void checkContinueButtonClickable() {
        logInfo("Checking continue button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        logInfo("login continue is clickable now");        
        
    }
    @FindBy(xpath="//input [@name = 'caseName']")
    public WebElement caseNameField; 
    
    @FindBy(xpath="//input [@name = 'effectiveDate']")
    public WebElement effectiveDateField;
    
    @FindBy(xpath="//input [@name = 'zipCode']")
    public WebElement zipCodeField;
    
    @FindBy(xpath="//input [@name = 'totalEligible']")
    public WebElement totalEligibleField; 
    
    @FindBy(xpath="//input [@name = 'custom.totalEmployeesApplying']")
    public WebElement totalEmployeesField; 
    
    @FindBy(xpath="//input [@name = 'sicCode']")
    public WebElement sicCodeField;
    
    public void fillOutProfileForm(String companyName, String zipCode, String zipCodeDesc, String effectiveDate, 
                                    String sicCode, String sicCodeDesc) {
        logInfo("Filling out profile form, companyName: " + companyName + ", zipCode: " 
                    + zipCode + ", zipCodeDesc: " + zipCodeDesc                     
                    + ", effectiveDate: " + effectiveDate + ", sicCode: " + sicCode + ", sicCodeDesc: " + sicCodeDesc);
        
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(caseNameField));                
        /******************COMPANY NAME********************/
        logInfo("Filling company name...");       
        caseNameField.clear();
        caseNameField.sendKeys(companyName);
        /*******************ZIP CODE*********************/        
        wait.until(ExpectedConditions.visibilityOf(zipCodeField));
        zipCodeField.clear();
        zipCodeField.sendKeys(zipCode);               
        
        String liZipCodeXxpath="//li[text() = '" + zipCodeDesc + "']";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(liZipCodeXxpath)));        
        driver.findElement(By.xpath(liZipCodeXxpath)).click();                
        /*******************EFFECTIVE DATE*********************/
        wait.until(ExpectedConditions.visibilityOf(effectiveDateField)); 
        effectiveDateField.click();
        effectiveDateField.clear();        
        effectiveDateField.sendKeys(effectiveDate);
        logInfo("DEBUG - Effective date sent: "+effectiveDate);
        /*******************TOTAL ELIGIBLE*********************/
        String employees = "2";
        
        wait.until(ExpectedConditions.visibilityOf(totalEligibleField));
        totalEligibleField.clear();
        totalEligibleField.sendKeys(employees);        
        /*******************TOTAL EMPLOYEES*********************/        
        wait.until(ExpectedConditions.visibilityOf(totalEmployeesField));
        totalEmployeesField.clear();
        totalEmployeesField.sendKeys(employees);        
        /*******************SIC CODE*********************/        
        wait.until(ExpectedConditions.visibilityOf(sicCodeField));
        sicCodeField.clear();
        sicCodeField.sendKeys(sicCode);               
        
        String liSicCodeXxpath="//li[text() = '" + sicCodeDesc + "']";
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(liSicCodeXxpath)));
        driver.findElement(By.xpath(liSicCodeXxpath)).click();
         
    }
    /************************************************************************************************/
    public LocationPage goToLocationPage() {
        logInfo("Clicking continue...");
        continueButton.click();
        logInfo("continue clicked");
        return LocationPage.getPage(driver, logHandler);        
    }

}

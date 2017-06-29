package com.connecture.performance.pages.uhg;

import org.apache.log.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class DashboardPage extends BasePage  {        
    
    // Factory method
    public static DashboardPage getPage(WebDriver driver, Logger logHandler) {
        DashboardPage page = PageFactory.initElements(driver, DashboardPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
    
    public DashboardPage(WebDriver driver) {
        super(driver);
    }    
        
    @FindBy (xpath="//a[@id = 'activities']")
    public WebElement activitiesLink;
    
    @FindBy (xpath="//a[@id = 'newQuote']")
    public WebElement newQuoteLink;  

    @FindBy(xpath="//input [@name = 'caseName']")
    public WebElement caseNameField; //GroupProfilePageElement
    
    
    public void checkActivitiesMenuClickable() {
        logInfo("Checking if page is loaded");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(activitiesLink));
        wait.until(ExpectedConditions.elementToBeClickable(activitiesLink));
        logInfo("Page is loaded");
    }
    
    /**
     * Select new quote menu item
     */
    public void selectActivitiesMenuItem() {
        logInfo("Selecting new quote menu item...");
        Actions action = new Actions(driver);
        action.moveToElement(activitiesLink);
        activitiesLink.click();
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(newQuoteLink));
        wait.until(ExpectedConditions.elementToBeClickable(newQuoteLink));
        logInfo("New quote menu item is visible now");
        
    }
    private final static String LOADING_INDICATOR_XPATH_STRING = "//img[@src='../styles/themes/custom/images/loading.gif']";
    
    
	public void waitForLoadIndicatorToDisappear(){
		logInfo("In method waitForLoadIndicatorToDisappear...");
        WebDriverWait wait = new WebDriverWait(driver, 900);
        logInfo("Waiting for loadIndicator to disappear");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(LOADING_INDICATOR_XPATH_STRING)));
        logInfo("LoadIndicator is gone...");  
	}
    
    //img id = load_indicator
    
    /*
     * At this point, new quote menu item should be already selected, ready for click 
     */
    public GroupProfilePage goToNewQuote_1() {
        logInfo("Clicking new quote...");
        
        Actions action = new Actions(driver);
        action.moveToElement(newQuoteLink);
        action.click();
        action.perform();
        
        //newQuoteLink.click();
        logInfo("New Quote clicked");
        return GroupProfilePage.getPage(driver, logHandler);        
    }
    
    public GroupProfilePage goToNewQuote(){
    	logInfo("Select activities menu...");
    	WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.elementToBeClickable(activitiesLink));
    	activitiesLink.click();
    	logInfo("Clicking new quote...");
        wait.until(ExpectedConditions.elementToBeClickable(newQuoteLink));
        newQuoteLink.click();
        sleepFor(1);
        waitForLoadIndicatorToDisappear();
        /*if(driver.findElements(By.xpath("//a[@id = 'activities']")).size() != 0){
    		goToNewQuote();
    	}*/
    	return GroupProfilePage.getPage(driver, logHandler);
    }
    
    

}

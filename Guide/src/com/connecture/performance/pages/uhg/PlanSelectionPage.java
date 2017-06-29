package com.connecture.performance.pages.uhg;

import java.util.List;

import org.apache.log.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class PlanSelectionPage extends BasePage{
	
	// Factory method
    public static PlanSelectionPage getPage(WebDriver driver, Logger logHandler) {
    	PlanSelectionPage page = PageFactory.initElements(driver, PlanSelectionPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
        
    public PlanSelectionPage(WebDriver driver) {
        super(driver);
    }
    
    @FindBy (xpath = "//select[@id = '_sortdropdown']")
    public WebElement sortByCombo;
    
	public void orderByPrice(){
		logInfo("Sorting By price - low to high");
		logInfo("Waiting for sort combo to be visible"); 
		WebDriverWait wait = new WebDriverWait(driver, 300);
		wait.until(ExpectedConditions.visibilityOf(sortByCombo));
		logInfo("Selecting lowest to highest option"); 
		Select select = new Select(sortByCombo);
		select.selectByIndex(0);
		logInfo("Plans are now being sorted by lowest to highest price"); 
	}
	
	@FindBy (xpath = "//div[@class = 'rate _currency' and contains(text(),'$')]")
    public List<WebElement> ratings;
    
    public void waitForRatingsToBeLoaded(){
		logInfo("waitForRatingsToBeLoaded...");
		WebDriverWait wait = new WebDriverWait(driver, 1000);
		wait.until(ExpectedConditions.visibilityOfAllElements(ratings));		
		logInfo("Rates are loaded now...");		
	}
    
    private final static String UI_WIDGET_XPATH_STRING = "//div[@class='ui-widget-overlay ui-front']";
    
    public void waitForUIWidgetToDisappear(){
    	logInfo("In method waitForUIWidgetToDisappear...");
        WebDriverWait wait = new WebDriverWait(driver, 900);
        logInfo("Waiting for ui-widget to disappear");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(UI_WIDGET_XPATH_STRING)));
        logInfo("UI-Widget gone...");
    }
    
    private final static String BLOCK_UI_XPATH_STRING = "//div[@class='blockUI blockOverlay']";
    
    
    public void waitForBlockUIToDisappear() {
        logInfo("In method waitForBlockUIToDisappear...");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        logInfo("Waiting for blockui to disappear");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(BLOCK_UI_XPATH_STRING)));
        logInfo("BlockUI is gone...");        
        if(driver.findElements(By.xpath("//div[@class='ui-widget-overlay ui-front']")).size() > 0){
        	waitForUIWidgetToDisappear();
        }
        	
    }
    
    public void waitForElementToAppear(String elementXPath) {
        logInfo("In method waitForElementToAppear...");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        logInfo("Waiting for element with xpath: " + elementXPath);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(elementXPath)));
        logInfo("Element appeared!");    
        
    }
    @FindBy (xpath="//a[text() = 'View More Plans']")
	public WebElement viewMorePlans;
    
    public void checkViewMorePlansClickable(){
    	
    	logInfo("Checking view more plans link to be visible");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.visibilityOf(viewMorePlans));
        logInfo("View more plans link is visible now"); 
        logInfo("Checking view more plans link to be clickable");
        wait.until(ExpectedConditions.elementToBeClickable(viewMorePlans));
        logInfo("View more plans link is clickable now"); 
    }
    
    public void clickViewMorePlans(){
    	checkViewMorePlansClickable();
    	logInfo("Clicking View More Plans Link...");    	
        viewMorePlans.click();
        logInfo("View More Plans Link clicked");  
    }

    @FindBy (xpath="//div[contains(text(),'Unable to rate plan at this time.')]")
	public List<WebElement> unableRatesMessage;

    public int findAddToQuoteWithRates(){
    	int resize = 0;    	
    	if(unableRatesMessage.size()>0){
    		resize = unableRatesMessage.size();
    	}    	
    	for(int i=0; i<ratings.size(); i++){
    		WebElement rating = ratings.get(i);
    		if(!rating.getText().contains("$0.00")){
    			return resize + i;
    		}
    	}
    	return -1;
    }   
    
    @FindBy (xpath="//a[text() = 'Add to quote']")
	public List<WebElement> addToQuoteButtonList;
    
    public void checkAddToQuoteClickable() {
    	WebDriverWait wait = new WebDriverWait(driver, 300);
    	logInfo("Waiting for blockui to dissapear...");
    	waitForBlockUIToDisappear();
		logInfo("BlockUI is gone");
		waitForRatingsToBeLoaded();
        logInfo("Checking AddToQuote button list to be visible");
        wait.until(ExpectedConditions.visibilityOfAllElements((addToQuoteButtonList)));
        logInfo("Checking AddToQuote button list is visible now");
        logInfo("Checking AddToQuote button to be clickable");
        wait.until(ExpectedConditions.elementToBeClickable(addToQuoteButtonList.get(findAddToQuoteWithRates())));
        logInfo("AddToQuote button is clickable now");
        waitForBlockUIToDisappear();
    }     
    
    private final static String LOADING_INDICATOR_XPATH_STRING = "//img[@src='../styles/themes/custom/images/loading.gif']";
    
    
	public void waitForLoadIndicatorToDisappear(){
		logInfo("In method waitForLoadIndicatorToDisappear...");
        WebDriverWait wait = new WebDriverWait(driver, 900);
        logInfo("Waiting for loadIndicator to disappear");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(LOADING_INDICATOR_XPATH_STRING)));
        logInfo("LoadIndicator is gone...");  
	}
    
    public void clickAddToQuote() {    	
        logInfo("clickAddToQuote, button is clickable now"); 
        logInfo("Waiting for blockui to dissapear...");
		waitForBlockUIToDisappear();
		logInfo("BlockUI is gone");
		waitForRatingsToBeLoaded();
		WebElement addToQuoteButton = addToQuoteButtonList.get(findAddToQuoteWithRates());
		logInfo("Button is " + addToQuoteButton + ", clicking..." );
		addToQuoteButton.click();    	   
		logInfo("Add to Quote clicked...");
		waitForBlockUIToDisappear();        
    }
    
    @FindBy (xpath = "//a[contains(text(), 'Remove from quote')]")
    public WebElement removeFromQuoteButton;
    
    public void checkPlanAdded() {
    	logInfo("Checking Plan added");
    	logInfo("Checking Remove Button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.visibilityOf(removeFromQuoteButton));
        wait.until(ExpectedConditions.elementToBeClickable(removeFromQuoteButton));
        logInfo("Remove Button is clickable now"); 
        logInfo("Plan successfully added"); 
        waitForBlockUIToDisappear();
    }   
    
    @FindBy (xpath="//a[text() = 'Dental']")
    public WebElement dentalLink;
    
    public void checkDentalLinkClickable(){    	
    	logInfo("Waiting for blockui to dissapear...");
    	waitForBlockUIToDisappear();
		logInfo("BlockUI is gone");
    	logInfo("Checking Dental Link to be visible");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.visibilityOf(dentalLink));
        logInfo("Checking Dental Link to be clickable");
        wait.until(ExpectedConditions.elementToBeClickable(dentalLink));
        logInfo("Dental Link  is clickable now"); 
        waitForBlockUIToDisappear();
    }
    
    public void goToDental() {
    	logInfo("Clicking Dental Link...");    	
        dentalLink.click();
        logInfo("Dental Link clicked");       
    }
    

    @FindBy (xpath="//a[text() = 'Vision']")
    public WebElement visionLink;
    
    public void checkVisionLinkClickable(){    	
    	logInfo("Waiting for blockui to dissapear...");
    	waitForBlockUIToDisappear();
		logInfo("BlockUI is gone");
    	logInfo("Checking Vision Link to be visible");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.visibilityOf(visionLink));
        logInfo("Checking Vision Link to be clickable");
        wait.until(ExpectedConditions.elementToBeClickable(dentalLink));
        logInfo("Vision Link  is clickable now"); 
        waitForBlockUIToDisappear();
    }
    
    public void goToVision() {
    	logInfo("Clicking Vision Link...");    	
        visionLink.click();
        logInfo("Vision Link clicked"); 
    }  

    @FindBy (xpath="//a[text() = 'Basic Life & AD&D']")
    public WebElement basicLifeLink;       
    
    public void checkBasicLifeLinkClickable(){    	
    	logInfo("Waiting for blockui to dissapear...");
    	waitForBlockUIToDisappear();
		logInfo("BlockUI is gone");
    	logInfo("Checking BasicLife Link to be visible");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.visibilityOf(basicLifeLink));
        logInfo("Checking BasicLife Link to be clickable");
        wait.until(ExpectedConditions.elementToBeClickable(basicLifeLink));
        logInfo("BasicLife Link  is clickable now");
        waitForBlockUIToDisappear();
    }
    
    public void goToBasicLife() {
    	logInfo("Clicking BasicLife Link...");    	
        basicLifeLink.click();
        logInfo("BasicLife Link clicked"); 
    }
   
    
    @FindBy (xpath="//a[text() = 'Short Term Disability']")
    public WebElement shortTermDisabilityLink;
    
    public void checkShortTermDisabilityLinkClickable(){    	
    	logInfo("Waiting for blockui to dissapear...");
    	waitForBlockUIToDisappear();
		logInfo("BlockUI is gone");
    	logInfo("Checking shortTermDisability Link to be visible");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.visibilityOf(shortTermDisabilityLink));
        logInfo("Checking shortTermDisability Link to be clickable");
        wait.until(ExpectedConditions.elementToBeClickable(shortTermDisabilityLink));
        logInfo("Basic shortTermDisability Link  is clickable now");
        waitForBlockUIToDisappear();
    }
    
    public void goToSTD() {
    	logInfo("Clicking shortTermDisability Link...");    	
    	shortTermDisabilityLink.click();
        logInfo("ShortTermDisability Link clicked"); 
    }
    
    @FindBy (xpath="//a[text() = 'Long Term Disability']")
    public WebElement longTermDisabilityLink;
    
    public void checkLongTermDisabilityLinkClickable(){    	
    	logInfo("Waiting for blockui to dissapear...");
    	waitForBlockUIToDisappear();
		logInfo("BlockUI is gone");
    	logInfo("Checking longTermDisability Link to be visible");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.visibilityOf(longTermDisabilityLink));
        logInfo("Checking longTermDisability Link to be clickable");
        wait.until(ExpectedConditions.elementToBeClickable(longTermDisabilityLink));
        logInfo("Basic longTermDisability Link  is clickable now");
        waitForBlockUIToDisappear();
    }
    
    public void goToLTD() {
    	logInfo("Clicking longTermDisability Link...");    	
    	longTermDisabilityLink.click();
        logInfo("Vision longTermDisability clicked"); 
    }
    
    @FindBy (xpath="//a[contains(text() , 'Continue')]")
    public WebElement continueButton;   
    
    public void checkContinueButtonClickable() {
    	logInfo("Checking continue button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, 300);
     
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(BLOCK_UI_XPATH_STRING)));
        logInfo("Overlay is gone!");
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        logInfo("Continue button is clickable now"); 
    }   
    
    
    public QuoteSummaryPage goToQuoteSummaryPage() {
    	logInfo("Clicking Continue button...");    	
        continueButton.click();
        logInfo("Continue button clicked");
        return QuoteSummaryPage.getPage(driver, logHandler);
        
    }
    
}

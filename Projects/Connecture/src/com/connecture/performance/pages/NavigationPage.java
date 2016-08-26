package com.connecture.performance.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.bcbsmisg.PortalPage;

public class NavigationPage extends BasePage{
    
    private final static String BLOCK_UI_XPATH_STRING = "//div[@class='blockUI blockOverlay']";

	
	public NavigationPage(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	/************************************METHODS****************************/	
	
	public BasePage getNextPageClick() {
		// do{
			System.out.println("DEBUG - getNextPageClick iteration for..." + this);
			String nextPageButtonXPath = getNextPageButtonXpath();
			System.out.println("DEBUG - button xpath: " + nextPageButtonXPath);
			WebElement button = driver.findElement(By.xpath(nextPageButtonXPath));			
			System.out.println("DEBUG - element:" +  button);
			WebDriverWait wait = new WebDriverWait(driver, 300);
			wait.until(ExpectedConditions.elementToBeClickable(button));
			System.out.println("DEBUG - element is clickable now, clicking it...");
			button.click();
			System.out.println("DEBUG - button clicked...");
			waitForNextPageFullyLoaded();
		// }while(! nextPageFullyLoaded());
		
		return getNextPage();
	}
	
	protected void waitForNextPageFullyLoaded() {
		System.out.println("DEBUG - checking if next page is fully loaded...");
		String nextPageElementXPath = getNextPageElementXpath();
		System.out.println("DEBUG - element xpath: " + nextPageElementXPath);				
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(nextPageElementXPath)));
        System.out.println("Is fully loaded now...");
	}
	
	
	public String getNextPageButtonXpath(){
		return "//button[contains(@value, 'Continue')]";
	}
	
	public String getNextPageElementXpath(){
		return null;
	}
	
	public BasePage getNextPage(){
		return null;		
	}
	
	
	public BasePage getLogOutClick(){
		do{
			driver.findElement(By.xpath(getLogOutXpath())).click();
		}while(driver.findElements(By.xpath(getLogOutElementXpath())).size() < 0 );
		
		return new PortalPage(driver);
	} 
	
	/*************************LOGOUT CASCADE METHODS************************/
	
    public BasePage clickLogoutCascade(){
    	//Logs
    	logInfo("In method clickLogoutCascade...");
    	
    	WebDriverWait wait = new WebDriverWait(driver, 300);
    	logInfo("Waiting for element: "+getLogoutMenuXpath()+ " xpath");
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(getLogoutMenuXpath())));    
    	goToLogoutMenu();
    	
    	logInfo("Waiting for element: "+getLogOutElementXpath()+ " xpath");
    	WebElement logout = driver.findElement(By.xpath(getLogOutXpath())) ;
    	logout.click();
    	
		return new PortalPage(driver);
    }  
    public void goToLogoutMenu(){
    	logInfo("goToLogoutMenu - start");
		Actions action = new Actions(driver);
		do{
		    logInfo("goToLogoutMenu - clicking new Quote Button...");
			action.moveToElement(driver.findElement(By.xpath(getLogoutMenuXpath())));
			action.click();
			action.perform();
			logInfo("goToLogoutMenu - action performed, waiting for " + getLogOutXpath());
		}while(driver.findElements(By.xpath(getLogOutXpath())).size() < 1);
		logInfo("goToLogoutMenu - end");
    }
    public String getLogoutMenuXpath(){
    	return "//span[@class = 'welcomeName']";
    }
		
	public String getLogOutXpath(){
			return "//a[contains(text(),'Log Out')]";
	}
		
	public String getLogOutElementXpath(){
		return "//b[contains(text(), ' About Our Company')]";
	}
	/************************END OF LOGOUT CASCADE METHODS SECTION***********/
	
    public void waitForBlockUIToAppearAndDisappear() {
        logInfo("In method waitForBlockUIToAppearAndDisappear...");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        logInfo("Waiting for blockui to appear...");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(BLOCK_UI_XPATH_STRING)));
        logInfo("BlockUI is here!, waiting for it to disappear");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(BLOCK_UI_XPATH_STRING)));
        logInfo("BlockUI is gone...");        
    }
	
    public void waitForBlockUIToDisappear() {
        logInfo("In method waitForBlockUIToDisappear...");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        logInfo("Waiting for blockui to disappear");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(BLOCK_UI_XPATH_STRING)));
        logInfo("BlockUI is gone...");        
    }
    
    public void waitForElementToAppear(String elementXPath) {
        logInfo("In method waitForElementToAppear...");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        logInfo("Waiting for element with xpath: " + elementXPath);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(elementXPath)));
        logInfo("Element appeared!");    
        
    }
    


}

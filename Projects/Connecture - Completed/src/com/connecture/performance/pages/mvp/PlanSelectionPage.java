package com.connecture.performance.pages.mvp;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.NavigationPage;

public class PlanSelectionPage extends NavigationPage{
    

	public PlanSelectionPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	
	
	
	/********************************SUPERCLASS METHODS***********************/
	@Override
	public String getNextPageElementXpath() {		
		return "//a[text() = 'Generate Proposal']";
	}
	@Override
	public QuoteSummaryPage getNextPage() {		
		return new QuoteSummaryPage(driver);
	}
	
	@Override
	public String getNextPageButtonXpath() {		
		return "//a[text() = 'Continue']";
	}	
	/************************************METHODS****************************/

	public void waitForRatingsToBeLoaded(){
		logInfo("waitForRatingsToBeLoaded...");
		WebDriverWait wait = new WebDriverWait(driver, 80);
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//td[contains(text(),'EE')]")));		
		logInfo("Rates are loaded now...");	
		
	}
	public void clickAddToQuote (){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);

		
     
		
		logInfo("Finding add to quote..");      
        List<WebElement> addToQuote = driver.findElements(By.xpath("//a[text() = 'Add to quote']"));        
        logInfo("addToQuote is: " + addToQuote);
        //WebElement addToQuoteButton = addToQuote.get(1);
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("(//a[text() = 'Add to quote'])[1]"))));
        logInfo("clickAddToQuote, button is clickable now"); 
        logInfo("Waiting for blockui to dissapear...");
		waitForBlockUIToDisappear();
		logInfo("BlockUI is gone");
		waitForRatingsToBeLoaded();
		WebElement addToQuoteButton = driver.findElement(By.xpath("(//a[text() = 'Add to quote'])[1]"));
		logInfo("Button is " + addToQuoteButton + ", clicking..." );
		addToQuoteButton.click();    	   
		logInfo("Add to Quote clicked...");
		waitForBlockUIToDisappear();
		
	}
	
}

package com.connecture.performance.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class HeaderMenu extends NavigationPage{
	
	
	public HeaderMenu(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);	
	}

	@FindBy (xpath = "//img[contains(following-sibling::text(),'Activities')]/..")
	WebElement activities;
	
	@FindBy (xpath = "//a[text()= 'Small Group Quick']")
	WebElement smallGroupQuick;
	
	Actions action = new Actions(driver);
	/*********************************METHODS*********************************/
	
	public void goToActivities (String xpathNextLevel){
	    logInfo("goToActivities - start");
		do{
		    logInfo("goToActivities - clicking activities button");    
			action.moveToElement(activities);
		    action.click();
		    action.perform();
		    logInfo("goToActivities - action performed, waiting for " + xpathNextLevel);
		}while(driver.findElements(By.xpath(xpathNextLevel)).size() < 1);
		logInfo("goToActivities - end");
	}
	
	public void goToNewQuote( String xpathNewQuote, String xpathNextLevel){
	    logInfo("goToNewQuote - start");
		goToActivities(xpathNewQuote);
		do{
		    logInfo("goToNewQuote - clicking new Quote Button...");
			action.moveToElement(driver.findElement(By.xpath(xpathNewQuote)));
			action.click();
			action.perform();
			logInfo("goToNewQuote - action performed, waiting for " + xpathNextLevel);
		}while(driver.findElements(By.xpath(xpathNextLevel)).size() < 1);
		logInfo("goToNewQuote - end");
	}
	
	public void goToSmallGroup(String newQuoteXpath, String xpathSmallGroup, String xpathNextPage){
	    logInfo("goToSmallGroup - start");
		goToNewQuote(newQuoteXpath, xpathSmallGroup);
	    logInfo("goToSmallGroup - clicking Small Group button ...");	    
	    Actions action = new Actions(driver);    
		action.moveToElement(driver.findElement(By.xpath(xpathSmallGroup)));
		action.click();
		action.perform();
		logInfo("goToSmallGroup - action performed, waiting for " + xpathNextPage);
		WebDriverWait wait = new WebDriverWait(driver, 300);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathNextPage)));
		logInfo("goToSmallGroup - end");
	}
	
	public void goToSmallGroupQuick(String newQuoteXpath, String xpathSmallGroupQuick, String xpathNextPage){
		goToNewQuote(newQuoteXpath, xpathSmallGroupQuick);
		do{
			action.moveToElement(smallGroupQuick);
			action.click();
			action.perform();
		}while(driver.findElements(By.xpath(xpathNextPage)).size() < 1);
	}
}

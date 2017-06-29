package com.connecture.performance.pages.mvp;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import com.connecture.performance.pages.HeaderMenu;
import com.connecture.performance.pages.NavigationPage;


public class DashboardPage extends NavigationPage{

	public DashboardPage(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver,this);
	}
	HeaderMenu menu = null;
		
	@FindBy (xpath = "//a[@id = 'activities']")
	WebElement activities;
	
	@FindBy (xpath = "//a[@id = 'newQuote']")
	WebElement newQuote;
	
	@FindBy (xpath = "//a[@id = 'sgQuote']")
	WebElement smallGroup;

	
	
	/********************************SUPERCLASS METHODS****************************/
	@Override
	public String getNextPageElementXpath() {	
		return "//button[@value= 'View']";
	}
	
	@Override
	public NavigationPage getNextPage() {		
		return null;//TODO
	}

	@Override
	public String getNextPageButtonXpath() {		
		return "//img[@id='quickSearchButton']";
	}	
	Actions action = new Actions(driver);
	/************************************METHODS****************************/	

	
	public void goToActivities (){
		do{
			action.moveToElement(activities);
		    action.click();
		    action.perform();
		}while(driver.findElements(By.xpath("//a[@id = 'newQuote']")).size() < 1);
	}
	
	public void goToNewQuote(){
		goToActivities();
		do{
			action.moveToElement(driver.findElement(By.xpath("//a[@id = 'newQuote']")));
			action.click();
			action.perform();		
		}while(driver.findElements(By.xpath("//a[@id = 'sgQuote']")).size() < 1);		
	}
	
	//public void goToSmallGroup(){
		//goToActivities();
		//goToNewQuote();
		//do{
			//action.moveToElement(driver.findElement(By.xpath("//a[@id = 'sgQuote']")));
			//action.click();
			//action.perform();
			//}while(driver);
	
	

	//}
			}
	
	


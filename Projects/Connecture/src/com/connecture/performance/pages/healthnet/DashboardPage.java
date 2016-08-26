package com.connecture.performance.pages.healthnet;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import com.connecture.performance.pages.HeaderMenu;
import com.connecture.performance.pages.NavigationPage;

public class DashboardPage extends NavigationPage{

	public DashboardPage(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver,this);
	}
	HeaderMenu menu = null;
	
	@FindBy(xpath="//input [@class = 'searchtext']")
	public WebElement searchTextBox;	
	
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
	/************************************METHODS****************************/	
	
	public void enterSearchCriteria(String criteria){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(searchTextBox));
		boolean loop = true;
		do{
			searchTextBox.sendKeys(criteria);
			if(searchTextBox.getAttribute("value").equals(criteria)){
				loop = false;
			}
			else{
				searchTextBox.clear();
			}
		}while(loop);
		
		
	}
	
	
	

	
	
}

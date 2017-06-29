package com.connecture.performance.pages.mvp;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.NavigationPage;

public class LoginPage extends NavigationPage {
	
	public LoginPage(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver,this);
	}
	
	@FindBy(xpath="//input [@name = 'username']")
	public WebElement usernameField;
	
	@FindBy(xpath="//input [@name = 'password']")
	public WebElement passwordField;
	
	/********************************SUPERCLASS METHODS***********************/
	@Override
	public String getNextPageElementXpath() {		
		return "//input [@class = 'searchtext']";
	}
	@Override
	public DashboardPage getNextPage() {		
		return new DashboardPage(driver);
	}
	
	@Override
	public String getNextPageButtonXpath() {		
		return "//button [@value = 'Login']";
	}	
	/************************************METHODS****************************/	
	 
	public void fillLoginForm(String username, String password){		
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(usernameField));
		usernameField.clear();
		usernameField.sendKeys(username);
		
		wait.until(ExpectedConditions.visibilityOf(passwordField));
		passwordField.clear();
		passwordField.sendKeys(password);
	}
	
	
	
}

package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class LoginPage extends BasePage{
	
	public  LoginPage(WebDriver driver) {
	  super (driver);
	}
	
	public static LoginPage getPage(WebDriver driver, Logger logHandler) {
		LoginPage  page = PageFactory.initElements(driver, LoginPage.class);
	    page.setLogHandler(logHandler);
	    return page;	    
	}
	
	@FindBy(xpath="//input[@id = 'username']")
	public WebElement usernameField;

	public void fillUsername (String username){
		logInfo("Filling Username...");
	    WebDriverWait wait = new WebDriverWait(driver, timeOut);
	    wait.until(ExpectedConditions.visibilityOf(usernameField));       
	    usernameField.sendKeys(username);
	    if(!usernameField.getAttribute("value").equals(username)){
	    	usernameField.clear();
	    	fillUsername(username);
	   	}
	}
	   
	@FindBy(xpath="//input[@id = 'password']")
	public WebElement passwordField;

	public void fillPassword (String password){
		logInfo("Filling Password...");
	    WebDriverWait wait = new WebDriverWait(driver, timeOut);
	    wait.until(ExpectedConditions.visibilityOf(passwordField));       
	    passwordField.sendKeys(password);
	    if(!passwordField.getAttribute("value").equals(password)){
	    	passwordField.clear();
	    	fillUsername(password);
	    }
	}
	
	@FindBy(xpath="//a[@id = 'loginLink']")
	public WebElement loginButton;
	public void loginButtonClickable(){
		logInfo("Checking Login button to be clickable");
	    WebDriverWait wait = new WebDriverWait(driver, timeOut);
	    wait.until(ExpectedConditions.visibilityOf(loginButton));
	    wait.until(ExpectedConditions.elementToBeClickable(loginButton));
	    logInfo("Login button is clickable now");
	}
	    
	public CuramTestPage goToCuramTestPage(){
		logInfo("Clicking Login Link...");    	
		loginButton.click();
	    logInfo("Login Link clicked");
	    return CuramTestPage.getPage(driver, logHandler);
	}
	
}




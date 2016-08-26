package com.connecture.performance.pages.uhg;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;


public class LoginPage extends BasePage {
        
    
    // Factory method
    public static LoginPage getPage(WebDriver driver, Logger logHandler) {
        LoginPage page = PageFactory.initElements(driver, LoginPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
    
    public LoginPage(WebDriver driver) {
        super(driver);
    }    
        
    @FindBy(xpath="//input [@name = 'username']")
    public WebElement usernameField;
    
    @FindBy(xpath="//input [@name = 'password']")
    public WebElement passwordField;
    
    @FindBy (xpath="//*[@name = 'login.button']")
    public WebElement loginButton;    
    
    
    public void fillOutLoginForm(String user, String password) {
        logInfo("Filling out login info for user: " + user);
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(usernameField));
        usernameField.clear();
        usernameField.sendKeys(user);
        
        wait.until(ExpectedConditions.visibilityOf(passwordField));
        passwordField.clear();
        passwordField.sendKeys(password);
        
        sleepFor(2);
        logInfo("Login form filled out");
    }
    
    public DashboardPage goToDashboardPage() {
        logInfo("Clicking login...");
        loginButton.click();
        logInfo("Login clicked");
        return DashboardPage.getPage(driver, logHandler);        
    }
    
    public void checkLoginButtonClickable() {
        logInfo("DEBUG - checking login button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        logInfo("login button is clickable now");        
        
    }
    
}

package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class StateAdvantageLinksPage extends BasePage{

	public StateAdvantageLinksPage(WebDriver driver) {
		super(driver);
	}

	public static StateAdvantageLinksPage getPage(WebDriver driver, Logger logHandler) {
		StateAdvantageLinksPage  page = PageFactory.initElements(driver, StateAdvantageLinksPage.class);
	    page.setLogHandler(logHandler);
	    return page;	    
	}
	
	@FindBy(xpath="//a[text() = 'Portal']")
    public WebElement portalLink;
	
	public void checkPortalLinkClickable(){
		logInfo("Checking Portal link to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(portalLink));
        wait.until(ExpectedConditions.elementToBeClickable(portalLink));
        logInfo("Portal link is clickable now");
	}
	
	public PortalPage goToPortalPage(){
		logInfo("Clicking Portal link...");    	
        portalLink.click();
        logInfo("Portal link clicked");
        return PortalPage.getPage(driver, logHandler);
	}
	
	@FindBy(xpath="//a[contains(text(), 'Curam Test')]")
    public WebElement curamTestLink;
	
	public void checkCuramTestLinkClickable(){
		logInfo("Checking Curam Test link to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(curamTestLink));
        wait.until(ExpectedConditions.elementToBeClickable(curamTestLink));
        logInfo("Curam Test link is clickable now");
	}
	
	public CuramTestPage goToCuramTestPage(){
		logInfo("Clicking Curam Test link...");    	
		curamTestLink.click();
        logInfo("Curam Test link clicked");
        return CuramTestPage.getPage(driver, logHandler);
	}
	
	@FindBy(xpath="//a[text()='Login']")
    public WebElement loginLink;
	
	public void checkLoginLinkClickable(){
		logInfo("Checking Login Link to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(loginLink));
        wait.until(ExpectedConditions.elementToBeClickable(loginLink));
        logInfo("Login Link is clickable now");
	}
	
	public LoginPage goToLoginPage(){
		logInfo("Clicking Login Link...");    	
		loginLink.click();
        logInfo("Login Link clicked");
        return LoginPage.getPage(driver, logHandler);
	}
	
}

package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class ConfirmationAndSignaturePage extends BasePage{
	public ConfirmationAndSignaturePage(WebDriver driver){
		super (driver);
		
	}

	public static ConfirmationAndSignaturePage getPage(WebDriver driver, Logger logHandler) {
		ConfirmationAndSignaturePage  page = PageFactory.initElements(driver, ConfirmationAndSignaturePage.class);
	    page.setLogHandler(logHandler);
	    return page;	
}
	
	public void waitForLoadingPopupToDisappear(){		
		logInfo("Checking Continue button to be clickable");		
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='actionindicator']")));
        logInfo("Start Button is clickable now");
	}
	

	/***************************************************Methods**************************************************/
	
	
	 @FindBy(xpath="//input[@id = 'CHECKBOX5CB57F5A701BBDB6EBCG']")
	    public WebElement agreeCheckbox;
		
		public void checkAgreeCheckboxClickable(){
			logInfo("Checking Agree Checkbox to be clickable");
	        WebDriverWait wait = new WebDriverWait(driver, timeOut);
	        wait.until(ExpectedConditions.visibilityOf(agreeCheckbox));
	        wait.until(ExpectedConditions.elementToBeClickable(agreeCheckbox));
	        logInfo("Agree Checkbox is clickable now");
		}
	
	
	
	
	@FindBy(xpath="//input[contains(@name,'First_Name')]")
	public WebElement firstnameField;

	public void fillFirstname (String firstname){
		logInfo("Filling Firtsname...");
	    WebDriverWait wait = new WebDriverWait(driver, timeOut);
	    wait.until(ExpectedConditions.visibilityOf(firstnameField));       
	    firstnameField.sendKeys(firstname);
	    if(!firstnameField.getAttribute("value").equals(firstname)){
	    	firstnameField.clear();
	    	fillFirstname(firstname);
	   	}
	}
	   
	@FindBy(xpath="//input[contains(@name,'Last_Name')]")
	public WebElement lastnameField;

	public void fillLastname (String lastname){
		logInfo("Filling Lastname...");
	    WebDriverWait wait = new WebDriverWait(driver, timeOut);
	    wait.until(ExpectedConditions.visibilityOf(lastnameField));       
	    lastnameField.sendKeys(lastname);
	    if(!lastnameField.getAttribute("value").equals(lastname)){
	    	lastnameField.clear();
	    	fillLastname(lastname);
	    }
	}
	
	    @FindBy(xpath="//a[@class = 'buttonDone']")
	    public WebElement submitButton;
		
		public void checkSubmitButtonClickable(){
			logInfo("Checking Submit button to be clickable");
	        WebDriverWait wait = new WebDriverWait(driver, timeOut);
	        wait.until(ExpectedConditions.visibilityOf(submitButton));
	        wait.until(ExpectedConditions.elementToBeClickable(submitButton));
	        logInfo("Submit button is clickable now");
		}
	    
	    
		public SubmitPage goToSubmitPage(){
			logInfo("Clicking Submit Button...");    	
			agreeCheckbox.click();
			submitButton.click();
		    logInfo("Login Link clicked");
		   return SubmitPage.getPage(driver, logHandler);
		}
	    
}






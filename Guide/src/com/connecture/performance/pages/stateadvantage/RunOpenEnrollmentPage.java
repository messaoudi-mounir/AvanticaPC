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

public class RunOpenEnrollmentPage extends BasePage{
	
	public RunOpenEnrollmentPage(WebDriver driver){
		super(driver);

}
	
	public static RunOpenEnrollmentPage getPage(WebDriver driver, Logger logHandler) {
		RunOpenEnrollmentPage  page = PageFactory.initElements(driver, RunOpenEnrollmentPage.class);
	    page.setLogHandler(logHandler);
	    return page;	
}

	public void waitForLoadingPlansPopupToDisappear(){		
		logInfo("Checking Continue button to be clickable");		
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='actionindicator']")));
        logInfo("Start Button is clickable now");
	
	}

/*****************************************Methods*************************************************/

   
    @FindBy(xpath="//input[@id = 'externalCodeInitial']")
    public WebElement enrollmentIDField;

    public void fillEnrollmentID(String enrollmentID){
	logInfo("Filling Enrollment ID...");
    WebDriverWait wait = new WebDriverWait(driver, timeOut);
    wait.until(ExpectedConditions.visibilityOf(enrollmentIDField));       
    enrollmentIDField.sendKeys(enrollmentID);
    if(!enrollmentIDField.getAttribute("value").equals(enrollmentID)){
    	enrollmentIDField.clear();
    	fillEnrollmentID(enrollmentID);
    }
    }
    
    
    @FindBy(xpath="//a[@id = 'getDetailsLinkInitial']")
    public WebElement getEmployeeButton;
	
	public void checkGetEmployeeButtonClickable(){
		logInfo("Checking Get Employee button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(getEmployeeButton));
        wait.until(ExpectedConditions.elementToBeClickable(getEmployeeButton));
        getEmployeeButton.click();
        logInfo("Get Employee button is clickable now");
	}
	
	
	
	
	
	@FindBy(xpath="//a[@id = 'goToEmployeeInitial']")
    public WebElement employeeLinkButton;
	
	public void checkEmployeeLinkButtonClickable(){
		logInfo("Checking Go To Employee Link button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(employeeLinkButton));
        wait.until(ExpectedConditions.elementToBeClickable(employeeLinkButton));
        logInfo("Go To Employee Link button is clickable now");
	}
	
	public GetStartedPage goToGetStartedPage(){
		logInfo("Clicking Go To Employee Link..."); 
		checkEmployeeLinkButtonClickable();
		employeeLinkButton.click();
        logInfo("Go To Employee Link clicked");
        return GetStartedPage.getPage(driver, logHandler);
	}
}
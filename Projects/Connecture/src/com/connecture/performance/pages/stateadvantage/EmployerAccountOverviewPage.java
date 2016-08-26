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

public class EmployerAccountOverviewPage extends BasePage {

	public EmployerAccountOverviewPage(WebDriver driver) {
		super(driver);		
	}
	
	public static EmployerAccountOverviewPage getPage(WebDriver driver, Logger logHandler) {
		EmployerAccountOverviewPage  page = PageFactory.initElements(driver, EmployerAccountOverviewPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
	
	@FindBy (xpath="//a[@class = 'buttonPrimary']")
	public WebElement enrollInPlansButton;
	
	public void checkEnrollInPlansButtonToBeClickable(){
		logInfo("Checking Enroll In Plans Button to be clickable");
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(enrollInPlansButton));
        wait.until(ExpectedConditions.elementToBeClickable(enrollInPlansButton));
        logInfo("Enroll In Plans Button is now clickable");
	}	
	
	public void waitForPopupToDisappear(){        
        logInfo("Checking Continue button to be clickable");        
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='actionindicator']")));
        logInfo("Continue button is clickable now");
    }
	
	public EnrollmentGetStartedPage goToEnrollmentGetStartedPage(){        
		logInfo("Clicking Enroll In Plans Button...");     
		checkEnrollInPlansButtonToBeClickable();
        alternativeClick(enrollInPlansButton);
        waitForPopupToDisappear();
        logInfo("Enroll In Plans Button clicked...");
        return EnrollmentGetStartedPage.getPage(driver, logHandler);
    }
	
	@FindBy (xpath="//h2[@class = '_congratsMessage congrats']")
	public WebElement congratsButton;
	
	public void checkCongratsButtonToBeClickable(){
		logInfo("Checking congratsButton to be clickable");
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(congratsButton));
        wait.until(ExpectedConditions.elementToBeClickable(congratsButton));
        logInfo("congratsButton is now clickable");
	}	
	
}

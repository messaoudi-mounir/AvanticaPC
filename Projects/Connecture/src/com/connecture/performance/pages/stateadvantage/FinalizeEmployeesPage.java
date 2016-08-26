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

public class FinalizeEmployeesPage extends BasePage{
	public FinalizeEmployeesPage(WebDriver driver) {
		super(driver);		
	}
	
	public static FinalizeEmployeesPage getPage(WebDriver driver, Logger logHandler) {
		FinalizeEmployeesPage  page = PageFactory.initElements(driver, FinalizeEmployeesPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
	public void waitForPopupToDisappear(){        
        logInfo("Checking Continue button to be clickable");        
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='actionindicator']")));
        logInfo("Continue button is clickable now");
    }
	
	@FindBy (xpath="//a[contains(@class, 'buttonNext')]")
	public WebElement continueButton;
		
	public void checkContinueButtonToBeClickable(){
		logInfo("Checking Continue button to be clickable");
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(continueButton));
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        logInfo("Continue button is now clickable");
	}
	
	public EligibilityConfirmationPage goToEligibilityConfirmationPage(){        
        logInfo("Clicking Continue Button...");            
        continueButton.click();
        logInfo("Continue button clicked...");
        return EligibilityConfirmationPage.getPage(driver, logHandler);
    }
	
	public EnrollmentCompanyContributionPage goToEnrollmentCompanyContributionPage(){
		logInfo("Clicking Next Button...");            
        continueButton.click();
        waitForPopupToDisappear();
        logInfo("Next button clicked...");
        return EnrollmentCompanyContributionPage.getPage(driver, logHandler);
	}
}

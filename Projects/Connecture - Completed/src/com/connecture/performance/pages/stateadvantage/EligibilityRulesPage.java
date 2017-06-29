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

public class EligibilityRulesPage extends BasePage{

	public EligibilityRulesPage(WebDriver driver) {
		super(driver);		
	}
	
	public static EligibilityRulesPage getPage(WebDriver driver, Logger logHandler) {
		EligibilityRulesPage  page = PageFactory.initElements(driver, EligibilityRulesPage.class);
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
	
	public EnrollmentConfirmationPage goToEnrollmentConfirmationPage(){        
        logInfo("Clicking Continue Button...");            
        continueButton.click();
        waitForPopupToDisappear();
        logInfo("Continue button clicked...");
        return EnrollmentConfirmationPage.getPage(driver, logHandler);
    }

	
}

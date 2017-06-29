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

public class EnrollmentCompanyContributionPage extends BasePage{

	public EnrollmentCompanyContributionPage(WebDriver driver) {
		super(driver);		
	}
	
	public static EnrollmentCompanyContributionPage getPage(WebDriver driver, Logger logHandler) {
		EnrollmentCompanyContributionPage  page = PageFactory.initElements(driver, EnrollmentCompanyContributionPage.class);
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
	public WebElement nextButton;
		
	public void checkNextButtonToBeClickable(){
		logInfo("Checking Next button to be clickable");
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(nextButton));
        wait.until(ExpectedConditions.elementToBeClickable(nextButton));
        logInfo("Next button is now clickable");
	}
	
	public EligibilityRulesPage goToEligibilityRulesPage(){        
        logInfo("Clicking Next Button..."); 
        checkNextButtonToBeClickable();
        nextButton.click();
        waitForPopupToDisappear();
        logInfo("Next button clicked...");
        return EligibilityRulesPage.getPage(driver, logHandler);
    }
	
}

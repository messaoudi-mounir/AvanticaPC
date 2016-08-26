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

public class ContributionTypePage extends BasePage {   

    /**
     * Constructor has to be public to be used by Page Factory, but we should not call this constructor externally
     */
    public ContributionTypePage(WebDriver driver) {
        super(driver);
    }
    
    // Factory method
    public static ContributionTypePage getPage(WebDriver driver, Logger logHandler) {
        ContributionTypePage  page = PageFactory.initElements(driver, ContributionTypePage.class);
        page.setLogHandler(logHandler);
        return page;
    }

    public void waitForLoadingPlansPopupToDisappear(){		
		logInfo("Checking Continue button to be clickable");		
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='actionindicator']")));
        logInfo("Continue button is clickable now");
	}
    
	@FindBy (xpath="//a[contains(@class, 'buttonNext _show_contribution')]")
	public WebElement continueButton;
	
	public void checkContinueButtonClickable(){		
		logInfo("Checking Continue button to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(continueButton));
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        logInfo("Continue button is clickable now");
	}
	
	public AllEmployeesReferencePlanContributionsPage goToAllEmployeesReferencePlanContributionsPage(){
		logInfo("Clicking Continue Button...");    
		continueButton.click();
        logInfo("Continue button clicked...");
        return AllEmployeesReferencePlanContributionsPage.getPage(driver, logHandler);
	}
  
}

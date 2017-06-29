package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class EligibilityGetStartedPage extends BasePage{

	public EligibilityGetStartedPage(WebDriver driver) {
		super(driver);
	}
	
	public static EligibilityGetStartedPage getPage(WebDriver driver, Logger logHandler) {
		EligibilityGetStartedPage  page = PageFactory.initElements(driver, EligibilityGetStartedPage.class);
        page.setLogHandler(logHandler);
        return page;
    }

	@FindBy (xpath="//a[contains(@class, 'buttonSaveAndExit')]")
	public WebElement saveAndExitButton;
	
	public void checkSaveAndExitButtonClickable(){		
		logInfo("Checking save and exit button to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(saveAndExitButton));
        wait.until(ExpectedConditions.elementToBeClickable(saveAndExitButton));
        logInfo("Save and exit button is clickable now");
	}
	
	@FindBy (xpath="//a[contains(@class, 'buttonOk')]")
	public WebElement saveAndExitPopupButton;
	
	public void saveAndExit(){
		logInfo("Clicking Save And Exit Button...");    
		saveAndExitButton.click();
		logInfo("Checking save and exit popup button to be clickable");
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(saveAndExitPopupButton));
        wait.until(ExpectedConditions.elementToBeClickable(saveAndExitPopupButton));
        logInfo("Save and exit popup button is now clickable");
        saveAndExitPopupButton.click();
        logInfo("Save And Exit Button clicked...");        
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
	
	public EmployerInformationPage goToEmployerInformationPage(){        
        logInfo("Clicking Next Button...");            
        nextButton.click();
        logInfo("Next button clicked...");
        return EmployerInformationPage.getPage(driver, logHandler);
    }
	
}

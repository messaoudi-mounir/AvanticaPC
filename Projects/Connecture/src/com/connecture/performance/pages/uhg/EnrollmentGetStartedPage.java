package com.connecture.performance.pages.uhg;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class EnrollmentGetStartedPage extends BasePage{

	public EnrollmentGetStartedPage(WebDriver driver) {
		super(driver);
	}
	
	public static EnrollmentGetStartedPage getPage(WebDriver driver, Logger logHandler) {
		EnrollmentGetStartedPage page = PageFactory.initElements(driver, EnrollmentGetStartedPage.class);
        page.setLogHandler(logHandler);
        return page;
    }

	@FindBy (xpath = "//a[@class = 'buttonNext']")
	WebElement nextButton;
	
	public void checkNextButtonToBeClickable(){
		logInfo("In method checkNextButtonToBeClickable");
        WebDriverWait wait = new WebDriverWait(driver, 600);
        logInfo("Checking Next button to be Visible");
        wait.until(ExpectedConditions.visibilityOf(nextButton));
        logInfo("Checking Next button to be clickable");
        wait.until(ExpectedConditions.elementToBeClickable(nextButton));
        logInfo("Next button is clickable now");
	}
	
	public EmployerInformationPage goToEnrollGetStarted(){
    	logInfo("Clicking enroll button...");    	
    	nextButton.click();
        logInfo("Enroll button clicked");       
        return EmployerInformationPage.getPage(driver, logHandler);
    }
	
}

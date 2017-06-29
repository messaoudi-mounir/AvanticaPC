package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class HowManyPlansDoYouWantToOfferPage extends BasePage{

	public HowManyPlansDoYouWantToOfferPage(WebDriver driver) {
		super(driver);
	}

	public static HowManyPlansDoYouWantToOfferPage getPage(WebDriver driver, Logger logHandler) {
		HowManyPlansDoYouWantToOfferPage  page = PageFactory.initElements(driver, HowManyPlansDoYouWantToOfferPage.class);
        page.setLogHandler(logHandler);
        return page;
    }	
	
	@FindBy (xpath="//a[@class = 'buttonNext _navigateForward _navigate _navigateTooltip']")
	public WebElement nextQuestionButton;
	
	public void checkNextQuestionButtonClickable(){		
		logInfo("Checking Next Question button to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(nextQuestionButton));
        wait.until(ExpectedConditions.elementToBeClickable(nextQuestionButton));
        logInfo("Next Question button is clickable now");
	}
	
	public WhichCarrierPlansWouldYouLikeToOfferToEmployeesPage goToWhichCarrierPlansWouldYouLikeToOfferToEmployeesPage(){
		logInfo("Clicking Continue Button...");    
		nextQuestionButton.click();
        logInfo("Continue button clicked...");
        return WhichCarrierPlansWouldYouLikeToOfferToEmployeesPage.getPage(driver, logHandler);
	}
	
	@FindBy (xpath="//label[@for = 'radio_q5allplan']")
	public WebElement allPlansRadio;
	
	public void checkAllPlansRadioButtonClickable(){		
		logInfo("Checking all plans radio button to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(allPlansRadio));
        wait.until(ExpectedConditions.elementToBeClickable(allPlansRadio));
        logInfo("all plans radio button is clickable now");
	}
	
	public void selectAllPlans(){
		checkAllPlansRadioButtonClickable();
		logInfo("Selecting all plans radio button");
		allPlansRadio.click();
		logInfo("All plans radio button selected");
	}
	
}

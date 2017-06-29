package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class HowWouldYouLikeToShopForPlansPage extends BasePage{

	public HowWouldYouLikeToShopForPlansPage(WebDriver driver) {
		super(driver);		
	}

	public static HowWouldYouLikeToShopForPlansPage getPage(WebDriver driver, Logger logHandler) {
		HowWouldYouLikeToShopForPlansPage  page = PageFactory.initElements(driver, HowWouldYouLikeToShopForPlansPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
	@FindBy (xpath="//a[@class = 'buttonNext _erSelectPlan']")
	public WebElement continueButton;
	
	public void checkContinueButtonClickable(){		
		logInfo("Checking Continue button to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(continueButton));
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        logInfo("Continue button is clickable now");
	}
	
	@FindBy (xpath="//span[contains(text(), 'One carrier')]")
	public WebElement oneCarrierRadio;
	
	public void checkOneCarrierRadioButtonClickable(){		
		logInfo("Checking one carrier radio button to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(oneCarrierRadio));
        wait.until(ExpectedConditions.elementToBeClickable(oneCarrierRadio));
        logInfo("One carrier radio button is clickable now");
	}
	
	public void selectOneCarrier(){
		logInfo("Selecting One Carrier radio button");
		oneCarrierRadio.click();
		logInfo("One Carrier radio button selected");
	}
	public HowManyPlansDoYouWantToOfferPage goToHowManyPlansDoYouWantToOfferPage(){
		logInfo("Clicking Continue Button...");    
		continueButton.click();
        logInfo("Continue button clicked...");
        return HowManyPlansDoYouWantToOfferPage.getPage(driver, logHandler);        
	}
	
}

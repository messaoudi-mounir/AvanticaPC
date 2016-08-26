package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class TypeOfPlansPage extends BasePage{

	public TypeOfPlansPage(WebDriver driver) {
		super(driver);
	}

	public static TypeOfPlansPage getPage(WebDriver driver, Logger logHandler) {
		TypeOfPlansPage  page = PageFactory.initElements(driver, TypeOfPlansPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
	@FindBy(xpath="//a[contains(@class, 'buttonNext _navigateForward')]")
    public WebElement nextQuestionButton;
	
	public void checkNextQuestionButtonClickable(){
		logInfo("Checking Next Question Button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(nextQuestionButton));
        wait.until(ExpectedConditions.elementToBeClickable(nextQuestionButton));
        logInfo("Next Question Button is clickable now");
	}
	
	public MetalLevelPage goToMetalLevelPage(){
		logInfo("Clicking Next Question Button...");    	
		nextQuestionButton.click();
        logInfo("Next Question Button clicked");
        return MetalLevelPage.getPage(driver, logHandler);
	}

}

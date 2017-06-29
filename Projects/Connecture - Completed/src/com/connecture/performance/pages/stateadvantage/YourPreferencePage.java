package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class YourPreferencePage extends BasePage{

	public YourPreferencePage(WebDriver driver) {
		super(driver);
	}

	public static YourPreferencePage getPage(WebDriver driver, Logger logHandler) {
		YourPreferencePage  page = PageFactory.initElements(driver, YourPreferencePage.class);
        page.setLogHandler(logHandler);
        return page;
    }

	@FindBy(xpath="//a[contains(@class, 'startPA')]")
    public WebElement startButton;
	
	public void checkStartButtonClickable(){
		logInfo("Checking Start Button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(startButton));
        wait.until(ExpectedConditions.elementToBeClickable(startButton));
        logInfo("Start Button is clickable now");
	}
	
	public TypeOfPlansPage goToTypeOfPlansPage(){
		logInfo("Clicking Start Button...");    	
		startButton.click();
        logInfo("Start Button clicked");
        return TypeOfPlansPage.getPage(driver, logHandler);
	}
	
}

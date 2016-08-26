package com.connecture.performance.pages.uhg;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.connecture.performance.pages.BasePage;

public class EmployerInformationPage extends BasePage{
	
	public EmployerInformationPage(WebDriver driver) {
		super(driver);
	}
	
	public static EmployerInformationPage getPage(WebDriver driver, Logger logHandler) {
		EmployerInformationPage page = PageFactory.initElements(driver, EmployerInformationPage.class);
	    page.setLogHandler(logHandler);
	    return page;
	}
	
	@FindBy (xpath = "//a[@class = 'buttonNext']")
	WebElement nextButton;
}

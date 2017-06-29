package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.connecture.performance.pages.BasePage;

public class EnrollmentEmployerInformationPage extends BasePage{

	public EnrollmentEmployerInformationPage(WebDriver driver) {
		super(driver);		
	}
	
	public static EnrollmentEmployerInformationPage getPage(WebDriver driver, Logger logHandler) {
		EnrollmentEmployerInformationPage  page = PageFactory.initElements(driver, EnrollmentEmployerInformationPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
}

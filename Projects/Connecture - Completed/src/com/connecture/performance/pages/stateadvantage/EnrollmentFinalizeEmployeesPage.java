package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.connecture.performance.pages.BasePage;

public class EnrollmentFinalizeEmployeesPage extends BasePage{

	public EnrollmentFinalizeEmployeesPage(WebDriver driver) {
		super(driver);		
	}
	
	public static EnrollmentFinalizeEmployeesPage getPage(WebDriver driver, Logger logHandler) {
		EnrollmentFinalizeEmployeesPage  page = PageFactory.initElements(driver, EnrollmentFinalizeEmployeesPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	

}

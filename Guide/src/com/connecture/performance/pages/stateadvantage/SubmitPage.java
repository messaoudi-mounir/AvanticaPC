package com.connecture.performance.pages.stateadvantage;


import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;


import org.openqa.selenium.support.PageFactory;

import com.connecture.performance.pages.BasePage;

public class SubmitPage extends BasePage {
	public  SubmitPage(WebDriver driver) {
		  super (driver);
		}

	public static SubmitPage getPage(WebDriver driver, Logger logHandler) {
		SubmitPage  page = PageFactory.initElements(driver, SubmitPage.class);
	    page.setLogHandler(logHandler);
	    return page;	    
	}
}

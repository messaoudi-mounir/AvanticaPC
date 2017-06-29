package com.connecture.performance.pages.bcbsmisg;

import org.openqa.selenium.WebDriver;


import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.connecture.performance.pages.NavigationPage;

public class PortalPage extends NavigationPage {
	
	public PortalPage (WebDriver driver) {
		super (driver);
		PageFactory.initElements (driver, this);
	}
	
	@FindBy(xpath = "html/body/form/table/tbody/tr[5]/td/table/tbody/tr[1]/td[3]/table/tbody/tr[3]/td/table/tbody/tr/td/table/tbody/tr[2]/td[2]/a")
	public WebElement ReturningUserLogin;
	
	
	
	
	
	
}




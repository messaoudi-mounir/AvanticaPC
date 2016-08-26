package com.connecture.performance.pages.bcbsmisg;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.connecture.performance.pages.NavigationPage;

public class CompanySummarySmallGroupPage extends NavigationPage{

	public CompanySummarySmallGroupPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}	
	
	/********************************SUPERCLASS METHODS****************************/
	
	@Override
	public CopyRenewalPage getNextPage(){
		return new CopyRenewalPage(driver);
	}
	
	@Override
	public String getNextPageElementXpath(){
		return "//input[contains(@value, 'Benefit Change')]";
	}
	
	@Override
	public String getNextPageButtonXpath() {
		return "//td[./img]/following-sibling::td[2]/button";
	}
	
	@Override
	public String getLogOutXpath(){
		return "//a[contains(text(),'Log Out')]";
	}	
	
	/************************************METHODS****************************/
	

	

	
	
}

package com.connecture.performance.pages.bcbsmisg;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.connecture.performance.pages.NavigationPage;

public class RenewalSummaryPage extends NavigationPage{
	
	public RenewalSummaryPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy (id="ViewRenewalQuote_0")
	public WebElement AcceptPlansButton;

	
	/********************************SUPERCLASS METHODS****************************/
	@Override
	public RenewalSummaryAcceptPlansPage getNextPage(){
		return new RenewalSummaryAcceptPlansPage(driver);		
	}
	
	@Override
	public String getNextPageElementXpath(){
		return "//td[contains(text(), 'Renewal Summary - Accept Plans')]";
	}

	@Override
	public String getNextPageButtonXpath() {
		return "//button[contains(@value, 'Accept Plans')]";
	}	
	
	/************************************METHODS****************************/	

}

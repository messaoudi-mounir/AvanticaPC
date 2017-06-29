package com.connecture.performance.pages.bcbsmisg;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.connecture.performance.pages.NavigationPage;

public class QuoteSummaryPreliminaryQuote extends NavigationPage{

	public QuoteSummaryPreliminaryQuote(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	/********************************SUPERCLASS METHODS****************************/
	@Override
	public RenewalSummaryPage getNextPage(){
		return new RenewalSummaryPage(driver);
		
	}
	@Override
	public String getNextPageElementXpath(){
		return "//div[contains(@class, 'actionindicator')]";
	}

	@Override
	public String getNextPageButtonXpath() {
		return "//button[contains(@value, 'Continue')]";
	}	
	/************************************METHODS****************************/	
	
}

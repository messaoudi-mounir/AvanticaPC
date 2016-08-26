package com.connecture.performance.pages.bcbsmisg;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.connecture.performance.pages.NavigationPage;

public class RenewalSummaryElectionsPage extends NavigationPage{

	public RenewalSummaryElectionsPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver , this);
	}
	
	/********************************SUPERCLASS METHODS****************************/
	@Override
	public RenewalSummaryAcceptPlansPage getNextPage(){
		return new RenewalSummaryAcceptPlansPage(driver);
		
	}
	@Override
	public String getNextPageElementXpath(){
		return "//button[contains(@value, 'Confirm')]";
	}
	
	@Override
	public String getNextPageButtonXpath() {
		return "//button[contains(@value, 'Continue')]";
	}	
	/************************************METHODS****************************/	

}

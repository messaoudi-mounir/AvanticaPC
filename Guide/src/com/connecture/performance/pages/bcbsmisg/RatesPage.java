package com.connecture.performance.pages.bcbsmisg;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;


import com.connecture.performance.pages.NavigationPage;

public class RatesPage extends NavigationPage{

	public RatesPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	/********************************SUPERCLASS METHODS****************************/
	@Override
	public GenerateProposalPage getNextPage(){
		return new GenerateProposalPage(driver);
		
	}
	@Override
	public String getNextPageElementXpath(){
		return "//button[@value =  'Send Proposal']";
	}



	@Override
	public String getNextPageButtonXpath() {
		return "//button[contains(@value, 'Continue')]";
	}	
	/************************************METHODS****************************/	
	
	
	
}

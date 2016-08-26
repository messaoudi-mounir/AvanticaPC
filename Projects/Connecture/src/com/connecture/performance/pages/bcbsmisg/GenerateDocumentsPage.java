package com.connecture.performance.pages.bcbsmisg;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;


import com.connecture.performance.pages.NavigationPage;

public class GenerateDocumentsPage extends NavigationPage {
	
	public GenerateDocumentsPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	/********************************SUPERCLASS METHODS****************************/
	@Override
	public RenewalSummarySubmitPage getNextPage(){
		return new RenewalSummarySubmitPage(driver);
	}
	 
	@Override
	public String getNextPageElementXpath(){
		return "//button[contains(@value, 'Submit')]";
	}
	
	@Override
	public String getNextPageButtonXpath(){
		return "//button[contains(@value, 'Continue')]";
	}
		
	/************************************METHODS****************************/	
	
}

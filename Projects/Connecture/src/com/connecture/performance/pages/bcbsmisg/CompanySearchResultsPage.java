package com.connecture.performance.pages.bcbsmisg;



import org.openqa.selenium.WebDriver;


import org.openqa.selenium.support.PageFactory;

import com.connecture.performance.pages.NavigationPage;


public class CompanySearchResultsPage extends NavigationPage {
	
	
	public CompanySearchResultsPage (WebDriver driver){
		super(driver);
		PageFactory.initElements(driver, this);
	}  
    
    /********************************SUPERCLASS METHODS****************************/
	@Override
	public String getNextPageElementXpath() {	
		return "//td[./img]/following-sibling::td[2]/button";
	}
	
	@Override
	public CompanySummarySmallGroupPage getNextPage() {		
		return new CompanySummarySmallGroupPage(driver);	
	}

	@Override
	public String getNextPageButtonXpath() {		
		return "//button[@value= 'View']";
	}	
	/************************************METHODS****************************/

	


	
}


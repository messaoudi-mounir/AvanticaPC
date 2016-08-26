package com.connecture.performance.pages.bcbsmisg;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;




import com.connecture.performance.pages.BasePage;
import com.connecture.performance.pages.NavigationPage;

public class SubgroupsPage extends NavigationPage {
	
	public SubgroupsPage (WebDriver driver){
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	public int pageType = 0;	
//-----------------------------------------SUPERCLASS METHODS------------------------------------------------
	
	@Override
	public BasePage getNextPage(){
		switch(pageType){
		case 1: return new CompanySummaryPage(driver);
		case 2: return new RenewalSummarySubmitPage(driver);
		default:
			System.out.println("Please select 1 for CompanySummary or 2 for RenewalSummarySubmit");
			return null;
		}
	}
	
	@Override
	public String getNextPageElementXpath(){
		switch(pageType){
		case 1: return "//button[@value = 'Save']";
		case 2: return "//button[text() = 'Withdraw']";
		default:
			System.out.println("Please select 1 for CompanySummary or 2 for RenewalSummarySubmit");
			return null;
		}
		
	}
	
	@Override
	public String getNextPageButtonXpath (){
		switch(pageType){
		case 1: return "//button[@value = 'Edit Company Information']";
		case 2: return "//button[contains(@value, 'Continue')]";
		default:
			System.out.println("Please select 1 for CompanySummary or 2 for RenewalSummarySubmit");
			return null;
		}
		
	}
	
	//----------------------------------------- Methods --------------------------------
	
	public BasePage clickeditCompanyInformation () {		
		pageType = 1;
		return getNextPageClick();
	}
	
	public BasePage clickcontinueButton (){
	pageType = 2;
	return getNextPageClick();
	}
}

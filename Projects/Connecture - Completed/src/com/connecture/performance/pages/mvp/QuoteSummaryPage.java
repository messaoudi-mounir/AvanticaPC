package com.connecture.performance.pages.mvp;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.connecture.performance.pages.NavigationPage;

public class QuoteSummaryPage extends NavigationPage{

	public QuoteSummaryPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	/********************************SUPERCLASS METHODS***********************/
	@Override
	public String getNextPageElementXpath() {		
		return "//input[@value = 'otherPeople']";
	}
	@Override
	public GenerateProposalPage getNextPage() {		
		return new GenerateProposalPage(driver);
	}
	
	@Override
	public String getNextPageButtonXpath() {		
		return "//a[text() = 'Generate Proposal']";
	}	
	
	/*************************************METHODS****************************/

	
}

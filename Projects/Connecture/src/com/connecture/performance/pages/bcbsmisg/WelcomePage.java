package com.connecture.performance.pages.bcbsmisg;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.connecture.performance.pages.NavigationPage;

public class WelcomePage extends NavigationPage{
	
	

	public WelcomePage(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver,this);
	}
	
	
	/********************************SUPERCLASS METHODS****************************/
	@Override
	public String getNextPageElementXpath() {
		return "//input [@name = 'username']";
	}

	@Override
	public LoginPage getNextPage() {
		return new LoginPage(driver);
	}
	
	@Override
	public String getNextPageButtonXpath() {
		return "//a[contains(text(),'Log In')]";
	}
	
	/************************************METHODS****************************/
	

	



}

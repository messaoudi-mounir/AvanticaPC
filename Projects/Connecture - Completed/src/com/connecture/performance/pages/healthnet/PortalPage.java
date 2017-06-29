package com.connecture.performance.pages.healthnet;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import com.connecture.performance.pages.NavigationPage;


public class PortalPage extends NavigationPage{

	public PortalPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	
	
	/********************************SUPERCLASS METHODS****************************/
	
	@Override
	public NavigationPage getNextPage(){
		return new LoginPage(driver); 
		
	}
	
	@Override
	public String getNextPageElementXpath(){
		return "//button [@value = 'Login']";
	}
	
	@Override
	public String getNextPageButtonXpath() {
		return "//a[@id = 'rLogin']";
	}
	
	/************************************METHODS****************************/
}

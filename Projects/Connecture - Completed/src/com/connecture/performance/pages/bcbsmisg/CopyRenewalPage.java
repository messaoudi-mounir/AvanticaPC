package com.connecture.performance.pages.bcbsmisg;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;
import com.connecture.performance.pages.NavigationPage;

public class CopyRenewalPage extends NavigationPage{

	public CopyRenewalPage(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver, this);	
	}

	public int pageType = 0;
	
	@FindBy(xpath = "//input[contains(@value, 'Benefit Change')]")
	public WebElement benefitChangeCheckBox;
	
	@FindBy(xpath = "//td[contains(text(), 'Plan Selection')]")
	public WebElement planSelectionText;
	
	@FindBy (id ="CopyQuote_copyQuoteInfo_editSubgroupMaintenance Change")
	public WebElement maintenanceChangeCheckBox;
	
	
	/********************************SUPERCLASS METHODS****************************/
	
	@Override
	public BasePage getNextPage(){
		switch(pageType){
		case 1: return new PlanSelectionPage(driver);
		case 2: return new SubgroupsPage(driver);
		default:
			System.out.println("Please select 1 for Benefit or 2 for Maintenance");
			return null;
		}
	}
	
	@Override
	public String getNextPageElementXpath(){
		switch(pageType){
		case 1: return "//button[@value = 'Add/Edit Plans']";
		case 2: return "//button[@value = 'Edit Company Information']";
		default:
			System.out.println("Please select 1 for Benefit or 2 for Maintenance");
			return null;		}
		
	}
	
	@Override
	public String getNextPageButtonXpath() {
		return "//button[@value = 'Continue']";
	}
	
	/************************************METHODS****************************/
	
	public void selectBenefitChangeCheckBox (){
		WebDriverWait wait = new WebDriverWait (driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf (benefitChangeCheckBox));
		do{
			benefitChangeCheckBox.click();
		}while(!benefitChangeCheckBox.isSelected());
		pageType = 1;
	}
	
	
	public void maintenanceChangeCheckBox (){
		WebDriverWait wait = new WebDriverWait (driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf (maintenanceChangeCheckBox));
		do {
			maintenanceChangeCheckBox.click();
		}while(!maintenanceChangeCheckBox.isSelected());
		pageType = 2;
	}

	

	
	
}

package com.connecture.performance.pages.bcbsmisg;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;
import com.connecture.performance.pages.NavigationPage;

public class PlanSelectionPage extends NavigationPage{

	public PlanSelectionPage(WebDriver driver ) {
		super(driver);
		PageFactory.initElements(driver, this);	
	}
	
	@FindBy (id = "reqEffDate")
	public WebElement effectiveDateDatePicker;	
	
	@FindBy(xpath="//button[contains(@value, 'Yes')]")
	public WebElement popupYesButton;
	
	int pageType = 0;
	
	/********************************SUPERCLASS METHODS****************************/
	
	@Override
	public BasePage getNextPage(){
		switch(pageType){
		case 1: return new PlansPage(driver);
		case 2: return new RatesPage(driver);
		case 3: return new RatesPage(driver);
		default:
			System.out.println("Please select 1 for Plans or 2 for Rates");
			return null;
		}
	}
	
	@Override
	public String getNextPageElementXpath(){switch(pageType){
	case 1: return "//img[@id = 'Img00']";
	case 2: return "//td[contains(text(), 'Rates')]";
	case 3: return "//button[contains(@value, 'Yes')]";
	
	default:
		return "//div[contains(@class, 'actionindicator')]";		
	}
		
	}
	
	@Override
	public String getNextPageButtonXpath(){
		switch(pageType){
		case 1: return "//button[@value = 'Add/Edit Plans']";
		case 2: return "//button[contains(@value, 'Continue')]";
		case 3: return "//button[contains(@value, 'Yes')]";
		default:
			System.out.println("Please select 1 for the xpath Plans or 2 for Rates or 3 for the popup");
			return null;
		}
	}
	
	/************************************METHODS****************************/
	
	public void setEffectiveDate(String effectiveDate){
		WebDriverWait wait = new WebDriverWait (driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf (effectiveDateDatePicker));
		
		do{
			effectiveDateDatePicker.clear();
			effectiveDateDatePicker.sendKeys(effectiveDate);			
			
		}while(!effectiveDateDatePicker.getAttribute("value").equals(effectiveDate));				
	}
	
	public PlansPage clickAddPlansButton(){		
		pageType = 1;
		return (PlansPage) getNextPageClick();
	}
	
	public RatesPage clickContinueButton(){
		pageType = 2;
		RatesPage rates = (RatesPage)getNextPageClick();
		
		if (driver.findElements(By.xpath("//button[contains(@value, 'Yes')]")).size() > 0){
			pageType = 3;
			return (RatesPage)getNextPageClick();	
		}	
		else{
			return rates;
			
		}
			
	}	
	
}

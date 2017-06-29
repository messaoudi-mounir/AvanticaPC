package com.connecture.performance.pages.bcbsmisg;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;




import com.connecture.performance.pages.NavigationPage;

public class RenewalSummarySubmitPage extends NavigationPage {
	
	public RenewalSummarySubmitPage (WebDriver driver) {
		super (driver);
		PageFactory.initElements (driver, this);
	}

	
	@FindBy (id = "ViewRenewalQuote_0")
	public WebElement submitButton;
	
	int pageType = 1;
	
	/********************************SUPERCLASS METHODS****************************/
	@Override
	public NavigationPage getNextPage(){
		switch(pageType){
			case 1: return new CompanySummaryPage(driver);
			case 2:	return new RenewalSummaryWithdrawRenewalPage(driver);
			default: System.out.println("WARNING: The pageType should be, 1) CompanySummaryPage, 2) RenewalSummaryWithdrawRenewalPage");
			return null;
		}
	}
	 
	@Override
	public String getNextPageElementXpath(){
		switch(pageType){
			case 1: return "//button[text() = 'Submit']";
			case 2:	return "//select[@name = 'reasonSelected']";
			default: System.out.println("WARNING:[getNextPageElementXpath()] 	The pageType should be, 1) Submit button, 2) reasonSelected combobox");
			return null;
		}
	}
	
	@Override
	public String getNextPageButtonXpath(){
		
		switch(pageType){
		case 1: return "//button[text() = 'Submit']";
		case 2:	return "//button[text() = 'Withdraw']";
		default: System.out.println("WARNING:[getNextPageButtonXpath()] 	The pageType should be, 1) Submit button, 2) Withdraw button");
		return null;
	}
	}
		
	/************************************METHODS****************************/	


	public CompanySummaryPage clickSubmitButton ()  {
		WebDriverWait wait = new WebDriverWait (driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf (submitButton));
		boolean loop = true;
		do{
			submitButton.click ();
			if(driver.findElements(By.xpath("//span[text() = 'Yes, I am ready to submit']")).size() > 0){
				driver.findElement(By.xpath("//span[text() = 'Yes, I am ready to submit']")).click ();
				loop = false;
			}
		}while(loop);		
		return new CompanySummaryPage(driver);
	}
	
	public RenewalSummaryWithdrawRenewalPage clickWithdrawButton ()  {
		WebDriverWait wait = new WebDriverWait (driver, timeOut);
		WebElement withdrawButton = driver.findElement(By.xpath("//button[text() = 'Withdraw']"));
		wait.until(ExpectedConditions.visibilityOf (withdrawButton));
		pageType = 2;
		return (RenewalSummaryWithdrawRenewalPage)getNextPageClick();
		
	}
	
	
	
	public CompanySummaryPage clickreadyForSubmitButton () {
		WebDriverWait wait = new WebDriverWait (driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//span[text() = 'Yes, I am ready to submit']"))));
		driver.findElement(By.xpath("//span[text() = 'Yes, I am ready to submit']")).click ();
		
		return new CompanySummaryPage (driver);
	}
	
	

}
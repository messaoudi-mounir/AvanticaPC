package com.connecture.performance.pages.bcbsmisg;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.NavigationPage;

public class RenewalSummaryWithdrawRenewalPage extends NavigationPage{

	public RenewalSummaryWithdrawRenewalPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	/********************************SUPERCLASS METHODS****************************/
	@Override
	public String getNextPageElementXpath() {	
		return "//a[text() = 'Log Out']";
	}
	
	@Override
	public CompanySummarySmallGroupPage getNextPage() {		
		return new CompanySummarySmallGroupPage(driver);
	}

	@Override
	public String getNextPageButtonXpath() {		
		return "//button[@value= 'Save']";
	}	
	/************************************METHODS****************************/	
	
	public void selectReason(){
		WebDriverWait wait = new WebDriverWait (driver, timeOut);
		WebElement reasonCombo = driver.findElement(By.xpath("//select[@name = 'reasonSelected']"));
		wait.until(ExpectedConditions.visibilityOf (reasonCombo));
		Select select = new Select(reasonCombo);
		do{
			select.selectByVisibleText("Other");
		}while(!(select.getFirstSelectedOption().getText().equals("Other")));
		
	}
}

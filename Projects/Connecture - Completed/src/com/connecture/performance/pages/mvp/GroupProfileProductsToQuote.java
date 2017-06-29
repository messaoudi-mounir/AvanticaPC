package com.connecture.performance.pages.mvp;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.NavigationPage;

public class GroupProfileProductsToQuote extends NavigationPage{

	public GroupProfileProductsToQuote(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//input[@name = 'hasPriorCoverage' and @value = 'N']")
	public WebElement radioNo;
	
	@FindBy(xpath="//input[@name = 'hasPriorCoverage' and @value = 'Y']")
	public WebElement radioYes;
	
	/********************************SUPERCLASS METHODS***********************/
	@Override
	public String getNextPageElementXpath() {		
		return "//input[@name = 'addrLine1']";
	}
	@Override
	public GroupProfilePrimaryLocation getNextPage() {		
		return new GroupProfilePrimaryLocation(driver);
	}
	
	@Override
	public String getNextPageButtonXpath() {		
		return "//a[text() = 'Continue']";
	}	
	/************************************METHODS****************************/
	
	public void selectPriorCoverage(boolean hasPriorCoverage){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		if(hasPriorCoverage){			
			wait.until(ExpectedConditions.visibilityOf(radioYes));
			radioYes.click();
		}
		else{			
			wait.until(ExpectedConditions.visibilityOf(radioNo));
			radioNo.click();
		}
		
	}
	
}

package com.connecture.performance.pages.healthnet;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.NavigationPage;

public class GenerateProposalPage extends NavigationPage{

	public GenerateProposalPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//input[@value = 'otherPeople']")
	public WebElement checkboxEmailOther;
	
	@FindBy(xpath="//input[@value = 'copyMe']")
	public WebElement checkboxCopyMe;
	
	@FindBy(xpath="//input[@name = 'otherPeople']")
	public WebElement textBoxEmailOther;
	/********************************SUPERCLASS METHODS***********************/
	@Override
	public String getNextPageElementXpath() {		
		return "//a[text() = 'Enroll']";
	}
	@Override
	public QuoteSummaryPage getNextPage() {		
		return new QuoteSummaryPage(driver);
	}
	
	@Override
	public String getNextPageButtonXpath() {		
		return "//a[text() = 'Send Proposal']";
	}	
	/*************************************METHODS****************************/

	public void setGeneralSettings(String email){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(checkboxEmailOther));
		checkboxEmailOther.click();
		wait.until(ExpectedConditions.visibilityOf(checkboxCopyMe));
		checkboxCopyMe.click();	
		wait.until(ExpectedConditions.visibilityOf(textBoxEmailOther));
		while(!textBoxEmailOther.getAttribute("value").equals(email)){			
			textBoxEmailOther.clear();
			textBoxEmailOther.sendKeys(email);
		}		
	} 
	
}

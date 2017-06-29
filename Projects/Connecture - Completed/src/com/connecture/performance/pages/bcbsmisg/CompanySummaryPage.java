package com.connecture.performance.pages.bcbsmisg;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.NavigationPage;

public class CompanySummaryPage extends NavigationPage {


	public CompanySummaryPage (WebDriver driver){
		super (driver);
		PageFactory.initElements (driver, this);
	}
	



	@FindBy (id = "editCompanyForm_save_button")
	public WebElement saveButton;
	
	
	
	
	//----------------------------------------- Methods --------------------------------
	
	public SubgroupsPage clicksaveButton () {
		WebDriverWait wait = new WebDriverWait (driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(saveButton));
		saveButton.click ();
		return new SubgroupsPage(driver);
	}
	
	
	
	
}		
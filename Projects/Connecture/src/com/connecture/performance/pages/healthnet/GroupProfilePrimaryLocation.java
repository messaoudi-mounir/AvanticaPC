package com.connecture.performance.pages.healthnet;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.NavigationPage;

public class GroupProfilePrimaryLocation extends NavigationPage{

	public GroupProfilePrimaryLocation(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy(xpath="//input[@name = 'addrLine1']")
	public WebElement txtAddress;
	
	@FindBy(xpath="//input[@name = 'city']")
	public WebElement txtCity;
	
	/********************************SUPERCLASS METHODS***********************/
	@Override
	public String getNextPageElementXpath() {		
		return "//a[text() = 'Add Employee']";
	}
	@Override
	public EmployeeCensusPage getNextPage() {		
		return new EmployeeCensusPage(driver);
	}
	
	@Override
	public String getNextPageButtonXpath() {		
		return "//a[text() = 'Continue']";
	}	
	/************************************METHODS****************************/
	
	public void fillAdressLine (String address){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(txtAddress));
		do{
			txtAddress.clear();
			txtAddress.sendKeys(address);
		}while(!txtAddress.getAttribute("value").equals(address));
	}
	
	public void fillCityField (String city){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(txtCity));
		do{
			txtCity.clear();
			txtCity.sendKeys(city);
		}while(!txtCity.getAttribute("value").equals(city));
	}
	
	public void fillForm (String address, String city){
		fillAdressLine(address);
		fillCityField(city);
	}
}

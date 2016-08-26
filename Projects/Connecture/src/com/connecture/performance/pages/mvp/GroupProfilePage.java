package com.connecture.performance.pages.mvp;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.NavigationPage;

public class GroupProfilePage extends NavigationPage{

	public GroupProfilePage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	
	@FindBy(xpath="//input [@name = 'caseName']")
	public WebElement txtCaseName;
	
	@FindBy(xpath="//input[@id ='zipCode9']")
	public WebElement txtZipCode;

	@FindBy(xpath="//li[text() = '12345 - Schenectady, NY (Schenectady)']")

	public WebElement liZipCode;
	
	@FindBy(xpath="//input[@id = 'effectiveDate4']")
	public WebElement txtdate;
	
	@FindBy(xpath="//input[@name = 'totalEligible']")
	public WebElement txtTee;
	
	@FindBy (xpath="//input[@name = 'totalEmployed']")
	public WebElement txtTemp;
	
	@FindBy(xpath="//input[@name = 'sicCode']")
	public WebElement txtSicCode;
	
	@FindBy(xpath="//li[text() = '0111 - Wheat']")
	public WebElement liSicCode;
	
	@FindBy(xpath = "//select[@id='associationTypeId8']")
	WebElement associationComboBox;
	
	
	
	/********************************SUPERCLASS METHODS***********************/
	@Override
	public String getNextPageElementXpath() {		
		return "//input[@name = 'hasPriorCoverage']";
	}
	@Override
	public GroupProfileProductsToQuote getNextPage() {		
		return new GroupProfileProductsToQuote(driver);
	}
	
	@Override
	public String getNextPageButtonXpath() {		
		return "//a[text() = 'Continue']";
	}	
	/************************************METHODS****************************/	
	
	public void fillCaseName(String caseName){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(txtCaseName));
		do{
			txtCaseName.clear();
			txtCaseName.sendKeys(caseName);
		}while(!txtCaseName.getAttribute("value").equals(caseName));
	}
	
	public void fillZipCode(String zipCode){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(txtZipCode));	
		
		do{
			txtZipCode.clear();
			txtZipCode.sendKeys(zipCode);		
		}while(!txtZipCode.getAttribute("value").equals(zipCode));
		
		wait.until(ExpectedConditions.visibilityOf(liZipCode));
		do{
			liZipCode.click();				

		}while(driver.findElements(By.xpath("//li[text() = '12345 - Schenectady, NY (Schenectady)']")).size() < 1);				

	}
	
	public void filldate(String date){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
				wait.until(ExpectedConditions.visibilityOf(txtdate));	
				do{
					txtdate.clear();
					txtdate.sendKeys(date);		
				}while(!txtdate.getAttribute("value").equals(date));
				
	}			
	
	public void fillTotalNumberOfEligibleEmployees(){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(txtTee));
		do{
			txtTee.clear();
			txtTee.sendKeys("2");
		}while(!txtTee.getAttribute("value").equals("2"));
	}
	
	public void fillTotalEmployees(){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(txtTemp));
		do{
			txtTemp.clear();
			txtTemp.sendKeys("2");
		}while(!txtTemp.getAttribute("value").equals("2"));
	}
	
	public void fillSicCode(String sicCode){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(txtSicCode));	
		
		do{
			txtSicCode.clear();
			txtSicCode.sendKeys(sicCode);		
		}while(!txtSicCode.getAttribute("value").equals(sicCode));
		
		wait.until(ExpectedConditions.visibilityOf(liSicCode));
		do{
			liSicCode.click();				

		}while(driver.findElements(By.xpath("//li[text() = '0111 - Wheat']")).size() < 1);				

	}
	
	public void fillForm(String caseName, String zipCode, String date, String sicCode){		
		fillCaseName(caseName);
		fillZipCode(zipCode);
		filldate(date);
		fillTotalNumberOfEligibleEmployees();
		fillTotalEmployees();
		fillSicCode(sicCode);		
	}
	

}

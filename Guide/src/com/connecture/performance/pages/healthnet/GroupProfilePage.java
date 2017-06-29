package com.connecture.performance.pages.healthnet;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.NavigationPage;

public class GroupProfilePage extends NavigationPage{

	public GroupProfilePage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	
	@FindBy(xpath="//input [@name = 'caseName']")
	public WebElement txtCaseName;
	
	@FindBy(xpath="//input [@name = 'zipCode']")
	public WebElement txtZipCode;
	
	@FindBy(xpath="//li[text() = '90210 - Beverly Hills, CA (Los Angeles)']")
	public WebElement liZipCode;
	
	@FindBy(xpath="//select[@name = 'effectiveDate']")
	public WebElement cbRed;
	
	@FindBy(xpath="//input[@name = 'totalEligible']")
	public WebElement txtTee;
	
	@FindBy(xpath="//input[@name = 'sicCode']")
	public WebElement txtSicCode;
	
	@FindBy(xpath="//li[text() = '2011 - Meat Packing Plants']")
	public WebElement liSicCode;
	
	
	
	
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
		}while(driver.findElements(By.xpath("//div[text() = 'Los Angeles']")).size() < 1);				
	}
	
	public void selectRED (){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(cbRed));
		Select select = new Select(cbRed);
		do{
			select.selectByIndex(2);			
		}while(select.getFirstSelectedOption().getAttribute("value") == cbRed.getAttribute("value"));
	}
	
	public void fillTotalNumberOfEligibleEmployees(){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(txtTee));
		do{
			txtTee.clear();
			txtTee.sendKeys("2");
		}while(!txtTee.getAttribute("value").equals("2"));
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
		}while(driver.findElements(By.xpath("//div[text() = 'Meat Packing Plants']")).size() < 1);				
	}
	
	public void fillForm(String caseName, String zipCode, String sicCode){		
		fillCaseName(caseName);
		fillZipCode(zipCode);
		selectRED();
		fillTotalNumberOfEligibleEmployees();
		fillSicCode(sicCode);		
	}
	
}

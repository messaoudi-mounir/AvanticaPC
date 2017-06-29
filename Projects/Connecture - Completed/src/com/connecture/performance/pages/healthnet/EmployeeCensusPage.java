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

public class EmployeeCensusPage extends NavigationPage{

	public EmployeeCensusPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	@FindBy(xpath="//a[text() = 'Add Employee']")
	public WebElement btnAddEmployee;

	@FindBy(xpath="//input[@name = 'firstName']")
	public WebElement txtFirstName;
	
	@FindBy(xpath="//input[@name = 'lastName']")
	public WebElement txtLastName;
	
	@FindBy(xpath="//input[@name = 'dateOfBirth']")
	public WebElement txtBirthDate;
	
	@FindBy(xpath="//select[@name = 'genderType']")
	public WebElement cbGender;
	
	@FindBy(xpath="//button[./span/text() = 'Add Another Employee']")
	public WebElement btnAddAnotherEmployee;

	@FindBy(xpath="//input[@name = 'custom.zipCode']")
	public WebElement txtZipCode;

	@FindBy(xpath="//button[./span/text() = 'Save']")
	public WebElement btnSave;
	
	/********************************SUPERCLASS METHODS***********************/
	@Override
	public String getNextPageElementXpath() {		
		return "//a[text() = 'Add to quote']";
	}
	@Override
	public PlanSelectionPage getNextPage() {		
		return new PlanSelectionPage(driver);
	}
	
	@Override
	public String getNextPageButtonXpath() {		
		return "//a[text() = 'Continue']";
	}	
	/************************************METHODS****************************/
	
	public void clickAddEmployeeButton(){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(btnAddEmployee));
		do{
			btnAddEmployee.click();
		}while(driver.findElements(By.xpath("//input[@name = 'firstName']")).size() < 1);
	}
	
	public void clickAddAnotherEmployeeButton(){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);		
		do{
			System.out.println("DEBUG - Waiting for element Add Another Employee");	
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[./span/text() = 'Add Another Employee']")));
			System.out.println("DEBUG - Clicking Add Another Button");			
			driver.findElement(By.xpath("//button[./span/text() = 'Add Another Employee']")).click();
			System.out.println("DEBUG - firstName Field? "+driver.findElements(By.xpath("//input[@name = 'firstName']")).size());
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[./span/text() = 'Add Another Employee']")));
			
		}while(driver.findElements(By.xpath("//input[@name = 'firstName']")).size() < 1);
	}
	
	public void clickSaveButton(){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);		
		do{
			wait.until(ExpectedConditions.visibilityOf(btnSave));
			System.out.println("DEBUG - Clicking Save Button");
			btnSave.click();
		}while(driver.findElements(By.xpath("//span[text() = 'Family Members:']")).size() < 1);
	}
	
	public void fillFirstName(String firstName){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(txtFirstName));
		do{
			txtFirstName.clear();
			txtFirstName.sendKeys(firstName);
		}while(!txtFirstName.getAttribute("value").equals(firstName));
	}
	
	public void fillLastName(String lastName){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);		
		do{
			wait.until(ExpectedConditions.visibilityOf(txtLastName));
			txtLastName.clear();
			txtLastName.sendKeys(lastName);
		}while(!txtLastName.getAttribute("value").equals(lastName));
	}
	
	public void fillBirthDate(String month, String day, String year){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		do{
			wait.until(ExpectedConditions.visibilityOf(txtBirthDate));
			txtBirthDate.clear();
			txtBirthDate.sendKeys(month+day+year);
		}while(!txtBirthDate.getAttribute("value").equals(month+"/"+day+"/"+year));
	}
	
	public void selectGender(String gender){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(cbGender));
		Select select = new Select(cbGender);
		select.selectByVisibleText(gender);		
	}
	
	public void fillZipCode(String zipCode){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);		
		do{
			wait.until(ExpectedConditions.visibilityOf(txtZipCode));
			txtZipCode.clear();
			txtZipCode.sendKeys(zipCode);
		}while(!txtZipCode.getAttribute("value").equals(zipCode));
	}
	
	public void fillEmployeeInformation(String firstName, String lastName, String month, String day, String year, String gender, String zipCode){
		fillFirstName(firstName);
		fillLastName(lastName);
		fillBirthDate(month,day,year);
		selectGender(gender);
		fillZipCode(zipCode);				
	}
	
	
	
}

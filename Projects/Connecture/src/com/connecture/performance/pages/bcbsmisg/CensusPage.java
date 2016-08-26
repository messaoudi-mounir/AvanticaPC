package com.connecture.performance.pages.bcbsmisg;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.NavigationPage;

public class CensusPage extends NavigationPage{

	public CensusPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver,this);
	}
	
	@FindBy(xpath="//button[@value = 'Delete Selected']")
	public WebElement deleteButton;
	
	@FindBy(xpath="//button[@value = 'Yes']")
	public WebElement popupYesButton;
	
	/********************************SUPERCLASS METHODS****************************/
	@Override
	public String getNextPageElementXpath() {	
		return "//img[@id = 'Img00']";
	}
	
	@Override
	public PlansPage getNextPage() {		
		return new PlansPage(driver);
	}
	
	@Override
	public String getNextPageButtonXpath(){
		return "//button[contains(@value, 'Continue')]";
	}	
	/************************************METHODS****************************/	
	public void deleteCensusField(int amountOfDeletes, int startingPoint){
		WebDriverWait wait = new WebDriverWait (driver, timeOut);
		int loop = startingPoint + amountOfDeletes;
		for(int j = startingPoint; j < loop; j++){
			wait.until(ExpectedConditions.visibilityOf (driver.findElement(By.xpath("//input[@name =  'selectedMembers']")) ));
			List<WebElement> employee = driver.findElements(By.xpath("//input[@name =  'selectedMembers']"));
			do{
				employee.get(j).click();
			}while(!employee.get(j).isSelected());			
		}
		
		do{
			deleteButton.click();			
		}while(driver.findElements(By.xpath("//button[@value = 'Yes']")).size() < 1);
		
		popupYesButton.click();
		
	}
	public void fillForm (){
		for(int i = 0; i < 2 ; i++){
			
			
			/*Filling Birth Dates for all employees*/
			WebDriverWait wait = new WebDriverWait (driver, timeOut);
			wait.until(ExpectedConditions.visibilityOf (driver.findElement(By.xpath("//input[@name= 'members["+i+"].month']"))));
			WebElement month = driver.findElement(By.xpath("//input[@name= 'members["+i+"].month']"));
			
			do{
			month.clear();
			month.sendKeys("01");
			}while(!month.getAttribute("value").equals("01"));
			
			wait.until(ExpectedConditions.visibilityOf (driver.findElement(By.xpath("//input[@name= 'members["+i+"].day']"))));
			WebElement day = driver.findElement(By.xpath("//input[@name= 'members["+i+"].day']"));
			
			do{
				day.clear();
				day.sendKeys("01");
			}while(!day.getAttribute("value").equals("01"));
			
			
			wait.until(ExpectedConditions.visibilityOf (driver.findElement(By.xpath("//input[@name= 'members["+i+"].year']"))));
			WebElement year = driver.findElement(By.xpath("//input[@name= 'members["+i+"].year']"));
			do{
				year.clear();
				year.sendKeys("1990");
			}while(!year.getAttribute("value").equals("1990"));
			
			
			/*Select Status for all employees*/				
			wait.until(ExpectedConditions.visibilityOf (driver.findElement(By.xpath("//select[@name= 'members["+i+"].memberStatusKey']"))));
			WebElement status = driver.findElement(By.xpath("//select[@name= 'members["+i+"].memberStatusKey']"));
			
			Select selectStatus = new Select(status);
			selectStatus.selectByIndex(1);
			
			/*Select Carrier for all employees*/
			
			wait.until(ExpectedConditions.visibilityOf (driver.findElement(By.xpath("//select[@name= 'members["+i+"].msgMemberExtData.carrierKey']"))));
			WebElement carrier = driver.findElement(By.xpath("//select[@name= 'members["+i+"].msgMemberExtData.carrierKey']"));
			
			Select selectCarrier = new Select(carrier);
			selectCarrier.selectByIndex(2);
		}
		//deleteCensusField(2,2);
		
	}
	
	
	
}

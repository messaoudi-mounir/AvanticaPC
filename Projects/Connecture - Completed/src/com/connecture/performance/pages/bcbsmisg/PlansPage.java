package com.connecture.performance.pages.bcbsmisg;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.NavigationPage;

public class PlansPage extends NavigationPage{
	public PlansPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	
	@FindBy (xpath = "//img[@id ='Img00']")
	public WebElement iconForClick;
	
	@FindBy (xpath = "//input[@type = 'checkbox' and @name = 'productLines[0].products[0].plans[0].selected']")
	public WebElement planCheckBox;	
	
	
	@FindBy (xpath = "//img[@id = 'Img00']")
	public WebElement plansArrow;	
	
	@FindBy (xpath = "//button[@value =  'Add Plans']")
	public WebElement addPlansButton;	
	/********************************SUPERCLASS METHODS****************************/
	
	@Override
	public NavigationPage getNextPage(){
		
		return new PlanSelectionPage(driver);
	}
	
	@Override
	public String getNextPageElementXpath(){
		return "//button[@value = 'Add/Edit Plans']";
	}
	
	@Override
	public String getNextPageButtonXpath() {	
		return "//button[@value = 'Continue']";		
	}
	
	
	/************************************METHODS****************************/	
	
	public void displayPlans(){
	   WebDriverWait wait = new WebDriverWait(driver, timeOut);
	   wait.until(ExpectedConditions.visibilityOf(iconForClick));
	   Actions action = new Actions(driver);
	   do{
		   action.moveToElement(iconForClick);
		   action.click();
		   action.perform();
	   }while(!iconForClick.isSelected());
	   }
	
	
	public  void selectPlans(){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(planCheckBox));
		Actions action = new Actions(driver);
		do{
			action.moveToElement(planCheckBox);
			action.click();
			action.perform();
		}while(!planCheckBox.isSelected());
	}

	public void addPlans (){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(plansArrow));
		do{
			plansArrow.click();
		}while(driver.findElements(By.xpath("//input[@type = 'checkbox' and @name = 'productLines[0].products[0].plans[0].selected']")).size() < 1);
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[@type = 'checkbox' and @name = 'productLines[0].products[0].plans[0].selected']"))));
		planCheckBox = driver.findElement(By.xpath("//input[@type = 'checkbox' and @name = 'productLines[0].products[0].plans[0].selected']"));
		selectPlans();
		do{
			addPlansButton.click();
		}while(driver.findElements(By.xpath("//button[@value = 'Continue']")).size() < 1);
		wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//button[@value = 'Continue']"))));
	}
	
	public RatesPage clickContinueButton(){
		do{
			
			driver.findElement(By.xpath("//button[@value = 'Continue']")).click();
			
		}while(driver.findElements(By.xpath("//td[contains(text(), 'Rates')]")).size() < 0 );
		
		return new RatesPage(driver);
	}
	
	
}

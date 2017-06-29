package com.connecture.performance.pages.bcbsmisg;


import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;






import com.connecture.performance.pages.NavigationPage;

public class RenewalSummaryAcceptPlansPage extends NavigationPage{
	public RenewalSummaryAcceptPlansPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	int pageType = 1;
		
	@FindBy (xpath = "//button[@value = 'Delete Package']")
	public WebElement deletePackage;
	
	@FindBy (xpath = "//button[contains(@value, 'Continue')]")
	public WebElement continueButton;
	
	@FindBy (xpath = "//button[contains(@value, 'Confirm')]")
	public WebElement confirmButton;
	

	@FindBy (xpath = "//button[contains(@value, 'Yes')]")
	public WebElement popupYesButton;
	/********************************SUPERCLASS METHODS****************************/
	@Override
	public NavigationPage getNextPage(){
		switch(pageType){
			case 1: return new GenerateDocumentsPage(driver);
			case 2:	return new RenewalSummaryElectionsPage(driver);
			default: System.out.println("WARNING: The pageType should be, 1) GenerateDocumentsPage, 2) RenewalSummaryElectionsPage");
			return null;
		}
	}
	 
	@Override
	public String getNextPageElementXpath(){
		
		switch(pageType){
		case 1: return  "//button[contains(@value, 'Continue')]";//"//td[contains(text(), ' Renewal Summary - Accept Plans ')]"
		case 2:	return "//img[@src='../images/icons/add.png']";
		default: System.out.println("WARNING:[getNextPageElementXpath()] 	The pageType should be, 1) GenerateDocumentsPage, 2) RenewalSummaryElectionsPage");
		return null;
	}
	}
	
	@Override
	public String getNextPageButtonXpath(){
		
		switch(pageType){
		case 1: return "//button[contains(@value, 'Confirm')]";
		case 2:	return "//button[contains(@value, 'Continue')]";
		default: System.out.println("WARNING:[getNextPageButtonXpath()] 	The pageType should be, 1) GenerateDocumentsPage, 2) RenewalSummaryElectionsPage");
		return null;
	}
	}
		
	/************************************METHODS****************************/	
	
	public void selectYesCoverageToAnyLineOfBusieness(){
		WebDriverWait wait = new WebDriverWait (driver, timeOut);
		WebElement yesButton = driver.findElement(By.id("AcceptPlans_wiveAnswertrue"));
		wait.until(ExpectedConditions.visibilityOf (yesButton));
		do{
			yesButton.click();
		}while(!yesButton.isSelected());		
	}
	
	public void selectAndCreatePackage(){
		WebDriverWait wait = new WebDriverWait (driver, timeOut);
		
		
		//for(int i = 0; i < 2; i++){
		    System.out.println("DEBUG: Finding packageCheckbox...");
			List<WebElement> packageCheckbox = driver.findElements(By.xpath("//input[@type = 'checkbox' and @name = 'selectedPlans']"));
			System.out.println("DEBUG: Finding createNewPackageButton...");
			List<WebElement> createNewPackageButton = driver.findElements(By.xpath("//button[@value = 'Create Package']"));
			int i = 0;
			if(createNewPackageButton.size() == 1){
				do{
				    System.out.println("DEBUG: Waiting for visiblity of checkbox...");
					wait.until(ExpectedConditions.visibilityOf (packageCheckbox.get(i)));
					System.out.println("DEBUG: Clicking checkbox...");
					packageCheckbox.get(i).click();		
					
				}while(!packageCheckbox.get(i).isSelected());							
				sleepFor(2);
				System.out.println("DEBUG: Finding again createNewPackageButton...");	
				createNewPackageButton = driver.findElements(By.xpath("//button[@value = 'Create Package']"));
				System.out.println("DEBUG: Waiting for visiblity of createNewPackageButton..");
				wait.until(ExpectedConditions.visibilityOf (createNewPackageButton.get(i)));
				// do{ 
			    System.out.println("DEBUG: Clicking createNewPackageButton.");
			    WebElement createButtonElement = 	createNewPackageButton.get(i);
			    System.out.println("DEBUG: " + createButtonElement);
			    createButtonElement.click();
			    System.out.println("DEBUG:  Click done...");
					
				// }while(!deletePackage.isDisplayed());
			/*}else{
				if(packageCheckbox.size() > 3){
					do{
						wait.until(ExpectedConditions.visibilityOf (packageCheckbox.get(4)));
						packageCheckbox.get(4).click();		
						
					}while(!packageCheckbox.get(4).isSelected());			
				
					wait.until(ExpectedConditions.visibilityOf (createNewPackageButton.get(i)));
					do{ 
						createNewPackageButton.get(i).click();
					}while(!deletePackage.isDisplayed());
				}*/
			}
		
		//}
	}
	
	public GenerateDocumentsPage clickConfirmButton(){
		pageType = 1;		
		return (GenerateDocumentsPage)getNextPageClick();
	}
	
	public RenewalSummaryElectionsPage clickContinueButton(){
		WebDriverWait wait = new WebDriverWait (driver, timeOut);
		// System.out.println("DEBUG:  Waiting for visibility of continue button...");
		// wait.until(ExpectedConditions.visibilityOf(continueButton));
		pageType = 2;
		//do{
	        System.out.println("DEBUG:  Finding cont button...");
	        WebElement contButton = driver.findElement(By.xpath("//button[contains(@value, 'Continue')]"));
	        System.out.println("DEBUG:  contButton: " + contButton);
	        System.out.println("DEBUG:  Clicking cont button...");
	        contButton.click();
			if(driver.findElements(By.xpath("//button[contains(@value, 'Yes')]")).size() > 0){				
				driver.findElement(By.xpath("//button[contains(@value, 'Yes')]")).click();
				return new RenewalSummaryElectionsPage(driver);
			}else{
		//}while(driver.findElements(By.xpath("//img[@src='../images/icons/add.png']")).size() < 1);
				return (RenewalSummaryElectionsPage)getNextPageClick();
			}
	}
	
	
}

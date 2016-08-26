package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class EmployerInformationPage extends BasePage {

	public EmployerInformationPage(WebDriver driver) {
		super(driver);		
	}
	
	public static EmployerInformationPage getPage(WebDriver driver, Logger logHandler) {
		EmployerInformationPage  page = PageFactory.initElements(driver, EmployerInformationPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	/********************************************************************************************************************/	
	
	@FindBy (xpath="//input[contains(@name, 'legalName')]")
	public WebElement employerNameElement;
	
	@FindBy (xpath="//input[contains(@name, 'name')]")
	public WebElement companyNameElement;
	
	@FindBy (xpath="//input[contains(@name, 'federalTaxId')]")
	public WebElement federalEmployerElement;
	
	@FindBy (xpath="//select[contains(@name, 'employerType')]")
	public WebElement employerTypeElement;
	
	@FindBy (xpath="//input[contains(@name, 'fteCount')]")
	public WebElement fteCountElement;
	
	@FindBy (xpath="//select[contains(@name, 'primaryAddress.state')]")
	public WebElement stateComboElement;
	
	@FindBy (xpath="//input[contains(@name, 'addressSameAsPrimary') and @type='checkbox']")
	public WebElement addressSameAsPrimaryCheckbox;	
	
	@FindBy (xpath="//a[contains(@class, 'buttonNext')]")
	public WebElement nextButton;
	
	public void checkContinueButtonToBeClickable(){
		logInfo("Checking Next button to be clickable");
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(nextButton));
        wait.until(ExpectedConditions.elementToBeClickable(nextButton));
        logInfo("Next button is now clickable");
	}
	
	public void waitForPopupToDisappear(){        
        logInfo("Checking Continue button to be clickable");        
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='actionindicator']")));
        logInfo("Continue button is clickable now");
    }
	
	public FinalizeEmployeesPage goToFinalizeEmployeesPage(){        
        logInfo("Clicking Next Button...");            
        alternativeClick(nextButton);
        waitForPopupToDisappear();
        logInfo("Next button clicked...");
        return FinalizeEmployeesPage.getPage(driver, logHandler);
    }
	
	public void selectState(String state){
		logInfo("Filling state info...");
		logInfo("Waiting for state combo to be visible"); 
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(stateComboElement));
		logInfo("Selecting value: "+state); 
		Select select = new Select(stateComboElement);
		select.selectByValue(state); 
	}
	
	public void selectEmployerType(String employerType){
		logInfo("Filling employerType info...");
		logInfo("Waiting for employerType combo to be visible"); 
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(employerTypeElement));
		logInfo("Selecting value: "+employerType); 
		Select select = new Select(employerTypeElement);
		select.selectByValue(employerType); 
	}
	
	public void fillForm(String employerName, String federalEmployer, String employerTypeValue, String fte){
		logInfo("Filling Form..."); 
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		logInfo("Checking employer name field to be visible..."); 
		wait.until(ExpectedConditions.visibilityOf(employerNameElement)); 
		logInfo("Sending employer name: "+employerName+"..."); 
		employerNameElement.sendKeys(employerName);	
		
		logInfo("Checking employer name field to be visible..."); 
		wait.until(ExpectedConditions.visibilityOf(federalEmployerElement)); 
		logInfo("Sending employer name: "+employerName+"..."); 
		federalEmployerElement.sendKeys(federalEmployer);
		employerNameElement.click();
		
		logInfo("Checking employer type field to be visible..."); 
		wait.until(ExpectedConditions.visibilityOf(employerTypeElement)); 
		logInfo("Sending employer type: "+employerName+"..."); 
		employerTypeElement.sendKeys(""+employerTypeValue);		
		
		selectEmployerType(employerTypeValue);
		
		logInfo("Checking employer name field to be visible..."); 
		wait.until(ExpectedConditions.visibilityOf(fteCountElement));
		sleepFor(10);
		logInfo("Sending employer name: "+fte+"..."); 
		fteCountElement.clear();
		fteCountElement.sendKeys(fte);
		
		selectState("DC");
		
		addressSameAsPrimaryCheckbox.click();
        
	}
	/********************************************************************************************************************/
}

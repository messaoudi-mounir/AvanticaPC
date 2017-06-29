package com.connecture.performance.pages.stateadvantage;

import java.util.List;

import org.apache.log.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class PlansPage extends BasePage{

	public PlansPage(WebDriver driver) {
		super(driver);
	}

	public static PlansPage getPage(WebDriver driver, Logger logHandler) {
		PlansPage  page = PageFactory.initElements(driver, PlansPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
	
	
	public List<WebElement> comparePlansCheckbox =  driver.findElements(By.xpath("//input[@id = 'checkbox0']"));
	
	
	public void checkComparePlansCheckboxClickable(int index){
		logInfo("Checking Compare Plans Checkbox to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);       
        wait.until(ExpectedConditions.visibilityOf(comparePlansCheckbox.get(index)));
        wait.until(ExpectedConditions.elementToBeClickable(comparePlansCheckbox.get(index)));
        logInfo("Compare Plans Checkbox is clickable now");
	}
	
	public void checkComparePlansButtonClickable(){
		logInfo("Checking Compare Plans Button to be clickable");
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(comparePlansButton));
        wait.until(ExpectedConditions.elementToBeClickable(comparePlansButton));
        logInfo("Compare Plans Button is now clickable");
	}
	
	public void selectPlans(int index){
		logInfo("Selecting plan, index: "+index);
		checkComparePlansCheckboxClickable(index);		
		comparePlansCheckbox.get(index).click();
		logInfo("Selected plan, index: "+index);
	}
	
	@FindBy(xpath="//a[contains(@class, 'buttonInline _comparePlans')]")
    public WebElement comparePlansButton;
	
	public void fillForm(int sleep){		
		selectPlans(0);
		sleepFor(sleep);
		selectPlans(1);
		sleepFor(sleep);
		selectPlans(2);
		sleepFor(sleep);
	}
	
	public ComparePlansPage goToComparePlansPage(){       
        logInfo("Clicking Compare Plans Button");
        comparePlansButton.click();
        logInfo("Compare Plans Button Clicked");        
        return ComparePlansPage.getPage(driver, logHandler);
	}
	
	public PlansPage goToPlansPage(){
		logInfo("Clicking View Plans Button...");    	
		//comparePlansCheckbox.get(0).click();
        logInfo("View Plans Button clicked");
        return PlansPage.getPage(driver, logHandler);
	}
	
	

	@FindBy(xpath="//a[@class = 'buttonPrimary _enroll']")
    public WebElement selectButton;
	
	public void selectButtonClickable(){
		logInfo("Select button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(selectButton));
        wait.until(ExpectedConditions.elementToBeClickable(selectButton));
        logInfo("Select Button button is clickable now");
	}
    
	public CoverageSummaryPage goToCoverageSummaryPage(){
		logInfo("Clicking Select Button...");    	
		selectButton.click();
        logInfo("Select Button clicked");
        return CoverageSummaryPage.getPage(driver, logHandler);
	}
	
	
	@FindBy(xpath="(//a[@class = '_viewDetails displayHelp'])[1]")
	 public WebElement viewPlanDetailsLink;
	
	
	public PlansDetailsPage goToPlansDetailsPage(){
		logInfo("Clicking View Plans Details Link...");    	
		viewPlanDetailsLink.click();
        logInfo("View Plans Details Link clicked");
        return PlansDetailsPage.getPage(driver, logHandler);
	}
	
	
	@FindBy(xpath="(//a[@class = 'buttonPrimary _enroll'])[1]")
	 public WebElement selectPlanButton;
	
	
	public PlanSummaryPage goToPlanSummaryPage(){
		logInfo("Clicking View Plans Details Link...");    	
		selectPlanButton.click();
       logInfo("View Plans Details Link clicked");
       return PlanSummaryPage.getPage(driver, logHandler);
	}
	
	
	
	
	
	
}








	
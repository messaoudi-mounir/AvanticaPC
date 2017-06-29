package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class PlanSummaryPage extends BasePage {

	public PlanSummaryPage(WebDriver driver) {
		super(driver);		
	}
	
	public void waitForLoadingPlansPopupToDisappear(){		
		logInfo("Checking Continue button to be clickable");		
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='actionindicator']")));
        logInfo("Continue button is clickable now");
	}

	public static PlanSummaryPage getPage(WebDriver driver, Logger logHandler) {
		PlanSummaryPage  page = PageFactory.initElements(driver, PlanSummaryPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
	
	@FindBy (xpath="//a[contains(text(), 'Add Contributions')]")
	public WebElement AddContributionsButton;
	
	public void checkAddContributionsButtonClickable(){		
		logInfo("Checking Add Contributions button to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(AddContributionsButton));
        wait.until(ExpectedConditions.elementToBeClickable(AddContributionsButton));
        logInfo("Add Contributions button is clickable now");
	}
	
	public ContributionTypePage goToContributionTypePage(){
		logInfo("Clicking Add Contributions Button...");    
		AddContributionsButton.click();
        logInfo("Add Contributions button clicked...");
        return ContributionTypePage.getPage(driver, logHandler);
	}
	
	
	
	@FindBy (xpath="//a[@id = 'dentalShop']")
	public WebElement ShopDentalPlanButton;
	
	public void checkShopDentalPlanButtonClickable(){		
		logInfo("Checking Shop Dental Plan button to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(ShopDentalPlanButton));
        wait.until(ExpectedConditions.elementToBeClickable(ShopDentalPlanButton));
        logInfo("Shop Dental Plan button is clickable now");
	}
	
	public DentalPlansPage goToDentalPlansPage(){
		logInfo("Clicking Shop Dental Plan Button...");    
		ShopDentalPlanButton.click();
        logInfo("Shop Dental Plan button clicked...");
        return DentalPlansPage.getPage(driver, logHandler);
	}
	
	
	
	@FindBy (xpath="//a[@class = '_checkOutBtn buttonPrimary']")
	public WebElement EnrollButton;
	
	public void checkEnrollButtonClickable(){		
		logInfo("Checking Enroll Button to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(EnrollButton));
        wait.until(ExpectedConditions.elementToBeClickable(EnrollButton));
        logInfo("Enroll button is clickable now");
	}
	
	public TaxCreditOptionsPage goToTaxCreditOptionsPage(){
		logInfo("Clicking Enroll Button...");    
		EnrollButton.click();
        logInfo("Enroll button clicked...");
        return TaxCreditOptionsPage.getPage(driver, logHandler);
	}
	
	
	
	
	@FindBy (xpath="//a[@class = 'buttonNext']")
	public WebElement ContinueButton;
	
	public void checkContinueButtonClickable(){		
		logInfo("Checking Continue Button to be clickable");		
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(ContinueButton));
        wait.until(ExpectedConditions.elementToBeClickable(ContinueButton));
        logInfo("Continue button is clickable now");
	}
	
	public YourInformationPage goToYourInformationPage(){
		logInfo("Clicking Continue Button...");    
		ContinueButton.click();
        logInfo("Continue button clicked...");
        return YourInformationPage.getPage(driver, logHandler);
	}
	
	
}

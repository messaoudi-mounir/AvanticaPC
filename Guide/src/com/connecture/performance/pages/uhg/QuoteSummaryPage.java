package com.connecture.performance.pages.uhg;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;


public class QuoteSummaryPage extends BasePage {
    
    public QuoteSummaryPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
    public static QuoteSummaryPage getPage(WebDriver driver, Logger logHandler) {
    	QuoteSummaryPage page = PageFactory.initElements(driver, QuoteSummaryPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
    
    @FindBy (xpath="//a[contains(text() , 'Generate Proposal')]")
    public WebElement genPropButton;
    
	public void checkGenerateProposalButtonClickable() {		
        logInfo("Checking generate proposal button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, 600);
        wait.until(ExpectedConditions.visibilityOf(genPropButton));
        wait.until(ExpectedConditions.elementToBeClickable(genPropButton));
        logInfo("login continue is clickable now");        
	    
    }
    
    public GenerateProposalPage goToGenerateProposalPage() {
    	logInfo("Clicking generate proposal button...");    	
    	genPropButton.click();
        logInfo("Generate proposal button clicked");
        return GenerateProposalPage.getPage(driver, logHandler);
    }    
    
    @FindBy (xpath="//a[contains(text() , 'Enroll')]")
    public WebElement enrollButton;
    
    public void checkEnrollButtonClickable() {
		logInfo("In method checkEnrollButtonClickable");
        WebDriverWait wait = new WebDriverWait(driver, 600);
        logInfo("Checking Enroll button to be Visible");
        wait.until(ExpectedConditions.visibilityOf(enrollButton));
        logInfo("Checking Enroll button to be clickable");
        wait.until(ExpectedConditions.elementToBeClickable(enrollButton));
        logInfo("Enroll button is clickable now");
    }   
    
    public void clickEnrollButton(){
    	logInfo("Clicking enroll button...");    	
        enrollButton.click();
        logInfo("Enroll button clicked");   
    }
    
    @FindBy (xpath="//span[contains(text() , 'Enroll')]")
    public WebElement enrollButton2;
    
    public void checkEnrollButton2Clickable() {
		logInfo("In method checkEnrollButtonClickable");
        WebDriverWait wait = new WebDriverWait(driver, 600);
        logInfo("Checking Enroll button to be Visible");
        wait.until(ExpectedConditions.visibilityOf(enrollButton2));
        logInfo("Checking Enroll button to be clickable");
        wait.until(ExpectedConditions.elementToBeClickable(enrollButton2));
        logInfo("Enroll button is clickable now");
    }
    
    public EnrollmentGetStartedPage goToEnrollGetStarted(){
    	logInfo("Clicking enroll button...");    	
        enrollButton2.click();
        logInfo("Enroll button clicked");       
        return EnrollmentGetStartedPage.getPage(driver, logHandler);
    }
    
	

}

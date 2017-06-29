package com.connecture.performance.pages.uhg;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class PreviewProposalPage extends BasePage{

    
    public PreviewProposalPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}
    
    public static PreviewProposalPage getPage(WebDriver driver, Logger logHandler) {
    	PreviewProposalPage page = PageFactory.initElements(driver, PreviewProposalPage.class);
        page.setLogHandler(logHandler);
        return page;
    }    
    
    @FindBy (xpath="//embed[@type = 'application/pdf']")
    public WebElement embedPdfBody;
    
	public void checkPageLoaded() {
		logInfo("Checking Plan added");
    	logInfo("Checking Remove Button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("//embed[@type = 'application/pdf']"));        
        logInfo("Remove Button is clickable now"); 
        logInfo("Plan successfully added");        
    }
    
}

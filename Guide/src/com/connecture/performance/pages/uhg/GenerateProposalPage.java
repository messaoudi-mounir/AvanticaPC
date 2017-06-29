package com.connecture.performance.pages.uhg;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;

public class GenerateProposalPage extends BasePage {
    
    public GenerateProposalPage(WebDriver driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

    public static GenerateProposalPage getPage(WebDriver driver, Logger logHandler) {
    	GenerateProposalPage page = PageFactory.initElements(driver, GenerateProposalPage.class);
        page.setLogHandler(logHandler);
        return page;
    }
    
    @FindBy (xpath="//a[text() = 'Preview Proposal']")
    public WebElement previewProposalButton;
    
	public void checkPreviewProposalClickable() {
		logInfo("Checking generate proposal button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, 600);
        wait.until(ExpectedConditions.visibilityOf(previewProposalButton));
        wait.until(ExpectedConditions.elementToBeClickable(previewProposalButton));
        logInfo("login continue is clickable now");
    }
    
	@FindBy(xpath="//input[@value = 'otherPeople']")
	public WebElement checkboxEmailOther;
	
	@FindBy(xpath="//input[@value = 'copyMe']")
	public WebElement checkboxCopyMe;
	
	@FindBy(xpath="//input[@name = 'otherPeople']")
	public WebElement textBoxEmailOther;
	
    public void fillOutProposalForm(String otherEmailAddress) {
    	WebDriverWait wait = new WebDriverWait(driver, timeOut);
    	logInfo("Waiting for visibility of the Other People checkbox");
		wait.until(ExpectedConditions.visibilityOf(checkboxEmailOther));
		logInfo("Selecting Email Other checkbox");
		checkboxEmailOther.click();
		logInfo("Waiting for visibility of the Copy Me checkbox");
		wait.until(ExpectedConditions.visibilityOf(checkboxCopyMe));
		logInfo("Deselect Copy Me checkbox");
		checkboxCopyMe.click();	
		logInfo("Waiting for Email Other Textbox");
		wait.until(ExpectedConditions.visibilityOf(textBoxEmailOther));
		int counter = 0;
		while(!textBoxEmailOther.getAttribute("value").equals(otherEmailAddress)){			
			logInfo("Clearing the Email Other Textbox, counter: " +counter++);
			textBoxEmailOther.clear();
			logInfo("Entering new email: "+otherEmailAddress+", counter:" +counter++);
			textBoxEmailOther.sendKeys(otherEmailAddress);
		}
    }
    
    
    
    
    public PreviewProposalPage goToPreviewProposalPage() {
    	logInfo("Clicking Continue button...");    	
        previewProposalButton.click();
        logInfo("Continue button clicked");
        return PreviewProposalPage.getPage(driver, logHandler);
        
    }
    
    @FindBy (xpath="//a[contains(text() , 'Send Proposal')]")
    public WebElement sendProposalButton;
    
    public void checkSendProposalClickable() {
		logInfo("Checking generate proposal button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, 600);
        wait.until(ExpectedConditions.visibilityOf(sendProposalButton));
        wait.until(ExpectedConditions.elementToBeClickable(sendProposalButton));
        logInfo("login continue is clickable now");
    }
    
    public QuoteSummaryPage sendProposal() {
    	logInfo("Clicking Send Proposal button...");    	
        sendProposalButton.click();
        logInfo("Send Proposal button clicked");
        return QuoteSummaryPage.getPage(driver, logHandler);
       
    }
    

}

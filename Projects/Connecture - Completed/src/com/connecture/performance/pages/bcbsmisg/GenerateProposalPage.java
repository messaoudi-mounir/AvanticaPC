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


public class GenerateProposalPage extends NavigationPage{
	
	public GenerateProposalPage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	@FindBy (xpath = "//button[@value =  'Send Proposal']")
	public WebElement sendProposalButton;	
	
	@FindBy (xpath = "//input[@id = 'popup_ok']")
	public WebElement popupOk;	
	
	/********************************SUPERCLASS METHODS****************************/
	@Override
	public RenewalSummaryPage getNextPage(){
		return new RenewalSummaryPage(driver);
		
	}
	@Override
	public String getNextPageElementXpath(){
		return "//button[contains(@value, 'Accept Plans')]";
	}
	@Override
	public String getNextPageButtonXpath() {
		return "//button[contains(@value, 'Continue')]";
	}	
	/************************************METHODS****************************/	
	
	public  QuoteSummaryPreliminaryQuote  sendProposal (){		
		Actions actions = new Actions(driver);
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//button[@value =  'Send Proposal']"))));
		
		//do{
			if(!driver.findElement(By.xpath("//input[@name = 'includeSbcPage']")).isSelected()){
				driver.findElement(By.xpath("//input[@name = 'includeSbcPage']")).click();
			}
			if(driver.findElement(By.xpath("//input[@name = 'emailManagingAgent']")).isSelected()){
				driver.findElement(By.xpath("//input[@name = 'emailManagingAgent']")).click();
				
				driver.findElement(By.xpath("//input[@name = 'emailOther']")).click();
				driver.findElement(By.xpath("//input[@name = 'emailOtherAddress']")).sendKeys("test@connecture.com");
			}
			driver.findElement(By.xpath("//button[@value =  'Send Proposal']")).click();
			if(driver.findElements(By.xpath("//input[@id = 'popup_ok']")).size() == 1){
				actions.moveToElement(driver.findElement(By.xpath("//input[@id = 'popup_ok']")));
				actions.click();
				actions.perform();
			}
			
							
			//}while(driver.findElements(By.xpath("//button[@id = 'popup_ok']")).size() > 1);
			
		//}while(driver.findElements(By.xpath("//td[contains(text(), 'Quote Summary - Preliminary Quote')]")).size() < 1);
		
		
		return new QuoteSummaryPreliminaryQuote(driver);
		
		
	}
}

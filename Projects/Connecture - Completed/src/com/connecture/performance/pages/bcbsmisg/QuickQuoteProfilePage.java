package com.connecture.performance.pages.bcbsmisg;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;






import com.connecture.performance.pages.NavigationPage;

public class QuickQuoteProfilePage extends NavigationPage {
	
	public QuickQuoteProfilePage (WebDriver driver){
		super (driver);
		PageFactory.initElements (driver,this);
	}
	
	public int ENROLLMENT = 2;
	
	@FindBy (xpath="//input[@id='grpName']")
	    public WebElement CompanyNameField;
	
	
	@FindBy (xpath="//select[@id='quoteProfileData.groupProfile.mgaId']")
	    public WebElement ManagingAgentCombo;
	
	@FindBy (xpath="//input[@id='totalEligible']")
	     public WebElement TotalFTEField;
	
	
	@FindBy (xpath="//input[@id='total_elig']")
	     public WebElement ExpectedActiveEnrollingField;
	
	@FindBy (xpath="//input[@id='quote_zip']")
	     public WebElement ZipCodeField;
	
	
	@FindBy (xpath="//input[@id='quote_sic']")
	     public WebElement SICCodeField;
	
	
	@FindBy (xpath="//input[@id='quote_date']")
	     public WebElement RequestEfectiveDateField;
	
	@FindBy (xpath="//input[@id='quote_date_rate']")
	   public WebElement RenewalEffectiveDateField;
	
	@FindBy (xpath="//button[@id='gene_rate_btn']")
	  public WebElement ViewRatesOnlineButton;
	
	@FindBy (xpath="//button[@id='btn_drug_plan_no']")
	 public WebElement NoPediatric;
	Actions action = new Actions(driver);

	private WebElement standAlone;
	private WebElement plans;
	
	
	/********************************SUPERCLASS METHODS****************************/
	
	@Override
	public RatesPage getNextPage(){
		return new RatesPage(driver);
		
	}
	@Override
	public String getNextPageElementXpath(){
		return "//button[@id='gene_rate_btn']";
	}



	@Override
	public String getNextPageButtonXpath() {
		return "//button[@id='ViewRates_10']";
	}	
	
	     
	/************************************METHODS****************************/	
	 
	public void fillCompanyNameField(String companyName){
		do{
			WebDriverWait wait = new WebDriverWait(driver, timeOut);
			wait.until(ExpectedConditions.visibilityOf(CompanyNameField));
			CompanyNameField.clear();
			CompanyNameField.sendKeys(companyName);
			
		}while( !CompanyNameField.getAttribute("value").equals(companyName) );
	}
	
	public void fillManagingAgentCombo(int index){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(ManagingAgentCombo));
		Select select = new Select(ManagingAgentCombo);
		select.selectByIndex(index);			
	}
	
	public void fillTotalFTEField(){
		do{
			WebDriverWait wait = new WebDriverWait(driver, timeOut);
			wait.until(ExpectedConditions.visibilityOf(TotalFTEField));
			TotalFTEField.clear();
			TotalFTEField.sendKeys(""+ENROLLMENT);
			
		}while( CompanyNameField.getAttribute("value").equals(TotalFTEField) );
	}
	
	public void fillZipCodeFIeld(int zipCode) {		
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		do{
			wait.until(ExpectedConditions.visibilityOf(ZipCodeField));
			ZipCodeField.clear();
			ZipCodeField.sendKeys(""+zipCode);			
		}while( CompanyNameField.getAttribute("value").equals(zipCode) );
		
	}
	
	public void clickDentalVision(){
		do{
			WebDriverWait wait = new WebDriverWait(driver, timeOut);
			wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[@id='dentalVisionCoveragefalse']"))));
			driver.findElement(By.xpath("//input[@id='dentalVisionCoveragefalse']")).click();
		}while(driver.findElements(By.xpath("//input[@id='dentalVisionCoveragefalse']")).size() <1);
	}

	public void fillRED (String RequestEfectiveDate){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(RequestEfectiveDateField));
		do{
			RequestEfectiveDateField.clear();
			RequestEfectiveDateField.sendKeys(RequestEfectiveDate);
		}while(!RequestEfectiveDateField.getAttribute("value").equals(RequestEfectiveDate));
	
	}
		


	public void clickNoPediatric (){
	WebDriverWait wait = new WebDriverWait (driver, timeOut);
	System.out.println("DEBUG: Before waiting the No Button from the popup");
	wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//button[@id='btn_drug_plan_no']"))));
	System.out.println("DEBUG: Before clicking No from the popup");
	action.moveToElement(driver.findElement(By.xpath("//button[@id='btn_drug_plan_no']")));	
	action.click();
	action.perform();
	System.out.println("DEBUG: Clicked No from the popup");
	// wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath(getLogOutXpath()))));
	System.out.println("Method finished");
		
	// }while(driver.findElements(By.xpath("//td[contains(text(), 'Generate Proposal')]")).size() < 1);// || driver.findElements(By.xpath("//td[contains(text(), 'Rates')]")).size() < 1
	
}
	
	public void fillQuickQuoteForm(String companyName, int ZipCode, int SICCode,  String RequestEfectiveDate, int agentComboIndex){	
		
		fillCompanyNameField(companyName);
		fillManagingAgentCombo(agentComboIndex);
		fillTotalFTEField();
		do{
			action.moveToElement(CompanyNameField);
		    action.click();
		    action.perform();
		}while(driver.findElements(By.xpath("//input[@id='quote_zip']")).size() < 1);
		 
	    fillZipCodeFIeld(ZipCode);
	    
	    clickStandAlone();
	    clickDentalVision();
	    fillRED(RequestEfectiveDate);
	    
		do{
			action.moveToElement(CompanyNameField);
		    action.click();
		    action.perform();
		}while(driver.findElements(By.xpath("//input[@id='quoteProfileData.productLines[0].products[2].selected']")).size() < 1);
		
		 clickPlans();
	}
	
	public void clickStandAlone (){
		WebDriverWait wait = new WebDriverWait (driver, timeOut);
		boolean loop = true;
		
		while(loop){
			action.moveToElement(CompanyNameField);	
			action.click();
			action.perform();
			
			if(driver.findElements(By.xpath("//img[@src = '../images/planfinder/ajax-loader.gif']")).size() < 1){
				loop = false;
			}
		}
		
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("standAlonefalse"))));			
		standAlone = driver.findElement(By.id("standAlonefalse"));			
		do{				
			standAlone.click();				
		}while(!standAlone.isSelected() && driver.findElements(By.id("dentalVisionCoveragefalse")).size() < 1);
	}
	
	public void clickPlans (){
		WebDriverWait wait = new WebDriverWait (driver, timeOut);
		boolean loop = true;
		
		while(loop){
			action.moveToElement(CompanyNameField);	
			action.click();
			action.perform();
			
			if(driver.findElements(By.xpath("//img[@src = '../images/planfinder/ajax-loader.gif']")).size() < 1){
				loop = false;
			}
		}
		
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[@id='quoteProfileData.productLines[0].products[2].selected']"))));			
		plans = driver.findElement(By.xpath("//input[@id='quoteProfileData.productLines[0].products[2].selected']"));			
		do{				
			plans.click();				
		}while(!plans.isSelected() && driver.findElements(By.xpath("//input[@id='quoteProfileData.productLines[0].products[2].selected']")).size() < 1);
	}

	public  void  clickViewRatesOnline (){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//button[@id='gene_rate_btn']"))));
		
		action.moveToElement(driver.findElement(By.xpath("//button[@id='gene_rate_btn']")));
		action.click();
        action.perform();
        
        // Wait for loading to appear....
        while (driver.findElements(By.xpath("//img[@src = '../images/planfinder/ajax-loader.gif']")).size() < 1 ) {            
        }
        
        // Wait for loading to disappear...
        while (driver.findElements(By.xpath("//img[@src = '../images/planfinder/ajax-loader.gif']")).size() > 0) {            
        }     
        
        // Waiting for 'no' button to be clickable
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//button[@id='btn_drug_plan_no']"))));
	}
	
		
	
		
	public  void  clickGenerateProposal (){
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("submit_btn"))));
        
        action.moveToElement(driver.findElement(By.id("submit_btn")));
        action.click();
        action.perform();
        
        // Wait for loading to appear....
        while (driver.findElements(By.xpath("//img[@src = '../images/planfinder/ajax-loader.gif']")).size() < 1 ) {            
        }
        
        // Wait for loading to disappear...
        while (driver.findElements(By.xpath("//img[@src = '../images/planfinder/ajax-loader.gif']")).size() > 0) {            
        }     
        
        // Waiting for 'no' button to be clickable
        wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//button[@id='btn_drug_plan_no']"))));
        

	}
}

			
	
				
		
			
			
			
		
		  
		
				
		
			
		
		
		
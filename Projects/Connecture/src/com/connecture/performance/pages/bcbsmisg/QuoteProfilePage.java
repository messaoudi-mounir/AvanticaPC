package com.connecture.performance.pages.bcbsmisg;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.interactions.Actions;

import com.connecture.performance.pages.NavigationPage;

public class QuoteProfilePage extends NavigationPage{


	public QuoteProfilePage(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	public int ENROLLING = 2;
	
	@FindBy(xpath = "//input[@id='grpName']")
	WebElement companyNameField;
	
	@FindBy(xpath = "//input[@id='quoteProfileForm_quoteProfile_quoteZip']")
	WebElement zipCodeTxt;
	
	@FindBy(xpath = "//select[@id='group_state']")
	WebElement stateComboBox;
	
	@FindBy(xpath = "//input[@id='quoteProfileForm_quoteProfile_groupProfile_sicCode']")
	WebElement sicCodeTxt;
	
	@FindBy(xpath = "//input[@id='totalEligibleMedical']")
	WebElement totalFTETxt;
	
	@FindBy (xpath ="//input[@id=totalEligibleHipaaMedical")
	WebElement totalHIPAA;
	
	@FindBy(xpath = "//input[@id='totalEnrolledMedical']")
	WebElement expectedActiveEnrollingTxt;
	
	@FindBy(xpath = "//input[@id='cobraRetireeMedical']")
	WebElement expectedCOBRAEnrollingTxt;
	
	@FindBy(xpath = "//input[@id='hasesMedical']")
	WebElement hasesTxt;
	
	@FindBy(xpath = "//input[@id='quoteProfileForm_quoteProfile_quoteDescription']")
	WebElement nameOfTheQuoteTxt;
	
	@FindBy(xpath = "//input[@id='reqEffDate']")
	WebElement requestedEffectiveDate;
	
	@FindBy(xpath = "//input[@id='rateEffDate']")
	WebElement renewalEffectiveDate;
	
	
	@FindBy(xpath = "//select[@id = 'quoteProfile.groupProfile.mgaId']")
	WebElement managingAgentCombo;
	
	@FindBy(xpath = "//button[@value ='Continue']")
	WebElement continueButton;
	
	@FindBy (xpath="//button[@id='btn_drug_plan_no']")
	 public WebElement NoPediatric;
	Actions action = new Actions(driver);
	
	
	/********************************SUPERCLASS METHODS****************************/
	@Override
	public CensusPage getNextPage(){
		return new CensusPage(driver);
			
		}
		@Override
		public String getNextPageElementXpath(){
			return "//button[@id='btn_drug_plan_no']";
		}



		@Override
		public String getNextPageButtonXpath() {
			return "//button[@id='ViewRates_10']";
		}
	
	/************************************METHODS****************************/
	
		public void fillCompanyNameField(String companyName){
			do{
				WebDriverWait wait = new WebDriverWait(driver, timeOut);
				wait.until(ExpectedConditions.visibilityOf(companyNameField));
				companyNameField.clear();
				companyNameField.sendKeys(companyName);
				
			}while( !companyNameField.getAttribute("value").equals(companyName) );
		}
		
		public void fillZipCodeFIeld(int zipCode) {		
			WebDriverWait wait = new WebDriverWait(driver, timeOut);
			do{
				wait.until(ExpectedConditions.visibilityOf(zipCodeTxt));
				zipCodeTxt.clear();
				zipCodeTxt.sendKeys(""+zipCode);			
			}while( zipCodeTxt.getAttribute("value").equals(zipCode) );
			
		}
	
		public void fillSicCodeFIeld(int sicCode) {		
			WebDriverWait wait = new WebDriverWait(driver, timeOut);
			do{
				wait.until(ExpectedConditions.visibilityOf(sicCodeTxt));
				sicCodeTxt.clear();
				sicCodeTxt.sendKeys(""+sicCode);			
			}while( sicCodeTxt.getAttribute("value").equals(sicCodeTxt) );
			
		}
		
		public void fillManagingAgentCombo(int index){
			WebDriverWait wait = new WebDriverWait(driver, timeOut);
			wait.until(ExpectedConditions.visibilityOf(managingAgentCombo));
			Select select = new Select(managingAgentCombo);
			select.selectByIndex(index);			
		}
		
		public void fillNameOfQuoteTxt(String nameOfQuote){
			do{
				WebDriverWait wait = new WebDriverWait(driver, timeOut);
				wait.until(ExpectedConditions.visibilityOf(nameOfTheQuoteTxt));
				nameOfTheQuoteTxt.clear();
				nameOfTheQuoteTxt.sendKeys(nameOfQuote);
				
			}while( !nameOfTheQuoteTxt.getAttribute("value").equals(nameOfQuote) );
		}
	
		
		public void fillRED (String RequestEfectiveDate){
			WebDriverWait wait = new WebDriverWait(driver, timeOut);
			wait.until(ExpectedConditions.visibilityOf(requestedEffectiveDate));
			do{
				requestedEffectiveDate.clear();
				requestedEffectiveDate.sendKeys(RequestEfectiveDate);
			}while(!requestedEffectiveDate.getAttribute("value").equals(RequestEfectiveDate));
		
		}
		
		public void fillRNED (String RenewalEfectiveDate){
			WebDriverWait wait = new WebDriverWait(driver, timeOut);
			wait.until(ExpectedConditions.visibilityOf(requestedEffectiveDate));
			do{
				requestedEffectiveDate.clear();
				requestedEffectiveDate.sendKeys(RenewalEfectiveDate);
			}while(!requestedEffectiveDate.getAttribute("value").equals(RenewalEfectiveDate));
		
		}
		
		public void fillTotalFTETxt(){
			do{
				WebDriverWait wait = new WebDriverWait(driver, timeOut);
				wait.until(ExpectedConditions.visibilityOf(totalFTETxt));
				totalFTETxt.clear();
				totalFTETxt.sendKeys(""+ENROLLING);
				
			}while( totalFTETxt.getAttribute("value").equals(totalFTETxt) );
		}
		
		public void fillTotalHIPAA(){
			do{
				WebDriverWait wait = new WebDriverWait(driver, timeOut);
				wait.until(ExpectedConditions.visibilityOf(totalHIPAA));
				totalHIPAA.clear();
				totalHIPAA.sendKeys(""+ENROLLING);
				
			}while( totalHIPAA.getAttribute("value").equals(totalHIPAA) );
		}
		
		public void fillExpectedActiveEnrollingTxt(){
			do{
				WebDriverWait wait = new WebDriverWait(driver, timeOut);
				wait.until(ExpectedConditions.visibilityOf(expectedActiveEnrollingTxt));
				expectedActiveEnrollingTxt.clear();
				expectedActiveEnrollingTxt.sendKeys(""+ENROLLING);
				
			}while( expectedActiveEnrollingTxt.getAttribute("value").equals(totalFTETxt) );
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
			System.out.println("Method finished");				
			// }while(driver.findElements(By.xpath("//button[@id='quoteCensusForm_2']")).size() < 1);// || driver.findElements(By.xpath("//button[@id='quoteCensusForm_2']")).size() < 1
			
		}
		
		public void fillQuickQuoteForm(String companyName, int ZipCode, int SICCode, String NameOfQuoteTx,  String RequestEfectiveDate,String RenewalEfectiveDate, int index){	
			
			fillCompanyNameField(companyName);
			fillManagingAgentCombo(index);
		    fillZipCodeFIeld(ZipCode);
		    fillSicCodeFIeld(SICCode);
		    fillNameOfQuoteTxt(NameOfQuoteTx);	    
		    fillRED(RequestEfectiveDate);
		    fillRNED(RenewalEfectiveDate);
		    fillTotalFTETxt();
		    //fillTotalHIPAA();
		    fillExpectedActiveEnrollingTxt();
		    
		}
		
		public  void  clickContinue (){
			WebDriverWait wait = new WebDriverWait(driver, timeOut);
			wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//button[@value ='Continue']"))));
			
			action.moveToElement(driver.findElement(By.xpath("//button[@value ='Continue']")));
			action.click();
	        action.perform();
	        
	        // Wait for loading to appear....
	        while (driver.findElements(By.xpath("//button[@id='btn_drug_plan_no']")).size() < 1 ) {            
	        }
		
		
		
}
}

//**********************************************************************************************************************************************

//************************************************OLDCODE****************************************************************************
//package com.connecture.performance.pages.bcbsmisg;

//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.FindBy;
//import org.openqa.selenium.support.PageFactory;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.FluentWait;
//import org.openqa.selenium.support.ui.Select;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.openqa.selenium.interactions.Actions;

//import com.connecture.performance.pages.NavigationPage;

//public class QuoteProfilePage extends NavigationPage{

	
	//private static final FluentWait<WebDriver> wait = null;

	//private static final Actions action = null;
	//public QuoteProfilePage(WebDriver driver) {
		///super(driver);
		//PageFactory.initElements(driver, this);
	//}
	
	//int ENROLLING = 2;
	
	//@FindBy(xpath = "//input[@id='grpName']")
	//WebElement companyNameField;
	
	//@FindBy(xpath = "//input[@id='quoteProfileForm_quoteProfile_quoteZip']")
	//WebElement zipCodeTxt;
	
	//@FindBy(xpath = "//select[@id='group_state']")
	//WebElement stateComboBox;
	
	//@FindBy(xpath = "//input[@id='quoteProfileForm_quoteProfile_groupProfile_sicCode']")
	//WebElement sicCodeTxt;
	
	//@FindBy(xpath = "//input[@id='totalEligibleMedical']")
	//WebElement totalFTETxt;
	
	//@FindBy(xpath = "//input[@id='totalEnrolledMedical']")
	//WebElement expectedActiveEnrollingTxt;
	
	////@FindBy(xpath = "//input[@id='cobraRetireeMedical']")
	//WebElement expectedCOBRAEnrollingTxt;
	
	//@FindBy(xpath = "//input[@id='hasesMedical']")
	//WebElement hasesTxt;
	
	//@FindBy(xpath = "//input[@id='quoteProfileForm_quoteProfile_quoteDescription']")
	//WebElement nameOfTheQuoteTxt;
	
	//@FindBy(xpath = "//input[@id='reqEffDate']")
	//WebElement requestedEffectiveDate;
	
	//@FindBy(xpath = "//input[@id='rateEffDate']")
	//WebElement renewalEffectiveDate;
	
	
	//@FindBy(xpath = "//select[@id = 'quoteProfile.groupProfile.mgaId']")
	//WebElement managingAgentCombo;
	
	//int pageType = 0;

	//private Object requestedDate;
	//********************************SUPERCLASS METHODS****************************
	//@Override
	//public CensusPage getNextPage(){
		//return new CensusPage(driver);
		
	//}
	//@Override
	//public String getNextPageElementXpath(){
		//return "//button[@id='btn_drug_plan_no'";
	//}



	//@Override
	//public String getNextPageButtonXpath() {
	//	return "//button[@id='ViewRates_10']";
	//}	
	
	//************************************METHODS****************************
	
	
	
	//public void fillCompanyNameField(String companyName){
		//do{
		//*	WebDriverWait wait = new WebDriverWait(driver, timeOut);
			//wait.until(ExpectedConditions.visibilityOf(companyNameField));
			//companyNameField.clear();
			
			//companyNameField.sendKeys(companyName);
			//requestedEffectiveDate.clear();
			//requestedEffectiveDate.sendKeys((CharSequence[]) requestedDate);
		//}while(requestedEffectiveDate.getText().equals(requestedDate));
		
		//wait.until(ExpectedConditions.visibilityOf (totalFTETxt));
		//totalFTETxt.clear();
		//totalFTETxt.sendKeys(""+ENROLLING);
		
		//wait.until(ExpectedConditions.visibilityOf (expectedActiveEnrollingTxt));
		//expectedActiveEnrollingTxt.clear();
		//expectedActiveEnrollingTxt.sendKeys(""+ENROLLING);
		
		//wait.until(ExpectedConditions.visibilityOf (expectedCOBRAEnrollingTxt));
		//expectedCOBRAEnrollingTxt.clear();
		//expectedCOBRAEnrollingTxt.sendKeys(""+0);
		
		//wait.until(ExpectedConditions.visibilityOf (hasesTxt));
		//hasesTxt.clear();
		//hasesTxt.sendKeys(""+0);
		
		//wait.until(ExpectedConditions.visibilityOf (managingAgentCombo));
		//Select select = new Select(managingAgentCombo);
		//select.selectByIndex(10);	
		
	//}
	
//public void fillQuoteForm(String companyName, int ZipCode, int SICCode,  String RequestEfectiveDate){	
		
		//fillCompanyNameField(companyName);
		//fillmanagingAgentCombo();
		//fillTotalFTETxt();
		//do{
			//action.moveToElement(companyNameField);
		    //action.click();
		    //action.perform();
		//}while(driver.findElements(By.xpath("//input[@id='quote_zip']")).size() < 1);
		 
	   // fillZipCodeFIeld(ZipCode);
	
	//public  void  clickContinueButton (){
		//WebDriverWait wait = new WebDriverWait(driver, timeOut);
		//wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("btn_continue_dental_plan"))));
        
        //action.moveToElement(driver.findElement(By.id("btn_continue_dental_plan")));
        //action.click();
        //action.perform();
        
        // Wait for loading to appear....
       // while (driver.findElements(By.xpath("//img[@src = '../images/planfinder/ajax-loader.gif']")).size() < 1 ) {            
       // }
        
        // Wait for loading to disappear...
        //while (driver.findElements(By.xpath("//img[@src = '../images/planfinder/ajax-loader.gif']")).size() > 0) {            
       // }     
        
        /// Waiting for 'no' button to be clickable
       // wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//button[@id='btn_drug_plan_no']"))));
        
        //action.moveToElement(driver.findElement(By.xpath("//button[@id='btn_drug_plan_no']")));
        //action.click();
        //action.perform();
       // }}

	
	
	




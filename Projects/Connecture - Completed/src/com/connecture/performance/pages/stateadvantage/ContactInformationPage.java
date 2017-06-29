package com.connecture.performance.pages.stateadvantage;

import org.apache.log.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.connecture.performance.pages.BasePage;


public class ContactInformationPage extends BasePage{

	public ContactInformationPage(WebDriver driver) {
		super(driver);	
	}
	
	
	public static ContactInformationPage getPage(WebDriver driver, Logger logHandler) {
		ContactInformationPage  page = PageFactory.initElements(driver, ContactInformationPage.class);
	    page.setLogHandler(logHandler);
	    return page;	    
	}
	
	
	/**************************************Filing Forms methods**************************************/
	
	@FindBy(xpath="//input[@id = 'firstName']")
    public WebElement firstNameField;
	
	public void fillFirstName(String firstName){
		logInfo("Filling First name...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(firstNameField));       
        firstNameField.sendKeys(firstName);
        if(!firstNameField.getAttribute("value").equals(firstName)){
        	firstNameField.clear();
        	fillFirstName(firstName);
        }
	}
	
	@FindBy(xpath="//input[@id = 'lastName']")
    public WebElement lastNameField;
	
	public void fillLastName(String lastName){
		logInfo("Filling Last name...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(lastNameField));
        lastNameField.sendKeys(lastName);
        
        if(!lastNameField.getAttribute("value").equals(lastName)){
        	lastNameField.clear();
        	fillLastName(lastName);
        }
	}
	
	@FindBy(xpath="//input[@id = 'addressLineOneHome']")
    public WebElement streetAddress;
	
	public void fillStreetAddress(String address){
		logInfo("Filling Street Address...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(streetAddress));
        streetAddress.clear();
        streetAddress.sendKeys(address);
	}
	
	@FindBy(xpath="//input[@id = 'cityHome']")
    public WebElement cityField;
	
	public void fillCity(String city){
		logInfo("Filling city...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(cityField));
        cityField.clear();
        cityField.sendKeys(city);
	}
	
	@FindBy(xpath="//select[@id = 'stateHome']")
    public WebElement stateCombo;
	
	public void selectState(String state){
		logInfo("Filling state info...");
		logInfo("Waiting for state combo to be visible"); 
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(stateCombo));
		logInfo("Selecting value: "+state); 
		Select select = new Select(stateCombo);
		select.selectByValue(state); 
	}
	
	@FindBy(xpath="//input[@id = 'zipCodeHome']")
    public WebElement zipCodeField;
	
	public void fillZipCode(String zipCode){
		logInfo("Filling zip code...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(zipCodeField));
        zipCodeField.sendKeys(zipCode);
	}
	
	@FindBy(xpath="//input[@id = 'phoneNumber']")
    public WebElement phoneField;
	
	public void fillPhoneNumber(String phone){
		logInfo("Filling zip code...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(zipCodeField));
        phoneField.clear();
        phoneField.sendKeys(phone);
	}
	
	@FindBy(xpath="//input[@id = 'ssn']")
    public WebElement ssnField;
	
	public void fillSSN(String ssn){
		logInfo("Filling SSN...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(ssnField));
        ssnField.clear();
        ssnField.sendKeys(ssn);
	}
	
	@FindBy(xpath="//input[@id = 'dateOfBirth']")
    public WebElement dateField;
	
	public void fillDateOfBirth(String date){
		logInfo("Filling date of birth...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(dateField));
        dateField.clear();
        dateField.sendKeys(date);
	}
	
	@FindBy(xpath="//input[@id = 'userName']")
    public WebElement userField;
	
	public void fillUserName(String userDynamic){
		logInfo("Filling user name...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(userField));
        userField.clear();
        userField.sendKeys(userDynamic);
	}
	
	@FindBy(xpath="//input[@id = 'email']")
    public WebElement emailField;
	
	public void fillEmailAddress(String email){
		logInfo("Filling email...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.elementToBeClickable(emailField));
        emailField.sendKeys(email);
        
        if(!emailField.getAttribute("value").equals(email)){
        	emailField.clear();
        	fillEmailAddress(email);
        }
        
	}
	
	@FindBy(xpath="//input[@id = 'userPassword']")
    public WebElement passwordField;
	
	public void fillPassword(String password){
		logInfo("Filling password...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(passwordField));
        passwordField.clear();
        passwordField.sendKeys(password);
	}
	
	@FindBy(xpath="//input[@id = 'confPassword']")
    public WebElement confPasswordField;
	
	public void fillConfirmPassword(String password){
		logInfo("Filling password confirmation...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(confPasswordField));
        confPasswordField.clear();
        confPasswordField.sendKeys(password);
	}
	
	public void fillContactInfoForm(String firstName, String lastName, String address, 
			String city, String state, String zipCode, String phone, String ssn, String date,
			String user, String email){
		logInfo("Filling Contact Information Form...");		
		fillFirstName(firstName);
		fillLastName(lastName);
		fillStreetAddress(address);
		fillCity(city);
		selectState(state);
		fillZipCode(zipCode);
		fillPhoneNumber(phone);
		fillSSN(ssn);
		fillDateOfBirth(date);
		fillUserName(user);
		fillEmailAddress(email);
		fillPassword("password");
		fillConfirmPassword("password");
		
		selectQuestion1(0);
		fillAnswer1("cousin");
		selectQuestion2(1);
		fillAnswer2("father");
		selectQuestion3(2);
		fillAnswer3("mother");
		
		logInfo("Contact Information Form Filled...");
	}
	/******************************************Security Questions**********************************************************/

	@FindBy(xpath="//select[@id = 'questionOne']")
    public WebElement questionOne;
	
	public void selectQuestion1(int index){
		logInfo("Filling state info...");
		logInfo("Waiting for state combo to be visible"); 
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(questionOne));		
		Select select = new Select(questionOne);
		select.selectByIndex(index); 
	}
	
	@FindBy(xpath="//select[@id = 'questionTwo']")
    public WebElement questionTwo;
	
	public void selectQuestion2(int index){
		logInfo("Filling state info...");
		logInfo("Waiting for state combo to be visible"); 
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(questionTwo));		
		Select select = new Select(questionTwo);
		select.selectByIndex(index);  
	}
	
	@FindBy(xpath="//select[@id = 'questionThree']")
    public WebElement questionThree;
	
	public void selectQuestion3(int index){
		logInfo("Filling state info...");
		logInfo("Waiting for state combo to be visible"); 
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(questionThree));		 
		Select select = new Select(questionThree);
		select.selectByIndex(index); 
	}
	
	@FindBy(xpath="//input[@id = 'answerOne']")
    public WebElement answerOne;
	
	public void fillAnswer1(String answer){
		logInfo("Filling Answer One...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(answerOne));
        answerOne.clear();
        answerOne.sendKeys(answer);
	}
	
	@FindBy(xpath="//input[@id = 'answerTwo']")
    public WebElement answerTwo;
	
	public void fillAnswer2(String answer){
		logInfo("Filling Answer Two...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(answerTwo));
        answerTwo.clear();
        answerTwo.sendKeys(answer);
	}
	
	@FindBy(xpath="//input[@id = 'answerThree']")
    public WebElement answerThree;
	
	public void fillAnswer3(String answer){
		logInfo("Filling Answer Three...");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(answerThree));
        answerThree.clear();
        answerThree.sendKeys(answer);
	}
	
	/**********************************************************************************************************************/
	
	@FindBy(xpath="//a[contains(text(), 'Continue')]")
    public WebElement continueButton;
	
	public void checkContinueButtonClickable(){
		logInfo("Checking Continue button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(continueButton));
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        logInfo("Continue button is clickable now");
	}
	
	
	public CompanyInformationPage goToCompanyInformationPage(){
		logInfo("Clicking Continue Button...");    	
        continueButton.click();
        logInfo("Continue button clicked...");
        return CompanyInformationPage.getPage(driver, logHandler);        
	}
	

}

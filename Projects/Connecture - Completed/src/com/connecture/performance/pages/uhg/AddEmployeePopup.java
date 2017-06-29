package com.connecture.performance.pages.uhg;

import org.apache.log.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import com.connecture.performance.pages.BasePage;

public class AddEmployeePopup extends BasePage {
    
    // Factory method
    public static AddEmployeePopup getPage(WebDriver driver, Logger logHandler) {
        AddEmployeePopup  page = PageFactory.initElements(driver, AddEmployeePopup.class);
        page.setLogHandler(logHandler);
        return page;
    }
    
    public AddEmployeePopup(WebDriver driver) {
        super(driver);
    }    
    
    
    @FindBy(xpath="//span[text() = 'Save']/..")
    public WebElement saveButton;
    
    
    public void checkSaveButtonClickable() {
        logInfo("Checking save button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        
        // Check that label is not "Saving"
        logInfo("Skipping any 'not saving'");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//span[text() = 'Saving']")));
        
        logInfo("Checking save button...");        
        wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        logInfo("Save button is clickable now");                                      
        
        
    }
    
    public void fillOutEmployeeForm(String numEmployeeStr) {
        fillOutEmployeeForm(Integer.parseInt(numEmployeeStr));
    }
    
    
    public void fillOutEmployeeForm(int numEmployee) {
        logInfo("Filling out form for employee # " + numEmployee);
        fillOutAge(numEmployee);
        fillOutSalary(numEmployee);
        fillEmail();
        fillDateOfHire();
        fillHoursWorked();
        sleepFor(5);
    }
    
    @FindBy(xpath="//input [@name = 'emailAddress']")
    public WebElement emailField;  
    
    public void fillEmail (){
    	WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(emailField));
        emailField.clear();
        emailField.sendKeys("noreply@connecture.com");
        
    }
    
    @FindBy(xpath="//input [contains(@name,  'dateOfHire')]")
    public WebElement dateOfHireField; 
    
    public void fillDateOfHire(){
    	WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(dateOfHireField));
        dateOfHireField.click();
        dateOfHireField.clear();
        dateOfHireField.sendKeys("03032013");
    }
    
    @FindBy(xpath="//input [contains(@name,  'numHoursWorked')]")
    public WebElement hoursWorkedField;
    
    public void fillHoursWorked(){
    	WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(hoursWorkedField));
        hoursWorkedField.click();
        hoursWorkedField.clear();
        hoursWorkedField.sendKeys("40");
    }
    
    @FindBy(xpath="//input [@name = 'age']")
    public WebElement ageField;    

    public void fillOutAge(int numEmployee) {
        logInfo("Filling age ...");

        // Ages starting at 25
        int age = 24 + numEmployee ;
        String ageStr = Integer.toString(age);

        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(ageField));
        ageField.clear();
        ageField.sendKeys(ageStr);               
    }

    @FindBy(xpath="//input [@name = 'salary']")
    public WebElement salaryField;    
    
    
    public void fillOutSalary(int numEmployee) {
        logInfo("Filling salary");
        // Salaries starting at 25,000
        int salary = 24000 + (numEmployee * 1000);        
        String salaryStr = Integer.toString(salary) + "00";
        logInfo("Salary string: " + salaryStr);
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        wait.until(ExpectedConditions.visibilityOf(salaryField));        
        salaryField.click();
        salaryField.sendKeys(Keys.END);
        fillSalary(salaryStr);
        logInfo("JS value: "+salaryField.getAttribute("value"));
        emailField.click();
    }

    public void fillSalary(String salaryStr){
    	for(int i=0;i<salaryStr.length();i++){
        	salaryInput(i+1, salaryStr.charAt(i));
        }
    }    
    public void salaryInput(int spacesToMove, char salary){
    	moveToRight(spacesToMove);
    	logInfo("Typing.. " + salary + "into the salary field");
    	salaryField.sendKeys(""+salary);
    	
    }
    
    public void moveToRight (int spacesToMove){
    	logInfo("Moving " + spacesToMove + "spaces to the right");
    	for(int i = 0; i< spacesToMove; i++){
    		salaryField.sendKeys(Keys.ARROW_RIGHT);
    	}
    }
    
    public void implicitWaitForElement(int milliseconds){
    	try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @FindBy(xpath="//span[contains(text(), 'Add Another Employee')]")
    public WebElement addAnotherButton;

    public void clickAddAnotherButton() { 
        checkAddAnotherButtonClickable();
        logInfo("Clicking add another ...");
        addAnotherButton.click();
        logInfo("Add another clicked");
    }
    
    public void checkAddAnotherButtonClickable() {
        logInfo("Checking add another employee button to be clickable");
        WebDriverWait wait = new WebDriverWait(driver, 300);
        wait.until(ExpectedConditions.elementToBeClickable(addAnotherButton));
        logInfo("Add another employee button is clickable now");                                      
    }
    
  
    public CensusPage clickSaveButton() {
    	logInfo("Clicking Save button...");
        saveButton.click();
        logInfo("Save button clicked");
        return CensusPage.getPage(driver, logHandler);
     
    }
        
}

package Stage1;


import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RegisterTests{
	private WebDriver driver;
	private String baseUrl;

	

	//Setting up the firefox driver and URL to work with.
	@BeforeClass(alwaysRun = true)
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
	    baseUrl = "http://192.168.0.103:86/";
	    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}
	
	@Test
	public void goToRegisterPage(){
		driver.get(baseUrl + "/default.aspx");
		clickElement(By.id("ctl00_LoginView_RegisterLink"));
		Assert.assertTrue(isElementPresent(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_FirstName")));		    	    
	}
	
	@Test
	public void emptySubmitErrors(){
		goToRegisterPage();
		clickElement(By.id("ctl00_Main_CreateUserWizardControl___CustomNav0_StepNextButtonButton")); //click Submit
		verifyErrorLabelsPresent();
	}
	
	@Test
	public void verifyDuplicateUserError(){
		goToRegisterPage();
		
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_FirstNameRequired")).sendKeys("FirstName");
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_LastNameRequired")).sendKeys("LastName");
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_EmailRequired")).sendKeys("alejandro.quesada@avantica.com");
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_UserNameRequired")).sendKeys("aquesada");//already exists
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_PasswordRequired")).sendKeys("passw0rd#");
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_ConfirmPasswordRequired")).sendKeys("passw0rd#");
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_QuestionRequired")).sendKeys("question?");
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_AnswerRequired")).sendKeys("yes");
		
		clickElement(By.id("ctl00_Main_CreateUserWizardControl___CustomNav0_StepNextButtonButton"));
		
		verifyUserAvailable();
	}
	
	@Test
	public void verifyPasswordAndConfirm(){
		goToRegisterPage();
		
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_FirstNameRequired")).sendKeys("FirstName");
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_LastNameRequired")).sendKeys("LastName");
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_EmailRequired")).sendKeys("alejandro.quesada@avantica.com");
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_UserNameRequired")).sendKeys("aquesada");//already exists
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_PasswordRequired")).sendKeys("passw0rd#");
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_ConfirmPasswordRequired")).sendKeys("passw0rd!");
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_QuestionRequired")).sendKeys("question?");
		driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_AnswerRequired")).sendKeys("yes");
		
		clickElement(By.id("ctl00_Main_CreateUserWizardControl___CustomNav0_StepNextButtonButton"));
		
		verifyPassAndConfirm();
	}
	
	public void verifyPassAndConfirm(){
		Assert.assertTrue(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_PasswordCompare")).isDisplayed());
	}
	
	public void verifyUserAvailable(){
		Assert.assertTrue(driver.findElement(By.id("ctl00_Main_InfoLabel")).isDisplayed());
	}
	
	public void verifyErrorLabelsPresent(){
		Assert.assertTrue(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_FirstNameRequired")).isDisplayed());
		Assert.assertTrue(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_LastNameRequired")).isDisplayed());
		Assert.assertTrue(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_EmailRequired")).isDisplayed());
		Assert.assertTrue(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_UserNameRequired")).isDisplayed());
		Assert.assertTrue(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_PasswordRequired")).isDisplayed());
		Assert.assertTrue(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_ConfirmPasswordRequired")).isDisplayed());
		Assert.assertTrue(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_QuestionRequired")).isDisplayed());
		Assert.assertTrue(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_AnswerRequired")).isDisplayed());
	}
	
	public void clickElement(By by){	
		waitElementBy(by);
		driver.findElement(by).click();
		
	}
	
	public void waitElementBy (By by){
		WebDriverWait wait = new WebDriverWait(driver, 15);		
		wait.until(ExpectedConditions.visibilityOfElementLocated(by));
		wait.until(ExpectedConditions.elementToBeClickable(by));		
	}
	
	public boolean isElementPresent(By by){
		try{
			return driver.findElements(by).size() > 0;
		}
		catch(Exception ex){
			return false;
		}
	}
}	

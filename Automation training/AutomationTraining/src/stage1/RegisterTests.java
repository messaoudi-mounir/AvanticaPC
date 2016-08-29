package stage1;


import static org.testng.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegisterTests extends BasePage{
	
	
	public RegisterTests (WebDriver driver){
		super(driver);
	}
	
	private WebDriver driver;
	private String baseUrl;
	private StringBuffer verificationErrors = new StringBuffer();

	
	
	@BeforeClass
	public void className (){
		System.out.println("Executing class: RegisterTests");
	}
	//Setting up the firefox driver and URL to work with.
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
	    baseUrl = "http://192.168.0.103:86/";
	    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);	
		System.out.println("Creating Driver...");	    
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
		
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_FirstNameRequired"),"FirstName");
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_LastNameRequired"),"LastName");
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_EmailRequired"),"alejandro.quesada@avantica.com");
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_UserNameRequired"),"aquesada");//already exists
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_PasswordRequired"),"passw0rd#");
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_ConfirmPasswordRequired"),"passw0rd#");
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_QuestionRequired"),"question?");
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_AnswerRequired"),"yes");
		
		clickElement(By.id("ctl00_Main_CreateUserWizardControl___CustomNav0_StepNextButtonButton"));
		
		verifyUserAvailable();
	}
	
	@Test
	public void verifyPasswordAndConfirm(){
		goToRegisterPage();
		
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_FirstNameRequired"),"FirstName");
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_LastNameRequired"),"LastName");
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_EmailRequired"),"alejandro.quesada@avantica.com");
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_UserNameRequired"),"aquesada");//already exists
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_PasswordRequired"),"passw0rd#");
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_ConfirmPasswordRequired"),"passw0rd!");
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_QuestionRequired"),"question?");
		sendKeysBy(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_AnswerRequired"),"yes");
		
		clickElement(By.id("ctl00_Main_CreateUserWizardControl___CustomNav0_StepNextButtonButton"));
		
		verifyPassAndConfirm();
	}
	
	 @AfterMethod(alwaysRun = true)
	  public void tearDown() throws Exception {
		  System.out.println("Deleating driver...");
		  driver.quit();
		  String verificationErrorString = verificationErrors.toString();
		  if (!"".equals(verificationErrorString)) {
			  fail(verificationErrorString);
		  }
	  }
	  
	  @AfterTest
	  public void afterTestMethod(){
		  System.out.println("Completing Execution...");
	  }
	  
	  @AfterClass
	  public void afterClassMethod(){
		  System.out.println("Completing execution class: RegisterTests");
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
	
	
	
	public boolean isElementPresent(By by){
		try{
			return driver.findElements(by).size() > 0;
		}
		catch(Exception ex){
			return false;
		}
	}
}	

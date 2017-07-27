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

public class RegisterTests {
	
	
	public RegisterTests (WebDriver driver){
		this.driver = driver;
		baseUrl = "http://192.168.0.103:86/";
		basePage = new BasePage(driver);
	}
	
	private WebDriver driver;
	private String baseUrl;
	private BasePage basePage;
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
		basePage.clickElement(driver.findElement(By.id("ctl00_LoginView_RegisterLink")));
		Assert.assertTrue(isElementPresent(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_FirstName")));		    	    
	}
	
	@Test
	public void emptySubmitErrors(){
		goToRegisterPage();
		basePage.clickElement(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl___CustomNav0_StepNextButtonButton"))); //click Submit
		verifyErrorLabelsPresent();
	}
	
	@Test
	public void verifyDuplicateUserError(){
		goToRegisterPage();
		
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_FirstNameRequired")),"FirstName");
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_LastNameRequired")),"LastName");
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_EmailRequired")),"alejandro.quesada@avantica.com");
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_UserNameRequired")),"aquesada");//already exists
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_PasswordRequired")),"passw0rd#");
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_ConfirmPasswordRequired")),"passw0rd#");
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_QuestionRequired")),"question?");
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_AnswerRequired")),"yes");
		
		basePage.clickElement(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl___CustomNav0_StepNextButtonButton")));
		
		verifyUserAvailable();
	}
	
	@Test
	public void verifyPasswordAndConfirm(){
		goToRegisterPage();
		
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_FirstNameRequired")),"FirstName");
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_LastNameRequired")),"LastName");
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_EmailRequired")),"alejandro.quesada@avantica.com");
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_UserNameRequired")),"aquesada");//already exists
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_PasswordRequired")),"passw0rd#");
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_ConfirmPasswordRequired")),"passw0rd!");
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_QuestionRequired")),"question?");
		basePage.sendKeysBy(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_AnswerRequired")),"yes");
		
		basePage.clickElement(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl___CustomNav0_StepNextButtonButton")));
		
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
		Assert.assertTrue(basePage.waitElementVisible(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_FirstNameRequired"))));
		Assert.assertTrue(basePage.waitElementVisible(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_LastNameRequired"))));
		Assert.assertTrue(basePage.waitElementVisible(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_EmailRequired"))));
		Assert.assertTrue(basePage.waitElementVisible(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_UserNameRequired"))));
		Assert.assertTrue(basePage.waitElementVisible(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_PasswordRequired"))));
		Assert.assertTrue(basePage.waitElementVisible(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_ConfirmPasswordRequired"))));
		Assert.assertTrue(basePage.waitElementVisible(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_QuestionRequired"))));
		Assert.assertTrue(basePage.waitElementVisible(driver.findElement(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_AnswerRequired"))));
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

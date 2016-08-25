package Stage1;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class LogInTests {
	
	private WebDriver driver;
	private String baseUrl;
	private StringBuffer verificationErrors = new StringBuffer();

	@BeforeClass
	public void className (){
		System.out.println("Executing class: LogInTests");
	}
	//Setting up the firefox driver and URL to work with.
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {
		System.out.println("Creating driver...");
		driver = new FirefoxDriver();
	    baseUrl = "http://192.168.0.103:86/";	    
	    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);	    
	}
	@BeforeTest
	public void startExecution (){
		System.out.println("Starting Execution...");
	}
	
	//Shuts down the Firefox driver
	
	   /* Excecutes the test, follows the workflow
	    * 1-Adding the Login path to t he baseUrl
	    * 2-Clicking element Login Link
	    * 3-Verifies if the value of the login button is "Log In"
	    * 4-Verifies that the "Forgot Password" element is present
	    * 3 and 4 Validating that we're effectively on the Login Page.*/  
	  @Test
	  public void testAlejandroQuesadaStage1QAAutomationTrainingPractice1Login1() throws Exception {
	    driver.get(baseUrl + "/Login.aspx");
	    driver.findElement(By.id("ctl00_LoginView_LoginLink")).click();
	    assertEquals(driver.findElement(By.id("ctl00_Main_LoginConrol_LoginButton")).getAttribute("value"), "Log In");
	    assertTrue(driver.findElement(By.id("ctl00_Main_ForgotPasswordButton")).getText().matches("^exact:Forgot Password[\\s\\S]$"));
	  }
	  /* Excecutes the test, follows the workflow
	    * 1-Adding the Login path to the baseUrl
	    * 2-Enters the user name
	    * 3-Enters the password value
	    * 4-Verifies that the "Logout" element is present
	    * Validating that we were able to successfully log into the application.*/ 
	  @Test
	  public void testAlejandroQuesadaStage1QAAutomationTrainingPractice1Login2() throws Exception {
	    driver.get(baseUrl + "/default.aspx");
	    driver.findElement(By.id("ctl00_LoginView_LoginLink")).click();
	    driver.findElement(By.id("ctl00_Main_LoginConrol_UserName")).clear();
	    driver.findElement(By.id("ctl00_Main_LoginConrol_UserName")).sendKeys("aquesada");
	    driver.findElement(By.id("ctl00_Main_LoginConrol_Password")).clear();
	    driver.findElement(By.id("ctl00_Main_LoginConrol_Password")).sendKeys("Ljnd1709#");
	    driver.findElement(By.id("ctl00_Main_LoginConrol_LoginButton")).click();
	    assertEquals(driver.findElement(By.id("ctl00_LoginView_MemberLoginStatus")).getText(), "Logout");
	    assertTrue(driver.findElement(By.id("ctl00_Main_LoginConrol_UserName")).getText() == "aquesada");
	  } 
	  /* Excecutes the test, follows the workflow
	   * 1-Adding the Login path to the baseUrl
	   * 2-Enters a valid user name
	   * 3-Enters the wrong password value
	   * 4-Verifies that the an error is displayed indicating that the login was unsuccessful
	   * Validating that we were able to successfully log into the application.*/ 
	  @Test
	  public void testAlejandroQuesadaStage1QAAutomationTrainingPractice1Login3() throws Exception {
	    driver.get(baseUrl + "/");
	    driver.findElement(By.id("ctl00_LoginView_LoginLink")).click();
	    driver.findElement(By.id("ctl00_Main_LoginConrol_UserName")).clear();
	    driver.findElement(By.id("ctl00_Main_LoginConrol_UserName")).sendKeys("aquesada");
	    driver.findElement(By.id("ctl00_Main_LoginConrol_Password")).clear();
	    driver.findElement(By.id("ctl00_Main_LoginConrol_Password")).sendKeys("password");
	    driver.findElement(By.id("ctl00_Main_LoginConrol_LoginButton")).click();
	    assertEquals(driver.findElement(By.xpath("//table[@id='ctl00_Main_LoginConrol']/tbody/tr/td/table/tbody/tr[4]/td")).getText(), "Your login attempt was not successful. Please try again.");
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
		  System.out.println("Completing execution class: LogInTests");
	  }
}

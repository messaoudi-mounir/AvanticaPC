package com.example.tests;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.testng.annotations.*;
import static org.testng.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

// Setting up the Class and variables.
public class AlejandroQuesadaStage1QAAutomationTrainingPractice1Login1 {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  //Setting up the firefox driver and URL to work with.
  @BeforeClass(alwaysRun = true)
  public void setUp() throws Exception {
    driver = new FirefoxDriver();
    baseUrl = "http://192.168.0.103:86/";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
  }

   /* Excecutes the test, follows the workflow
    * 1-Adding the Login path to the baseUrl
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

  //Shuts down the Firefox driver
  @AfterClass(alwaysRun = true)
  public void tearDown() throws Exception {
    driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}

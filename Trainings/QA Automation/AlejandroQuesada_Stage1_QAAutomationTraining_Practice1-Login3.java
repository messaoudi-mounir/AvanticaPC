package com.example.tests;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.testng.annotations.*;
import static org.testng.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

//Setting up the Class and variables.
public class AlejandroQuesadaStage1QAAutomationTrainingPractice1Login3 {
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

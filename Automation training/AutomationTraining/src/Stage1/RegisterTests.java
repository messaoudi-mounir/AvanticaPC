package Stage1;

import static org.testng.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RegisterTests{
	private WebDriver driver;
	private String baseUrl;
	private WebElement element;
	

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
		    	    
	}
	
	
	
	public void clickElement(By by){	
		waitElementById(by);
		driver.findElement(by).click();
		Assert.assertTrue(isElementPresent(By.id("ctl00_Main_CreateUserWizardControl_CreateUserStepContainer_FirstName")));
	}
	
	public void waitElementById (By by){
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

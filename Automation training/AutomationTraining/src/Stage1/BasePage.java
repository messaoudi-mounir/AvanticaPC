package stage1;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {
	
	private WebDriver driver;
	public String baseUrl;
	
	public BasePage(WebDriver driver){
		this.driver = driver;
		setup(driver);
	}
	
	public void setup(WebDriver driver){
		driver = new FirefoxDriver();
	    baseUrl = "http://192.168.0.103:86/";
	    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);	
		System.out.println("Creating Driver...");	  
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
	
}

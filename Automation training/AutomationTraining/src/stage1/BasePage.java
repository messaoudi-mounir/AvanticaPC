package stage1;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {
	
	private WebDriver driver;	
	private WebDriverWait wait = new WebDriverWait(driver, 15);
	
	public BasePage(WebDriver driver){
		this.driver = driver;		
	}
	
	public boolean clickElement(By by){	
		try{
			waitElementClickable(by);
			driver.findElement(by).click();
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public boolean waitElementVisible (By by){
		try{
			return wait.until(ExpectedConditions.visibilityOfElementLocated(by)) != null;			
		}catch(Exception ex){
			return false;
		}					
	}
	
	public boolean waitElementClickable(By by){
		try{
		return wait.until(ExpectedConditions.elementToBeClickable(by)) != null;				
		
		}catch(Exception ex){
			return false;
		}
	}
	
	public boolean sendKeysBy(By by, String text){
		try{
			waitElementVisible(by);
			driver.findElement(by).clear();
			driver.findElement(by).sendKeys(text);
			return true;
		}catch (Exception ex){
			return false;
		}
	}
	
	public String getTextBy(By by){
		try{
			return driver.findElement(by).getText();			
		}catch (Exception ex){
			return null;
		}
	}
	
	public String getAttributeBy(By by, String attribute){
		try{
			return driver.findElement(by).getAttribute(attribute);			
		}catch (Exception ex){
			return null;
		}
	}
	
	
}

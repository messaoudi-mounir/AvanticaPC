package stage1;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {
	
	private WebDriver driver;	
	private WebDriverWait wait = new WebDriverWait(driver, 15);
	
	public BasePage(WebDriver driver){
		this.driver = driver;		
	}
	
	public boolean clickElement(WebElement element){	
		try{
			waitElementClickable(element);
			element.click();
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public boolean waitElementVisible (WebElement element){
		try{
			wait.until(ExpectedConditions.visibilityOf(element));
			return true;
		}catch(Exception ex){
			return false;
		}					
	}
	
	public boolean waitElementClickable(WebElement element){
		try{
			wait.until(ExpectedConditions.elementToBeClickable(element));
			return true;
		}catch(Exception ex){
			return false;
		}
		
	}
	
	public boolean waitElementEnabled(String locator){
		try{
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
			return true;
		}catch(Exception ex){
			return false;
		}
		
	}
	
	public WebElement findElementBy(By by){
		try{
		// Agregar una espera a que el elemento este presente en el html
		return driver.findElement(by);				
		
		}catch(Exception ex){
			return null;
		}
	}
	
	public boolean sendKeysBy(WebElement element, String text){
		try{
			waitElementVisible(element);	// aca seria solo enabled
			element.clear();
			element.sendKeys(text);
			
			return true;
		}catch (Exception ex){
			return false;
		}
	}
	
	// waitfortextonElement(webeleemt string txt)
	// va a esperar hasta que el elemento tenga el texto
	// element.getText().equals(txt);
	
	public String getTextBy(By by){
		try{
			// agregar espera por presente
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
package avantica;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;





public class Pruebas {
	public final int timeOut = 600;
	public WebDriver driver;
	public WebDriverWait wait = new WebDriverWait(driver, timeOut);

	
	public Pruebas(WebDriver driver){
		this.driver = driver;
	}
	
	@FindBy(xpath="//a[@id="+"ctl00_LoginView_LoginLink"+"]")
	private WebElement loginLink;
	
	@FindBy(xpath="//span[@class='bcrBienesLogo']")
	private WebElement logoBCR;
	
	public boolean click(WebElement  element){
		try{
			wait.until(ExpectedConditions.elementToBeClickable(element)).click();
			return true;
			
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			e.getStackTrace();
			
			return false;
			
		}
		
	}
	
	public void goToLoginPage(){
		click(loginLink);
	}
	
}


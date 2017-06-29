package avantica;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage extends Pruebas{
	
	public LoginPage (WebDriver driver){
		super(driver);		
	}
	
	@FindBy(xpath="//a[@id="+"ctl00_Main_LoginConrol_UserName"+"]")
	private WebElement usernameField;
	
	@FindBy(xpath="//a[@id="+"ctl00_Main_LoginConrol_Password"+"]")
	private WebElement passwordField;
	
	public void fillLoginForm(String username, String password){		
		WebDriverWait wait = new WebDriverWait(driver, timeOut);
		wait.until(ExpectedConditions.visibilityOf(usernameField));
		usernameField.clear();
		usernameField.sendKeys(username);
		
		wait.until(ExpectedConditions.visibilityOf(passwordField));
		passwordField.clear();
		passwordField.sendKeys(password);
	}
}

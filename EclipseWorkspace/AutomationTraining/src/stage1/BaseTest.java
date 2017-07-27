package stage1;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class BaseTest {
	
	private WebDriver driver;
	private String nodeUrl;

	public boolean createLocalDriver(String browserType){
		System.out.println("Creating driver...");
		try{
			switch(browserType){
			case "firefox":
				driver = new FirefoxDriver();	    
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				return true;
			case "chrome":
				driver = new ChromeDriver();	    
				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				return true;
			default:				
				return false;
			}
		}catch(Exception ex){
			System.out.println("Driver creation failed");
			return false;
		}
	}
	
	public boolean createRemoteDriver(String browserType){
		System.out.println("Creating driver...");
		try{
			switch(browserType){
			case "firefox":
				nodeUrl = "http://localhost:4444/wd/hub";
				DesiredCapabilities capabilityFF = DesiredCapabilities.firefox();
				capabilityFF.setBrowserName(browserType);
				capabilityFF.setPlatform(Platform.WINDOWS);
				driver = new RemoteWebDriver(new URL(nodeUrl), capabilityFF);
				return true;
			case "chrome":
				DesiredCapabilities capabilityCH = DesiredCapabilities.chrome();
				capabilityCH.setBrowserName(browserType);
				capabilityCH.setPlatform(Platform.WINDOWS);
				driver = new RemoteWebDriver(new URL(nodeUrl), capabilityCH);
				return true;
			default:				
				return false;
			}
		}catch(Exception ex){
			System.out.println("Driver creation failed");
			return false;
		}
		
				
	}
	
}

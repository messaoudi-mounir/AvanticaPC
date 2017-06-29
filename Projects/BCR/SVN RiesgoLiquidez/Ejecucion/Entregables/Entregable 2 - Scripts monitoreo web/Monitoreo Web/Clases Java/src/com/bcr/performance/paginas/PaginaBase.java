package com.bcr.performance.paginas;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;

public class PaginaBase {
	protected WebDriver driver;
	private WebDriverWait wait;	
	
	@FindBy (id="btnLogOff")
	private WebElement logoutBtn;
	
	public PaginaBase(WebDriver otherDriver){
		driver = otherDriver;
		wait = new WebDriverWait(driver, 10);
	}
	
	public boolean click(WebElement element){
		try{
//			wait.until(ExpectedConditions.elementToBeClickable(element)).click()
			 boolean vlr = wait.until(new ExpectedCondition<Boolean>(){
				   public Boolean apply(WebDriver arg0)
			   {
			       return element.isEnabled();
		       } 
				   });
			   
			element.click();
			return vlr;
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public boolean esperarElemento(WebElement element){
		try{
			return wait.until(ExpectedConditions.visibilityOf(element))!=null;
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public boolean esperarElementos(List<WebElement> lista){
		try{
			return wait.until(ExpectedConditions.visibilityOfAllElements(lista))!=null;
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public boolean escribirEnElemento(WebElement element, String texto){
		try{
			esperarElemento(element);
			element.sendKeys(texto);
			return true;
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	protected WebElement encontrarElemento(WebElement element, By by) {
		try{
			return element.findElement(by);
		}catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println(e.getStackTrace());
			return null;
		}
	}
	
	public WebElement encontrarElemento(By by){
		
		try{
			
			return wait.until(ExpectedConditions.presenceOfElementLocated(by));
			
			
		}catch(NoSuchElementException e){
			
			return null;
		}
		
	}
	
	public boolean esperarElementoHabilitado(WebElement element){		
		try{
			return wait.until(new ExpectedCondition<Boolean>(){
			    public Boolean apply(WebDriver arg0){
			    	return element.isEnabled();
			    }});			
		}catch(Exception e){
			return false;			
		}
	}
	
	public boolean cerrarSesion(){
		try{				
			return click(logoutBtn);				
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public boolean seleccionarElemento(WebElement element, String datos){
		try{
			Select opcion = new Select(element);
			click(element);
			opcion.selectByVisibleText(datos);
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	
	public boolean esperarTamanioMinimoLista(final List<WebElement> elements, final int minSize)
    {
	   try
	   {
		   return wait.until(new ExpectedCondition<Boolean>(){
		   public Boolean apply(WebDriver arg0)
	   {
	       return elements.size() >= minSize;
       } 
		   });
	   }
	   catch (Exception e)
	   {  
	    return false;
	   }
	   
    }
}

package com.bcr.performance.paginas;
import java.util.List;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PaginaBase {
	protected WebDriver driver;
	private WebDriverWait wait;
	public PaginaBase(WebDriver otherDriver){
		driver = otherDriver;
		wait = new WebDriverWait(driver, 10);
	}
	
	//click function
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
	
	//Retorna verdadero si el elemento web es visible antes de 10 segundos
	public boolean esperarElemento(WebElement element){
		
		
		return wait.until(ExpectedConditions.visibilityOf(element))!=null;

		
	}
	//Retorna verdadero si la lista de elementos web es visible antes de 10 segundos
	public boolean esperarElementos(List<WebElement>list){
		
		return wait.until(ExpectedConditions.visibilityOfAllElements(list))!=null;

		
	}
	//Retorna el texto que hay en un elemento web.
	public String obtenerTexto(WebElement element){
		try{
			return wait.until(ExpectedConditions.visibilityOf(element)).getText();
			
		}
		catch(Exception e){
			return null;
			
		}
		
	}
	//Retorna verdadera si el tamaño minimo de la lista es el esperado.
	public boolean esperarPorTamanioMinimoLista(final List<WebElement> elements, final int minSize)
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
	//Retorna verdadero si el contenido de la lista coinicide con el esperado
	public boolean verificarContenidoLista(final List<WebElement> elements, final List<String> content){
		boolean equal = false;
		for(int i = elements.size();i-->0 && (equal=obtenerTexto(elements.get(i)).equals(content.get(i)));){}
		return equal;
	}
	
	//Retorna verdadero si el elemento web contiene el texto esperado.
	public boolean esperarTexto(WebElement element,String text){
		try
		   {
			   return wait.until(new ExpectedCondition<Boolean>(){
			   public Boolean apply(WebDriver arg0)
		   {
		       return element.getText().equals(text);
	       } 
			   });
		   }
		   catch (Exception e)
		   {  
		    return false;
		   }
	}
	//escribe en un elemento
	public boolean escribirEnElemento(WebElement element,String text){
		
		try{
			wait.until(ExpectedConditions.visibilityOf(element)).clear();
			element.sendKeys(text);
			return true;
			
		}
		catch(Exception e){
			return false;
			
		}
	}
}

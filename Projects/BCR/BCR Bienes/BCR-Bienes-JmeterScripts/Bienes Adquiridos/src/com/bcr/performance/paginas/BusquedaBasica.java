package com.bcr.performance.paginas;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class BusquedaBasica extends PaginaBase{
	@FindBy(xpath="//a[.//label[text()='Casas']]")
	private WebElement opcionCasa;
	@FindBy(xpath="//a[.//label[text()='Terrenos']]")
	private WebElement opcionTerrenos;
	@FindBy(xpath="//a[.//label[text()='Comercios']]")
	private WebElement opcionComercios;
	@FindBy(xpath="//a[.//label[text()='Descuentos']]")
	private WebElement opcionDescuentos;
	public BusquedaBasica(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	//verifica que los elementos de la página están presentes
	public boolean verificarCarga(){
		return esperarElemento(opcionCasa) && esperarElemento(opcionTerrenos);	
		
	}
	//retorna la pagina de Casas si logra acceder a ella
	public PaginaCasa irAPaginaCasa(){
		PaginaCasa pagina= null;
		if(esperarElemento(opcionCasa) && click(opcionCasa)){
			   pagina = new PaginaCasa(driver);
			
		}
		
		return pagina;		
		
		
	}
	
	//retorna la pagina de Terrenos si logra acceder a ella
	public PaginaTerrenos irAPaginaTerrenos(){
		PaginaTerrenos pagina= null;
		if(esperarElemento(opcionTerrenos) && click(opcionTerrenos)){
			   pagina = new PaginaTerrenos(driver);
			
		}
		
		return pagina;		
		
		
	}
	
	//retorna la pagina de Comercios si logra acceder a ella
	public PaginaComercios irAPaginaComercios(){
		PaginaComercios pagina= null;
		if(esperarElemento(opcionComercios) && click(opcionComercios)){
			   pagina = new PaginaComercios(driver);
			
		}
		
		return pagina;		
		
		
	}
	
	//retorna la pagina de descuentos, si logra acceder a ella
	public PaginaDescuentos irAPaginaDescuentos(){
		PaginaDescuentos pagina= null;
		if(esperarElemento(opcionDescuentos) && click(opcionDescuentos)){
				pagina = new PaginaDescuentos(driver);
			
		}
		
		return pagina;
	}
		
		
}


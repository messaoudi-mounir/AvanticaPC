package com.bcr.performance.paginas;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
public class PaginaTodasLasPlantillas extends PaginaBase {
	@FindBy (xpath="//h3[text()='Todas las Plantillas']")
	private WebElement tituloPagina;
	
	public PaginaTodasLasPlantillas(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver,this);
		
	}
	
	public boolean verificarCarga(){
		return esperarElemento(tituloPagina);
		
	}
	
	
	
}

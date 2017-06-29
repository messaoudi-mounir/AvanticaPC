package com.bcr.performance.paginas;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PaginaTodosLosReportes extends PaginaBase{
	
	@FindBy (xpath="//h3[text()='Todos los Reportes']")
	private WebElement tituloReportes;
	
	@FindBy (xpath="//table[@id='reportsTable']")
	private WebElement tablaReportes;
	
	public PaginaTodosLosReportes(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver, this);
	}
	//Verifica que se carguen los elementos de la pagina
	public boolean verificarCarga(){
		return esperarElemento(tituloReportes) && esperarElemento(tablaReportes);
		
	}

	
}

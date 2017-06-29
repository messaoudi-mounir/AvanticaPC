package com.bcr.performance.paginas;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
public class PaginaContactenos extends PaginaBase {
	
	@FindBy (xpath="//form[@id='enviarCorreoForm']")
	private WebElement formulario;
	
	public PaginaContactenos(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	public boolean verificarCarga(){
		return esperarElemento(formulario);	
		
	}
}
package com.bcr.performance.paginas;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PaginaTodasLasOrdenes extends PaginaBase{
	
	@FindBy (xpath="//h3[text()='Todas las Órdenes']")
	private WebElement tituloOrdenes;
	
	@FindBy (xpath="//table[@id='ordersTable']")
	private WebElement tablaOrdenes;
	
	@FindBy (xpath="//button[@id='btnNewOrder']")
	private WebElement nuevaOrden;
	public PaginaTodasLasOrdenes(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	public boolean verificarCarga(){
		return esperarElemento(tituloOrdenes) && esperarElemento(tablaOrdenes) && esperarElemento(nuevaOrden);
		
	}
	
}

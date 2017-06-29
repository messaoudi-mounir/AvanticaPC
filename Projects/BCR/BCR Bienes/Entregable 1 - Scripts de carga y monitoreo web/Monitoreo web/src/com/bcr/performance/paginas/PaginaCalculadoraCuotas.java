package com.bcr.performance.paginas;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
public class PaginaCalculadoraCuotas extends PaginaBase {
	
	@FindBy (xpath="//label[text()='calculadora de cuotas de propiedades']")
	private WebElement calcPropiedades;
	
	@FindBy (xpath="//label[text()='calculadora de cuotas de vehiculos']")
	private WebElement calcVehiculos;
	public PaginaCalculadoraCuotas(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	public boolean verificarCarga(){
		return esperarElemento(calcPropiedades) && esperarElemento(calcVehiculos);	
		
	}
}

package com.bcr.performance.paginas;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
public class PaginaComoAdquirirBien extends PaginaBase {
	@FindBy (xpath="//p/img[contains(@src,'/wps/wcm/connect/bcrb')]")
	private List<WebElement> seccionDetalle;
	
//	@FindBy (xpath="//h1/img[contains(@src,'/wps/wcm/connect/bcrb')]")
//	private WebElement imgPrincipal;
	public PaginaComoAdquirirBien(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	public boolean verificarCarga(){
		return  esperarPorTamanioMinimoLista(seccionDetalle, 2);	
		
	}
}

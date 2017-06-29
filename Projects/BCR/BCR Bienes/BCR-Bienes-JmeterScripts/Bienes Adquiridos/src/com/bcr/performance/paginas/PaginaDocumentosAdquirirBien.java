package com.bcr.performance.paginas;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
public class PaginaDocumentosAdquirirBien extends PaginaBase {
	
	@FindBy (xpath="//div[contains(@class,'docsCell pagingItem')]")
	private List<WebElement> seccionDetalle;
	public PaginaDocumentosAdquirirBien(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	public boolean verificarCarga(){
		return esperarPorTamanioMinimoLista(seccionDetalle, 4);	
		
	}
}
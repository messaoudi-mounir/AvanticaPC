package com.bcr.performance.paginas;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
public class PaginaTerrenoDetalle extends PaginaBase {
	@FindBy (xpath="//a[contains(@onclick,'openImageDialog')]/img")
	private WebElement imagenDetalle;
	@FindBy (xpath="//div[contains(@class,'detailTextSectionBox')]")
	private WebElement seccionDetalle;
	public PaginaTerrenoDetalle(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}

	public boolean verificarCarga(){
		return esperarElemento(imagenDetalle) && esperarElemento(seccionDetalle);	
		
	}
}

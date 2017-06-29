package com.bcr.performance.paginas;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PaginaTerrenos extends PaginaBase{
	@FindBy (xpath="//div[contains(@class,'BCRSlider slider')]")
	private WebElement sliderTerrenos;
	
	@FindBy (xpath ="//div[contains(@class,'catalogoBienesHomeBox')]//a[contains(@href,'tipo_propiedad')]")
	private List<WebElement> listaTerrenos;
	
	public PaginaTerrenos(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver, this);		
		
	}
	
	//verifica que los elementos de la página están presentes
	public boolean verificarCarga(){
		return esperarElemento(sliderTerrenos) && esperarElementos(listaTerrenos) && esperarPorTamanioMinimoLista(listaTerrenos,1);	
		
	}
	
	public PaginaTerrenoDetalle irATerrenoDetalle(){
		PaginaTerrenoDetalle pagina= null;
		if(esperarElemento(listaTerrenos.get(0)) && click(listaTerrenos.get(0))){
			pagina = new PaginaTerrenoDetalle(driver);
			
		}
		
		return pagina;
		
	}
}

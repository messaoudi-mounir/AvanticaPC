package com.bcr.performance.paginas;

import java.util.List;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PaginaCasa extends PaginaBase{
	@FindBy (xpath="//div[contains(@class,'BCRSlider slider')]")
	private WebElement sliderCasa;
	
	@FindBy (xpath ="//div[contains(@class,'catalogoBienesHomeBox')]//a[contains(@href,'tipo_propiedad')]")
	private List<WebElement> listaCasas;
	
	public PaginaCasa(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver, this);		
		
	}
	
	//verifica que los elementos de la página están presentes
	public boolean verificarCarga(){
		return esperarElemento(sliderCasa) && esperarElementos(listaCasas) && esperarPorTamanioMinimoLista(listaCasas,1);	
		
	}
	
	public PaginaCasaDetalle irACasaDetalle(){
		PaginaCasaDetalle pagina= null;
		if(esperarElemento(listaCasas.get(0)) && click(listaCasas.get(0))){
			pagina = new PaginaCasaDetalle(driver);
			
		}
		
		return pagina;
		
	}
}

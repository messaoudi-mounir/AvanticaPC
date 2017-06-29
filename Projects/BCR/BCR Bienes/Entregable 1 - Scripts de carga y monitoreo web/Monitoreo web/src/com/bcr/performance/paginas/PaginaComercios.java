package com.bcr.performance.paginas;


import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
public class PaginaComercios extends PaginaBase{
	@FindBy (xpath="//div[contains(@class,'BCRSlider slider')]")
	private WebElement sliderComercios;
	
	@FindBy (xpath ="//div[contains(@class,'catalogoBienesHomeBox')]//a[contains(@href,'tipo_propiedad')]")
	private List<WebElement> listaComercios;
	
	public PaginaComercios(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver, this);		
		
	}
	
	//verifica que los elementos de la página están presentes
	public boolean verificarCarga(){
		return esperarElemento(sliderComercios) && esperarElementos(listaComercios) && esperarPorTamanioMinimoLista(listaComercios,1);	
		
	}
	
	public PaginaComercioDetalle irAComercioDetalle(){
		PaginaComercioDetalle pagina= null;
		if(esperarElemento(listaComercios.get(0)) && click(listaComercios.get(0))){
			pagina = new PaginaComercioDetalle(driver);
			
		}
		
		return pagina;
		
	}
}

package com.bcr.performance.paginas;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PaginaVehiculos extends PaginaBase{
	@FindBy (xpath="//div[contains(@class,'BCRSlider slider')]")
	private WebElement sliderVehiculos;
	
	@FindBy (xpath ="div[contains(@class,'catalogoBienesHomeBox')]//a[contains(@href,'codigo')]")
	private List<WebElement> listaVehiculos;
	
	public PaginaVehiculos(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver, this);		
		
	}
	
	//verifica que los elementos de la página están presentes
	public boolean verificarCarga(){
		return esperarElemento(sliderVehiculos) && esperarElementos(listaVehiculos) && esperarPorTamanioMinimoLista(listaVehiculos,1);	
		
	}
	
//	public PaginaVehiculoDetalle irATerrenoDetalle(){
//		PaginaVehiculoDetalle pagina= null;
//		if(esperarElemento(listaVehiculos.get(0)) && click(listaVehiculos.get(0))){
//			pagina = new PaginaVehiculoDetalle(driver);
//			
//		}
//		
//		return pagina;
//		
//	}

}

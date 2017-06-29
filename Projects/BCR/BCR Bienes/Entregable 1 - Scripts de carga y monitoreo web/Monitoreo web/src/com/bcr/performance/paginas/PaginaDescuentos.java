package com.bcr.performance.paginas;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
public class PaginaDescuentos extends PaginaBase{
	@FindBy (xpath="//div[contains(@class,'BCRSlider slider')]")
	private WebElement sliderDescuentos;
	
	@FindBy (xpath ="//div[contains(@class,'catalogoBienesHomeBox')]//a[contains(@href,'descuento')]")
	private List<WebElement> listaDescuentos;
	
	public PaginaDescuentos(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver, this);		
		
	}
	
	//verifica que los elementos de la página están presentes
	public boolean verificarCarga(){
		return esperarElemento(sliderDescuentos) && esperarElementos(listaDescuentos) && esperarPorTamanioMinimoLista(listaDescuentos,1);	
		
	}
	
	public PaginaDescuentoDetalle irADescuentoDetalle(){
		PaginaDescuentoDetalle pagina= null;
		if(esperarElemento(listaDescuentos.get(0)) && click(listaDescuentos.get(0))){
			pagina = new PaginaDescuentoDetalle(driver);
			
		}
		
		return pagina;
		
	}

}

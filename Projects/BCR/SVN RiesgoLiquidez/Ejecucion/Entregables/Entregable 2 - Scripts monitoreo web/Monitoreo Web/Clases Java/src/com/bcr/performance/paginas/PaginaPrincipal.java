package com.bcr.performance.paginas;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PaginaPrincipal extends PaginaBase {
	@FindBy (xpath="//div[@id='navbar']/ul")
	private WebElement barraPrincipal;
	
	public PaginaPrincipal(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver,this);		
	}
	
	
	public boolean verificarCarga(){
		return esperarElemento(barraPrincipal);		
	}
	
	public PaginaSolicitud irAPaginaSolicitud(){
		PaginaSolicitud pagina = null;
		if(click(obtenerElementoBarra("Solicitud"))){
			pagina = new PaginaSolicitud(driver);
			
		}
		return pagina;
	}
	
	public PaginaTodasLasPlantillas irAPaginaPlantillas(){
		PaginaTodasLasPlantillas pagina = null;
		if(click(obtenerElementoBarra("Plantillas")) && click(obtenerElementoBarra("Todas las Plantillas"))){
			pagina = new PaginaTodasLasPlantillas(driver);
			
		}
		return pagina;
		
	}
	

	public PaginaTodasLasOrdenes irAPaginaOrdenes(){
		PaginaTodasLasOrdenes pagina = null;
		if(click(obtenerElementoBarra("Órdenes")) && click(obtenerElementoBarra("Todas las Órdenes"))){
			pagina = new PaginaTodasLasOrdenes(driver);
			
		}
		return pagina;
			
	}
	
	public PaginaTodosLosReportes irAPaginaReportes(){
		PaginaTodosLosReportes pagina = null;
		if(click(obtenerElementoBarra("Reportes")) && click(obtenerElementoBarra("Todos los Reportes"))){
			pagina = new PaginaTodosLosReportes(driver);
			
			
		}
		
		return pagina;
		
	}
	private WebElement obtenerElementoBarra(String opcion){
		return esperarElemento(barraPrincipal) ? encontrarElemento(barraPrincipal, By.xpath(".//li/a[text()='"+opcion+"']")):null;		
	}
	
	
	
}

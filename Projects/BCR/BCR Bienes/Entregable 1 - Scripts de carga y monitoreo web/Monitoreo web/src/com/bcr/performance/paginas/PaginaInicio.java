package com.bcr.performance.paginas;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PaginaInicio extends PaginaBase {
	@FindBy(xpath="//ul[@class='main']/li")
	private List<WebElement> listaBusquedaBasica;
	
	@FindBy(xpath="//span[@class='bcrBienesLogo']")
	private WebElement logoBCR;
	
	@FindBy(xpath="//ul[@class='sec']//a[.//label[text()='Planes de Financiamiento']]")
	private WebElement planesFinanciamiento;
	
	@FindBy(xpath="//ul[@class='sec']//a[.//label[text()='¿Cómo adquirir un bien?']]")
	private WebElement comoAdquirirBien;
	
	@FindBy(xpath="//ul[@class='sec']//a[.//label[text()='Documentos para adquirir un bien']]")
	private WebElement documentoAdquirirBien;
	
	@FindBy(xpath="//ul[@class='sec']//a[.//label[text()='calculadora de cuotas']]")
	private WebElement calculadoraCuotas;
	
	@FindBy(xpath="//ul[@class='sec']//a[.//label[text()='contáctenos']]")
	private WebElement  contactenos;
	
	@FindBy(xpath="//ul[@class='sec']//a[.//label[text()='Inicio']]")
	private WebElement inicio;
	public PaginaInicio(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	//verifica que los elementos de la página están presentes
	public boolean verificarCarga(){
		return esperarElementos(listaBusquedaBasica) && esperarPorTamanioMinimoLista(listaBusquedaBasica,5)
				&& esperarElemento(logoBCR);	
		
	}
	//retorna la lista de busquedaBasica
	public BusquedaBasica irABusquedaBasica(){
		BusquedaBasica pagina= null;
		pagina = new BusquedaBasica(driver);		
		return pagina;		
		
		
	}
	//retorna la pagina de planes de financiamiento
	public PaginaPlanesFinanciamiento irAPlanesFinanciamiento(){
		PaginaPlanesFinanciamiento pagina = null;
		if(esperarElemento(planesFinanciamiento) && click(planesFinanciamiento)){
			pagina = new PaginaPlanesFinanciamiento(driver);
		}
		return pagina;
		
	}
	//retorna la pagina de Como adquirir un bien
	public PaginaComoAdquirirBien irAComoAdquirirBien(){
		PaginaComoAdquirirBien pagina = null;
		if(esperarElemento(comoAdquirirBien) && click(comoAdquirirBien)){
			pagina = new PaginaComoAdquirirBien(driver);
		}
		return pagina;
		
	}
	//retorna la pagina de Calculadora de cuotas
	public PaginaCalculadoraCuotas irACalculadoraCuotas(){
		PaginaCalculadoraCuotas pagina = null;
		if(esperarElemento(calculadoraCuotas) && click(calculadoraCuotas)){
			pagina = new PaginaCalculadoraCuotas(driver);
		}
		return pagina;
		
	}
	//retorna la pagina de documentos para adquirir un bien
	public PaginaDocumentosAdquirirBien irADocumentosAdquirirBien(){
		PaginaDocumentosAdquirirBien pagina = null;
		if(esperarElemento(documentoAdquirirBien) && click(documentoAdquirirBien)){
			pagina = new PaginaDocumentosAdquirirBien(driver);
		}
		return pagina;
		
	}
	//retorna la pagina de contactenos
	public PaginaContactenos irAContactenos(){
		PaginaContactenos pagina = null;
		if(esperarElemento(contactenos) && click(contactenos)){
			pagina = new PaginaContactenos(driver);
		}
		return pagina;
		
	}
	
	public PaginaInicio irAInicio(){
		PaginaInicio pagina = null;
		if(esperarElemento(inicio) && click(inicio)){
				pagina = new PaginaInicio(driver);
		}
		return pagina;
		
	}
	
	

}

package com.bcr.performance.paginas;

import java.util.List;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


public class PaginaSolicitud extends PaginaBase {
	
	
	public PaginaSolicitud(WebDriver driver){
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	/*Elementos de Activos y Pasivos*****************/
	@FindBy (xpath="//select[@id='fldReportType']")
	private WebElement tipoReporteFld;
	
	@FindBy(xpath="//input[@id='fldHorizon']")
	private WebElement horizonteTxt;
	
	@FindBy(xpath="//input[@id='fldConfidenceLevel']")
	private WebElement confianzaTxt;
	
	@FindBy(xpath="//button[contains(@onclick,'addFilter')]")
	private List<WebElement> agregarTipoEspecifico;
	
	@FindBy(xpath="//select[@id='fldCurrency']")
	private WebElement moneda;
	
	@FindBy(xpath="//select[@id='fldAssetProduct']")
	private WebElement producto;
	
	@FindBy(xpath="//select[@id='fldLiabilityProduct']")
	private WebElement productos;	
	
	@FindBy(xpath="//select[@id='fldClientGroup']")
	private WebElement grupoCliente;
	
	@FindBy (xpath="//select[@id='fldClientType']")
	private WebElement cliente;
	
	@FindBy (xpath="//input[@id='fldClientId']")
	private WebElement clienteId;
	
	@FindBy (xpath="//input[@id='fldFilterCDPRenovationRate']")
	private WebElement renovacionCDP;
	
	@FindBy(xpath="//input[@id='fldFlterDelinquentRate']")
	private WebElement tasaMora;
	
	@FindBy(xpath="//input[@id='fldFilterEarlyPaymentRate']")
	private WebElement tasaPrepago;
	
	@FindBy(xpath="//input[@id='fldFilterCreditLineAvgUsage']")
	private WebElement avgUsoLineasTarjeta;
	
	@FindBy(xpath="//input[@id='fldFilterCreditPortfolioGrowth']")
	private WebElement crecimientoCarteraCredito;
	
	@FindBy (xpath="//input[@id='fldFilterLiabPublicGrowth']")
	private WebElement crecimientoPasivoPublico;
	
	@FindBy (xpath="//input[@id='fldFilterEarlyCDPCancellation']")
	private WebElement cancelacionAnticipadaCDP;
	
	@FindBy (xpath="//button[@id='btnFilterOk']")
	private WebElement btnAgregarEspecifico;
	
	/*Elementos de la opcion efectivos***********************/
	@FindBy(xpath="//form[@id='panelMacroeconomicParameter']")
	private WebElement formParametrosMacro;
	
	@FindBy (xpath="//form[@id='panelMacroeconomicParameter']//label")
	private List <WebElement> datosMacroEconomicos;
	
	/*Elementos de BackTesting *********/
	@FindBy(xpath="//input[@id='fldTemplateTitle']")
	private WebElement titulo;
	
	@FindBy (xpath="//button[@id='btnUploadFileObservationInputFile']")
	private WebElement cargarArchivo;
	
	@FindBy(xpath="//input[@type='file']")
	private WebElement cargarArchivoTxt;
	
	@FindBy (xpath="//button[@id='btnUploadFileStart']")
	private WebElement aceptarCargaArchivo;
	
	@FindBy(xpath="//span[@id='fldFileUploadName']")
	private WebElement nombreArchivoASubir;
	
	//Revisa que los elementos de la pagina carguen
	public boolean verificarCarga(){
		return esperarElemento(tipoReporteFld);
		
	}
	//Selecciona el tipo de reporte 
	public boolean seleccionaTipoReporte(String tipoReporte){
		return click(tipoReporteFld) && click(obtenerElementoLista(tipoReporteFld, tipoReporte));
	}
	
	/*Pasivos***************************************************/
	public boolean verificarCargaPasivos(){
		return esperarElemento(horizonteTxt) && esperarElemento(confianzaTxt) && esperarElemento(agregarTipoEspecifico.get(0));		
	}
	
	public boolean llenarEspecificoPasivos(String [] datos){
		if(click(agregarTipoEspecifico.get(0))){
			return verificarCargaEspecificosPasivos() 
					&& seleccionarElemento(moneda,datos[0])
					&& seleccionarElemento(productos,datos[1]) 
					&& seleccionarElemento(grupoCliente, datos[2])
					&& seleccionarElemento(cliente,datos[3])
					&& escribirEnElemento(clienteId, datos[4]) 
					&& escribirEnElemento(renovacionCDP, datos[5]) 
					&& escribirEnElemento(crecimientoPasivoPublico, datos[6])
					&& escribirEnElemento(cancelacionAnticipadaCDP, datos[7]);			
		}
		return false;		
	}
	
	public boolean agregarEspecifico(){
		try{
			return click(btnAgregarEspecifico);
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}

	}
	
	public boolean verificarCargaEspecificosPasivos(){
		return esperarElemento(moneda) 
				&& esperarElemento(productos)
				&& esperarElemento(cliente) 
				&& esperarElemento(grupoCliente) 
				&& esperarElemento(clienteId)			
				&& esperarElemento(renovacionCDP) 
				&& esperarElemento(crecimientoPasivoPublico) 
				&& esperarElemento(cancelacionAnticipadaCDP) 
				&& esperarElemento(btnAgregarEspecifico);
		
	}
	//Recibe una opcion y devuelve el elemento Web correspondiente de la lista
	private WebElement obtenerElementoLista(WebElement element, String opcion){
		return esperarElemento(element) ? encontrarElemento(element, By.xpath("descendant::option[text()='"+opcion+"']")):null;		
	}
	
	/*Activos y Pasivos ********************************************/
	
	public boolean verificarCargaActivosYPasivos(){
		return esperarElemento(horizonteTxt) && esperarElemento(confianzaTxt) && esperarElemento(agregarTipoEspecifico.get(1));		
	}
	public boolean verificarCargaEspecificosActivos(){
		return esperarElemento(moneda) 
				&& esperarElemento(producto)
				&& esperarElemento(cliente) 
				&& esperarElemento(grupoCliente) 
				&& esperarElemento(clienteId) 
				&& esperarElemento(tasaMora) 
				&& esperarElemento(tasaPrepago) 
				&& esperarElemento(avgUsoLineasTarjeta) 
				&& esperarElemento(crecimientoCarteraCredito);
	}
	
	public boolean llenarEspecificoActivosYPasivos(String [] datos){
		/*try {
			Thread.sleep(3000);
		} catch(Exception e) {
			
		}*/
		if(click(agregarTipoEspecifico.get(1))){	// wait for list min size
			return verificarCargaEspecificosActivos() 
					&& seleccionarElemento(moneda, datos[0] )					
					&& seleccionarElemento(producto,datos[1]) 
					&& seleccionarElemento(grupoCliente, datos[2])
					&& seleccionarElemento(cliente,datos[3])
					&& escribirEnElemento(clienteId, datos[4]) 
					&& escribirEnElemento(tasaMora, datos[5]) 
					&& escribirEnElemento(tasaPrepago, datos[6])
					&& escribirEnElemento(avgUsoLineasTarjeta, datos[7]) 
					&& escribirEnElemento(crecimientoCarteraCredito, datos[8])!= false;			
		}
		return false;		
	}
	
	/*Efectivos **************************************************/
	
	public boolean verificarCargaEfectivos(int tam){
		try{
			Thread.sleep(3000);
		}catch (Exception e){
			
		}
		return esperarElemento(formParametrosMacro) && esperarElementos(datosMacroEconomicos) && esperarTamanioMinimoLista(datosMacroEconomicos, tam);
	}
	
	/*Backtesting*************************************************/
	public boolean verificarCargaBackTesting(){
		return esperarElemento(titulo) && esperarElemento(cargarArchivo);		
	}
	//Abre el modal para cargar archivo nuevo
	public boolean clickCargarArchivo(){
		return click(cargarArchivo);		
	}
	
	public boolean cargarArchivo(String rutaArchivo){
		return esperarElementoHabilitado(cargarArchivoTxt) && escribirEnElemento(cargarArchivoTxt,rutaArchivo) &&
		esperarElemento(aceptarCargaArchivo) && click(aceptarCargaArchivo);				
	}
	
	
	public boolean verificarCargaArchivo(String nombreArchivo){
		return esperarElemento(revisarCargaArchivo(nombreArchivo));
	}
	
	private WebElement revisarCargaArchivo(String nombreArchivo){
		return encontrarElemento(By.xpath("//div[@id='lblFileObservationInputFile' and text()='"+nombreArchivo+"']"));
	}

}

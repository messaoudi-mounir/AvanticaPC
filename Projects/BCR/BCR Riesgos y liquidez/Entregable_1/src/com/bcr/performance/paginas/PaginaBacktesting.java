package com.bcr.performance.paginas;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class PaginaBacktesting extends PaginaBase{

	public PaginaBacktesting(WebDriver driver) {
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	@FindBy (id="btnUploadFileObservationInputFile")
	private WebElement cargarArchivoBtn;
	
	@FindBy (id="fldFileUploadName")
	private WebElement seleccionarArchivoBtn; 
	@FindBy (id="btnUploadFileStart")
	private WebElement subirArchivoBtn;
	
	public boolean clickCargarArchivo(){
		try{
			return click(cargarArchivoBtn);			 
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
	}

	public boolean cargarArchivo(String nombreArchivo){
		try{
			escribirEnElemento(seleccionarArchivoBtn, nombreArchivo);
			return click(subirArchivoBtn);
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	
	
	public boolean verificarCarga(){
		return esperarElemento(cargarArchivoBtn);		
	}
	
}

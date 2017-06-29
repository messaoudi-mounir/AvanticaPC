package com.bcr.performance.paginas;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class Login extends PaginaBase{
	//FindBy 
	@FindBy (id="fldUserLogin")
	private WebElement userTxt;
	
	@FindBy (id="fldUserPassword")
	private WebElement passTxt;
	
	@FindBy (id="btnLogin")
	private WebElement loginBtn;
	
	
	/*************************************/
	public Login (WebDriver driver){
		super(driver);
		PageFactory.initElements(driver, this);
	}
	
	
	public boolean verificarCarga(){
		return esperarElemento(userTxt) && esperarElemento(passTxt) && esperarElemento(loginBtn);		
	}
	//Navegacion
	
	public boolean llenarLogin (String usuario, String contrasena){
		try{
			escribirEnElemento(userTxt, usuario);
			escribirEnElemento(passTxt, contrasena);
			return true;
		}catch(Exception e){
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	public PaginaPrincipal iniciarSesion(){
		try{				
			click(loginBtn);				
			return new PaginaPrincipal(driver);
		}catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	
}

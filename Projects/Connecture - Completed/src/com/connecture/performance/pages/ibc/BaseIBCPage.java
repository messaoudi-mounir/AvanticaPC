package com.connecture.performance.pages.ibc;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BaseIBCPage {
    //  Wait time out, in seconds
    public static final int DEFAULT_TIMEOUT = 20;
    
    protected final WebDriver driver;
    
    protected static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    
    protected BaseIBCPage(WebDriver driver) {
        this.driver = driver;
    }
    
    protected int getWaitTimeot() {
        return DEFAULT_TIMEOUT;
    }
    
    protected static void pause(long millseconds) {
        try {
            Thread.sleep(millseconds);
            
        } catch (InterruptedException  e) {}
    }
    
    protected static void setDateField(WebDriver driver, WebElement dateField, String dateValue){
        WebDriverWait wait = new WebDriverWait (driver, DEFAULT_TIMEOUT);
        wait.until(ExpectedConditions.visibilityOf (dateField));        
        dateField.clear();
        dateField.sendKeys(dateValue);                        
    }
    
    public void closeBrowser() {
        driver.close();
    }
    
    public void clearCookies() {
        driver.manage().deleteAllCookies();        
    }
    
    // Print some info on stardand output alogin with date/ time
    public void printTimedInfo(String msg) {
        System.out.println(dateFormat.format(new Date()) + "-" + msg);
    }
    
    // Print performance related data to standard output
    public void printPerformanceData() {
        JavascriptExecutor jsExec = (JavascriptExecutor) driver;
        String jsonStr = (String)jsExec.executeScript("var perfarray = window.performance.getEntries();"+"return JSON.stringify(perfarray); ");
        printTimedInfo(jsonStr);        
    }

}

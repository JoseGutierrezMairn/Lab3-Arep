package edu.escuelaing.arep.app;

import java.io.IOException;

/**
 *Se encarga de iniciar los servicios de la aplicacion web
 *@author Jose Gutierrez
 */
public class App 
{
	/***
	 * Es la clase principal de la aplicacion la cual inicia todos los servicios de la aplicacion
	 * @param args
	 */
    public static void main( String[] args )
    {
    	 httpServer svr = new httpServer();
         sparkcito.put("/about", "En esta Pagina encontraras la informacion de como termino el mundial de Rusia 2018\r\n");
 
         try {
         	svr.start();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }
}

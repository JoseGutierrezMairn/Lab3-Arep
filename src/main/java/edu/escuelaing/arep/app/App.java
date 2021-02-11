package edu.escuelaing.arep.app;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	 httpServer svr = new httpServer();
         sparkcito.put("/about", "En esta Pagina encontraras la informacion de como terminó el mundial de Rusia 2018\r\n");
         		//+ "\r\n"
         		//+ "Composición del Bombo 2, segundo de grupo: Porto, Lazio, Barcelona, RB Leipzig, Sevilla, Atalanta, Borussia Monchengladbach, Atletico de Madrid. La normativa indica que no podran enfrentarse equipos del mismo país ni clubes que ya se hayan competido en la fase de grupos por lo que sera un sorteo condicionado.");
         try {
         	svr.start();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }
}

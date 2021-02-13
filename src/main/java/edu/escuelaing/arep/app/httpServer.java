package edu.escuelaing.arep.app;

import java.io.BufferedReader;
import java.awt.image.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import javax.imageio.ImageIO;

/***
 * Gestiona y recibe todas las peticiones de los usuarios y monta el servidor para tener el servicio listo para los usuarios
 * @author Jose Gutierrez
 *
 */
public class httpServer {
	private String route = "src/main/resources";
	private PrintWriter out;
	private DBConnection dbc;
	
	/***
	 * Constructor de la clase
	 */
	public httpServer() {
		super();
	}
	
	/***
	 * Metodo principal de la clase, incializa el servidor
	 * @param args
	 */
	public static void main(String[] args) {
		httpServer server = new httpServer();
		try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/***
	 * @throws IOException
	 */
	public void start() throws IOException {
		dbc = new DBConnection();
		int p = getPort();
		   
		   boolean working = true;
		   while(working) {
			   ServerSocket serverSocket = null;
			   Socket clientSocket = null;
			   try { 
				      serverSocket = new ServerSocket(p);
				   } catch (IOException e) {
				      System.err.println("Could not listen on port: 35000.");
				      System.exit(1);
				   }
			   try {
			       System.out.println("Listo para recibir ...");
			       clientSocket = serverSocket.accept();
			   } catch (IOException e) {
			       System.err.println("Accept failed.");
			       System.exit(1);
			   }
			    processRequest(clientSocket);
			    out.close(); 

			    clientSocket.close();
			    serverSocket.close(); 
		   }
		    
		    
		  
	}
	
	/***
	 * define el puerto en el que se va a correr el servicio
	 * @return devuelve un entero que indica el numero del puerto en el que se va a correr la aplicacion
	 */
	private int getPort() {
		if (System.getenv("PORT") != null) {
			return Integer.parseInt(System.getenv("PORT"));
		}
		return 35002; //returns default port if heroku-port isn't set(i.e. on localhost)
	}
	
	/***
	 * procesa una solicitud realizada por el usuario
	 * @param clientSocket El Socket del cliente
	 * @throws IOException
	 */
	private void processRequest(Socket clientSocket) throws IOException {
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader b = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String l, file = "";

		while((l = b.readLine()) != null) {
			//Procesar solicitud
			if(l.contains("GET")){
				file = l.split(" ")[1];
				if(file.startsWith("/Apps")) {
					
					String app = file.substring(5);
					//invoke(app);
					out.println(invoke(app));
				}else {
					if(file.equals("/")) {
						
						file = "/index.html";
					}
					getResourse(file, clientSocket);
				}
			}
			//Si no esta listo
			if(!b.ready()) {
				break;
			}
		}
		b.close();
	}
		
		/***
		 * Consigue el recurso solicitado
		 * @param file El path del recurso solicitado
		 * @param clientSocket Socket del cliente
		 * @throws IOException
		 */
		private void getResourse(String file, Socket clientSocket) throws IOException {
			
			String line;
			int tipo = getType(file);
			if(tipo == 0) {
				line = getFile(file, "html");
				out.println(line);
			}else if(tipo == 1) {
				line = getFile(file, "json");
				out.println(line);
			}else if(tipo == 2) {
				getImage(file, clientSocket.getOutputStream());
			}
		}
		/***
		 * Define el tipo de archivo 
		 * @param type El archivo del cual se quiere saber que tipo de archivo es
		 * @return answ Entero si es un 1 indica que es js, 0 para html
		 */
		private int getType(String type) {
			// TODO Auto-generated method stub
			int answ = 2;
			//Para js
			if(type.contains("js")) {
				answ = 1;
				//Por si es html
			}else if(type.contains("html")) {
				answ = 0;
			}
			return answ;
		}
		
		/***
		 * Encuentra la imagen solicitada
		 * @param type indica el tipo de imagen 
		 * @param outClienttype 
		 */
		private void getImage(String type, OutputStream outClient) {
			
			String path = route + type;
			File file = new File(path); 
			if(file.exists()) {
				try {
					BufferedImage image = ImageIO.read(file);
					ByteArrayOutputStream bit = new ByteArrayOutputStream();
					DataOutputStream data = new DataOutputStream(outClient);
					ImageIO.write(image, "PNG", bit);
					data.writeBytes("HTTP/1.1 200 OK \r\n" + "Content-Type: image/png \r\n" + "\r\n");
					data.write(bit.toByteArray());
				} catch (IOException e) {
	                e.printStackTrace();
	            }
			}else {
				out.println(errorResponse(file.getName()));
			}
			
		}
		
		/***
		 * Obtiene y devuelve el archivo que se busca abrir
		 * @param routee ruta del archivo
		 * @param tipo Inidica el tipo de archivo
		 * @return l string que indica el archivo que se esta buscando
		 */
		private String getFile(String routee, String tipo) {
			String l = getHeader(tipo);
			String path = this.route + routee;
			File file = new File(path);
			if(file.exists()) {
				String contenido;
				try {
					BufferedReader b = new BufferedReader(new FileReader(file));
					while((contenido = b.readLine()) != null) {
						l = l + contenido;
					}
				}catch (IOException e) {
	                e.printStackTrace();
	            }
			}else {
				l = errorResponse(file.getName());
			}
			
			return l;
		}
		
		/***
		 * Muestra error en la aplicacion web
		 * @param error error que se muestra
		 * @return line	El error que se va a mostrar
		 */
		private String errorResponse(String error) {
	        String line = "HTTP/1.1 404 Not Found \r\nContent-Type: text/html \r\n\r\n <!DOCTYPE html> <html>"
	                + "<head><title>404</title></head>" + "<body> <h1>404 Not Found " + error
	                + "</h1></body></html>";
	        return line;
		}
		
		/***
		 * Se encarga de mostrar el contenido que el usuario esta buscando
		 * @param path Direccion de lo que busca el usuario
		 * @return String String que describe el resutlado de la busqueda
		 */
		private String invoke(String path) {
			String line = getHeader("html");
			String file = sparkcito.getMap(path);
			file = "";
			//System.out.println(query.get(0));
			if(path.equals("/grupoA") || path.equals("/grupoA?")) {
				ArrayList<String[]> query = dbc.getGrupoA();
				for (String[] info : query) {
					
					file = file +"Equipo: "+info[0]+" Partidos Jugados: "+info[1]+" Partidos Ganados: "+info[2]+" Partidos Empatados: "+info[3]+" Puntos: "+info[4]+" \n";

				}
				return line + file;
				
			}else if(path.equals("/partidos") || path.equals("/partidos?")) {
			
				ArrayList<String[]> query = dbc.getPartidos();
				
				for (String[] info : query) {
					
					file = file +info[0]+" VS "+info[1]+"    Marcador Final: "+info[2]+"    Fecha: "+info[3]+"\r\n";

				}
				return line + file;
			}
			if(file != null) {
				return line + file;
			}
					
			return errorResponse(path);
		}
		
		private String getHeader(String type) {
			return "HTTP/1.1 200 OK\r\n" + "Content-Type: text/"+ type +"\r\n" + "\r\n";
		}
		
		
		
}

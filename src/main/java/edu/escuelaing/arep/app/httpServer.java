package edu.escuelaing.arep.app;

import java.io.BufferedReader;
import java.awt.image.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import javax.imageio.ImageIO;

public class httpServer {
	private String route = "src/main/resources";
	private PrintWriter out;
	private DBConnection dbc;
	
	public httpServer() {
		super();
	}
	
	public static void main(String[] args) {
		httpServer server = new httpServer();
		try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
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
			  // PrintWriter out = new PrintWriter(
                //       clientSocket.getOutputStream(), true)
			    //out.println(outputLine);
			    processRequest(clientSocket);
			    out.close(); 
			   // in.close(); 
			    clientSocket.close();
			    serverSocket.close(); 
		   }
		    
		    
		  
	}
	
	private int getPort() {
		if (System.getenv("PORT") != null) {
			return Integer.parseInt(System.getenv("PORT"));
		}
		return 35000; //returns default port if heroku-port isn't set(i.e. on localhost)
	}
	
	
	private void processRequest(Socket clientSocket) throws IOException {
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader b = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		String l, file = "";
		//System.out.println("Fileiflasdaksdkjsadksla");
		while((l = b.readLine()) != null) {
			//Procesar solicitud
			if(l.contains("GET")){
				file = l.split(" ")[1];
				if(file.startsWith("/Apps")) {
					String app = file.substring(5);
					System.out.println("NNuevo file"+file);
					out.println(invoke(app));
				}else {
					if(file.equals("/")) {
						
						file = "/index.html";
					}
					getResourse(file, clientSocket);
				}
			}
			//Si no está listo
			if(!b.ready()) {
				break;
			}
		}
		b.close();
	}
		
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
		
		private String errorResponse(String error) {
	        String line = "HTTP/1.1 404 Not Found \r\nContent-Type: text/html \r\n\r\n <!DOCTYPE html> <html>"
	                + "<head><title>404</title></head>" + "<body> <h1>404 Not Found " + error
	                + "</h1></body></html>";
	        return line;
		}

		private String invoke(String path) {
			String line = getHeader("html");
			String file = sparkcito.getMap(path);
			ArrayList<String[]> query = dbc.getGrupoA();
			//System.out.println(query.get(0));
			if(path.equals("/grupoA") || path.equals("/grupoA?")) {
				file = "";
				for (String[] info : query) {
					file = file +"Equipo: "+info[0]+" Partidos Jugados: "+info[1]+" Partidos Ganados: "+info[2]+" Partidos Empatados: "+info[4]+" Puntos: "+info[5]+" \n";
				}
				return line + file;
				
			}if(file != null) {
				return line + file;
			}
					
			return errorResponse(path);
		}
		
		private String getHeader(String type) {
			return "HTTP/1.1 200 OK\r\n" + "Content-Type: text/"+ type +"\r\n" + "\r\n";
		}
		
		
		
}

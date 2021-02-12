package edu.escuelaing.arep.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DBConnection {
	private static String uri = "jdbc:postgresql://ec2-3-211-245-154.compute-1.amazonaws.com:5432/d1s8lu3t47f6cm?ssl=true&sslmode=require";
	private static String user = "lyydybmfpdsoyf";
	private static String password = "17e675c3cf6ae9399f94b245f8f1b28d2b572e58e9a2f3a0a310722e86811cec";
	private static Connection c = null;
	
	/**
	 * Constructor de la Clase.
	 */
	public DBConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection(uri, user, password);
		}catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	/**
	 * Permite crear la tabla partidos.
	 */
	
	/**
	 * Permite obtener la informacion de la tabla.
	 * @return retorna un arreglo con los datos que contiene la base de datos.
	 */
	public ArrayList<String[]> getGrupoA(){
		
		ArrayList<String[]> answ = new ArrayList<String[]>();
		String query = "SELECT * FROM grupoa;";
		try {
			Statement  s = c.createStatement();
			ResultSet rs = s.executeQuery(query);
			while(rs.next()) {
				String equipo = rs.getString("EQUIPO");
				String pj = rs.getString("PG");
				String pg = rs.getString("PG");
				String pe = rs.getString("PE");
				String pts = rs.getString("PTS");
				String[] column = {equipo, pj, pg, pe, pts};
				answ.add(column);
			}
		} catch (SQLException e) {
            e.printStackTrace();
        }
		return answ;
	}
	
	
public ArrayList<String[]> getPartidos(){
		
		ArrayList<String[]> answ = new ArrayList<String[]>();
		String query = "SELECT * FROM partidosGrupoA;";
		try {
			Statement  s = c.createStatement();
			ResultSet rs = s.executeQuery(query);
			while(rs.next()) {
				String e1 = rs.getString("EQUIPO1");
				String e2 = rs.getString("EQUIPO2");
				String m = rs.getString("MARCADOR");
				String f = rs.getString("FECHA");
				String[] column = {e1, e2, m, f};
				answ.add(column);
			}
		} catch (SQLException e) {
            e.printStackTrace();
        }
		return answ;
	}
}

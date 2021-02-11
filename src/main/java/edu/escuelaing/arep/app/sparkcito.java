package edu.escuelaing.arep.app;



import java.util.*;


public class sparkcito {
	
	
	private static Map<String, String> mapp = new HashMap<String, String>();
	
	public static String getMap(String map) {
		return mapp.get(map);
	}
	
	
	public static void put(String key, String value) {
		mapp.put(key, value);
	}
	
	
}

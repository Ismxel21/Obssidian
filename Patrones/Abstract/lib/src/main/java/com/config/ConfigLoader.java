package com.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigLoader {

	private Properties properties;
	public ConfigLoader(String filename){
		
		this.properties = new Properties();
		try (InputStream input = getClass().getClassLoader().getResourceAsStream(filename)){
			if (input==null) {
				throw new RuntimeException("ArchivoNoEcontrado");
			}
			properties.load(input);
		}catch(IOException e) {
			
		}
	
	}
	public String get(String key) {
		return properties.getProperty(key);
	}
}

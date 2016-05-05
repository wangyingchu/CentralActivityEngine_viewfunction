package com.viewfunction.activityEngine.extension;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ExtensionRuntimeUtil {
	
	private static Properties _properties;
	
	public static String getExtensionGlobalPropertyValue(String propertyName){
		if(_properties==null){
			_properties=new Properties();
			try {			
				String configFileLocation=ExtensionRuntimeUtil.class.getResource("/").getPath().toString()+"ActivityEngineExtensionGlobalCfg.properties";				
				_properties.load(new FileInputStream(configFileLocation));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return _properties.getProperty(propertyName);
	}
}

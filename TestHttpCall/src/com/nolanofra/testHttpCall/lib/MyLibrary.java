package com.nolanofra.testHttpCall.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyLibrary {

	public static String convertStreamToString(InputStream is) {
	        /*
	         * To convert the InputStream to String we use the BufferedReader.readLine()
	         * method. We iterate until the BufferedReader return null which means
	         * there's no more data to read. Each line will appended to a StringBuilder
	         * and returned as String.
	         */
	        BufferedReader reader = null;		
	        InputStreamReader inputStreamReader = new InputStreamReader(is); 
			reader = new BufferedReader(inputStreamReader);
			
	        StringBuilder sb = new StringBuilder();
	 
	        String line;
	        try {
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	            	inputStreamReader.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }        
	        try {

	        	return sb.toString();
	        	
			} catch (Exception e) {

				e.printStackTrace();
			}
			return "";
	}
}

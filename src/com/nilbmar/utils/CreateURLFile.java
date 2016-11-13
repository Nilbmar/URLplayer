package com.nilbmar.utils;

import java.io.FileWriter;

public class CreateURLFile {

	private String I_SHORTCUT = "[InternetShortcut]";
	private String URL_EQUALS = "URL=";	
	
	//private String split = null;
	
	private String errorMsg = null;
	
	public CreateURLFile() {
	}
	
	public void create(String p, String t, String u) {
		String path = p + System.getProperty("file.separator");
		String title = t;
		String url = u;
		
		try {
			String lineBreak = "\r\n";
			
			String content = I_SHORTCUT + lineBreak +
					URL_EQUALS + url + lineBreak;
			
			FileWriter writer = new FileWriter(path + title + ".url");
			
			writer.write(content);
			writer.close();
			
			System.out.println("File writing success: " + content);
			System.out.println("--- TEST ---");
			System.out.println(path + title + ".url");
			
		} catch(Exception e) {
			errorMsg = e.getMessage();
			System.out.println(errorMsg);			
		}		
	}
	
	// Allow class trying to create a file to 
	//     check for and read an error message
	public String getError() {
		if (errorMsg == null || errorMsg.isEmpty()) {
			errorMsg = "empty";
		}
		return errorMsg;
	}
}

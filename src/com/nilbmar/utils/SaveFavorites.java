package com.nilbmar.utils;

import java.io.FileWriter;
import java.io.IOException;

public class SaveFavorites {
	
    private String file = null;
	
	public SaveFavorites() {
		// Maintain separate list depending on the operating system
		GetLocations getLoc = new GetLocations();
		file = getLoc.getFavoritesList();
	}
	
	public void create(String f) {
		String lineBreak = "\r\n"; // Always use Windows line break
		String content = f;
		System.out.println(file);
		
		try {
			
			// TODO: CHANGE FROM WRITING SINGLE FAVORITE AT A TIME
			//       TO REWRITING FILE ON EXIT
			//       ALSO REMOVE ANY DUPLICATES
			FileWriter writer = new FileWriter(file, true);
			
			writer.append(lineBreak + content);
			writer.close();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
	}

}

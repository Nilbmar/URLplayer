package com.nilbmar.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class SaveFavorites {
	
    private String file = null;
	
	public SaveFavorites() {
		// Maintain separate list depending on the operating system
		GetLocations getLoc = new GetLocations();
		file = getLoc.getFavoritesList();
	}
	
	public void create(String favorite) {
		String lineBreak = "\r\n"; // Always use Windows line break
		String content = favorite;
		System.out.println(file);
		
		try {
			FileWriter writer = new FileWriter(file, true);
			
			writer.append(lineBreak + content);
			writer.close();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	public void delete(String favorite) {
		String lineToRemove = favorite;
		String lineBreak = "\r\n";
		
		ReadFavorites read = new ReadFavorites();
		String[] arrFaves = read.getString();
		ArrayList<String> listOfFavorites = new ArrayList<String>();
		
		for (String fave : arrFaves) {
			if (fave != null && !fave.isEmpty()) {
				if (!fave.contains(lineToRemove)) {
					listOfFavorites.add(fave);
				}
			}
			
		}
		
		System.out.println(listOfFavorites);
		
		
		try  {
			// Rewrite file instead of appending to the end
			FileWriter writer = new FileWriter(file, false);
			String[] arrNewFaves = new String[listOfFavorites.size()];
			arrNewFaves = listOfFavorites.toArray(arrNewFaves);
			
			int lineIndex = 0;
			
			// Write each element of array to file
			for (String fave : arrNewFaves) {
				// The first line (element 0) shouldn't have
				// a line break added to it, everything else should
				if (lineIndex != 0) {
					fave = lineBreak + fave; 
				} else {
					lineIndex++;
				}
				writer.append(fave);
			}
			
			writer.close();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}

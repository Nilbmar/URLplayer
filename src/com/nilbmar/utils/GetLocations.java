package com.nilbmar.utils;

import java.net.URL;

public class GetLocations {
	
	String os = null;
	
	public GetLocations() {
		// Maintain separate list depending on the operating system
		os = System.getProperty("os.name").contains("Windows") ? "windows" : "linux";
	}
	
	/* Construct full path to list of favorites
	 * based on OS
	 */
	public String getFavoritesList() {
		String FILE_EXT = ".txt";
		
		
		
		String saveLoc = getListLoc();
		
		String path = saveLoc + System.getProperty("file.separator");
		String file = path + os + FILE_EXT;
		return file;
	}
	
	/* Gets the location of the runnable JAR
	 * Strips the filename from the location
	 * Adds \\favorites or /favorites to end of string
	 * based on OS 
	 */
	private String getListLoc() {
		int locOfFilenameInPath = 0;
		URL fullLocation = null;
		String currentDir = null;
		String location = null;
		
		// Last sub-folder before JAR file
		// Will be tested as lowercase
		String lastFolder = "urlplayer";
		
		fullLocation = GetLocations.class.getProtectionDomain().getCodeSource().getLocation();
		currentDir = fullLocation.getPath();
		
		if (os.contains("windows")) {
			// fullLocation has a forward slash "/" at the start
			// don't include in Windows
			currentDir = currentDir.substring(1, currentDir.length());
			
			// Switch forward slash to backslash in Windows
			currentDir = currentDir.replaceAll("/", "\\\\");
		}
		
		// Get the folder directly above the JAR
		// Stripping anything after (Otherwise testing in IDE would be wrong)
		locOfFilenameInPath = currentDir.toLowerCase().lastIndexOf(lastFolder);
		currentDir = currentDir.substring(0,  locOfFilenameInPath + lastFolder.length());

		// Add subfolder
		location = currentDir + System.getProperty("file.separator") + "favorites";
		
		return location;
	}
}



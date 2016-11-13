package com.nilbmar.utils;

import java.io.IOException;

public class TitleStripper {

	private String url = null;
	
	public TitleStripper(String u) {
		url = u;		
	}
	
	public String getTitle() {
		String newTitle = null;
		
		newTitle = stripGoogleSearch();
		newTitle = stripOthers(newTitle);
		newTitle = stripYoutube(newTitle);
		
		return newTitle;
	}
	
	
	// URLs from a Google Search throws errors in TitleExtractor
	// Weed these URLs out, and manually create title
	private String stripGoogleSearch() {
		String newTitle = null;
		
		// TODO: CHANGE THIS TO READ FROM A CONFIG FILE
		//       SINCE GOOGLE MIGHT CHANGE IT
		String googleSearch = "https://www.google.com/#q=";
		if (url.contains(googleSearch)) {
			String tmpTitle = url.substring(googleSearch.length());
			
			// May be deprecated
			if (url.contains("&rlz")) {
			// "&rlz" follows the search term in a google search url
				tmpTitle = url.substring(googleSearch.length(), url.indexOf("&rlz"));
			}
			
			tmpTitle = tmpTitle.replace("+", " ") + " - Google Search";
			newTitle = tmpTitle;
		} else {
			// SET THE TITLE
	        try {							
	        	newTitle = TitleExtractor.getPageTitle(url);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return newTitle;
	}
	
	private String stripOthers(String t) {
		String newTitle = t;
		//TODO: MOVE ALL OF THESE TO A CONFIG FILE
		newTitle = newTitle.replaceAll("&quot;", "-");	// removes all quotation marks
		newTitle = newTitle.replaceAll(":", "-");		// removes all colons ":"
		newTitle = newTitle.replaceAll("/",  "-");		// removes all forward slashes "/"
		newTitle = newTitle.replaceAll("\\|",  "-");	// removes all pipes "|"
		newTitle = newTitle.replaceAll("\\\\", "-");	// removes all back slashes "\"
		newTitle = newTitle.replaceAll("\\?", "-");		// removes all question marks "?"
		newTitle = newTitle.replaceAll("&#39;", "\'");
		
		return newTitle;
	}
	
	// Some youtube links include " - YouTube" at the end of the title
	// remove this for better uniformity
	// aka: Will overwrite a file, instead of creating a second
	private String stripYoutube(String t) {
		String newTitle = t;
		
		newTitle = newTitle.replaceAll(" - YouTube", "");
		
		return newTitle;
	}
}

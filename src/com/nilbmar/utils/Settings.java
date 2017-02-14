package com.nilbmar.utils;

public class Settings {
	private enum FileManager {
		CAJA, NAUTILUS, THUNAR, PCMANFM, DOLPHIN, NEMO
	}

	private String fileMan = null;
	
	public String getFileMan() {
		String fileMan = null;
		
		FileManager fm = FileManager.CAJA;
		
		switch (fm) {
		case CAJA:
			fileMan = fm.valueOf("blurp").toString();
			break;
		
		}
		
		return fileMan;
	}
}

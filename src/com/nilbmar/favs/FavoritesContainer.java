package com.nilbmar.favs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import com.nilbmar.utils.ReadFavorites;
import com.nilbmar.utils.SaveFavorites;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class FavoritesContainer extends HBox {
	
	//HBox this;
	int idInList = 0;
	Label lblPath;
	Scene scene = null;
	ArrayList<Favorite> listOfFavorites = new ArrayList<Favorite>();
	
	public FavoritesContainer(Label lblPath, Scene scene) {
		this.lblPath = lblPath;
		this.scene = scene;
	}

	public void autoLoadFavorites() {
		// Reads a list of Favorites from config file
		// to load them at startup
		ReadFavorites faves = new ReadFavorites();
		String[] arrFaves = faves.getString();
		for (String fave : arrFaves) {
			
			if (fave != null && !fave.isEmpty()) {
				File folder = new File(fave);
				if (folder.exists()) {
					// Add saved Favorites (folder icon, path, label)
					// to the scene
					Favorite newFavorite = new Favorite(idInList, fave, this, lblPath, scene);
					// TODO: CHANGE THIS AND ALL ADDS SO THEY GO LAST IN LIST
					// IF DON'T USE THE INT, WILL BE ADDED AFTER ADDBUTTON
					this.getChildren().add(idInList, newFavorite.get());
					listOfFavorites.add(newFavorite);
				}
				idInList++;
			}
		}	
	}
	
	/* TODO: DO I NEED THIS HERE OR JUST IN URLRUN
	 * Resize window to fit number of favorites
	 * Called at program start
	 * when adding new favorites
	 * when removing favorites
	 */
	public Double getNewSize(Double defaultWidth) {
		// Get count for how many faves
		int faveCount = this.getChildren().size();
		
		
		// Calculate new width
		Double newSize = (double) ((faveCount * 64)); // was going + 2, leaving it out for now
		if (newSize < defaultWidth) {
			newSize = defaultWidth;
		}
		//lblMidSpacer.setPrefWidth(newSize);
		return newSize;
	}
	
	
	/* Remove favorite from
	 * listOfFavorites, this, and text file
	 * 
	 * Correct the id number of all Favorites after
	 * the one deleted
	 */
	public void deleteFave(int id) {
		int idToRemove = id;
		int countOfFavorites = this.getChildren().size();
		
		// Correct id numbers in Favorites
		int countToUpdate = countOfFavorites - (idToRemove + 1);
		if (countToUpdate > 0) {
			for (int x = id + 1; x < countOfFavorites; x++) {
				listOfFavorites.get(x).setFaveId(x - 1);
			}			
		}

		// Removal from display, save file, and list
		this.getChildren().remove(idToRemove);
		SaveFavorites delFromFile = new SaveFavorites();
		delFromFile.delete(listOfFavorites.get(idToRemove).getPath());
		listOfFavorites.remove(idToRemove);
	}
	
	public ArrayList<Integer> getSwapList() {
		ArrayList<Integer> swapList = new ArrayList<Integer>();
		int listSize = listOfFavorites.size();
		for (int x = 0; x < listSize; x++) {
			if (listOfFavorites.get(x).getSelected()) {
				listOfFavorites.get(x).setSelected(false);
				swapList.add(x);
			}
		}
		
		return swapList;
	}
	
	/* Allow favorites to be rearranged
	 * moveUp, true will move up index
	 * false will move down index
	 */
	public void swapFaves(int idOne, int idTwo) {
		// Swap in display
		ObservableList<Node> newList = FXCollections.observableArrayList(this.getChildren());
		Collections.swap(newList, idOne, idTwo);
		this.getChildren().setAll(newList);
		
		// Swap in list
		Collections.swap(listOfFavorites, idOne, idTwo);
		System.out.println(listOfFavorites);
		
		/* Save full list of swapped favorites */
		// Get a list of locations stored in each Favorite 
		ArrayList<String> listFaveLocations = new ArrayList<String>();
		for (Favorite f : listOfFavorites) {
			listFaveLocations.add(f.getPath());
		}
		// Change the ArrayList to String[]
		String[] arrOfFavorites = new String[listFaveLocations.size()];
		arrOfFavorites = listFaveLocations.toArray(arrOfFavorites);
		// Do the actual saving
		SaveFavorites saveList = new SaveFavorites();
		saveList.save(arrOfFavorites);
	}
	
	public int getListSize() {
		return listOfFavorites.size();
		
	}
	
	public void addFaveToList(Favorite newFavorite) {
		listOfFavorites.add(newFavorite);
	}
	
	public ArrayList<Favorite> getListOfFavorites() {
		return listOfFavorites;
	}
	
	public int getLastIdInList() {
		return getListSize();
	}

}

package com.nilbmar.urlrun;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import com.nilbmar.utils.ReadFavorites;
import com.nilbmar.utils.SaveFavorites;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;


public class URLSave extends Application {
	
	Scene scene = null;
	HBox controlBox;
	HBox faveBox;
	HBox bottomBox;
	VBox outerBox;
	Button btnAddFolder;
	Button btnDelFolder;
	Label pathLabel;
	Label lblSaveConfirm;
	ArrayList<Favorite> listOfFavorites = new ArrayList<Favorite>();
	int idInList = 0;
	Double DEFAULT_WIDTH = 200.0;
	Double DEFAULT_HEIGHT = 200.0;
		
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			// Basic setup
			primaryStage.setTitle("Dropzone");
			primaryStage.setHeight(DEFAULT_HEIGHT);	// doubles
			primaryStage.setWidth(DEFAULT_WIDTH);

			// Create control buttons and label
						
			// Create button to add a favorite folder
			Image plusImg = new Image("/com/nilbmar/assets/Plus-icon.png");
			btnAddFolder = new Button();
			btnAddFolder.setGraphic(new ImageView(plusImg));
			btnAddFolder.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					
					// Allow user to pick a new folder location
					// That will be sent to the FolderBuilder
					// To create icon, label, delete button
					// for each new folder
					final DirectoryChooser directoryChooser = new DirectoryChooser();
					String dirPath  = directoryChooser.showDialog(primaryStage).getAbsolutePath();
					
					if (dirPath != null) {
						
						// Create a new Favorite Folder (folder icon, path, label)
						Favorite newFavorite = new Favorite(idInList, dirPath, faveBox, pathLabel, scene);
						
						// Save that location into OS appropriate config file
						SaveFavorites fav = new SaveFavorites();
						fav.create(dirPath);
						
						// Add Favoite to scene
						// TODO: DROP DOWN TO ANOTHER LEVEL
						faveBox.getChildren().add(idInList, newFavorite.get());
						listOfFavorites.add(newFavorite);
						idInList++;
						
						// Reset screen width
						primaryStage.setWidth(getNewSize());
					}
				}
			});
			
			// Create button to delete favorite folder
			Image delImg = new Image("/com/nilbmar/assets/Editing-Delete-icon.png");
			btnDelFolder = new Button();
			btnDelFolder.setGraphic(new ImageView(delImg));
			btnDelFolder.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {

					ArrayList<Integer> itemsToDel = new ArrayList<Integer>();
					
					for (int x = 0; x < listOfFavorites.size(); x++) {
						if (listOfFavorites.get(x).getSelected()) {
							itemsToDel.add(listOfFavorites.get(x).getFaveId());
						}
					}
					
					// Reverse order of itemsToDel so when deleted
					// id numbers won't go wrong
					Collections.reverse(itemsToDel);
					int[] deleteMe = new int[itemsToDel.size()];
					for (int x = 0; x < itemsToDel.size(); x++) {
						deleteMe[x] = (int) itemsToDel.get(x);
					}
					
					
					for (int id : deleteMe) {
						deleteFave(id);
						
						// Reset IDs so subsequent deletes
						// don't go out of bounds
						resetFaveIDs();
						
						// Reset screen width
						primaryStage.setWidth(getNewSize());
					}
					
				}
			});
			
			//TODO: FIGURE OUT HOW TO MAKE THIS DISAPPEAR AFTER A TIME
			//		OR CHANGE IT TO A ICON THAT SELF-DESTRUCTS
			lblSaveConfirm = new Label();
			lblSaveConfirm.setText(" ");
			lblSaveConfirm.setStyle("-fx-padding: 5;");
			
			// Shows the full path of a favorite when hovered over
			pathLabel = new Label();
			pathLabel.setText(" ");
			pathLabel.setStyle("-fx-padding: 5;");
			
			// Prepare scene areas
			outerBox = new VBox();
			controlBox = new HBox();
			faveBox = new HBox();
			bottomBox = new HBox();
			
			controlBox.getChildren().add(btnAddFolder);
			controlBox.getChildren().add(btnDelFolder);
			controlBox.getChildren().add(lblSaveConfirm);
			
			bottomBox.getChildren().add(pathLabel);
			
			outerBox.getChildren().add(controlBox);
			outerBox.getChildren().add(faveBox);
			outerBox.getChildren().add(bottomBox);
			
			
			
			scene = new Scene(outerBox);
			
			primaryStage.setScene(scene);
			primaryStage.show();
			
			autoLoadFavorites();
			
			// Reset screen width
			primaryStage.setWidth(getNewSize());
			

			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void autoLoadFavorites() {
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
					Favorite newFavorite = new Favorite(idInList, fave, faveBox, pathLabel, scene);
					// TODO: CHANGE THIS AND ALL ADDS SO THEY GO LAST IN LIST
					// IF DON'T USE THE INT, WILL BE ADDED AFTER ADDBUTTON
					faveBox.getChildren().add(idInList, newFavorite.get());
					listOfFavorites.add(newFavorite);
				}
				idInList++;
			}
		}	
	}
	
	/* Resize window to fit number of favorites
	 * Called at program start
	 * when adding new favorites
	 * when removing favorites
	 */
	private Double getNewSize() {
		// May need to change this back to ReadFavorites
		//ReadFavorites faves = new ReadFavorites();
		//String[] arrFaves = faves.getString();
		//int faveCount = arrFaves.length; // 2 folders showing, int will be 2
		
		// Get count for how many faves
		int faveCount = faveBox.getChildren().size();
		
		// Calculate new width
		Double newSize = (double) ((faveCount * 64)); // was going + 2, leaving it out for now
		if (newSize < DEFAULT_WIDTH) {
			newSize = DEFAULT_WIDTH;
		}
		
		return newSize;
	}
	
	private void resetFaveIDs() {
		// Reset IDs so subsequent deletes
		// don't go out of bounds
		// TODO: SETUP RESETING FAVORITE IDs
	}
	
	private void deleteFave(int i) {
		int idToRemove = i;
		int countOfFavorites = faveBox.getChildren().size() - 1;
		
		
		faveBox.getChildren().remove(idToRemove);
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}

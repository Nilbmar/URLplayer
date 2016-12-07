package com.nilbmar.urlrun;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import com.nilbmar.utils.ReadFavorites;
import com.nilbmar.utils.SaveFavorites;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
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
	
	Button btnSwap;
	Button btnMoveUp;
	Button btnMoveDown;
	Label lblPath;
	Label lblSaveConfirm;
	ArrayList<Favorite> listOfFavorites = new ArrayList<Favorite>();
	int idInList = 0;
	Double DEFAULT_WIDTH = 200.0;
	Double DEFAULT_HEIGHT = 200.0;

	Label lblMidSpacer;
	Double stageWidth = 0.0;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			// Basic setup
			primaryStage.setTitle("Dropzone");
			primaryStage.setHeight(DEFAULT_HEIGHT);	// doubles
			primaryStage.setWidth(DEFAULT_WIDTH);

			// Create control buttons and label
						
			// Create button to add a favorite folder
			Image plusImg = new Image("/com/nilbmar/assets/plus_16.png");
			btnAddFolder = new Button();
			//btnAddFolder.setText("New");
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
						Favorite newFavorite = new Favorite(idInList, dirPath, faveBox, lblPath, scene);
						
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
			Image delImg = new Image("/com/nilbmar/assets/close_16.png");
			btnDelFolder = new Button();
			//btnDelFolder.setText("Delete");
			btnDelFolder.setGraphic(new ImageView(delImg));
			btnDelFolder.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {

					ArrayList<Integer> itemsToDel = new ArrayList<Integer>();
					
					for (int x = 0; x < listOfFavorites.size(); x++) {
						if (listOfFavorites.get(x).getSelected()) {
							listOfFavorites.get(x).setSelected(false);
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
						
						// Reset screen width
						primaryStage.setWidth(getNewSize());
					}
					
				}
			});
			
			Image downImg = new Image("/com/nilbmar/assets/left_16.png");
			btnMoveDown = new Button();
			btnMoveDown.setGraphic(new ImageView(downImg));
			btnMoveDown.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					ArrayList<Integer> swapList = new ArrayList<Integer>();
					swapList = getSwapList();
					int swapNum = swapList.get(0);
					
					if (swapList.size() == 1 && swapNum != 0) {
						swapFaves(swapNum, swapNum - 1);
					} else {
						System.out.println("Please select one Favorite to move left");
					}
					
				}
				
			});
			
			Image upImg = new Image("/com/nilbmar/assets/right_16.png");
			btnMoveUp = new Button();
			//btnMoveUp.setText("Move Right");
			btnMoveUp.setGraphic(new ImageView(upImg));
			btnMoveUp.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					ArrayList<Integer> swapList = new ArrayList<Integer>();
					swapList = getSwapList();
					int swapNum = swapList.get(0);
					int lastSwapAvailable = listOfFavorites.size() - 1;
					
					if (swapList.size() == 1 && swapNum < lastSwapAvailable) {
						swapFaves(swapNum, swapNum + 1);
					} else {
						System.out.println("Please select one Favorite to move right");
					}
					
				}
				
			});
			
			/* Button to swap positions of favorites
			 * TEMP: HARD CODING NUMBERS FOR TESTING
			 * TODO: SWAP OUT FOR REAL LOGIC
			 */
			Image swapImg = new Image("/com/nilbmar/assets/swap_16.png");
			btnSwap = new Button();
			btnSwap.setGraphic(new ImageView(swapImg));
			btnSwap.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// TODO Auto-generated method stub
					ArrayList<Integer> swapList = new ArrayList<Integer>();
					
					swapList = getSwapList();
					
					if (swapList.size() == 2) {
						// Complete swap
						swapFaves(swapList.get(0), swapList.get(1));
					} else {
						System.out.println("Select two favorites to swap by clicking on their labels");
					}
				}
				
			});

			// Create space between New/Delete buttons and Move buttons
			lblMidSpacer = new Label();
			
			//TODO: FIGURE OUT HOW TO MAKE THIS DISAPPEAR AFTER A TIME
			//		OR CHANGE IT TO A ICON THAT SELF-DESTRUCTS
			lblSaveConfirm = new Label();
			lblSaveConfirm.setText(" ");
			lblSaveConfirm.setStyle("-fx-padding: 5;");
			
			// Shows the full path of a favorite when hovered over
			lblPath = new Label();
			lblPath.setText(" ");
			lblPath.setStyle("-fx-padding: 5;");
			
			// Prepare scene areas
			outerBox = new VBox();
			controlBox = new HBox();
			faveBox = new HBox();
			bottomBox = new HBox();
			
			controlBox.getChildren().add(btnAddFolder);
			controlBox.getChildren().add(btnDelFolder);
			controlBox.getChildren().add(lblMidSpacer);
			controlBox.getChildren().add(btnMoveDown);
			controlBox.getChildren().add(btnSwap);
			controlBox.getChildren().add(btnMoveUp);
			controlBox.getChildren().add(lblSaveConfirm);
			
			bottomBox.getChildren().add(lblPath);
			
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
					Favorite newFavorite = new Favorite(idInList, fave, faveBox, lblPath, scene);
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
		lblMidSpacer.setPrefWidth(newSize);
		return newSize;
	}
	
	/* Remove favorite from
	 * listOfFavorites, faveBox, and text file
	 * 
	 * Correct the id number of all Favorites after
	 * the one deleted
	 */
	private void deleteFave(int id) {
		int idToRemove = id;
		int countOfFavorites = faveBox.getChildren().size();
		
		// Correct id numbers in Favorites
		int countToUpdate = countOfFavorites - (idToRemove + 1);
		if (countToUpdate > 0) {
			for (int x = id + 1; x < countOfFavorites; x++) {
				listOfFavorites.get(x).setFaveId(x - 1);
			}			
		}

		// Removal from display, save file, and list
		faveBox.getChildren().remove(idToRemove);
		SaveFavorites delFromFile = new SaveFavorites();
		delFromFile.delete(listOfFavorites.get(idToRemove).getPath());
		listOfFavorites.remove(idToRemove);
	}
	
	private ArrayList<Integer> getSwapList() {
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
	private void swapFaves(int idOne, int idTwo) {
		// Swap in display
		ObservableList<Node> newList = FXCollections.observableArrayList(faveBox.getChildren());
		Collections.swap(newList, idOne, idTwo);
		faveBox.getChildren().setAll(newList);
		
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
	
	public static void main(String[] args) {
		launch(args);
	}
}

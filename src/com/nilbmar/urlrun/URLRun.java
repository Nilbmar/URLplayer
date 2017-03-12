package com.nilbmar.urlrun;

import java.util.ArrayList;
import java.util.Collections;

import com.nilbmar.favs.Favorite;
import com.nilbmar.favs.FavoritesContainer;
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


public class URLRun extends Application {
	
	Scene scene = null;
	HBox controlBox;
	FavoritesContainer faveBox;
	HBox bottomBox;
	VBox outerBox;
	Button btnAddFolder;
	Button btnDelFolder;
	
	Button btnSwap;
	Button btnMoveUp;
	Button btnMoveDown;
	Label lblPath;
	Label lblSaveConfirm;
	
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
						int idInList = faveBox.getLastIdInList();
						// Create a new Favorite Folder (folder icon, path, label)
						Favorite newFavorite = new Favorite(idInList, dirPath, faveBox, lblPath, scene);
						
						// Save that location into OS appropriate config file
						SaveFavorites fav = new SaveFavorites();
						fav.create(dirPath);
						
						// Add Favoite to scene
						// TODO: DROP DOWN TO ANOTHER LEVEL
						faveBox.getChildren().add(idInList, newFavorite.get());
						faveBox.addFaveToList(newFavorite);
						
						// TODO: Reset screen width
						primaryStage.setWidth(faveBox.getNewSize(DEFAULT_WIDTH));
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
					ArrayList<Favorite> listOfFavorites = faveBox.getListOfFavorites();
					
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
						faveBox.deleteFave(id);
						
						// TODO: Reset screen width
						primaryStage.setWidth(faveBox.getNewSize(DEFAULT_WIDTH));
					}
					
				}
			});
			
			// Create button to move favorite folder (to the left)
			Image downImg = new Image("/com/nilbmar/assets/left_16.png");
			btnMoveDown = new Button();
			btnMoveDown.setGraphic(new ImageView(downImg));
			btnMoveDown.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					ArrayList<Integer> swapList = new ArrayList<Integer>();
					swapList = faveBox.getSwapList();
					int swapNum = swapList.get(0);
					
					if (swapList.size() == 1 && swapNum != 0) {
						faveBox.swapFaves(swapNum, swapNum - 1);
					} else {
						System.out.println("Please select one Favorite to move left");
					}
					
				}
				
			});
			
			// Create button to move favorite folder (to the right)
			Image upImg = new Image("/com/nilbmar/assets/right_16.png");
			btnMoveUp = new Button();
			//btnMoveUp.setText("Move Right");
			btnMoveUp.setGraphic(new ImageView(upImg));
			btnMoveUp.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					ArrayList<Integer> swapList = new ArrayList<Integer>();
					swapList = faveBox.getSwapList();
					int swapNum = swapList.get(0);
					int lastSwapAvailable = faveBox.getListSize() - 1;
					
					if (swapList.size() == 1 && swapNum < lastSwapAvailable) {
						faveBox.swapFaves(swapNum, swapNum + 1);
					} else {
						System.out.println("Please select one Favorite to move right");
					}
					
				}
				
			});
			
			// Creates a button to swap positions of favorites
			Image swapImg = new Image("/com/nilbmar/assets/swap_16.png");
			btnSwap = new Button();
			btnSwap.setGraphic(new ImageView(swapImg));
			btnSwap.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// TODO Auto-generated method stub
					ArrayList<Integer> swapList = new ArrayList<Integer>();
					
					swapList = faveBox.getSwapList();
					
					if (swapList.size() == 2) {
						// Complete swap
						faveBox.swapFaves(swapList.get(0), swapList.get(1));
					} else {
						System.out.println("Select two favorites to swap by clicking on their labels");
					}
				}
				
			});

			// Create space between New/Delete buttons and Move buttons
			lblMidSpacer = new Label();
			
			// TODO: FIGURE OUT HOW TO MAKE THIS DISAPPEAR AFTER A TIME
			//	     OR CHANGE IT TO A ICON THAT SELF-DESTRUCTS
			// TODO: THIS MAY BE UNUSED, CAN I DELETE IT?
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
			faveBox = new FavoritesContainer(lblPath, scene);//new HBox(); // TODO: SET THIS TO FavoritesContainer
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
			
			faveBox.autoLoadFavorites();
			
			// TODO: Reset screen width
			primaryStage.setWidth(faveBox.getNewSize(DEFAULT_WIDTH));
			

			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		launch(args);
	}
}

package com.nilbmar.urlrun;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import com.nilbmar.utils.CreateURLFile;
import com.nilbmar.utils.TitleStripper;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class Favorite extends VBox{

	Parent parent;
	private VBox vBox;
	
	private int id;
	private boolean saveConfirmed = false;
	private boolean selected = false;
	private Label lblFolderName;
	private ImageView img;
	private ImageView checkmark = new ImageView("/com/nilbmar/assets/accept-icon.png");
	private String pathFull;
	
	private String url;
	private String title;
	
	public Favorite(int favId, String path, HBox p, Label pathLabel, Scene scene) {
		this.id = favId;
		this.pathFull = path;
		this.parent = p;
		
		vBox = new VBox();
		setCSS(selected);
		
		vBox.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				if (event.getButton() != null && event.getButton() == MouseButton.PRIMARY) {
					// (Remove) Hightlight favorite when label clicked
					if (selected == false) {
						selected = true;
						setCSS(selected);
					} else {
						selected = false;
						setCSS(selected);
					}
				}
			}
			
		});
		
		
		lblFolderName = new Label();
		// TODO: TEXT ALIGNMENT ISN'T WORKING
		lblFolderName.setTextAlignment(TextAlignment.LEFT);
		lblFolderName.setWrapText(true);
		lblFolderName.setMinSize(48, 48);
		lblFolderName.setMaxSize(48, 48);
		setLabel();
		
		
		img = new ImageView();
		img.setImage(new Image("/com/nilbmar/assets/Close-Folder-icon48x48.png"));
		img.setFitHeight(48);
		img.setFitWidth(48);	
		
		// When folder icon is clicked		
		img.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
				// If left mouse button is clicked
				// If the clipboard contains a URL
				// Create a .URL and save it to that folder
				if (event.getButton() != null && event.getButton() == MouseButton.PRIMARY) {

					// Grab the clipboard and look for a URL
					Clipboard clip = Clipboard.getSystemClipboard();
					
					// Not using Clipboard.hasUrl() because it doesn't
					// pick up all URLs
					String clipped = clip.getString();
					
					// Make sure clipped is a URL
					getClipUrl(clipped);
					
					event.consume();
					
					// Visual (checkmark) confirmation
					// of the link being saved
					// uses Timeline to remove
					if (saveConfirmed) {
			    		vBox.getChildren().add(checkmark);
			    		clearSaveConfirmation();
			    	}
				}
				
				// If right mouse button is clicked
				// Create a context menu with option to Delete favorite or Cancel
				if (event.getButton() != null && event.getButton() == MouseButton.SECONDARY) {
					System.out.println("Secondary button pressed.");
					
					ContextMenu cm = new ContextMenu();
					//MenuItem delItem = new MenuItem("Delete");
					MenuItem openItem = new MenuItem("Open Folder");
					MenuItem cancelItem = new MenuItem("Cancel");
					
					// Hides context menu then deletes favorite
					/*delItem.setOnAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent arg0) {
							// TODO: DEPRECATED IDEA
							// DELETE ITEM - MOVE MOUSE CLICKS OUT TO URLSAVE.JAVA
							// PUT FUNCTIONS INTO FAVORITE.JAVA FOR MOUSE CLICKS TO CALL
							delete = true;
							System.out.println("Delete item.");
							cm.hide();
							
						} 
					});
					*/
					
					// Opens folder in default file manager, then closes context menu
					openItem.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							String os = System.getProperty("os.name").contains("Windows") ? "windows" : "linux";
							if (os.contains("windows")) {
								
								// Open folder in default file manager
								File folder = new File(pathFull);
								try {
									Desktop.getDesktop().open(folder);
								} catch (IOException e) {
									// TODO ADD WARNING ABOUT NO LOCATION
									e.printStackTrace();
								}
							} else {
								// TODO: FIGURE OUT HOW TO OPEN DEFAULT FILE MANAGER
								//       IN LINUX, POSSIBLY FOR EACH KNOWN FM
								
								Alert alert = new Alert(AlertType.INFORMATION);
								alert.setTitle("Information: Feature Unavailable");
								alert.setHeaderText("This feature is currently unavailable in Linux");
								alert.setContentText("It will arrive, eventually."
										+ "\nCurrent Date: Nov. 6, 2016"); // Curious how long it takes to implement
								alert.showAndWait();
							}
							cm.hide();
						}
						
					});
					
					// Simply hides the context menu
					cancelItem.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent arg0) {
							cm.hide();
						}
						
					});
					
					//cm.getItems().add(delItem);
					cm.getItems().add(openItem);
					cm.getItems().add(cancelItem);
					cm.show(img, Side.RIGHT, 6.0, 0.0);

					event.consume();
				}
				
			}			
		});
		
		// Setup for dragging a link onto folder icon
		img.setOnDragOver(new EventHandler<DragEvent>() {
		    public void handle(DragEvent event) {
		        event.acceptTransferModes(TransferMode.ANY);
		        event.consume();
		    }
		});
		
		// Actually dropping link on folder icon
		img.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
			    // data is dropped on pane from other source
			    if (event.getGestureSource() != img &&
			            event.getDragboard().hasString()) {
			        
			    	// allow for copying
			        event.acceptTransferModes(TransferMode.COPY);
			        String clipped = event.getDragboard().getString();
					
			     // Make sure clipped is a URL
			        getClipUrl(clipped);
			    }
		    	event.consume();

		    	// Visual (checkmark) confirmation
				// of the link being saved
				// uses Timeline to remove
		    	if (saveConfirmed) {
		    		vBox.getChildren().add(checkmark);
		    		clearSaveConfirmation();
		    	}
			}
		});
		
		// When hovering over Favorite
		img.setOnMouseEntered(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				// TODO Change to ToolTip
				pathLabel.setText(pathFull);
				scene.setCursor(Cursor.HAND);
			}
			
		});
		
		// When not hovering over Favorite
		img.setOnMouseExited(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				// TODO Change to ToolTip
				pathLabel.setText(" ");
				scene.setCursor(Cursor.DEFAULT);
				
			}
			
		});
		
		
		// TODO: ADJUST SIZES AND PLACING OF NODES
		//vBox.getChildren().add(btnDelFavorite);
		vBox.getChildren().add(img);
		vBox.getChildren().add(lblFolderName);
		
	}
	
	private void setLabel() {
		String split = System.getProperty("file.separator");
		int locLastSplit = pathFull.lastIndexOf(split) + 1;
		
		// Set label to last folder name in path
		if (locLastSplit > 0) {
			lblFolderName.setText(pathFull.substring(locLastSplit));
			
			// If path is a drive (ie. 'C:\') set it to the full path
			if (pathFull.indexOf(split) == pathFull.length() - 1) {
				lblFolderName.setText(pathFull);		
			}
			
			// TODO: TRY TO MAKE THIS TOOLTIP SHOW UP WHEN HOVERING ABOVE THE WHOLE FAVORITE
			Tooltip tip = new Tooltip(pathFull);
			lblFolderName.setTooltip(tip);
		}
	}
	
	public String getPath() {
		return pathFull;
	}
	
	/* used to update when another favorite is removed */
	public int getFaveId() {
		return id;
	}
	
	/* used to update when another favorite is removed */
	public void setFaveId(int i) {
		id = i;
	}
	
	public boolean getSelected() {
		return selected;
	}
	
	public void setSelected(boolean s) {
		selected = s;
		setCSS(selected);
	}
	
	public Node get() {		
		return vBox;
	}
	
	/* Gets the clipboard contents as a string
	 * Checks if they contain the "http:" or "https:" at the beginning
	 * Getting contents as a URL instead of as a String sometimes fails
	 * Even it is a valid URL
	 * Most likely when it doesn't contain a recognized
	 * Top-Level-Domain like .com or .org
	 */
	public void getClipUrl(String clipped) {		
		if (clipped.contains("http:") || clipped.contains("https:")) {
			url = clipped;
			
			// TitleExtractor will throw a NullPointerException
			// if it isn't a real URL, but does start with "http:" or "https:"
			getUrlTitle();
	        output();				        
		} else {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning: Empty Clipboard");
			alert.setHeaderText("Clipboard does not contain a valid URL");
			alert.setContentText("Copy a URL from your browser and try again.");
			alert.showAndWait();
		}
	}

	/* Used TitleStripper util to cut out characters
	 * that can't be used as a filename
	 * 
	 * - TitleStripper manually sets title for google searches
	 *       because TitleExtractor can't handle them
	 * - TitleStripper then makes the call to TitleExtractor
	 *       and strips out characters not usable for filenames 
	 */
	private void getUrlTitle() {
		// TODO: PROBABLY NEED TO MAKE THIS RETURN STRING
		//		INSTEAD OF SETTING VARIABLE DIRECTLY
		try {
			TitleStripper stripper = new TitleStripper(url);
			title = stripper.getTitle();
		} catch (NullPointerException nullEx) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning: Address Invalid");
			alert.setHeaderText("Clipboard does not contain a valid URL");
			alert.setContentText("Make sure you have a full web address copied.");
			alert.showAndWait();
			
			// Only allow file creation if the try was successful
			// otherwise empty out the title
			title = "";
		} 
	}
	
	/* Creates the actual .URL file */
	private void output() {
		// Only allow file creation if title was successfully found
		if (title != null && !title.isEmpty()) {
			// Create a .url file on the filesystem
	        CreateURLFile file = new CreateURLFile();
        	file.create(pathFull, title, url);
        
	        // If an invalid filename was composed, show an error dialog box
	        // The filesystem won't allow it to be created
	        String error = file.getError();
	        if (!error.matches("empty")) {
	        	Alert alert = new Alert(AlertType.ERROR);
	        	alert.setTitle("Error: File Creation");
	        	alert.setHeaderText("Cannot create URL file:");
	        	alert.setContentText(error.substring(0, error.lastIndexOf("("))
	        			+ "\n\n" + error.substring(error.lastIndexOf("(")));
	        	
	        	alert.showAndWait();
	        } else {
	        	// Allow label to be set for visual confirmation
	        	// that the file was created
	        	saveConfirmed = true;
	        }
		} 
	}
	
	
	/* Highlight Folder and Label when label is clicked on
	 * Or set it back to default style when unselected 
	 */
	private void setCSS(Boolean highlight) {
		if (highlight) {
			vBox.setStyle("-fx-effect: innershadow(gaussian, #039ed3, 10, 1.0, 0, 0);" +
					"-fx-padding: 2, 0, 2, 0;" + 
	                "-fx-border-style: dotted inside;" + 
	                "-fx-border-width: 0;" +
	                "-fx-border-insets: 5;" + 
	                "-fx-border-radius: 5;"
					);
		} else {
			vBox.setStyle("-fx-effect: none;" +
					"-fx-padding: 2, 0, 2, 0;" + 
	                "-fx-border-style: dotted inside;" + 
	                "-fx-border-width: 0;" +
	                "-fx-border-insets: 5;" + 
	                "-fx-border-radius: 5;"
	                );
		}
	}
	
	/* Creates a checkmark image
	 * for visual confirmation of saving a link
	 * Then removes it after a certain time
	 */
	private void clearSaveConfirmation() {
		Timeline timeline = new Timeline(new KeyFrame(
				Duration.millis(2500),
				ae -> vBox.getChildren().remove(checkmark)));
		timeline.play();
	}
}

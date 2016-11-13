package com.nilbmar.utils;

import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class ReadFavorites {
	
    private String file = null;
    private List<Object> listOfFavorites = new ArrayList<Object>();
    private File input = null;
    private Scanner scan = null;
    
    public ReadFavorites() {
    	GetLocations getLoc = new GetLocations();
		file = getLoc.getFavoritesList();
    }
    
    
    // Used in read(), makes sure theres something to add()
    private Boolean hasNext() {
        boolean t = false;
        
        t = scan.hasNextLine();
        
        return t;
    }
    
    // Used in read(), adds content from file
    private void addNext() {

    	listOfFavorites.add(scan.nextLine());
    }
    
    // Sets a file, checks that it exists
    // Checks that it has content to add
    // Adds content to list to be converted to array
    private void read() {
    	
        try {
            // Set up and check for file existence
            input = new File(file);
            if (input.exists()) {
                scan = new Scanner(input);
               
               // Uses helper functions above for Factory Method
                while(hasNext()) {
                    addNext();
                }   
            }
        } catch(Exception e) {
            System.out.println("Exception Handled: " + e);    
        } finally {
           
        }
    }
    
    public String[] getString() {
        System.out.println("Reading file: " + file);
        
        read();
        
        int arrSize = listOfFavorites.size();
        String[] array = new String[arrSize];
        for (int x = 0; x < array.length; x++) {
            array[x] = listOfFavorites.get(x).toString();
        }
        
        return array;
    }
    
    

}
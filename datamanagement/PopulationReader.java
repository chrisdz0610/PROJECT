package edu.upenn.cit594.datamanagement;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class PopulationReader {
	
	protected String filename;
	
	public PopulationReader (String filename) {
		this.filename = filename;
	}
	
	public Map<Integer, Integer> getPopulationRows () throws IOException, CSVFormatException {
		
		// Zip code is not EXACTLY 5 digits OR population is not an integer.
		// 
		
		Map<Integer, Integer> popMap = new TreeMap<Integer, Integer>();		
		CharacterReader charReader = new CharacterReader(filename);		
		CSVReader csvReader = new CSVReader (charReader);
		
		int zipIndex = -1, popIndex = -1;
		String zipCode;
		int pop, zipCodeInt;
		String[] row;
		
		// read header (1st) row to get population and zip index 
		row = csvReader.readRow();
		
		// no need to handle when keywords missing from header? 
		for (int i = 0; i < row.length; i++) {
			if (row[i].equals("zip_code")) {
				zipIndex = i;
			}
			if (row[i].equals("population")) {
				popIndex = i;
			}
		}
		
		// start to read non-header row 
        while ((row = csvReader.readRow()) != null) {
        	
        	// check if zipcode length = 5
        	zipCode = row[zipIndex];
        	if (zipCode == null || zipCode.length() != 5) {
        		continue;
        	}
        	// check if zipcode is all digits, if not continue
        	try {
        		
        		zipCodeInt = Integer.parseInt(zipCode);
        		
        	} catch (NumberFormatException e) {
        		
        		continue;
        	}
        	
        	// check if the population number is integer, continue looping if not
        	if (row[popIndex] == null) {
        		continue;
        	}
        	try {
        		
                pop = Integer.parseInt(row[popIndex]);

                } catch (NumberFormatException e) {
                	
        		continue;
        	}
        	
        	// check if population map already have the zipCode as key, if yes, add the value--- could be deleted, 
        	// Ed post say each zipcode is unique
            if (popMap.containsKey(zipCodeInt)) {
            	
            	popMap.put(zipCodeInt, popMap.get(zipCodeInt) + pop);
            	
            }
            // if not, put key value in the map
            else {
            	popMap.put(zipCodeInt, pop);
            }
        }
		return popMap;
		
	}
	
//	public static void main(String arg[]) {
//		
//		PopulationReader popReader =  new PopulationReader("population11.csv");
//		int totalPop = 0;
//		try {
//			TreeMap<Integer, Integer> popRows = popReader.getPopulationRows();
//			for (Integer key: popRows.keySet()) {
//				totalPop += popRows.get(key);
//				System.out.println("zipCode: " + key + "  Population: " + popRows.get(key));
//				
//			}
//			System.out.println("Total Population: " + totalPop);
//		} catch (IOException | CSVFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
}

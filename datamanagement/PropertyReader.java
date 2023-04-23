package edu.upenn.cit594.datamanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import edu.upenn.cit594.util.PropertyData;

public class PropertyReader {
	
	protected String filename;
	
	public PropertyReader (String filename) {
		this.filename = filename;
	}
	
	
	public List<PropertyData> getAllRows() throws IOException, CSVFormatException {
		
		CharacterReader charReader = new CharacterReader(filename);		
		CSVReader csvReader = new CSVReader (charReader);
		List<PropertyData> propList = new ArrayList<PropertyData>();
		
		String[] row;
		int zipIndex = -1 ,marketIndex = -1, livableIndex = -1;
		
		// read header(1st) row to get pop and zip index 
		row = csvReader.readRow();
		
		String zipCode;
		Integer zipCodeInt;
		Double livableArea, marketVale ;
		
		// need to handle when keywords missing from header? NO

		for (int i = 0; i < row.length; i++) {
			
			if (row[i].equals("zip_code")) {
				zipIndex = i;
			}
			else if (row[i].equals("total_livable_area")) {
				livableIndex = i;
			}
			else if (row[i].equals("market_value")) {
				marketIndex = i;
			}
		}
		
		// start to read non-header row
		while ((row = csvReader.readRow()) != null) {
			
			zipCode = row[zipIndex];
			// zipCode length < 5 or first 5 character's lengths < 5 
			if (zipCode == null) {
				continue;
			}
			else if (zipCode.length() < 5 ||  zipCode.substring(0,5).length() < 5) {
				continue;
			}
			// check if first 5 chars of zipCode, market value and livable area numeric
			try {				
				zipCodeInt = Integer.parseInt(zipCode.substring(0, 5));
			// will catch "null" "empty" and non-numeric string
			} catch(NumberFormatException e) {			
				continue;			
			}			
			
			try {
				
				marketVale = Double.parseDouble(row[marketIndex]);
				
			} catch(NumberFormatException e) {				
				marketVale = null;				
			}
			try {
				
				livableArea = Double.parseDouble(row[livableIndex]);
				
			} catch(NumberFormatException e) {
				
				livableArea = null;
				
			}
			
			PropertyData property = new PropertyData(zipCodeInt, livableArea, marketVale);
			propList.add(property);
			
			}
		
		return propList;
	}

	public static void main(String[] args) {

		PropertyReader popReader =  new PropertyReader("properties.csv");
	
		try {
			List<PropertyData> rows = popReader.getAllRows();
			for (PropertyData row: rows) {
				System.out.println(row);
				
			}

		} catch (IOException | CSVFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

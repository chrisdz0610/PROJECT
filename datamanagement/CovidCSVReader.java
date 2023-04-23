package edu.upenn.cit594.datamanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.upenn.cit594.util.CovidData;

public class CovidCSVReader implements CovidReader {
	
	protected String filename;
	
	public CovidCSVReader (String filename) {
		this.filename = filename;
	}


	@Override
	public Map<Integer, List<CovidData>> getAllRows() throws IOException {
		//Ignore records: Zip code is not 5 digits or timestamp is not in the specific format. 
		//Any other empty field regard as “0”.
		Map<Integer, List<CovidData>> covidMap= new HashMap<Integer, List<CovidData>>();
		
		CharacterReader charReader = new CharacterReader(filename);		
		CSVReader csvReader = new CSVReader (charReader);
		
		int zipIndex = -1 , partIndex = -1, fullyIndex = -1, timeIndex = -1;
		String zipCode, timeStamp;
		int zipCodeInt, partialNum, fullyNum;
		int pop;
		String[] row;
		
		String regex = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]";
		Pattern pattern = Pattern.compile(regex);
		
		try {
			// find the index of each column through the header (first row)
			row = csvReader.readRow();
			// need to handle when keywords missing from header？
			for (int i = 0; i < row.length; i++) {
				
				if (row[i].equals("zip_code")) {
					zipIndex = i;
				}
				if (row[i].equals("partially_vaccinated")) {
					partIndex = i;
				}
				if (row[i].equals("fully_vaccinated")) {
					fullyIndex = i;
				}
				if (row[i].equals("etl_timestamp")) {
					timeIndex = i;
				}		
			}
			// read non-header rows
	        while ((row = csvReader.readRow()) != null) {
	        	
	        	zipCode = row[zipIndex];
	        	// double check zipcode missing or length not 5
	        	if (zipCode == null || zipCode.length() != 5) {
	        		continue;
	        	}
	        	// zipcode are not numeric
	        	try {	        		
	        		zipCodeInt = Integer.parseInt(zipCode);	        		
	        	} catch (NumberFormatException e) {	        		
	        		continue;
	        	}
	        	
	        	timeStamp = row[timeIndex];
				// check date format, better idea?
				Matcher matcher = pattern.matcher(timeStamp);
				if (!matcher.matches()) {
					continue;
				}				
				// set empty field as 0 for empty partial and fully vaccinated number
	        	if (row[partIndex] == null || row[partIndex].isEmpty()) {
	        		partialNum = 0;
	        	}        	
	        	// non-empty field (check if all digits? )
	            partialNum = Integer.parseInt(row[partIndex]);
	            
	        	if (row[fullyIndex] == null || row[fullyIndex].isEmpty()) {
	        		fullyNum = 0;
	        	}
	        	// non-empty field (check if all digits? )
	            fullyNum = Integer.parseInt(row[fullyIndex]);	            	 
	            
	            // check if current zipcode is already in the map, if yes, update the value
	            if (covidMap.containsKey(zipCodeInt)) {            	
	            	List<CovidData> covidList1 = covidMap.get(zipCodeInt);
	            	covidList1.add(new CovidData(zipCodeInt , partialNum, fullyNum, timeStamp));
	            	covidMap.put(zipCodeInt, covidList1);
	            	
	            }
	            // if not, put the new covidDataList in the map
	            else {
	            	
	            	List<CovidData> covidList = new ArrayList<>();
	            	covidList.add(new CovidData(zipCodeInt , partialNum, fullyNum, timeStamp));
	            	covidMap.put(zipCodeInt, covidList);            	
	            }
	        }
			
		} catch (IOException | CSVFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return covidMap;
	}
}

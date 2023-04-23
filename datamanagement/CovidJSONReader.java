package edu.upenn.cit594.datamanagement;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.upenn.cit594.util.CovidData;


public class CovidJSONReader implements CovidReader {
	
	protected String filename;
	
	public CovidJSONReader (String name) {
		filename = name;
	}

	@Override
	public Map<Integer, List<CovidData>> getAllRows() throws IOException {

		// Ignore records: Zip code is not 5 digits or timestamp is not in the specific format. 
		// Any other empty field regard as “0”.
		
		Map<Integer, List<CovidData>> covidMap = new HashMap<Integer, List<CovidData>>();
		
		JSONParser jsonParser = new JSONParser();
		
		// use regex to check the date format
		String regex = "[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]:[0-5][0-9]";
		Pattern pattern = Pattern.compile(regex);
		
		try (FileReader fr = new FileReader(filename)) {	
	
			Object obj = jsonParser.parse(fr);
			
			JSONArray ja = (JSONArray) obj;
			
			for (Object ob : ja) {
				
				JSONObject jo = (JSONObject) ob;
				
				// get zip code and ignore the missing zipcode record by continuing the loop 
				if (jo.get("zip_code") == null) {
					continue;
				}
				long zipCode = (long) jo.get("zip_code");
//				String zipString = String.valueOf(zipCode);
				int zipCodeInt = (int) zipCode;
				
				if (Integer.toString(zipCodeInt).length() != 5) {
					continue;
				}
				
				// get the date using pattern and matcher
				String timeStamp = (String) jo.get("etl_timestamp");
				
				Matcher matcher = pattern.matcher(timeStamp);
				if (!matcher.matches()) {
					continue;
				}
				
				// get the partial and fully vaccinated number, if key is missing or value is null, then set the value as "0"
				int  partialVacc,fullyVac;
				
				if (jo.get("partially_vaccinated") == null) {
					
					partialVacc = 0;
				
				} 
				
				else {
					
					long partialVaccLong = (long) jo.get("partially_vaccinated");
					partialVacc = (int) partialVaccLong;

				}
				
				if (jo.get("fully_vaccinated") == null) {
					
					fullyVac = 0;	
				}  
				
				else {
					
					long fullyVacLong = (long) jo.get("fully_vaccinated");
					fullyVac = (int) fullyVacLong;

				}
				
				// create new covid object and put it in the list
				CovidData covid = new CovidData(zipCodeInt, partialVacc, fullyVac, timeStamp);
				
				// check if the zipcode (key) already in the map, if yes, update the value
				if (covidMap.containsKey(zipCodeInt)) {
					List<CovidData> list = covidMap.get(zipCodeInt);
					list.add(covid);
					covidMap.put(zipCodeInt, list);
					
				}
				// if not create new list and put the key and value in the map
				else {
					List<CovidData> list1 = new ArrayList<CovidData>();
					list1.add(covid);
					covidMap.put(zipCodeInt, list1);
				}
			}
			
			return covidMap;

			
		} catch (FileNotFoundException e) {
		
			e.printStackTrace();
			
		} catch (ParseException e) {
	
			e.printStackTrace();
		}
		
		return covidMap;
		
	}
	
	public static void main(String arg[]) throws IOException {
		
		CovidJSONReader covidReader =  new CovidJSONReader("covid_data.json");

			Map<Integer, List<CovidData>> covidMap = covidReader.getAllRows();
			List<CovidData> l = covidMap.get(19103);
			for (CovidData d: l) {
				System.out.println("covid Data : "+ d );
			}
	}
	
}



package edu.upenn.cit594.processor;

import java.io.IOException;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.TreeMap;


import edu.upenn.cit594.datamanagement.CSVFormatException;
import edu.upenn.cit594.datamanagement.CovidReader;
import edu.upenn.cit594.datamanagement.PopulationReader;
import edu.upenn.cit594.datamanagement.PropertyReader;
import edu.upenn.cit594.util.CovidData;
import edu.upenn.cit594.util.PropertyData;

public class Processor {
	
	
	protected PopulationReader popReader;
	protected CovidReader covidReader;
	protected PropertyReader propReader;
	protected Map<Integer, Integer> populationMap;
	protected List<PropertyData> propertyList;
	protected Map<Integer, List<CovidData>> covidMap;
	
	public Processor (PopulationReader popReader,CovidReader covidReader, PropertyReader propReader) throws IOException, CSVFormatException {
		this.popReader = popReader;
		this.covidReader = covidReader;
		this.propReader = propReader;
		this.populationMap = popReader.getPopulationRows();
		this.propertyList = propReader.getAllRows();
		this.covidMap = covidReader.getAllRows();
	}
	
	public int getTotalPopulation() {
		
		int totalPop = 0;

			for (Integer key: populationMap.keySet()) {
				totalPop += populationMap.get(key);			
			}

		return totalPop;
	}
	

	public List<String> getVaccinationsPerCapita (String vacType, String date) {
		// Not Display for any Zip : 
		// Total vaccination = 0 as of the reporting date
		// Population is 0 or unknown, eg, zip code is not listed in the population file
		// Date out of range or no data (means part/full should be 0) for provided date, program should display 0

		List <String> result = new ArrayList<String>();			
		//population stored in TreeMap which will have key sorted in order
		for (int zipCode: populationMap.keySet()) {		
			int partialNum = 0, fullyNum = 0, popNum = 0;		
			//check if population is 0, already exclude invalid zipcode and populatin == null in the reader class.
			if (populationMap.get(zipCode) == 0) {
				continue;
			}
			popNum = populationMap.get(zipCode);			
			List <CovidData> covidDataList = covidMap.get(zipCode);
			// ignore  zipCode value missing in the covidmap
			if (covidDataList == null) {
				continue;
			}			
			for (CovidData data: covidDataList) {
				
				// try with user input date and check the two dates are match 
				String covidDate = data.getTimeStamp().substring(0,10);
				// locate date
				if (covidDate.equals(date)) {
					if (vacType.equals("partial")) {
						partialNum += data.getpartialVaccine();
						//ignore zip that has total vaccination is 0
					}
					else if (vacType.equals("full")) {
						fullyNum += data.getfullyVaccine();
						//ignore zip that has total vaccination is 0
					}								
				}				
			}
			
			if (vacType.equals("partial")) {
				if (partialNum == 0) {
					continue;
				}
				double partCapita = ((double) partialNum) / popNum;

				// rounded 4 digits after decimal points for result
	
				String s = zipCode + " " + String.format("%.4f",partCapita);
				result.add(s);
			}
			else if (vacType.equals("full")) {
				if (fullyNum == 0) {
					continue;
				}
				double fullCapita = (double) fullyNum / popNum;
				String s = zipCode + " " + String.format("%.4f",fullCapita );
				result.add(s);	
			}				
		}
		return result;
	}
	

	public int getAverageMarketValue(int zipCode) {
		
		int result = getAverageValue(new MarketValue(), zipCode);
		return result;
	}
	
	public int getAverageTotalLivableArea(int zipCode) {
		
		int result = getAverageValue(new LivableAreaValue(), zipCode);
		return result;
	}
	
	public int getTotalMarketValuePerCapita(int zipCode) {
		
		int marketCapita = 0;
		int popNum;
		double totalVal = 0;
//		for (Integer zip: populationMap.keySet()) {
//			popNum = populationMap.get(zip);
//			// double check below case
//			if (popNum == 0) {
//				return 0;
//			}
//		}
		if (!populationMap.containsKey(zipCode) ) {
			return 0;
		}
	
		popNum = populationMap.get(zipCode);
		if (popNum == 0) {
			return 0;
		}
		for (PropertyData d: propertyList) {
			if (d.getZipCode() == zipCode) {
				if (d.getMarketValue() != null) {
					totalVal += d.getMarketValue();
				}				
			}
		}
		if (totalVal == 0) {
			return 0;
		}
		
		marketCapita = (int) (totalVal/ popNum);
		
		return marketCapita;
	}
	// helper function for both 3.4 and 3.5
	public int getAverageValue (PropertyFieldType fieldType, int zipCode) {
		
		Double total = 0.0, fieldValue = null;
		int aveVal = 0, num = 0;
		
		for (PropertyData prop: propertyList) {
			
			if (prop.getZipCode() == zipCode) {			
				fieldValue = fieldType.getFieldType(prop);
				if ( fieldValue != null) {
					total += fieldValue;
					num++;
				}
				
				if (num == 0) {
					return 0;
				}
			}
			
		}
		aveVal = (int) (total/ num);
		return aveVal;
	}
//	public static void main(String[] args) throws IOException, CSVFormatException {
//		// TODO Auto-generated method stub
//		List <String> result = new ArrayList<String>();	
//		PopulationReader popReader = new PopulationReader("population.csv");
//		CovidReader covidReader = new CovidJSONReader("covid_data.json");
//		
//		PropertyReader propReader= new PropertyReader("properties.csv");
//		
//		Processor p = new Processor(popReader, covidReader,propReader);
//		result = p.getVaccinationsPerCapita ("partial", "2021-03-25");
//		for (String r: result) {
//			System.out.println(r);
//		}
//		int mkt = p.getAverageMarketValue(19145);
//		System.out.println("mkt: "+mkt);
//		int lv = p.getAverageTotalLivableArea(19145);
//		System.out.println("livable: "+ lv);
//		int capita = p.getTotalMarketValuePerCapita(19145);
//		System.out.println("capita: "+ capita);
//	}

}

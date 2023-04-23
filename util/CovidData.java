package edu.upenn.cit594.util;

public class CovidData {
	
	private final int zipCode;
	private final int partialVacc;
	private final int fullyVacc;
	private String timestamp;
	
	public CovidData ( int zipCode, int partialVacc, int fullyVacc, String timestamp) {
		this.zipCode = zipCode;
		this.partialVacc = partialVacc;
		this.fullyVacc = fullyVacc;
		this.timestamp = timestamp;
	}
	
	public int getZipCode() {
		return zipCode;
	}
	
	public int getpartialVaccine() {
		return partialVacc;
	}
	
	public int getfullyVaccine() {
		return fullyVacc;
	}
	
	public String getTimeStamp() {
		return timestamp;
	}
	
	@Override
	public String toString() {
		return "ZipCode: " + zipCode + " partial: " + partialVacc + " fully: " + fullyVacc + " time: " + timestamp.substring(0, 10);
	} 
} 

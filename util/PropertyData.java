package edu.upenn.cit594.util;

public class PropertyData {
	private final int zipCode;
	private final Double totalLivableArea;
	private final Double marketValue;
	
	
	public PropertyData( int zipCode, Double totalLivableArea, Double marketValue) {
		this.zipCode = zipCode;
		this.totalLivableArea = totalLivableArea;
		this.marketValue = marketValue;

	}
	
	public int getZipCode() {
		return zipCode;
	}
	
	public Double getTotalLivableArea() {
		return totalLivableArea;
	}
	
	public Double getMarketValue() {
		return marketValue;
	}

	
	@Override
	public String toString() {
		return "ZipCode: " + zipCode + " totalLivableArea: " + totalLivableArea + " marketValue: " + marketValue ;
	} 
}

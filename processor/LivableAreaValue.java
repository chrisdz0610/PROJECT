package edu.upenn.cit594.processor;

import edu.upenn.cit594.util.PropertyData;

public class LivableAreaValue implements PropertyFieldType {

	@Override
	public Double getFieldType(PropertyData data) {
		// TODO Auto-generated method stub
		return data.getTotalLivableArea();
	}

}

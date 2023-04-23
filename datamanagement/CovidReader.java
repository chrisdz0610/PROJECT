package edu.upenn.cit594.datamanagement;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import edu.upenn.cit594.util.CovidData;

public interface CovidReader {
	public Map<Integer, List<CovidData>> getAllRows() throws IOException;
}

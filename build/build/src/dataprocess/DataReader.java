package dataprocess;
import java.io.File;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.CSVLoader;


public class DataReader {
	Instances data;


	public Instances readCsvFile(String inputfile) throws IOException {

		File file = new File(inputfile);
		CSVLoader csvLoader = new CSVLoader();
		csvLoader.setSource(file);
		Instances data = csvLoader.getDataSet();
		return data;
	}

}

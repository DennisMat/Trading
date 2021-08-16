package com.dennis.aim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

public class OptionsScan {

	public static final float startingAmount = 10000f;

	static String outputFile = "C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles\\options\\output.txt";
	//static String rawDataFile = "C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles\\options\\data.txt";
	static String rawDataFile ="C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles\\options\\subset.txt";
	// static String rawDataFile ="C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles\\reduced_data.csv";

	public static void main(String[] args) throws IOException {

		String[] headers = { "UnderlyingSymbol", "UnderlyingPrice", "Flags", "OptionSymbol", "Blank", "Type",
				"Expiration", "DataDate", "Strike", "Last", "Bid", "Ask", "Volume", "OpenInterest", "T1OpenInterest" };

		File datafile = new File(rawDataFile);

		Reader in = new FileReader(datafile);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

		// we will assume that List<CSVRecord> is sorted by date
		Map<String, List<CSVRecord>> h = new HashMap<String, List<CSVRecord>>();

		long count = 0;
		for (CSVRecord record : records) {
			String optionSymbol = record.get(3);
			// System.out.println("Record count = " + count++);
			if (h.containsKey(optionSymbol)) {
				h.get(optionSymbol).add(record);
			} else {
				List<CSVRecord> l = new ArrayList<CSVRecord>();
				l.add(record);
				h.put(optionSymbol, l);
			}

		}

		in.close();
		System.out.println("----------------");
		for (Entry<String, List<CSVRecord>> set : h.entrySet()) {

			List<CSVRecord> l = set.getValue();

			final float[] optionPrice = new float[l.size()];
			final String[] dates = new String[l.size()];
			
			for(int i=0;i<l.size();i++) {
				optionPrice[i] = Float.parseFloat(l.get(i).get(10));
				dates[i]=l.get(i).get(7);
			}
			String symbol="symbol = \t" + set.getKey();
			System.out.println(symbol);
			File output = new File(outputFile);

			FileUtils.write(output, System.lineSeparator() + symbol);
			Line.processAllRows(dates,optionPrice,startingAmount,-1,true,outputFile);
	

		}

	}


}

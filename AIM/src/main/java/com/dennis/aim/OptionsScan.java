package com.dennis.aim;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

	public static void main(String[] args) throws IOException {
		String outputFile = "C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles\\options\\output.txt";
		String rawDataFile = "C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles\\options\\data\\subset.txt";
		// rawDataFile ="C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles\\options\\subset.txt";
		rawDataFile = "C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles\\options\\data\\original.csv";

		boolean print = true;
		print = false;

		String[] headers = { "UnderlyingSymbol", "UnderlyingPrice", "Flags", "OptionSymbol", "Blank", "Type", "Expiration", "DataDate", "Strike", "Last", "Bid", "Ask", "Volume", "OpenInterest",
				"T1OpenInterest" };

		File datafile = new File(rawDataFile);

		File output = new File(outputFile);
		try {
			output.delete();
		} catch (Exception e) {
		}


		Map<String, List<CSVRecord>> h = new HashMap<String, List<CSVRecord>>();
		BufferedReader reader = new BufferedReader(new FileReader(datafile));
		int line = 0;
		Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(reader);
		for (CSVRecord record : records) {

			String optionSymbol = record.get(3).trim();
			if (h.containsKey(optionSymbol)) {
				h.get(optionSymbol).add(record);
			} else {
				List<CSVRecord> l = new ArrayList<CSVRecord>();
				l.add(record);
				h.put(optionSymbol, l);
			}

			System.out.println(line++);
			if (line == 100000) {
				line = 0;
				crunchResults(h, print, outputFile);
				h = new HashMap<String, List<CSVRecord>>();
				System.gc();
				
			}
		}
		reader.close();

	}

	static void crunchResults(Map<String, List<CSVRecord>> h, boolean print, String outputFile) throws IOException {

		for (Entry<String, List<CSVRecord>> set : h.entrySet()) {

			List<CSVRecord> l = set.getValue();

			final float[] optionPrice = new float[l.size()];
			final String[] dates = new String[l.size()];

			for (int i = 0; i < l.size(); i++) {
				optionPrice[i] = Float.parseFloat(l.get(i).get(10));
				dates[i] = l.get(i).get(7);
			}
			String symbol = "symbol = \t" + set.getKey();
			System.out.println(symbol);

			File output = new File(outputFile);
			FileUtils.write(output, System.lineSeparator() + symbol, true);
			Line.processAllRows(dates, optionPrice, startingAmount, 0, print, outputFile);

		}

	}

}

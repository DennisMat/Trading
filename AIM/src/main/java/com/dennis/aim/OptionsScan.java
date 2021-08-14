package com.dennis.aim;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class OptionsScan {
	
	public static final float startingAmount = 10000f;
	public static int stockOwned;
	public static float cash;

	public static void main(String[] args) throws IOException {
		String[] headers = { "UnderlyingSymbol", "UnderlyingPrice", "Flags", "OptionSymbol", "Blank", "Type",
				"Expiration", "DataDate", "Strike", "Last", "Bid", "Ask", "Volume", "OpenInterest", "T1OpenInterest" };

		//File datafile = new File("C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles\\options\\data.txt");
		File datafile = new File("C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles\\reduced_data.csv");

		Reader in = new FileReader(datafile);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);

		// we will assume that List<CSVRecord> is sorted by date
		Map<String, List<CSVRecord>> h = new HashMap<String, List<CSVRecord>>();

		for (CSVRecord record : records) {
			String optionSymbol = record.get(3);
			if (h.containsKey(optionSymbol)) {
				h.get(optionSymbol).add(record);
			} else {
				List<CSVRecord> l = new ArrayList<CSVRecord>();
				l.add(record);
				h.put(optionSymbol, l);
			}

		}

		for (Entry<String, List<CSVRecord>> set : h.entrySet()) {

			List<CSVRecord> l= set.getValue();
			
			final float[] optionPrice = new float[l.size()];
			
			int index = 0;
			for (CSVRecord record : l) {
				optionPrice[index++] = Float.parseFloat(record.get(10));
				//System.out.println(record.get(10));
			}
			
			
			process(set.getKey(),optionPrice);
			
		}

	}

	static void process(String symbol,float[] optionPrice) {

		float interest = -1;
		cash = startingAmount / 2;
		stockOwned = Math.round(cash / optionPrice[0]);

		Line lineInt = new Line();

		lineInt.stockPrice = optionPrice[0];
		lineInt.stockValue = stockOwned * optionPrice[0];
		lineInt.safe = lineInt.stockValue / 10;
		lineInt.cash = cash;
		lineInt.stockOwned = stockOwned;
		lineInt.portfolioControl = lineInt.cash;
		lineInt.portfolioValue = startingAmount;

		boolean print = true;
		print = false;

		if (print) {
			LineInteger.printHeader();
			System.out.println();
			lineInt.printValues();
			System.out.println();
		}

		Line prevLine = lineInt;
		for (int i = 0; i < optionPrice.length; i++) {
			Line l = new Line(prevLine.stockOwned, prevLine.cash, optionPrice[i], prevLine.sharesBoughtSold,
					prevLine.portfolioControl, prevLine.marketOrder, prevLine.action, prevLine.interest, interest);
			if (print) {
				l.printValues();
				System.out.println();
			}
			prevLine = l;
		}

		System.out.println("Final Portfolio Value for symbol " + symbol + " is " + prevLine.portfolioValue);

	}

}

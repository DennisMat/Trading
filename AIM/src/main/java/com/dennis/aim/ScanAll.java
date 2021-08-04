package com.dennis.aim;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class ScanAll {
	
	public static final float startingAmount = 6000f;
	public static int stockOwned;
	public static float cash;



	public static void main(String[] args) {

		File directoryPath = new File("C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles\\1year");
		File filesList[] = directoryPath.listFiles();
		for (File file : filesList) {
			System.out.println("File name: " + file.getName());

			List<Float> stockPriceList = new ArrayList<Float>();
			Iterable<CSVRecord> records = null;
			try {
				Reader in = new FileReader(file);
				records = CSVFormat.EXCEL.parse(in);
			} catch (Exception e) {
			}

			boolean skipfirst = true;
			for (CSVRecord record : records) {
				if (!skipfirst) {
					try {
						stockPriceList.add(Float.parseFloat(record.get(1)));
					} catch (NumberFormatException e) {}
				}
				skipfirst = false;
			}
			final float[] stockPrice = new float[stockPriceList.size()];
			int index = 0;
			for (final Float value : stockPriceList) {
				stockPrice[index++] = value;
			}
			process(stockPrice);
		}

	}

	static void process(float[] stockPrice) {

		float interest = -10;
		cash=startingAmount/2;
		stockOwned = Math.round(cash / stockPrice[0]);

		Linef lineInt = new Linef();

		lineInt.stockPrice = stockPrice[0];
		lineInt.stockValue = stockOwned*stockPrice[0];
		lineInt.safe = lineInt.stockValue/10;
		lineInt.cash = cash;
		lineInt.stockOwned = stockOwned;
		lineInt.portfolioControl = lineInt.cash;
		lineInt.portfolioValue = startingAmount;

		Line.printHeader();
		 System.out.println();
		 lineInt.printValues();
		System.out.println();

		Linef prevLine = lineInt;
		for (int i = 0; i < stockPrice.length; i++) {
			Linef l = new Linef(stockPrice[i], prevLine.sharesBoughtSold, prevLine.portfolioControl,
					prevLine.marketOrder, prevLine.action, prevLine.interest, interest);
			// l.printValues();
			// System.out.println();
			prevLine = l;
		}

		System.out.println("Final Portfolio Value is " + prevLine.portfolioValue);

	}

}

package com.dennis.aim;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

public class ScanAll {

	public static final float startingAmount = 10000f;
	public static int stockOwned;
	public static float cash;

	public static void main(String[] args) {

		File directoryPath = new File("C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles\\1year");
		File filesList[] = directoryPath.listFiles();
		for (File file : filesList) {
			System.out.print("File name: " + file.getName());

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
					} catch (NumberFormatException e) {
					}
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

		float interest = -1;
		cash = startingAmount / 2;
		stockOwned = Math.round(cash / stockPrice[0]);

		Line lineInt = new Line();

		lineInt.stockPrice = stockPrice[0];
		lineInt.stockValue = stockOwned * stockPrice[0];
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
		for (int i = 0; i < stockPrice.length; i++) {
			Line l = new Line(prevLine.stockOwned, prevLine.cash, stockPrice[i], prevLine.sharesBoughtSold,
					prevLine.portfolioControl, prevLine.marketOrder, prevLine.action, prevLine.interest, interest);
			if (print) {
				l.printValues();
				System.out.println();
			}
			prevLine = l;
		}

		System.out.println("Final Portfolio Value is " + prevLine.portfolioValue);

	}

}

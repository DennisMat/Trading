package com.dennis.aim;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

public class ScallAll {

	public static final float startingAmount = 10000f;

//	public static String baseFolder="C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles";
//	public static String rawDataFolder = baseFolder+"\\Canadian100Subset";
//	public static String outputFile = rawDataFolder+"\\output.txt";

//	public static String rawDataFolder = "C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles\\penny";
//	public static String outputFile = "C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles\\penny\\output.txt";
	
	public static String baseFolder="C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles";
	public static String rawDataFolder = baseFolder+"\\india";
	public static String outputFile = rawDataFolder+"\\output.txt";

public static boolean print = true;

	public static void main(String[] args) throws IOException {
		print = false;

		File output = new File(outputFile);
		try {
			output.delete();
		} catch (Exception e) {
		}

		File directoryPath = new File(rawDataFolder);
		File filesList[] = directoryPath.listFiles();
		for (File file : filesList) {
			System.out.print("File name: " + file.getName() + "\n");

			List<Float> stockPriceList = new ArrayList<Float>();
			List<String> dateList = new ArrayList<String>();
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
						//yahoo
						stockPriceList.add(Float.parseFloat(record.get(1)));
						dateList.add(record.get(0));
						//india
//						stockPriceList.add(Float.parseFloat(record.get(3)));
//						dateList.add(record.get(2));
						
					} catch (NumberFormatException e) {
					}
				}
				skipfirst = false;
			}
			final float[] stockPrice = new float[stockPriceList.size()];
			int index = 0;

			final String[] dates = new String[stockPriceList.size()];

			for (int i = 0; i < stockPriceList.size(); i++) {
				stockPrice[i] = stockPriceList.get(i);
				dates[i] = dateList.get(i);
			}
			//Line.print = true;
			String symbol=file.getName().substring(0,file.getName().lastIndexOf("."));
			FileUtils.write(output, System.lineSeparator() + "https://ca.finance.yahoo.com/quote/" +symbol+"?"+symbol, true);
			Line.processAllRows(dates, stockPrice, startingAmount, -10, outputFile);
		}

	}

}

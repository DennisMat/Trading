package com.dennis.aim;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

public class ScanAll {

	public static final float startingAmount = 10000f;

	public static String rawDataFolder = "C:\\Users\\Lenovo\\Desktop\\DeleteLater\\trades\\csvfiles\\1year";
	public static boolean print = true;

	public static void main(String[] args) {
		print = false;

		File directoryPath = new File(rawDataFolder);
		File filesList[] = directoryPath.listFiles();
		for (File file : filesList) {
			System.out.print("File name: " + file.getName() + " ");

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
			Line.processAllRows(stockPrice, startingAmount, -1, false);
		}

	}

}

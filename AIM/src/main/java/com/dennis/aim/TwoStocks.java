package com.dennis.aim;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;

public class TwoStocks {

	public static final double startingAmount = 5000f;
	public static final double startingAmount1 = 5000f;



public static boolean print = true;

	public static void main(String[] args) throws IOException {
		print = false;


	
			final double[] stockPrice = {10,9,8,7,6,5,4,3,2,1};
			LocalDate[] dates = {null,null,null,null,null,null,null,null,null,null,null};
			
			Line.print = true;
			Line.processAllRows(dates, stockPrice, startingAmount, -10, "");

			System.out.println("------------------");
			final double[] stockPrice1 = {1,2,3,4,5,6,7,8,9,10};
			
			Line.processAllRows(dates, stockPrice1, startingAmount1, -10, "");

	}

}

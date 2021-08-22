package com.dennis.aim;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.dennis.aim.Line.Action;

public class Predict {

//https://finance.yahoo.com/chart/POU.TO
	public static void main(String[] args) {

		final float initialStockPrice = 14.33f;
		int startingStockOwned = 225;

		final float initialCash = 3072.64f;
		float interest = -0;

		final float incrementPrice = 0.2f;

		Line lineFirst = Line.getFirstLine(initialStockPrice, startingStockOwned, initialCash);

		lineFirst.printHeader();
		 lineFirst.printValues();

		Line prevLine = lineFirst;

		Line lastLine = findBuySellPrice(incrementPrice, prevLine, initialStockPrice, Action.SELL, interest);

		lineFirst = Line.getFirstLine(initialStockPrice, startingStockOwned, initialCash);

		System.out.println();
		findBuySellPrice(incrementPrice, prevLine, prevLine.stockPrice, Action.BUY, interest);

		// System.out.println("Final Portfolio Value is " + prevLine.portfolioValue);

	}

	static Line findBuySellPrice(final float incrementPrice, Line prevLine, float stockPriceForSellBuy, Action action,
			float interest) {
		Line l = null;
		int loopCount = 0;
		while (true) {
			loopCount++;
			if (action == Action.SELL) {
				stockPriceForSellBuy += incrementPrice;
			} else {
				stockPriceForSellBuy -= incrementPrice;
			}

			l = Line.getNewLine("",prevLine, stockPriceForSellBuy, interest);
			 l.printValues();
			// System.out.println();
			if (l.action == action || loopCount > 1000) {
				System.out.println(action + " Stock Price = " + l.stockPrice + " Quantity = " + l.sharesBoughtSold
						+ ". Market order will be " + l.marketOrder);
				break;
			}
			prevLine = l;
		}

		return l;
	}
}
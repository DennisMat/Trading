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

	public static final float startingAmount = 6000f;
	public static int stockOwned;
	public static float cash;

	public static void main(String[] args) {

		final float initialStockPrice = 15.16f;
		int startingStockOwned = 200;

		final float initialCash = 3435.84f;
		float interest=-10;

		final float incrementPrice = 0.94f;

		Line lineFirst = Line.getFirstLine(initialStockPrice, startingStockOwned, initialCash);

		Line.printHeader();
		System.out.println();
		// lineFirst.printValues();
		System.out.println();

		Line prevLine = lineFirst;

		findBuySellPrice(incrementPrice, prevLine, initialStockPrice, Action.SELL,interest);

		lineFirst = Line.getFirstLine(initialStockPrice, startingStockOwned, initialCash);
		prevLine = lineFirst;
		//lineFirst.printValues();
		System.out.println();
		findBuySellPrice(incrementPrice, prevLine, initialStockPrice, Action.BUY,interest);

		// System.out.println("Final Portfolio Value is " + prevLine.portfolioValue);

	}

	static void findBuySellPrice(final float incrementPrice, Line prevLine, float stockPriceForSellBuy, Action action,float interest) {
		int loopCount = 0;
		while (true) {
			loopCount++;
			if (action == Action.SELL) {
				stockPriceForSellBuy += incrementPrice;
			} else {
				stockPriceForSellBuy -= incrementPrice;
			}

			Line l = new Line(prevLine.stockOwned, prevLine.cash, stockPriceForSellBuy, prevLine.sharesBoughtSold,
					prevLine.portfolioControl, prevLine.marketOrder, prevLine.action, prevLine.interest,
					interest);
//			l.printValues();
//			System.out.println();
			if (l.action == action || loopCount > 500) {
				System.out.println(action + " Stock Price = " + l.stockPrice + " Quantity = " + l.sharesBoughtSold + ". Market order will be " + l.marketOrder);
				break;
			}
			prevLine = l;
		}
	}
}

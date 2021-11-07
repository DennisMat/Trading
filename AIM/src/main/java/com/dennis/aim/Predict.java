package com.dennis.aim;

import com.dennis.aim.Line.Action;

public class Predict {

//https://finance.yahoo.com/chart/POU.TO
	public static void main(String[] args) {

		final double currentStockPrice = 8.76f;
		int currentStocksOwned = 1026;
		double currentCash = 10000.8f;//
		double interest = 0;

		double portfolioControl = 10000.05f;

		final double incrementPrice = 0.01f;

		Line lineFirstA = Line.getFirstLine(currentStockPrice, currentStocksOwned, currentCash, portfolioControl);

//		lineFirst.printHeader();
//		lineFirst.printValues();
		// System.out.println();
		Line prevLineA = lineFirstA;

		findBuySellPrice(incrementPrice, prevLineA, currentStockPrice, Action.SELL, interest);
		// ---------------------Buy------------------------

		Line lineFirstB = Line.getFirstLine(currentStockPrice, currentStocksOwned, currentCash, portfolioControl);
		// lineFirstB.printValues();
		System.out.println();
		Line prevLineB = lineFirstB;
		findBuySellPrice(incrementPrice, prevLineB, prevLineB.stockPrice, Action.BUY, interest);

	}

	static Line findBuySellPrice(final double incrementPrice, Line prevLine, double stockPriceForSellBuy, Action action, double interest) {
		Line l = null;
		long loopCount = 0;
		while (true) {
			loopCount++;
			if (action == Action.SELL) {
				stockPriceForSellBuy += incrementPrice;
			} else {
				stockPriceForSellBuy -= incrementPrice;
			}

			l = Line.getNewLine("", prevLine, stockPriceForSellBuy, interest);
			// l.printValues();

			if (l.action == action || loopCount > 1000000000) {
				System.out.println();
				System.out.println(action + " Stock Price = " + l.stockPrice + " Quantity = " + l.sharesBoughtSold + ". Market order will be " + l.marketOrder);
				break;
			}
			prevLine = l;
		}

		return l;
	}
}

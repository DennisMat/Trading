package com.dennis.models;

import java.util.ArrayList;
import java.util.List;

import com.dennis.models.Line.Action;



public class Predict {

//https://finance.yahoo.com/chart/POU.TO
	public static void main(String[] args) {

		final double currentStockPrice = 8.76f;
		int currentStocksOwned = 1026;
		double currentCash = 10000.8f;//
		double interest = 0;

		double portfolioControl = 10000.05f;

		final double incrementPrice = 0.01f;
		
		Line lastLine= new Line();
		
		lastLine.stockPrice=currentStockPrice;
		lastLine.cash=currentCash;
		lastLine.interest=interest;
		lastLine.portfolioControl=portfolioControl;
		

		calculate(lastLine, incrementPrice);

	}




	static void calculate(Line lastLine, final double incrementPrice) {

		findBuyLimit(lastLine, incrementPrice);

		findSellLimit(lastLine, incrementPrice);
		
		
	}




	static Line findBuyLimit(Line lastLine, final double incrementPrice) {
		Line l = findBuySellPrice(incrementPrice, lastLine, lastLine.stockPrice, Action.SELL, lastLine.interest);
		return l;
	}




	static Line findSellLimit(Line lastLine, final double incrementPrice) {
		Line l = findBuySellPrice(incrementPrice, lastLine, lastLine.stockPrice, Action.BUY, lastLine.interest);
		return l;
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

package com.dennis.aim;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

public class Line {

	float stockPrice;

	float stockValue;
	float safe;
	float cash;
	int sharesBoughtSold;
	int stockOwned;
	float portfolioControl;
	float buyOrSellAdvice;
	float marketOrder;
	float interest;
	float portfolioValue;
	Action action;

	enum Action {
		DO_NOTHING, BUY, SELL;
	}

	public static void printHeader() {
		String[] headers = { "Stock Price", "Stock Value", "Safe", "Cash", "Shares buy and sell", "Stock Owned",
				"Portfolio Control", "Buy or Sell Advise", "Market Order", "interest", "Portfolio Value", "Action" };
		for (int i = 0; i < headers.length; i++) {
			System.out.print(headers[i] + "\t");
		}
	}

	public void printValues() {

		Object[] values = { stockPrice, stockValue, safe, cash, sharesBoughtSold, stockOwned, portfolioControl,
				buyOrSellAdvice, marketOrder, interest, portfolioValue, action };

		for (int i = 0; i < values.length; i++) {
			System.out.print(values[i] + "\t");
		}

	}

	/**
	 * Safe Cash Portfolio Control Buy or Sell Advise Market Order Portfolio Value
	 * 
	 * 
	 * cash = half of initial value safe= 10% of stock value
	 * 
	 * 
	 * 1)buyOrSellAdvice = stockValue - previous portfolioControl, if positive sell
	 * 2) If buyOrSellAdvice> safe, only then act. 3) Marketorder = buyOrSellAdvice
	 * -safe 4) If sell, portfolio control remains the same, else if buy
	 * newPortfolioControl = portfolioControl + marketOrder / 2;
	 * 
	 */

	public Line(int prevStocksOwned, float prevCash, float stockPrice, float prevSharesBoughtSold,
			float prevPortfolioControl, float prevMarketOrder, Action prevAction, float prevInterest, float interest) {

		this.stockPrice = stockPrice;
		this.stockOwned = prevStocksOwned;

		stockValue = this.stockOwned * stockPrice;

		safe = Math.round(stockValue / 10);
		this.cash = prevCash + prevInterest + prevMarketOrder;

		this.interest = interest;

		portfolioValue = stockValue + cash;

		// if positive sell
		buyOrSellAdvice = stockValue - prevPortfolioControl;

		// safe is like a threshold, only if abs (buyOrSellAdvice) is above safe you
		// sell or buy
		if (safe > Math.abs(buyOrSellAdvice)) {
			action = Action.DO_NOTHING;
		} else {

			float potentialMarketorder = Math.abs(safe - Math.abs(buyOrSellAdvice));

			if (buyOrSellAdvice > 0) {// if sell make sure there are sufficient stocks to sell
				if (stockValue < potentialMarketorder) {
					potentialMarketorder = stockValue;
				}
			}

			if (buyOrSellAdvice < 0) {// if buy make sure there is sufficient cash
				if (this.cash < potentialMarketorder) {
					potentialMarketorder = this.cash;
				}
			}

			if (potentialMarketorder > 100) {
				marketOrder = potentialMarketorder;
				sharesBoughtSold = Math.round(potentialMarketorder / stockPrice);
			}
			if (marketOrder == 0) {
				action = Action.DO_NOTHING;
			} else if (buyOrSellAdvice > 0) {
				action = Action.SELL;
				sharesBoughtSold = -sharesBoughtSold;
			} else if (buyOrSellAdvice < 0) {
				// One could have used marketOrder=-marketOrder, here instead but we want
				// rounded figures
				// marketOrder=-marketOrder;
				marketOrder = -Math.round(sharesBoughtSold * stockPrice);
				action = Action.BUY;
			}

			this.stockOwned += sharesBoughtSold;
			// this.stockOwned = stockOwnedGlobal;
		}

		if (action == Action.SELL) {
			portfolioControl = prevPortfolioControl;
		} else {
			portfolioControl = prevPortfolioControl - marketOrder / 2;
		}

	}

	public Line() {
		// TODO Auto-generated constructor stub
	}

	public static Line getFirstLine(float startingStockPrice, float startingAmount, float startingInterest) {
		Line lineFirst = new Line();

		lineFirst.cash = startingAmount / 2;

		lineFirst.stockPrice = startingStockPrice;
		lineFirst.stockOwned = Math.round(lineFirst.cash / startingStockPrice);
		lineFirst.stockValue = lineFirst.stockOwned * startingStockPrice;
		lineFirst.safe = lineFirst.stockValue / 10;
		lineFirst.cash = lineFirst.cash;

		lineFirst.portfolioControl = lineFirst.cash;
		lineFirst.portfolioValue = startingAmount;
		lineFirst.interest = startingInterest;
		return lineFirst;
	}
	
	public static Line getFirstLine(float[] stockPrices, float startingAmount, float startingInterest) {
		float startingStockPrice = 0;
	
		for (int i = 0; i < stockPrices.length; i++) {
			if(stockPrices[i]>0.2) {//sometimes the price is zero and these figures have to be skipped.
				startingStockPrice=stockPrices[i];
				break; 
			}
		}
			
			
			return getFirstLine( startingStockPrice,  startingAmount,  startingInterest) ;
	}

	public static Line getFirstLine(float startingStockPrice, int startingStockOwned, float startingCash) {
		Line lineFirst = new Line();

		lineFirst.cash = startingCash;

		lineFirst.stockPrice = startingStockPrice;
		lineFirst.stockOwned = startingStockOwned;
		lineFirst.stockValue = lineFirst.stockOwned * startingStockPrice;
		lineFirst.safe = lineFirst.stockValue / 10;

		lineFirst.portfolioControl = lineFirst.cash;
		lineFirst.portfolioValue = lineFirst.stockValue + lineFirst.cash;
		return lineFirst;
	}
	
	
	static void processAllRows(float[] stockPrice, float startingAmount,float interest,boolean print) {

		Line lineInt = Line.getFirstLine(stockPrice, startingAmount,  interest);

	

		if (print) {
			LineInteger.printHeader();
			System.out.println();
			lineInt.printValues();
			System.out.println();
		}

		Line prevLine = lineInt;
		for (int i = 0; i < stockPrice.length; i++) {
			Line l = getNewLine(prevLine, stockPrice[i], interest);
			if (print) {
				l.printValues();
				System.out.println();
			}
			prevLine = l;

		}

		System.out.println("\tFinal Portfolio Value is \t" + prevLine.portfolioValue);
		
//		File output = new File(outputFile);
//
//		FileUtils.write(output, System.lineSeparator() + symbol + "\t" + (int) Math.ceil(prevLine.portfolioValue),
//				StandardCharsets.UTF_8, true);

	}

	static Line getNewLine(Line prevLine, float stockPrice, float interest) {
		return new Line(prevLine.stockOwned, prevLine.cash, stockPrice, prevLine.sharesBoughtSold,
				prevLine.portfolioControl, prevLine.marketOrder, prevLine.action, prevLine.interest, interest);
	}

}

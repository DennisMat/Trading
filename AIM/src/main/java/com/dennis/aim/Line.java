package com.dennis.aim;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

public class Line {
	
	//sometimes the price is zero and these figures have to be skipped.
	static final float MINIMUM_PRICE=0.001f; 

	String date;
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

	public  void writeHeader(String outputFile) throws IOException {
		if(outputFile!=null) {
			File output = new File(outputFile);
			FileUtils.write(output, "\n"+getHeaderRow(),
					StandardCharsets.UTF_8, true);
		}
		
	}
	
	public  void printHeader() {
			System.out.print("\n"+getHeaderRow());
		
	}

	 String getHeaderRow() {
		StringBuffer sb = new StringBuffer();
		String[] headers = { "Date", "Stock Price", "Stock Value", "Safe", "Cash", "Shares buy and sell", "Stock Owned",
				"Portfolio Control", "Buy or Sell Advise", "Market Order", "interest", "Portfolio Value", "Action" };
		for (int i = 0; i < headers.length; i++) {
			sb.append(headers[i] + "\t");
		}
		
		return sb.toString();
	}
	
	public void printValues() {
		if(action!=Action.DO_NOTHING) {
			System.out.print("\n"+getRowValues());
		}
	}
	
	public void writeValues(String outputFile) throws IOException {
		if(outputFile!=null && action!=Action.DO_NOTHING) {
			File output = new File(outputFile);
			FileUtils.write(output, "\n"+getRowValues(),
					StandardCharsets.UTF_8, true);
		}
		
	}

	public String getRowValues() {
		StringBuffer sb = new StringBuffer();
		Object[] values = { date, stockPrice, stockValue, safe, cash, sharesBoughtSold, stockOwned, portfolioControl,
				buyOrSellAdvice, marketOrder, interest, portfolioValue, action };
		for (int i = 0; i < values.length; i++) {
			sb.append(values[i] + "\t");
		}

		return sb.toString();
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

	public Line(String date, int prevStocksOwned, float prevCash, float stockPrice, float prevSharesBoughtSold,
			float prevPortfolioControl, float prevMarketOrder, Action prevAction, float prevInterest, float interest) {

		this.date=date;
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
				this.interest =0;//no commission if shares are not 
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

	public static Line getFirstLine(String date,float startingStockPrice, float startingAmount, float startingInterest) {
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
	
	public static Line getFirstLine(String dates[], float[] stockPrices, float startingAmount, float startingInterest) {
		float startingStockPrice = 0;
		String date=null;
		for (int i = 0; i < stockPrices.length; i++) {
			if(stockPrices[i]>MINIMUM_PRICE) {//sometimes the price is zero and these figures have to be skipped.
				startingStockPrice=stockPrices[i];
				date=dates[i];
				break; 
			}
		}
			
			
			return getFirstLine(date, startingStockPrice,  startingAmount,  startingInterest) ;
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
	
	
	static void processAllRows(String dates[], float[] stockPrice, float startingAmount,float interest,boolean print, String outputFile) throws IOException {
		Line lineInt = Line.getFirstLine(dates,stockPrice, startingAmount,  interest);
		if (print) {
			lineInt.printHeader();
			lineInt.printValues();
		}
		lineInt.writeHeader(outputFile);

		Line prevLine = lineInt;
		for (int i = 0; i < stockPrice.length; i++) {
			Line l = getNewLine(dates[i],prevLine, stockPrice[i], interest);
			if (print) {
				l.printValues();
				
				System.out.println();
			}
			prevLine = l;
			l.writeValues(outputFile);
		}
		
		

		String finalPortfolioValue="\tFinal Portfolio Value is \t"+ (int) Math.ceil(prevLine.portfolioValue);
		//System.out.println(finalPortfolioValue);
		if(outputFile!=null) {
			File output = new File(outputFile);

			FileUtils.write(output, finalPortfolioValue,
					StandardCharsets.UTF_8, true);
		}
		

	}

	static Line getNewLine(String date,Line prevLine, float stockPrice, float interest) {

		if(stockPrice<MINIMUM_PRICE) {
			prevLine.date=date;
			return prevLine;
		}
		return new Line(date, prevLine.stockOwned, prevLine.cash, stockPrice, prevLine.sharesBoughtSold,
				prevLine.portfolioControl, prevLine.marketOrder, prevLine.action, prevLine.interest, interest);
	}

}

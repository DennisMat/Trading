package com.dennis.aim;

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


	public Line(int prevStocksOwned,float prevCash, float stockPrice, float prevSharesBoughtSold, float prevPortfolioControl, float prevMarketOrder,
			Action prevAction, float prevInterest, float interest) {

		this.stockPrice = stockPrice;
		this.stockOwned = prevStocksOwned;

		stockValue = this.stockOwned * stockPrice;

		safe = Math.round(stockValue / 10);
		this.cash = prevCash+prevInterest + prevMarketOrder;

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

			if (potentialMarketorder > 100) {
				marketOrder = potentialMarketorder;
				sharesBoughtSold = Math.round(potentialMarketorder / stockPrice);
			}

			
			if (marketOrder == 0) {
				action = Action.DO_NOTHING;
			} else if (buyOrSellAdvice > 0) {
				action = Action.SELL;
				sharesBoughtSold=-sharesBoughtSold;
			} else if (buyOrSellAdvice < 0) {
				//One could have used marketOrder=-marketOrder, here instead but we want rounded figures
				//marketOrder=-marketOrder;
				marketOrder= -Math.round(sharesBoughtSold *stockPrice);
				action = Action.BUY;
			}
			
			this.stockOwned += sharesBoughtSold;
			//this.stockOwned = stockOwnedGlobal;
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
		lineFirst.stockOwned = Math.round(lineFirst.cash/ startingStockPrice);
		lineFirst.stockValue = lineFirst.stockOwned * startingStockPrice;
		lineFirst.safe = lineFirst.stockValue / 10;
		lineFirst.cash = lineFirst.cash;

		lineFirst.portfolioControl = lineFirst.cash;
		lineFirst.portfolioValue = startingAmount;
		lineFirst.interest = startingInterest;
		return lineFirst;
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

}

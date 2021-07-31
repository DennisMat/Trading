package com.dennis.aim;

public class Line {

	// int range is -2,147,483,648 to +2,147,483,647
	int stockPrice;

	int stockValue;
	int safe;
	int cash;
	int sharesBoughtSold;
	int stockOwned;
	int portfolioControl;
	int buyOrSellAdvice;
	int marketOrder;
	int interest;
	int portfolioValue;
	Action action;
	int Notes;

	enum Action {
		DO_NOTHING, BUY, SELL;
	}

	public static void printHeader() {
		String[] headers = { "Stock Price", "Stock Value", "Safe", "Cash", "sharesB&S", "stockOwned",
				"Portfolio Control", "Buy or Sell Advise", "Market Order", "Interest", "Portfolio Value", "Action" };
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

	public Line(int stockPrice, int prevSharesBoughtSold, int prevPortfolioControl, int prevMarketOrder,
			Action prevAction, int prevInterest, int interest) {

		this.stockPrice = stockPrice;
		this.stockOwned = Main.stockOwned;

		stockValue = this.stockOwned * stockPrice;

		safe = stockValue / 10;
		Main.cash += prevInterest + prevMarketOrder;
		this.cash = Main.cash;

		this.interest = interest;

		if (prevAction == Action.SELL) {
			portfolioControl = prevPortfolioControl;
		} else {
			portfolioControl = prevPortfolioControl - prevMarketOrder / 2;
		}

		portfolioValue = stockValue + cash;

		// if positive sell
		buyOrSellAdvice = stockValue - portfolioControl;

		// safe is like a threshold, only if abs (buyOrSellAdvice) is above safe you
		// sell or buy
		if (safe > Math.abs(buyOrSellAdvice)) {
			action = Action.DO_NOTHING;
		} else {

			int quantityBuySell = Math.abs(safe - Math.abs(buyOrSellAdvice));

			if (quantityBuySell > 100) {
				marketOrder = quantityBuySell;
			}

			sharesBoughtSold = quantityBuySell / stockPrice;
			
			if (marketOrder == 0) {
				action = Action.DO_NOTHING;
			} else if (buyOrSellAdvice > 0) {
				action = Action.SELL;
				sharesBoughtSold=-sharesBoughtSold;
			} else if (buyOrSellAdvice < 0) {
				marketOrder=-marketOrder;
				action = Action.BUY;
			}
			
			Main.stockOwned += sharesBoughtSold;
			this.stockOwned = Main.stockOwned;
		}

	}

	public Line() {
		// TODO Auto-generated constructor stub
	}

}

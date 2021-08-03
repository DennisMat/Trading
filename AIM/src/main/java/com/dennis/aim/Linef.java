package com.dennis.aim;

public class Linef {


	float stockPrice;

	float stockValue;
	float safe;
	float cash;
	float sharesBoughtSold;
	float stockOwned;
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
				"Portfolio Control", "Buy or Sell Advise", "Market Order", "floaterest", "Portfolio Value", "Action" };
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

	public Linef(float stockPrice, float prevSharesBoughtSold, float prevPortfolioControl, float prevMarketOrder,
			Action prevAction, float prevfloaterest, float interest) {

		this.stockPrice = stockPrice;
		this.stockOwned = ScanAll.stockOwned;

		stockValue = this.stockOwned * stockPrice;

		safe = stockValue / 10;
		ScanAll.cash += prevfloaterest + prevMarketOrder;
		this.cash = ScanAll.cash;

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

			float quantityBuySell = Math.abs(safe - Math.abs(buyOrSellAdvice));

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
			
			ScanAll.stockOwned += sharesBoughtSold;
			this.stockOwned = ScanAll.stockOwned;
		}

	}

	public Linef() {
		// TODO Auto-generated constructor stub
	}

}

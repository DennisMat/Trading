package com.dennis.aim;

public class Linef {

	static float cashGlobal;
	static float stockOwnedGlobal;

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
			Action prevAction, float prevInterest, float interest) {

		this.stockPrice = stockPrice;
		this.stockOwned = stockOwnedGlobal;

		stockValue = this.stockOwned * stockPrice;

		safe = Math.round(stockValue / 10);
		cashGlobal += prevInterest + prevMarketOrder;
		this.cash = cashGlobal;

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
			}

			sharesBoughtSold = Math.round(potentialMarketorder / stockPrice);
			
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
			
			stockOwnedGlobal += sharesBoughtSold;
			this.stockOwned = stockOwnedGlobal;
		}
		
		if (action == Action.SELL) {
			portfolioControl = prevPortfolioControl;
		} else {
			portfolioControl = prevPortfolioControl - marketOrder / 2;
		}
		
		

	}

	public Linef() {
		// TODO Auto-generated constructor stub
	}

}

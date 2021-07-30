package com.dennis.aim;

public class Line {
	
	//int range is -2,147,483,648 to +2,147,483,647
	
	int stockValue;
	int safe;
	int cash;
	int portfolioControl;
	int buyOrSell;
	int marketOrder;
	int portfolioValue;
	Action action;
	int Notes;
	
	enum Action
	{
	    DO_NOTHING, BUY, SELL;
	}
	public Line(int stockValue, int prevCash, int prevPortfolioControl, int prevMarketOrder, Action prevAction ) {
	
		calculateOtherValues(stockValue, prevCash, prevPortfolioControl, prevMarketOrder, prevAction);
	
	}

	public void calculateOtherValues(int stockValue, int prevCash, int prevPortfolioControl, int prevMarketOrder,
			Action prevAction) {
		this.stockValue = stockValue;
		
		safe=stockValue/10;
		this.cash = prevCash+prevMarketOrder;
		
		if(prevAction==Action.SELL) {
			portfolioControl= prevPortfolioControl;
		}else {
			portfolioControl= prevPortfolioControl + prevMarketOrder/2;
		}
		
		
		buyOrSell=stockValue-portfolioControl;
		
		
	int quantityBuySell= safe-buyOrSell;
	
	int quantityBuySellAbs= Math.abs(quantityBuySell);
	
	if(quantityBuySellAbs>100 && quantityBuySellAbs<safe) {
		marketOrder=quantityBuySellAbs;
	}
	
	if(marketOrder==0) {
		action=Action.DO_NOTHING;
	}else if(quantityBuySell<0) {
		action=Action.BUY;
	}else {
		action=Action.SELL;
	}
	
	portfolioValue=stockValue+cash;
	}
	
	
	public void  printValues(){
		System.out.println(stockValue+"\t" +safe +"\t"+cash 
				+"\t"+portfolioControl+ "\t"+buyOrSell+ "\t"+marketOrder+ "\t"+portfolioValue+ "\t"+action);
	}
	

}

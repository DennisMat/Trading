package com.dennis.aim;


public class BookExample {
	
	
	public static int stockOwned=500;
	public static int cash=5000;

	public static void main(String[] args) {
		
		//Pages 64 to 71.
		// only hard coded for 2 years.

		int[] stockPrice= {8,5,4,5,8,10,8,5,4,5,8,//year 1
				10,8,5,4,5,8,10,8,5,4,5,8,
				10,8,5,4,5,8,10,8,5,4,5,8,
				10,8,5,4,5,8,10,8,5,4,5,8,
				10,8,5,4,5,8,10,8,5,4,5,8,
				10,8,5,4,5,8,10,8,5,4,5,8,
				10,8,5,4,5,8,10,8,5,4,5,8,
				10,8,5,4,5,8 //year 8
				};
		int[] interest= {19,10,2,2,2,17,27,27,15,3,3,24
				,38,38,22,6,6,34,53,53,31,10,10,48};
		
		
		

		Line lineInt = new Line();

		lineInt.stockPrice = 10;
		lineInt.stockValue = 5000;
		lineInt.safe = 500;
		lineInt.cash = cash;
		lineInt.sharesBoughtSold = 0;
		lineInt.stockOwned = stockOwned;
		lineInt.portfolioControl = 5000;
		lineInt.buyOrSellAdvice = 0;
		lineInt.marketOrder = 0;
		lineInt.interest = 22;
		lineInt.portfolioValue = 10000;
		
		
		
		
		
		
		Line.printHeader();
		System.out.println();
		lineInt.printValues();
		System.out.println();
		
		
		Line prevLine=lineInt;
		for(int i=0;i<interest.length;i++) {
		//for(int i=0;i<2;i++) {
			Line l= new Line(stockPrice[i], prevLine.sharesBoughtSold, prevLine.portfolioControl, prevLine.marketOrder,prevLine.action,
					prevLine.interest,interest[i]);
			l.printValues();
			System.out.println();
			prevLine=l;
		}

	}

}

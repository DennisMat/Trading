package com.dennis.aim;

public class Test {

	public static void main(String[] args) {

		double[] stockPrice = { 10, 8, 5, 4, 5, 8, 10, 8, 5, 4, 5, 8, // year 1
				10, 8, 5, 4, 5, 8, 10, 8, 5, 4, 5, 8, 10, 8, 5, 4, 5, 8, 10, 8, 5, 4, 5, 8, 10, 8, 5, 4, 5, 8, 10, 8, 5,
				4, 5, 8, 10, 8, 5, 4, 5, 8, 10, 8, 5, 4, 5, 8, 10, 8, 5, 4, 5, 8, 10, 8, 5, 4, 5, 8, 10, 8, 5, 4, 5, 8,
				10, 8, 5, 4, 5, 8, 10, 8, 5, 4, 5, 8 // year 8
		};
		double[] interest = { 22, 19, 10, 2, 2, 17, 27, 27, 15, 3, 3, 24 };
		// ,38,38,22,6,6,34,53,53,31,10,10,48};
		double[] expectedPortfolioValues = { 10000, 9022, 7316, 6324, 7818, 12296, 14451.5f, 12799, 10300, 8930, 10971,
				17088 };
		double[] expectedCash = { 5000, 5022, 4441, 2316, 358, 360, 3771.5f, 6063, 6090, 3390, 781, 784 };
		double startingAmount = 10000;
		
		Line.isTest=true;
		Line.print=true;
		
		//Line lineFirst = Line.getFirstLine("", stockPrice[0],startingAmount,interest[0]);


//		System.out.println();
		// lineFirst.printValues();
		System.out.println();

		Line prevLine = Line.getFirstLine(null, stockPrice[0],startingAmount,0);
		boolean testsPassed=true;
		for (int i = 0; i < interest.length; i++) {

			Line l = Line.getNewLine(null, prevLine, stockPrice[i], interest[i]);
			 l.printValues();
			// System.out.println();
			if (expectedPortfolioValues[i] != l.portfolioValue || expectedCash[i] != l.cash) {
				System.out.println("TEST FAILED on record " + i);
				testsPassed=false;
				break;
			}
			prevLine = l;
		}
		
		if(testsPassed) {
			System.out.println("TEST PASSED");
		}else {
			System.out.println("TEST FAILED");
		}

	}

}

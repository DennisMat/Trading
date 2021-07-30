package com.dennis.aim;

import com.dennis.aim.Line.Action;

public class Main {

	public static void main(String[] args) {
		
		
		Line line1 = new Line(5000, 5000, 5000, 0, Line.Action.DO_NOTHING);
		
		int[] stockValue= {5500,6000,5600,4400,4560};
		
		line1.printValues();
		Line prevLine=line1;
		for(int i=0;i<stockValue.length;i++) {
			Line l= new Line(stockValue[i], prevLine.cash, prevLine.portfolioControl, prevLine.marketOrder,prevLine.action);
			l.printValues();
			prevLine=l;
		}

	}

}

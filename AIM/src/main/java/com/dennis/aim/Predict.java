package com.dennis.aim;

import java.util.ArrayList;
import java.util.List;

import com.dennis.aim.Line.Action;



public class Predict {

//https://finance.yahoo.com/chart/POU.TO
	public static void main(String[] args) {

		final double currentStockPrice = 8.76f;
		int currentStocksOwned = 1026;
		double currentCash = 10000.8f;//
		double interest = 0;

		double portfolioControl = 10000.05f;

		final double incrementPrice = 0.01f;
		
		Line lastLine= new Line();
		
		lastLine.stockPrice=currentStockPrice;
		lastLine.cash=currentCash;
		lastLine.interest=interest;
		lastLine.portfolioControl=portfolioControl;
		

		Line.predict(lastLine, incrementPrice);

	}

}

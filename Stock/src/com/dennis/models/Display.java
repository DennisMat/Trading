package com.dennis.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.dennis.db.DB;
import com.dennis.models.Line.Action;

public class Display {

	public long parking_id;
	public long property_id;
	public long apartment_id;
	public String parking_spot_number;
	public String notes;

	public Display(long parking_id, long property_id, long apartment_id, String parking_spot_number, String notes) {
		super();
		this.parking_id = parking_id;
		this.property_id = property_id;
		this.apartment_id = apartment_id;
		this.parking_spot_number = parking_spot_number;
		this.notes = notes;
	}

	public static Map getLines(long user_id, long stock_id) {
		Map h = new HashMap();
		List<Line> lines = new ArrayList<Line>();

		Connection conn = DB.getConnection();
		if (!Individual.hasAccess(user_id, stock_id)) {
			return null;// no rights
		}
		try {
			if (conn != null) {

				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM history WHERE user_id= ? AND stock_id=? order by date_trade");
				stmt.setLong(1, user_id);
				stmt.setLong(2, stock_id);
				ResultSet rst = stmt.executeQuery();

				Line prevLine = null;

				while (rst.next()) {

					Line line = null;
					if (prevLine == null) {// this is for the very first line, it's hit only once
						prevLine = Line.getFirstLine(rst.getDouble("stock_price"), rst.getLong("stock_quantity_owned"), rst.getDouble("cash"), rst.getDouble("cash"));
						prevLine.date = rst.getObject("date_trade", LocalDate.class);
						prevLine.action=Action.BUY;

						line = prevLine;

					} else {
						// a very convoluted way of getting interest.
						double interest = rst.getDouble("cash") - prevLine.cash - prevLine.marketOrder;
						prevLine.interest = interest;
						line = Line.getNewLine(rst.getObject("date_trade", LocalDate.class), prevLine, rst.getDouble("stock_price"), interest);
						if(line.sharesBoughtSold>0) {
							line.action=Action.BUY;
						}else {
							line.action=Action.SELL;
						}
						
						prevLine = line;
					}
					
					
					
					lines.add(line);
					// System.out.print("\n");
					// line.printValues();
				}

				Line lastLine = prevLine;
				final double incrementPrice = 0.01f;
				Line bp = Line.findBuyLimit(lastLine, incrementPrice);
				Line sp = Line.findSellLimit(lastLine, incrementPrice);

				if (user_id == 1 && stock_id == 0) {
					test(lines, bp, sp );
				}

				h.put("history", lines);

				prevLine.interest = 0;

				h.put("history", lines);
				h.put("buyPredict", bp);
				h.put("sellPredict", sp);
				h.put("stock_id", stock_id);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}

		return h;
	}

	static void test(List<Line> lines, Line bp, Line sp) {

		double[] interest = { 22, 19, 10, 4, 17, 54, 15, 6 };
		double[] expectedPortfolioValues = { 10000, 9022, 7316, 6324, 12296, 14451.5f, 10300, 8930, 17088 };
		double[] expectedCash = { 5000, 5022, 4441, 2316, 360, 3771.5f, 6090, 3390, 784 };

		final double expectedBuyPrice = 6.190000040456653f;
		long expectedBuyQuantity = 17;

		final double expectedSellPrice = 8.009999999776483f;
		long expectedSellQuantity = -60;
		
		final double minDiff=0.0001f;

		boolean testsPassed = true;
		for (int i = 0; i < interest.length; i++) {
			if (expectedPortfolioValues[i] != lines.get(i).portfolioValue || expectedCash[i] != lines.get(i).cash) {
				System.out.println("TEST FAILED on record " + i);
				testsPassed = false;
				break;
			}

		}

		if (testsPassed) {
			if (Math.abs(expectedBuyPrice - bp.stockPrice)>minDiff 
					|| Math.abs(expectedBuyQuantity - bp.sharesBoughtSold)>minDiff) {
				System.out.println("TEST FAILED. Buy price = " + bp.stockPrice +" Buy quantity = "+ bp.sharesBoughtSold );
				testsPassed = false;
			}

		}

		if (testsPassed) {
			if (Math.abs(expectedSellPrice - sp.stockPrice)>minDiff 
					|| Math.abs(expectedSellQuantity - sp.sharesBoughtSold)>minDiff ){
				System.out.println("TEST FAILED. Sell price = " + sp.stockPrice +" Sell quantity = "+ sp.sharesBoughtSold );
				testsPassed = false;
			}

		}

		if (testsPassed) {
			System.out.println("TEST PASSED");
		} else {
			System.out.println("TEST FAILED");
		}

	}

}

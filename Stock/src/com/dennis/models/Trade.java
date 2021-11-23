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

public class Trade {

	public long trade_id;
	public long user_id;
	public long stock_id;
	public LocalDate date_trade;
	public double cash_added;
	public double stock_price;
	public long stock_quantity_traded;
	public String notes;
	public boolean active;

	public static Map getLines(long user_id) {
		Map<String, Map> h = new HashMap<String, Map>();
		Connection conn = DB.getConnection();

		try {
			if (conn != null) {
				//PreparedStatement stmt = conn.prepareStatement("SELECT * FROM stock WHERE  user_id= ? ");
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM stock WHERE stock_id=1 AND user_id= ? ");
				stmt.setLong(1, user_id);
				ResultSet rst = stmt.executeQuery();

				while (rst.next()) {
					h.put(rst.getString("stock_symbol"), getLines(user_id, rst.getLong("stock_id")));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		return h;
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

				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM trade WHERE active=true AND user_id= ? AND stock_id=? order by date_trade");
				stmt.setLong(1, user_id);
				stmt.setLong(2, stock_id);
				ResultSet rst = stmt.executeQuery();

				Line prevLine = null;

				while (rst.next()) {

					Line line = null;
					if (prevLine == null) {// this is for the very first line, it's hit only once
						double startingCash = rst.getLong("stock_quantity_traded") * rst.getDouble("stock_price");
						prevLine = Line.getFirstLine(rst.getDouble("stock_price"), rst.getLong("stock_quantity_traded"), startingCash, startingCash);
						prevLine.date = rst.getObject("date_trade", LocalDate.class);
						prevLine.action = Action.BUY;
						prevLine.interest = rst.getDouble("cash_added");
						line = prevLine;

					} else {
						line = Line.getNewLine(rst.getObject("date_trade", LocalDate.class), prevLine, rst.getDouble("stock_price"), rst.getDouble("cash_added"));
						// The figure in line.sharesBoughtSold is an advise to buy/sell not the actual figure
						line.sharesBoughtSold = rst.getLong("stock_quantity_traded");
						if (line.sharesBoughtSold == 0) {
							line.action = Action.DO_NOTHING;
						} else if (line.sharesBoughtSold > 0) {
							line.action = Action.BUY;
						} else {
							line.action = Action.SELL;
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
					test(lines, bp, sp);
				}

				//prevLine.interest = 0;
				h.put("stock_id", stock_id);
				h.put("trade", lines);
				h.put("buyPredict", bp);
				h.put("sellPredict", sp);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}

		return h;
	}

	public static long insertUpdateTradeRecord(long user_id, Trade t) {
		long tradeId = 0;
		String insert = "INSERT INTO trade VALUES (DEFAULT,?,?,?,?,?,?,?,?) RETURNING  trade_id";
		String update = "UPDATE  trade set active=false where trade_id=? AND user_id=?  RETURNING  trade_id";

		String stm = null;
		if (t.trade_id == 0) {
			stm = insert;
		} else {
			stm = update;
			tradeId = t.trade_id;
		}

		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmtInsert = conn.prepareStatement(insert);
				stmtInsert.setLong(1, user_id);
				stmtInsert.setLong(2, t.stock_id);
				stmtInsert.setObject(3, t.date_trade);
				stmtInsert.setDouble(4, t.stock_price);
				stmtInsert.setLong(5, t.stock_quantity_traded);
				stmtInsert.setDouble(6, t.cash_added);
				stmtInsert.setString(7, t.notes);
				stmtInsert.setBoolean(8, true);

				ResultSet rst = stmtInsert.executeQuery();

				if (rst.next()) {
					tradeId = rst.getLong(1);
				}

				if (t.trade_id > 0) {
					PreparedStatement stmtUpdate = conn.prepareStatement(update);
					stmtUpdate.setLong(1, t.trade_id);
					stmtUpdate.setLong(2, user_id);
					stmtUpdate.executeQuery();
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		return tradeId;

	}

	static void test(List<Line> lines, Line bp, Line sp) {

		double[] interest = { 22, 19, 10, 2, 2, 17, 27, 27, 15, 3, 3, 24 };
		double[] expectedPortfolioValues = { 10000, 9022, 7316, 6324, 7818, 12296, 14451.5f, 12799, 10300, 8930, 10971, 17088 };
		double[] expectedCash = { 5000, 5022, 4441, 2316, 358, 360, 3771.5f, 6063, 6090, 3390, 781, 784 };

		final double expectedBuyPrice = 6.190000040456653f;
		long expectedBuyQuantity = 17;

		final double expectedSellPrice = 8.009999999776483f;
		long expectedSellQuantity = -60;

		final double minDiff = 0.0001f;

		boolean testsPassed = true;
		for (int i = 0; i < interest.length; i++) {
			if (expectedPortfolioValues[i] != lines.get(i).portfolioValue || expectedCash[i] != lines.get(i).cash) {
				System.out.println("TEST FAILED on record " + i);
				testsPassed = false;
				break;
			}

		}

		if (testsPassed) {
			if (Math.abs(expectedBuyPrice - bp.stockPrice) > minDiff || Math.abs(expectedBuyQuantity - bp.sharesBoughtSold) > minDiff) {
				System.out.println("TEST FAILED. Buy price = " + bp.stockPrice + " Buy quantity = " + bp.sharesBoughtSold);
				testsPassed = false;
			}

		}

		if (testsPassed) {
			if (Math.abs(expectedSellPrice - sp.stockPrice) > minDiff || Math.abs(expectedSellQuantity - sp.sharesBoughtSold) > minDiff) {
				System.out.println("TEST FAILED. Sell price = " + sp.stockPrice + " Sell quantity = " + sp.sharesBoughtSold);
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

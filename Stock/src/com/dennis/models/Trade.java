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
	public double stock_price;
	public long stock_quantity_traded;
	public long stock_owned;
	public double cash;
	public double portfolio_value;
	public double cash_added;
	public String notes;
	public Action action;

	public Trade(long trade_id, long user_id, long stock_id, LocalDate date_trade, double stock_price, long stock_quantity_traded, long stock_owned, double cash, double portfolio_value,
			double cash_added, String notes, Action action) {
		super();
		this.trade_id = trade_id;
		this.user_id = user_id;
		this.stock_id = stock_id;
		this.date_trade = date_trade;
		this.stock_price = stock_price;
		this.stock_quantity_traded = stock_quantity_traded;
		this.stock_owned = stock_owned;
		this.cash = cash;
		this.portfolio_value = portfolio_value;
		this.cash_added = cash_added;
		this.notes = notes;
		this.action = action;
	}

	public static Map getLines(long user_id) {
		Map<String, Map> h = new HashMap<String, Map>();
		Connection conn = DB.getConnection();

		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM stock WHERE user_id= ? AND active=true");
				//PreparedStatement stmt = conn.prepareStatement("SELECT * FROM stock WHERE stock_id in(1) AND user_id= ? ");
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
		return getLines(user_id, stock_id, false);
	}

	public static Map getLines(long user_id, long stock_id, boolean isAdvice) {
		Map h = new HashMap();
		List<Line> lines = new ArrayList<Line>();
		List<Trade> trades = new ArrayList<Trade>();

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

				double lastStockPrice = 0;
				while (rst.next()) {

					Line line = null;
					if (prevLine == null) {// this is for the very first line, it's hit only once
						double startingCash = rst.getLong("stock_quantity_traded") * rst.getDouble("stock_price");
						prevLine = Line.getFirstLine(rst.getDouble("stock_price"), rst.getLong("stock_quantity_traded"), startingCash, startingCash);
						prevLine.date = rst.getObject("date_trade", LocalDate.class);
						prevLine.action = Action.BUY;
						prevLine.interest = rst.getDouble("cash_added");
						prevLine.sharesBoughtSold = rst.getLong("stock_quantity_traded");
						line = prevLine;

					} else {
						line = Line.getNewLine(rst.getObject("date_trade", LocalDate.class), rst.getDouble("stock_price"), rst.getLong("stock_quantity_traded"), rst.getDouble("cash_added"), prevLine,
								true);

						prevLine = line;
					}

					lines.add(line);

					if (line.stockPrice > 0) {
						lastStockPrice = line.stockPrice;
					}
					trades.add(new Trade(rst.getLong("trade_id"), user_id, stock_id, line.date, line.stockPrice, line.sharesBoughtSold, line.stockOwned, line.cash, line.portfolioValue, line.interest,
							rst.getString("notes"), line.action));
				}

				Line lastLine = prevLine;
				final double incrementPrice = 0.01f;

				Line bp =null;
				Line sp =null;
				if (lastLine!=null && lastLine.stockPrice == 0) {
					lastLine.stockPrice = lastStockPrice;
				}
				
				if (lastLine!=null) {
					bp = Line.findBuyLimit(lastLine, incrementPrice);
					sp = Line.findSellLimit(lastLine, incrementPrice);
				}

				if (user_id == 1 && stock_id == 0) {
					test(lines, bp, sp);
				}

				if (isAdvice) {
					h.put("lines", lines);
				} else {
					h.put("stock_id", stock_id);
					h.put("trades", trades);
					h.put("buyPredict", bp);
					h.put("sellPredict", sp);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}

		return h;
	}

	public static Map generateTradeAdvice(long user_id, Trade t) {

		// the portfolio control has a history hence all will have to calculated
		Map m = getLines(user_id, t.stock_id,true);

		List<Line> lines = (List<Line>) m.get("lines");

		Line lastLine = lines.get(lines.size() - 1);

		Line line = Line.getNewLine(null, t.stock_price, 0, 0, lastLine, false);

		Map h = new HashMap();
		h.put("advise", line);

		return h;

	}

	public static long updateTradeRecord(long user_id, Trade t) {

		long tradeId = 0;
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				String update = "UPDATE  trade set active=false where trade_id=? AND user_id=?  RETURNING  trade_id";

				if (t.trade_id > 0) {
					PreparedStatement stmtUpdate = conn.prepareStatement(update);
					stmtUpdate.setLong(1, t.trade_id);
					stmtUpdate.setLong(2, user_id);
					stmtUpdate.executeQuery();
					ResultSet rst = stmtUpdate.executeQuery();
					if (rst.next()) {
						tradeId = rst.getLong(1);
					}
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

	public static long insertUpdateTradeRecord(long user_id, Trade t) {
		long tradeId = 0;
		String insert = "INSERT INTO trade VALUES (DEFAULT,?,?,?,?,?,?,?,?) RETURNING  trade_id";

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
					updateTradeRecord(user_id, t);
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
		double[] expectedPortfolioValues = { 10000, 9022, 7316, 6324, 7818, 12296, 14449f, 12792, 10293, 8923, 10964, 17081 };
		double[] expectedCash = { 5000, 5022, 4441, 2316, 358, 360, 3769f, 6056, 6083, 3383, 774, 777 };

		final double expectedBuyPrice = 6.190000040456653f;
		long expectedBuyQuantity = 17;

		final double expectedSellPrice = 8.009999999776483f;
		long expectedSellQuantity = -60;

		final double minDiff = 0.0001f;

		boolean testsPassed = true;
		for (int i = 0; i < interest.length; i++) {
			if (expectedPortfolioValues[i] != lines.get(i).portfolioValue || expectedCash[i] != lines.get(i).cash) {
				System.out.println("TEST FAILED on record " + i);

				System.out.println("expectedPortfolioValue= " + expectedPortfolioValues[i] + "  obtained=" + lines.get(i).portfolioValue);
				System.out.println("expectedCash= " + expectedCash[i] + "  obtained=" + lines.get(i).cash);

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
		System.out.println("================================================");
		if (testsPassed) {
			System.out.println("TEST PASSED");
		} else {
			System.out.println("TEST FAILED");
		}
		System.out.println("================================================");
	}

}

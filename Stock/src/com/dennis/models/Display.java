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

		// (String date, double startingStockPrice, double startingAmount, double startingInterest)

		/* lines.add(Line.getLine("2021-05-01", 10, 0, 22)); lines.add(Line.getLine("2021-05-02", 8, 0, 19)); lines.add(Line.getLine("2021-05-03", 5, 0, 10)); lines.add(Line.getLine("2021-05-04", 4,
		 * 0, 2)); lines.add(Line.getLine("2021-05-05", 5, 0, 2));
		 * 
		 * lines.add(Line.getLine("2021-05-06", 8, 0, 17)); lines.add(Line.getLine("2021-05-07", 10, 0, 27)); //lines.add(Line.getLine("2021-05-08", 8, 0, 27)); lines.add(Line.getLine("2021-05-09", 5,
		 * 0, 15));
		 * 
		 * lines.add(Line.getLine("2021-05-10", 4, 0, 3)); //lines.add(Line.getLine("2021-05-11", 5, 0, 3)); lines.add(Line.getLine("2021-05-12", 8, 0, 24)); */

		// lines.add(Line.getLine("2021-05-13", 8.01, 0, 0));
		// lines.add(Line.getLine("2021-05-13", 6.43, 0, 0));

		Connection conn = DB.getConnection();
		if (!Individual.hasAccess(1, 1)) {
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
//						prevLine = new Line();
//						prevLine.date=rst.getObject("date_trade", LocalDate.class);
//						prevLine.cash = rst.getDouble("cash");
//						prevLine.stockPrice = rst.getDouble("stock_price");
//						prevLine.stockValue=prevLine.cash;
//						prevLine.portfolioControl = prevLine.cash;
						
						
						
						prevLine=Line.getFirstLine(rst.getDouble("stock_price"),
								rst.getLong("stock_quantity_owned"), 
								rst.getDouble("cash"), rst.getDouble("cash")) ;
						prevLine.date=rst.getObject("date_trade", LocalDate.class);
						
						line = prevLine;

					} else {
						// a very convoluted way of getting interest.
						double interest=rst.getDouble("cash") - prevLine.cash -prevLine.marketOrder;
						prevLine.interest=interest;
						line = Line.getNewLine(rst.getObject("date_trade", LocalDate.class), 
								prevLine, rst.getDouble("stock_price"), 
								interest);
						prevLine = line;
					}
					lines.add(line);
					// System.out.print("\n");
					line.printValues();
				}

				Line lastLine = prevLine;

				h.put("history", lines);

				final double incrementPrice = 0.01f;

				prevLine.interest = 0;

				Line bp = Line.findBuyLimit(lastLine, incrementPrice);
				Line sp = Line.findSellLimit(lastLine, incrementPrice);

				h.put("history", lines);
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

}

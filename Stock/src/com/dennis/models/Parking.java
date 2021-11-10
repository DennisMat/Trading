package com.dennis.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.dennis.db.DB;


public class Parking {

	public long parking_id;
	public long property_id;
	public long apartment_id;
	public String parking_spot_number;
	public String notes;

	public Parking(long parking_id, long property_id, long apartment_id, String parking_spot_number, String notes) {
		super();
		this.parking_id = parking_id;
		this.property_id = property_id;
		this.apartment_id = apartment_id;
		this.parking_spot_number = parking_spot_number;
		this.notes = notes;
	}

	public static Map getLines() {

		List l = new ArrayList();
		Map h = new HashMap();

		// (String date, double startingStockPrice, double startingAmount, double startingInterest)

		
		Line prevLine = Line.getLine("2021-04-30", 10, 10000, 0);
		List<Line> lines = new ArrayList<Line>();

		lines.add(Line.getLine("2021-05-01", 10, 0, 22));
		lines.add(Line.getLine("2021-05-02", 8, 0, 19));
		lines.add(Line.getLine("2021-05-03", 5, 0, 10));
		lines.add(Line.getLine("2021-05-04", 4, 0, 2));
		lines.add(Line.getLine("2021-05-05", 5, 0, 2));

		lines.add(Line.getLine("2021-05-06", 8, 0, 17));
		lines.add(Line.getLine("2021-05-07", 10, 0, 27));
		//lines.add(Line.getLine("2021-05-08", 8, 0, 27));
		lines.add(Line.getLine("2021-05-09", 5, 0, 15));
		
		lines.add(Line.getLine("2021-05-10", 4, 0, 3));
		//lines.add(Line.getLine("2021-05-11", 5, 0, 3));
		lines.add(Line.getLine("2021-05-12", 8, 0, 24));

		
		//lines.add(Line.getLine("2021-05-13", 8.01, 0, 0));
		//lines.add(Line.getLine("2021-05-13", 6.43, 0, 0));
		for (int i = 0; i < lines.size(); i++) {
			Line li = Line.getNewLine(lines.get(i).date, prevLine, lines.get(i).stockPrice, lines.get(i).interest);
			prevLine = li;
			if(i>(lines.size()-5)) {
				l.add(li);
			}
			
			
		}
		
		Line lastLine=prevLine;
		
		h.put("history", l);
		

		final double incrementPrice = 0.01f;
		
		prevLine.interest=0;
		
		Line bp=Line.findBuyLimit(lastLine, incrementPrice);

		Line sp= Line.findSellLimit(lastLine, incrementPrice);
		
		h.put("history", l);
		h.put("buyPredict", bp);
		h.put("sellPredict", sp);
		
		
		return h;
	}



}

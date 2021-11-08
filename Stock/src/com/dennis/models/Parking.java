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
import com.dennis.models.Line.Action;

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
	
	public static List getLines() {
		
		List l = new ArrayList();
		Map  h= new HashMap();
		
		//(String date, double startingStockPrice, double startingAmount, double startingInterest)
	
		
		Line line1 = Line.getLine("2021-05-01",10,0, 22);
		//line1.portfolioControl=5000;
		Line line2 = Line.getLine("2021-05-02",8,  0, 19);
		Line line3 = Line.getLine("2021-05-03",5,  0, 10);
		Line line4 = Line.getLine("2021-05-04",5,  0, 2);
		Line line5 = Line.getLine("2021-05-05",8,  0, 17);

		
		List<Line> lines = new ArrayList<Line>();
		lines.add(line1);
		lines.add(line2);
		lines.add(line3);
		lines.add(line4);
		lines.add(line5);
	
		Line prevLine = Line.getLine("2021-04-30",10,10000, 0);;

		for (int i = 0; i < lines.size(); i++) {
			//String date, Line prevLine, double stockPrice, double interest
			//String date, long prevStocksOwned, double prevCash, double stockPrice, double prevSharesBoughtSold, d
			//ouble prevPortfolioControl, double prevMarketOrder, Action prevAction,
			//double prevInterest, double interest
			
			//Line li = new Line(lines.get(i).date,lines.get(i).cash, lines.get(i).stockPrice, lines.get(i).sharesBoughtSold);
			
			Line li = Line.getNewLine(lines.get(i).date, prevLine, lines.get(i).stockPrice, lines.get(i).interest);
			
			prevLine = li;
			 //li.printValues();
			// System.out.println();
			
			 	l.add(li);
		}
		
		
		return l;
	}
	
	public static List getParkings(long  userId, long  property_id, long apartment_id, boolean unAllocated) {

		if (!Property.hasPropertyInformationAccess(userId, property_id)) {
			return null;// no rights
		}
		List l = new ArrayList();
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				
				String aptQuery="";
				if(apartment_id>0) {
					aptQuery= " AND parking.apartment_id=?";
				}else if (unAllocated){
					aptQuery= " AND parking.apartment_id is NULL ";
				}
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM parking left join apartment ON parking.apartment_id= apartment.apartment_id WHERE parking.property_id=? " + aptQuery );
				
				stmt.setLong (1, property_id);
				if(apartment_id>0) {
					stmt.setLong (2, apartment_id);
				}
				ResultSet rst = stmt.executeQuery();
				while (rst.next()) {
					
					Map  h= new HashMap();
					Apartment a = Apartment.constructObject(rst);
					h.put("apartment", a);
					
					Parking p= new Parking(rst.getLong ("parking_id"), rst.getLong ("property_id"), 
							rst.getLong("apartment_id"),
							rst.getString("parking_spot_number"),rst.getString("notes"))	;			
					h.put("parking", p);
			
					l.add(h);
	
					
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		return l;
	}
	
	
	public static long  insertUpdateParkingRecord(long user_id, Parking p) {

		if(!Property.hasPropertyInformationAccess(user_id,p.property_id)) {
			return 0;
		}

		String activitylog="";
		long  parkingId = 0;
		String insert = "INSERT into parking (apartment_id,parking_spot_number,notes, property_id) "
				+ "values(?,?,?,?)  RETURNING  parking_id";
		String update = "UPDATE  parking set apartment_id=?,parking_spot_number=?,notes=? WHERE"
				+ " property_id=? AND parking_id=? RETURNING  parking_id";

		String stm = null;
		if (p.parking_id == 0) {
			stm = insert;
			activitylog="parking_spot_number "+p.parking_spot_number+" created";
		} else {
			stm = update;
		}

		Connection conn = DB.getConnection();
		try {

			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement(stm);
				if(p.apartment_id==0) {
					stmt.setNull (1, Types.BIGINT);
					if(p.parking_id != 0){
						activitylog="parking_spot_number "+p.parking_spot_number+" allocated to apartment_id " +p.apartment_id;
						}
				}else {
					stmt.setLong (1, p.apartment_id); 
					activitylog="parking_spot_number "+p.parking_spot_number+" allocated to apartment_id " +p.apartment_id;
				}
				stmt.setString(2, p.parking_spot_number);
				stmt.setString(3, p.notes);
				stmt.setLong (4, p.property_id);
				if (p.parking_id > 0) {
					stmt.setLong (5, p.parking_id);
				}

				ResultSet rst = stmt.executeQuery();

				if (rst.next()) {
					parkingId = rst.getLong (1);
				}
		
				History.insertHistory(user_id, activitylog);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		return parkingId;

	}

	
	public static boolean  updateParkingRecords(long user_id, Parking[] pa) {
		
		boolean success=false;
		long property_id=pa[0].property_id;
		long apartment_id=pa[0].apartment_id;

		if(!Property.hasPropertyInformationAccess(user_id,property_id)) {
			return success;
		}
		
		StringBuffer inClause=new StringBuffer();
		for(Parking p:pa) {
			inClause.append("?,");
		}
		
		inClause.delete(inClause.length()-1, inClause.length());

		String stm = "UPDATE  parking set apartment_id=? WHERE property_id=? AND parking_id IN ("+inClause+")";


		Connection conn = DB.getConnection();
		try {

			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement(stm);
				int parmIndex=1;
				stmt.setLong (parmIndex++, apartment_id);
				stmt.setLong(parmIndex++, property_id);
		
				for(Parking p:pa) {
					stmt.setLong(parmIndex++, p.parking_id);
				}
				stmt.executeUpdate();
				success=true;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		return success;

	}
	
	public static boolean  removeParkingRecords(long user_id, Parking p) {
		boolean success=false;

		if(!Property.hasPropertyInformationAccess(user_id,p.property_id)) {
			return success;
		}


		String stm = "DELETE FROM  parking WHERE property_id=? AND parking_id =?";


		Connection conn = DB.getConnection();
		try {

			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement(stm);
				int parmIndex=1;
				stmt.setLong (parmIndex++, p.property_id);
				stmt.setLong(parmIndex++, p.parking_id);
				stmt.executeUpdate();
				success=true;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		return success;

	}


}

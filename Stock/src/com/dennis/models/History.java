package com.dennis.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.dennis.db.DB;

public class History {
	
	
	public long history_id;
	public long individual_id;
	public OffsetDateTime  date_time;
	public String notes;
	
	
	public History(long history_id, long individual_id, OffsetDateTime date_time, String notes) {
		super();
		this.history_id = history_id;
		this.individual_id = individual_id;
		this.date_time = date_time;
		this.notes = notes;
	}
	
	
	public static List<History> getHistory(long  user_id) {

		List<History> l = new ArrayList<History>();
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				String properties = "select property_id from property_access_individual where individual_id=?";
				String employeesOfProperties = "select individual_id from property_access_individual where property_id in("
						+ properties + ")";
				String history = "select * from history where individual_id in (" + employeesOfProperties + ")";
				PreparedStatement stmt = conn.prepareStatement(history);
				stmt.setLong (1, user_id);
				ResultSet rst = stmt.executeQuery();

				while (rst.next()) {
					l.add(new History(rst.getLong ("history_id"), rst.getLong ("individual_id"), 
							rst.getObject("date_time", OffsetDateTime.class),rst.getString("notes")));
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
	
	
	
	public static void insertHistory(long  user_id, String message) {
		
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement("INSERT INTO history(history_id, individual_id, date_time, notes) VALUES (DEFAULT, ?, DEFAULT, ?)");
				stmt.setLong (1, user_id);
				stmt.setString (2, message);
				stmt.executeUpdate();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		
	}
	
		
		

}

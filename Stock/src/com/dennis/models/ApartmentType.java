package com.dennis.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.dennis.db.DB;

public class ApartmentType {
	
	
	public long apartment_type_id;
	public String abbreviation;
	public String description;
	
	
	
	public ApartmentType(long  apartment_type_id, String abbreviation, String description) {
		super();
		this.apartment_type_id = apartment_type_id;
		this.abbreviation = abbreviation;
		this.description = description;
	}

	

	public static ApartmentType getApartmentType(long  apartment_type_id) {

		ApartmentType t = null;
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn
						.prepareStatement("SELECT * FROM apartment_type WHERE apartment_type_id=?");
				stmt.setLong (1, apartment_type_id);
				ResultSet rst = stmt.executeQuery();
				
				while (rst.next()) {
					t = new ApartmentType(rst.getLong ("apartment_type_id"), rst.getString("description"),
							rst.getString("abbreviation"));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		return t;
	}
	

	

	
	

}

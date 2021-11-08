package com.dennis.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.dennis.db.DB;

public class Lease {

	public long lease_id;
	public long apartment_id;
	public LocalDate lease_start_date;
	public LocalDate lease_end_date;
	public String lease_url;
	public String notes;

	public Lease(long lease_id, long apartment_id, LocalDate lease_start_date, LocalDate lease_end_date,
			String lease_url, String notes) {
		super();
		this.lease_id = lease_id;
		this.apartment_id = apartment_id;
		this.lease_start_date = lease_start_date;
		this.lease_end_date = lease_end_date;
		this.lease_url = lease_url;
		this.notes = notes;
	}

	public static List<Lease> getExpiringLeases(long userId, int days) {
		Property.getProperties(userId);

		List<Lease> l = new ArrayList<Lease>();
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement(
						"select * from lease where  (lease_end_date::DATE - CURRENT_DATE)< ? AND (lease_end_date>CURRENT_DATE) "
								+ " AND apartment_id in(select apartment_id from apartment "
								+ " WHERE property_id in (select property_id from property_access_individual where individual_id=? ))");
				stmt.setInt(1, days);
				stmt.setLong(2, userId);
				ResultSet rst = stmt.executeQuery();
				while (rst.next()) {
					l.add(new Lease(rst.getLong("lease_id"), rst.getLong("apartment_id"),
							rst.getObject("lease_start_date", LocalDate.class),
							rst.getObject("lease_end_date", LocalDate.class), rst.getString("lease_url"),
							rst.getString("notes")));

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

	public static List<Lease> getLeases(long userId, long apartment_id) {

		if (!Apartment.hasApartmentInformationAccess(userId, apartment_id)) {
			return null;// no rights
		}
		List<Lease> l = new ArrayList<Lease>();
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM lease WHERE apartment_id=? ");
				stmt.setLong(1, apartment_id);
				ResultSet rst = stmt.executeQuery();
				while (rst.next()) {
					l.add(new Lease(rst.getLong("lease_id"), rst.getLong("apartment_id"),
							rst.getObject("lease_start_date", LocalDate.class),
							rst.getObject("lease_end_date", LocalDate.class), rst.getString("lease_url"),
							rst.getString("notes")));
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

}

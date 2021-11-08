package com.dennis.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dennis.db.DB;

public class Rent {

	public long rent_id;
	public long apartment_id;
	public String amount;
	public String currency;
	public LocalDate date_paid;
	public long paid_by_individual_id;
	public String notes;

	public Rent(long rent_id, long apartment_id, String amount, String currency, LocalDate date_paid,
			long paid_by_individual_id, String notes) {
		super();
		this.rent_id = rent_id;
		this.apartment_id = apartment_id;
		this.amount = amount;
		this.currency = currency;
		this.date_paid = date_paid;
		this.paid_by_individual_id = paid_by_individual_id;
		this.notes = notes;
	}

	public static long insertRentHistory(long userId, Rent r) {

		if (!Apartment.hasApartmentInformationAccess(userId, r.apartment_id)) {
			return 0;// no rights
		}
		long rent_id = 0;
		String insertRent = "INSERT INTO rent(apartment_id, paid_by_individual_id, amount, currency, date_paid, notes) "
				+ "	VALUES ( ?, ?, ?, ?, ?, ?)  RETURNING  rent_id";

		Connection conn = DB.getConnection();
		try {
			PreparedStatement stmt = conn.prepareStatement(insertRent);

			stmt.setLong(1, r.apartment_id);
			stmt.setLong(2, r.paid_by_individual_id);
			stmt.setString(3, r.amount);
			stmt.setString(4, r.currency);
			stmt.setObject(5, LocalDate.now());
			stmt.setString(6, r.notes);

			ResultSet rst = stmt.executeQuery();
			if (rst.next()) {
				rent_id = rst.getLong(1);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}

		return rent_id;
	}

	public static List getRentHistory(long user_id, long apartment_id) {

		List l = new ArrayList();

		List<Rent> rh = new ArrayList<Rent>();
		Connection conn = DB.getConnection();
		if (!Apartment.hasApartmentInformationAccess(user_id, apartment_id)) {
			return null;// no rights
		}
		try {
			PreparedStatement stmtRent = conn.prepareStatement("select * from rent  LEFT JOIN  individual ON "
					+ "rent.paid_by_individual_id=individual.individual_id "
					+ "WHERE rent.apartment_id=? ORDER BY rent.date_paid DESC");

			stmtRent.setLong(1, apartment_id);
			ResultSet rstRent = stmtRent.executeQuery();
			while (rstRent.next()) {
				Map h = new HashMap();

				h.put("rent",
						new Rent(rstRent.getLong("rent_id"), rstRent.getLong("apartment_id"),
								rstRent.getString("amount"), rstRent.getString("currency"),
								rstRent.getObject("date_paid", LocalDate.class),
								rstRent.getLong("paid_by_individual_id"), rstRent.getString("notes")));

				h.put("individual",
						new Individual(rstRent.getLong("paid_by_individual_id"), rstRent.getString("first_name"),
								rstRent.getString("last_name"), rstRent.getString("last_name"),
								rstRent.getString("phone")));
				l.add(h);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}

		return l;
	}

}

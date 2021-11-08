package com.dennis.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.dennis.db.DB;

public class Address {
	public long address_id;
	public String address_line1;
	public String address_line2;
	public String city;
	public String state_province;
	public String postal_code;
public Address(long address_id, String address_line1, String address_line2, String city, String state_province,
		String postal_code) {
	super();
	this.address_id = address_id;
	this.address_line1 = address_line1;
	this.address_line2 = address_line2;
	this.city = city;
	this.state_province = state_province;
	this.postal_code = postal_code;
}
   
   

public static long  insertUpdateAddressRecord(Address a) {
	long  addressId = 0;
	String insert = "INSERT INTO address VALUES (DEFAULT,?,?,?,?,?) RETURNING  address_id";
	String update = "UPDATE  address set address_line1=?,address_line2=?,city=?, state_province=?, postal_code=? where address_id=?   RETURNING  address_id";

	String stm = null;
	if (a.address_id == 0) {
		stm = insert;
	} else {
		stm = update;
		addressId = a.address_id;
	}

	Connection conn = DB.getConnection();
	try {
		if (conn != null) {
			PreparedStatement stmt = conn.prepareStatement(stm);
			stmt.setString(1, a.address_line1);
			stmt.setString(2, a.address_line2);
			stmt.setString(3, a.city);
			stmt.setString(4, a.state_province);
			stmt.setString(5, a.postal_code);
			if (addressId > 0) {
				stmt.setLong (6, addressId);
			}

			ResultSet rst = stmt.executeQuery();

			if (rst.next()) {
				addressId = rst.getLong (1);
			}

		}

	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} finally {
		DB.closeConnection(conn);
	}
	return addressId;

}








/*
public static Address getAddress(long  address_id) {
	Address a = null;
	Connection conn = DB.getConnection();
	try {
		if (conn != null) {
			PreparedStatement stmt = conn.prepareStatement("select * from address WHERE id=? ");
			stmt.setLong (1, address_id);
			ResultSet rst = stmt.executeQuery();
			if (rst.next()) {
				// System.out.println("row found");
				a = new Address(rst.getLong ("id"), rst.getString("address_line1"), rst.getString("street_address"),
						rst.getString("city"), rst.getString("state_province"), rst.getString("postal_code"));
			}
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} finally {
		DB.closeConnection(conn);
	}
	return a;
}

*/




	
}

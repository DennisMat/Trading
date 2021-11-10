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

import com.dennis.Auth.Auth;
import com.dennis.db.DB;
import com.dennis.util.Util;

public class Individual {
	
    public long individual_id;
    
    public String first_name;
    public String last_name;
    
    public String email;
    public String phone;
    
    public String user_name;
    public String password;
    
	public Individual(long individual_id, String first_name, String last_name, String email, String phone) {
		super();
		this.individual_id = individual_id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.email = email;
		this.phone = phone;
	}
    
    

	 
	public static List<Individual> getFellowEmployees(long  userId) {
		List l = new ArrayList();
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {

				String properties = "select property_id from property_access_individual where individual_id=?";
				String employeesOfProperties = "select individual_id from property_access_individual where property_id in("
						+ properties + ")";
				String employees = "select * from individual where individual_id in (" + employeesOfProperties + ")";
				PreparedStatement stmt = conn.prepareStatement(employees);
				stmt.setLong (1, userId);
				ResultSet rst = stmt.executeQuery();
				while (rst.next()) {
					// System.out.println("row found");
					Map  h= new HashMap();
					h.put("individual",new Individual(rst.getLong ("individual_id"), rst.getString("first_name"), rst.getString("last_name"),
							rst.getString("email"), rst.getString("phone")));
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




	public static boolean verifyUser(String  user_name,String user_password) {
		
		boolean isPasswordCorrect=false;
		
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM individual where user_name=?");
				stmt.setString (1, user_name);
				ResultSet rst = stmt.executeQuery();
				while (rst.next()) {
					String saltAndHash=rst.getString("password");
					isPasswordCorrect=Auth.verifyPassword(user_password,saltAndHash);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		
		return isPasswordCorrect;
	}

	public static long insertUpdateUserRecord(long  userId, Individual individual) {


		return individual.individual_id;

	}
	


	static void createNewUserRights(long  userId, long  newUserId) {
//		List<Map> l=Property.getProperties(userId);		 
//		for (Map  m: l) {
//			insertOrUpdateUserRights(newUserId, ((Property)m.get("property")).property_id);
//		}
	}

	public static boolean insertOrUpdateUserRights(long  userId, long  propertyId) {

		boolean recordInserted = false;
		Connection conn = DB.getConnection();
		String insert = "INSERT into property_access_individual VALUES(?,?) ";
		// String update="UPDATE property_users set property_id=?, user_id=? WHERE";
		try {

			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement(insert);
				stmt.setLong (1, propertyId);
				stmt.setLong (2, userId);
				stmt.executeUpdate();
				recordInserted = true;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}

		return recordInserted;
	}

	
 
   
}

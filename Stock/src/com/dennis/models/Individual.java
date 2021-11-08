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

	public static List getOccupants(long  userId) {
		
		//List<Map> l=Property.getProperties(userId);
		List all=new ArrayList();
		
		List<Map>  occList=getOccupants(userId,0, false);
		

		return occList;
	}

	public static List getOccupants(long  userId, long apartment_id) {
		return getOccupants(userId, apartment_id, false);

	}

	/*
	 * Get all Occupants that a user has access to, if the apartment is mentioned
	 * then only get occupants of that apartment.
	 */

	public static List getOccupants(long  user_id, long apartment_id, boolean allPastAndPresentOccupants) {

		
		if(apartment_id!=0 && !Apartment.hasApartmentInformationAccess(user_id,apartment_id)) {
			return null;
		}
		List l = new ArrayList();
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {

				String query = "SELECT * from apartment_occupant,individual, apartment, property " + 
						"WHERE  individual.individual_id=apartment_occupant.individual_id " + 
						"AND apartment_occupant.apartment_id=apartment.apartment_id " + 
						"AND apartment.property_id=property.property_id " + 
						"AND  individual.individual_id=apartment_occupant.individual_id ";
					
				if (apartment_id>0) { // fetch only the occupant of a single apartment
					query += " AND apartment_occupant.apartment_id=? ";
				}else { //fetch occupants of all properties.
					query += " AND apartment_occupant.apartment_id in( " + 
							" SELECT apartment_id from apartment where property_id in " + 
							"(SELECT property_id from property_access_individual where individual_id=?)) ";
				}
				if (!allPastAndPresentOccupants) {
					query += " AND (apartment_occupant.date_to IS NULL OR apartment_occupant.date_to>CURRENT_DATE) ";
				}

				PreparedStatement stmt = conn.prepareStatement(query);
				
				if (apartment_id==0) {
					stmt.setLong (1, user_id);
				}else {
					stmt.setLong (1, apartment_id);
				}
				
				ResultSet rst = stmt.executeQuery();
				while (rst.next()) {
					Map  h= new HashMap();
					h.put("apartmentoccupant", new ApartmentOccupant( rst.getLong("apartment_id"), rst.getLong ("individual_id"),
							rst.getObject("date_from", LocalDate.class), rst.getObject("date_to", LocalDate.class)));
					h.put("individual", new Individual(rst.getLong ("individual_id"),rst.getString("first_name")
				    		,rst.getString("last_name"),rst.getString("last_name"),rst.getString("phone")));
					
					
					h.put("property", Property.constructObject(rst));
					
					h.put("apartment", Apartment.constructObject(rst));
					
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

	public static long insertUpdateUserRecord(long  userId, ApartmentOccupant apartmentOccupant,Individual individual) {

		if(userId!=0 && apartmentOccupant!=null) {
			if(!Apartment.hasApartmentInformationAccess(userId,apartmentOccupant.apartment_id)) {
				return 0;
			}
		}
		
		


		long  individualId = individual.individual_id;
		String insert = "INSERT into individual (first_name,last_name,email,phone,user_name,password) values(?,?,?,?,?,?)  RETURNING  individual_id";
		String update = "UPDATE  individual set first_name=?,last_name=?,email=?,phone=?,user_name=?,password=? where individual_id=?   RETURNING  individual_id";
		
		String insertApOcc = "INSERT into apartment_occupant (date_from,date_to,individual_id,apartment_id) values(?,?,?,?)";
		String updateApOcc = "UPDATE apartment_occupant SET date_from=?,date_to=? WHERE individual_id=? AND apartment_id=?";
		
		String stm = null;
		String stmAptOcc = null;
		if (individualId==0) {
			stm = insert;
			stmAptOcc=insertApOcc;
		} else {
			stm = update;
			stmAptOcc =updateApOcc;
			
		}
		
		

		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement(stm);
				stmt.setString(1, individual.first_name);
				stmt.setString(2, individual.last_name);
				stmt.setString(3, individual.email);
				stmt.setString(4, individual.phone);
				stmt.setString(5, individual.user_name);
				if(userId==0 && individual.user_name.length()>0 && individual.password.length()>0) { //new user
					stmt.setString(6, Auth.generateSaltAndHashPassword(individual.password));
				}else {
					stmt.setNull(6, Types.CHAR);
				}
				

				if (individualId > 0) {
					stmt.setLong (7, individualId);
				}

				ResultSet rst = stmt.executeQuery();

				if (rst.next()) {
					individualId = rst.getLong (1);
				}
				if(!Util.stringIsNullOrEmpty(individual.user_name)) {
					createNewUserRights(userId, individualId);
				}
				
				
				if(apartmentOccupant!=null) {
					PreparedStatement stmtAptOcc = conn.prepareStatement(stmAptOcc);

					if(apartmentOccupant.date_from==null) {
						stmtAptOcc.setNull(1,Types.DATE );
					}else {
						stmtAptOcc.setObject(1,apartmentOccupant.date_from );
					}
					
					if(apartmentOccupant.date_from==null) {
						stmtAptOcc.setNull(2,Types.DATE );
					}else {
						stmtAptOcc.setObject(2,apartmentOccupant.date_to );
					}
					
					stmtAptOcc.setObject(2,apartmentOccupant.date_to );
					stmtAptOcc.setLong (3, individualId);
					stmtAptOcc.setLong(4, apartmentOccupant.apartment_id);
				
					stmtAptOcc.executeUpdate();
					
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		return individualId;

	}
	


	static void createNewUserRights(long  userId, long  newUserId) {
		List<Map> l=Property.getProperties(userId);		 
		for (Map  m: l) {
			insertOrUpdateUserRights(newUserId, ((Property)m.get("property")).property_id);
		}
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

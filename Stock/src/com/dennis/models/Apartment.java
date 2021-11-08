package com.dennis.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dennis.db.DB;

public class Apartment {
	
	public long apartment_id;
	public long property_id;
	public String apartment_number;
	public long apartment_type_id;
	public boolean active;
	public long floor_plan_id;
	public String notes;
	
	public Apartment(long apartment_id, long property_id, String apartment_number, long apartment_type_id,
			boolean active, long floor_plan_id) {
		super();
		this.apartment_id = apartment_id;
		this.property_id = property_id;
		this.apartment_number = apartment_number;
		this.apartment_type_id = apartment_type_id;
		this.active = active;
		this.floor_plan_id = floor_plan_id;
	}

	
	public static long  changeActiveStatus(long user_id, Apartment a) {

		if (a.property_id > 0) {
			if (!Property.hasPropertyInformationAccess(user_id, a.property_id)) {
				return 0;// no rights
			}
		} else {
			return 0;// property not specified.
		}
		
		long  apartment_id = 0;
	String update = "UPDATE  apartment set active=? WHERE apartment_id=?   RETURNING  apartment_id";

	Connection conn = DB.getConnection();
	try {

		if (conn != null) {
			PreparedStatement stmt = conn.prepareStatement(update);
			stmt.setBoolean(1, a.active);
			stmt.setLong(2, a.apartment_id);
			ResultSet rst = stmt.executeQuery();
			if (rst.next()) {
				apartment_id = rst.getLong (1);
			}
		}

	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} finally {
		DB.closeConnection(conn);
	}
	return apartment_id;
		
	}

	public static boolean  insertUpdateApartmentRecord(long  userId,  Apartment a) {

		Connection conn = DB.getConnection();
		boolean isSuccess=false;
		try {
			if (a.property_id > 0) {
				if (!Property.hasPropertyInformationAccess(userId, a.property_id)) {
					return false;// no rights
				}
			} else {
				return false;// property not specified.
			}
			
			String floorPlanQuery="";
			String floorPlanParam="";
			if(a.floor_plan_id!=0) {
				floorPlanQuery=", floor_plan_id";
				floorPlanParam=",?";
				
			}
			String floorPlanQuery1="";
			if(a.floor_plan_id!=0) {
				floorPlanQuery1=", floor_plan_id=?";
			}

			
			String query="";
			if(a.apartment_id==0){
				query = "INSERT INTO apartment (apartment_number, apartment_type_id"+floorPlanQuery+",property_id) values(?, ? "+floorPlanParam+",?)";
			}else {
				query="UPDATE  apartment set apartment_number=?, apartment_type_id=? "+floorPlanQuery1+" WHERE property_id=?  AND apartment_id=?";
			}

			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement(query);
				int parmIndex=1;
				stmt.setString(parmIndex++, a.apartment_number);			
				stmt.setLong (parmIndex++, a.apartment_type_id);	
				if(a.floor_plan_id!=0){
					stmt.setLong (parmIndex++, a.floor_plan_id);
				}
				stmt.setLong (parmIndex++, a.property_id);				
				if(a.apartment_id!=0){
					stmt.setLong(parmIndex++, a.apartment_id);//where clause in update
				}				
				stmt.executeUpdate();
				isSuccess=true;

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		return isSuccess;

	}
	
	static boolean hasApartmentInformationAccess(long  userId, long  apartment_id) {
		boolean hasAccess = false;
		long property_id = getPropertyIdOfApartment(userId,apartment_id);
		if(property_id>0) {
			hasAccess= Property.hasPropertyInformationAccess( userId,  property_id);
		}
		return hasAccess;
	}
	
	static long getPropertyIdOfApartment(long  userId, long  apartment_id) {
		Connection conn = DB.getConnection();	
		long property_id=0;
		try {
					
			PreparedStatement stmtProperty = conn.prepareStatement("select property_id from apartment WHERE apartment_id=?");
			stmtProperty.setLong (1, apartment_id);
			
			ResultSet rstProp = stmtProperty.executeQuery();

			if (rstProp.next()) {
				property_id = rstProp.getLong("property_id");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}

		return property_id;
	
	}

	


	public static Map getApartmentDetails(long  userId, long apartment_id, boolean details) {
		Map h = new HashMap();
		Connection conn = DB.getConnection();
		if (!Apartment.hasApartmentInformationAccess(userId, apartment_id)) {
			return null;// no rights
		}
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement("select * from apartment WHERE apartment_id=?");
				stmt.setLong(1, apartment_id);
				ResultSet rst = stmt.executeQuery();
				long property_id=getPropertyIdOfApartment(userId,apartment_id);

				List<FloorPlan> fl = FloorPlan.getFloorPlans(userId,property_id );
				if (rst.next()) {
					
					Apartment a = constructObject(rst);

					h.put("apartment", a);
					h.put("apartmenttype", ApartmentType.getApartmentType(a.apartment_type_id));
					h.put("floorplan", FloorPlan.getFloorPlan(a.floor_plan_id,fl));	
					h.put("floorplans",FloorPlan.getFloorPlans(userId, property_id));
					h.put("property", Property.getProperty(userId, property_id));
					
					if(details==true) {
						h.put("occupants", Individual.getOccupants(userId, apartment_id));
						h.put("renthistory", Rent.getRentHistory(userId, apartment_id));
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		return h;
	}


	static Apartment constructObject(ResultSet rst) throws SQLException {
		Apartment a=new Apartment(rst.getLong ("apartment_id"),rst.getLong ("property_id"), rst.getString("apartment_number"), rst.getLong ("apartment_type_id"), rst.getBoolean("active"), rst.getLong ("floor_plan_id"));
		return a;
	}
	
	
	/*
	 * Get  apartment of a single property
	 */
	public static List getApartments(long  userId, long  propertyId) {
		if (!Property.hasPropertyInformationAccess(userId, propertyId)) {
			return null;// no rights
		}
		List l= new ArrayList();

		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement("select * from apartment WHERE property_id=?");
				stmt.setLong (1, propertyId);
				ResultSet rst = stmt.executeQuery();
				List<FloorPlan> fl = FloorPlan.getFloorPlans(userId, propertyId);
				while (rst.next()) {
					Apartment a = constructObject(rst);
					Map  h= new HashMap();
					h.put("apartment", a);
					h.put("apartmenttype", ApartmentType.getApartmentType(a.apartment_type_id));
					h.put("floorplan", FloorPlan.getFloorPlan(a.floor_plan_id,fl));
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

}

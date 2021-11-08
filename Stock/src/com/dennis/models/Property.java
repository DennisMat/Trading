package com.dennis.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dennis.db.DB;

public class Property {

	public long property_id;
	public String property_name;
	public long address_id;
	public boolean active;
	public String notes;

	public Property(long property_id, String property_name, long address_id, boolean active, String notes) {
		super();
		this.property_id = property_id;
		this.property_name = property_name;
		this.address_id = address_id;
		this.active = active;
		this.notes = notes;
	}

	public static Property getProperty(long  user_id, long property_id) {
		if(!hasPropertyInformationAccess(user_id,property_id)) {
			return null;
		}
		Property p= null;
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement("select * from property where property_id=?");
				stmt.setLong (1, property_id);
				ResultSet rst = stmt.executeQuery();
				if (rst.next()) {				
					p = constructObject(rst);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		return p;
	}

	static Property constructObject(ResultSet rst) throws SQLException {
		Property p;
		p=new Property(rst.getLong ("property_id"),rst.getString ("property_name"),rst.getLong ("address_id"),  rst.getBoolean("active"), rst.getString ("notes"));
		return p;
	}

	/**
	 * Get properties that the individual has access to
	 * 
	 * @param user_id
	 * @return
	 */
	public static List getProperties(long  user_id) {

		List<PropertyAccessIndividual> pa = new ArrayList<PropertyAccessIndividual>();
		List l= new ArrayList();
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement("select * from property_access_individual where individual_id=?");
				stmt.setLong (1, user_id);
				ResultSet rst = stmt.executeQuery();

				StringBuffer sbParam = new StringBuffer();
				sbParam.append( " (" );
				int count=0;
				while (rst.next()) {				
					sbParam.append("?, ");
					pa.add(new PropertyAccessIndividual(rst.getLong ("property_id"),rst.getLong ("individual_id")));
					count++;		
				}

				sbParam.delete(sbParam.length()-2, sbParam.length());

				sbParam.append(")");




				PreparedStatement stmtP = conn.prepareStatement("select * from property left join  address on property.address_id=address.address_id where property.property_id in " +sbParam.toString());
				for(int i=0;i<count;i++) {
					stmtP.setLong (i+1, pa.get(i).property_id);
				}

				ResultSet rstP = stmtP.executeQuery();
				ResultSetMetaData rsmd = rstP.getMetaData();


				while (rstP.next()) {
					Property pr= constructObject(rstP);
					Address ad=new Address(rstP.getLong ("address_id"), rstP.getString("address_line1"), rstP.getString("address_line2"), rstP.getString("city"), rstP.getString("state_province"), rstP.getString("postal_code"));

					Map  h= new HashMap();
					h.put("property", pr);
					h.put("address", ad);
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


	public static long  changeActiveStatus(long user_id, Property p, Address a) {

		if(p.property_id!=0 && !hasPropertyInformationAccess(user_id,p.property_id)) {
			return 0;
		}

		
		long  propertyId = 0;
	String update = "UPDATE  property set active=? WHERE property_id=?   RETURNING  property_id";

	Connection conn = DB.getConnection();
	try {

		if (conn != null) {
			PreparedStatement stmt = conn.prepareStatement(update);
			stmt.setBoolean(1, p.active);
			stmt.setLong(2, p.property_id);
			ResultSet rst = stmt.executeQuery();
			
			if (rst.next()) {
				propertyId = rst.getLong (1);
			}
		}

	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} finally {
		DB.closeConnection(conn);
	}
	return propertyId;
		
	}

	public static long  insertUpdatePropertyRecord(long user_id, Property p, Address a) {

		if(p.property_id!=0 && !hasPropertyInformationAccess(user_id,p.property_id)) {
			return 0;
		}

		long  propertyId = 0;
		String insert = "INSERT into property (property_name,address_id,notes) values(?,?,?)  RETURNING  property_id";
		String update = "UPDATE  property set property_name=?,address_id=?,notes=? WHERE property_id=?   RETURNING  property_id";

		String stm = null;
		if (p.property_id == 0) {
			stm = insert;
		} else {
			stm = update;
			propertyId = p.property_id;
		}

		
		long  addressId = Address.insertUpdateAddressRecord(a);

		if (addressId < 1) {
			return 0;
		}
		

		Connection conn = DB.getConnection();
		try {

			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement(stm);
				stmt.setString(1, p.property_name);
				stmt.setLong (2, addressId);
				stmt.setString(3, p.notes);
				if (propertyId > 0) {
					stmt.setLong (4, propertyId);
				}

				ResultSet rst = stmt.executeQuery();

				if (rst.next()) {
					propertyId = rst.getLong (1);
				}
				if (p.property_id == 0) {
					Individual.insertOrUpdateUserRights(user_id, propertyId);
				}

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		return propertyId;

	}


	public static boolean hasPropertyInformationAccess(long  userId, long  propertyId) {

		Boolean hasAccess = false;
		Connection conn = DB.getConnection();
		try {
			String getAccess = "select count(*) FROM property_access_individual WHERE property_id=? AND individual_id=?";

			PreparedStatement stmtAccess = conn.prepareStatement(getAccess);
			stmtAccess.setLong (1, propertyId);
			stmtAccess.setLong (2, userId);

			ResultSet rst = stmtAccess.executeQuery();

			int count = 0;
			if (rst.next()) {
				count = rst.getInt(1);
			}
			if (count > 0) {
				hasAccess = true;

			} else {
				// no rights
				// log error(user tried to access a property that he has no access to.)
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}

		return hasAccess;
	}





}

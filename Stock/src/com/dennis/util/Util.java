package com.dennis.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.dennis.db.DB;
import com.dennis.models.Address;
import com.dennis.models.Apartment;
import com.dennis.models.ApartmentOccupant;
import com.dennis.models.ApartmentType;
import com.dennis.models.FloorPlan;
import com.dennis.models.Individual;
import com.dennis.models.Property;
import com.dennis.models.PropertyAccessIndividual;

public class Util {

	public static void sendResponseToClient(HttpServletResponse response, String responseString) {

		try {
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			out.print(responseString);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static long getUserInSession(HttpServletRequest request) {
		// HttpSession session = request.getSession();
		// long userId = long .parselong ((String) session.getAttribute("userId"));
		long userId = 1;
		return userId;
	}

	public static boolean stringIsNullOrEmpty(String str) {
		if (str == null) {
			return true;
		}
		if (str.trim().isEmpty()) {
			return true;
		}
		return false;
	}

	public static long getLongValue(HttpServletRequest request, String paramName) {
		long longValue = 0;

		if (!Util.stringIsNullOrEmpty(request.getParameter(paramName))) {
			longValue = Long.parseLong(request.getParameter(paramName));
		}
		return longValue;
	}

	public static long getLongValue(Map<String, String> headers, String paramName) {
		long longValue = 0;

		if (!Util.stringIsNullOrEmpty(headers.get(paramName))) {
			longValue = Long.parseLong(headers.get(paramName));
		}
		return longValue;
	}

	public static int getIntValue(Map<String, String> headers, String paramName) {
		int intValue = 0;
		if (!Util.stringIsNullOrEmpty(headers.get(paramName))) {
			intValue = Integer.parseInt(headers.get(paramName));
		}
		return intValue;
	}

	public static int getIntValue(HttpServletRequest request, String paramName) {
		int val = 0;
		if (!Util.stringIsNullOrEmpty(request.getParameter(paramName))) {
			val = Integer.parseInt(request.getParameter(paramName));
		}
		return val;
	}

	public static float getFloatValue(HttpServletRequest request, String paramName) {
		float val = 0;
		if (!Util.stringIsNullOrEmpty(request.getParameter(paramName))) {
			val = Float.parseFloat(request.getParameter(paramName));
		}
		return val;
	}

	public static float getFloatValue(Map<String, String> headers, String paramName) {
		float val = 0;
		if (!Util.stringIsNullOrEmpty(headers.get(paramName))) {
			val = Float.parseFloat(headers.get(paramName));
		}
		return val;
	}

	public static Map<String, String> getHeaders(HttpServletRequest request) {

		Map<String, String> map = new HashMap<String, String>();

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			if (value == null) {
				value = "";
			}
			map.put(key, value);
		}

		return map;
	}

	public static byte[] exportData(long user_id) {

		ByteArrayOutputStream baos = null;

		try {

			

			baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);

			/*
			byte[] contents = getPropertyData(user_id);
			ZipEntry entry = new ZipEntry("properties.txt");
			entry.setSize(contents.length);
			zos.putNextEntry(entry);
			zos.write(contents);

			contents = getApartmentData(user_id);

			entry = new ZipEntry("apartments.txt");
			entry.setSize(contents.length);
			zos.putNextEntry(entry);
			zos.write(contents);
			
			*/
			byte[] contents = getData(user_id);
			ZipEntry entry = new ZipEntry("data.txt");
			entry.setSize(contents.length);
			zos.putNextEntry(entry);
			zos.write(contents);

			zos.closeEntry();
			zos.close();
			return baos.toByteArray();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return baos.toByteArray();

	}

	public static byte[] getPropertyData(long user_id) {

		byte[] contents = null;
		ByteArrayOutputStream baos = null;

		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement(
						"select * FROM property where property_id in (select property_id from property_access_individual where individual_id=?)");
				stmt.setLong(1, user_id);
				ResultSet rst = stmt.executeQuery();

				ResultSetMetaData metadata = rst.getMetaData();
				int columnCount = metadata.getColumnCount();

				StringBuffer sb = new StringBuffer();

				while (rst.next()) {
					String row = "";
					for (int i = 1; i <= columnCount; i++) {
						try {
							sb.append(rst.getString(i) + ", ");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}

				contents = sb.toString().getBytes();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}

		return contents;

	}
	
	public static byte[] getData(long user_id) {
		byte[] contents = null;
		StringBuffer sb = new StringBuffer();
		List listProperty= Property.getProperties(user_id);
		String indentation= "     ";
		
		 for (Object item : listProperty) {
	            
	            Map  h= (HashMap)item;
	            Property p= (Property) h.get("property");
	            Address ad= (Address) h.get("address");
	            
	            sb.append(p.property_name + " " + ad.address_line1 + " " + ad.address_line2 + " " + ad.city + "\n");
	            
	            List listApartment= Apartment.getApartments(user_id,p.property_id);
	            
	            for (Object a : listApartment) {
	            	 Map  ha= (HashMap)a;
	            	Apartment ap= (Apartment) ha.get("apartment");
	            	ApartmentType at= (ApartmentType) ha.get("apartmenttype");
	            	FloorPlan fp= (FloorPlan) ha.get("floorplan");
		            
	            	sb.append(indentation+ at.description + " " +  ap.apartment_number + "\n");
	            	
	            	List listIndividuals = Individual.getOccupants(user_id, ap.apartment_id);
	            	
	            	 for (Object i : listIndividuals) {
	            		 Map  hi= (HashMap)i;
	            		 Individual ind= (Individual) hi.get("individual");
	            		 ApartmentOccupant ao= (ApartmentOccupant) hi.get("apartmentoccupant");
	            		 sb.append(indentation+ indentation+ ind.first_name + " "  + ind.last_name  
	            				 + " "  + ind.last_name   + " "  + ao.date_from + " "  + ao.date_to + "\n");
	            	 }
	            	
	            }
	            
	            
	            
	        }
		 
		 
		 contents = sb.toString().getBytes();
		
		return contents;
	}
	
	public static byte[] getApartmentData(long user_id) {
		byte[] contents = null;
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement(
						"select * FROM apartment where property_id in (select property_id from property_access_individual where individual_id=?)");
				stmt.setLong(1, user_id);
				ResultSet rst = stmt.executeQuery();

				ResultSetMetaData metadata = rst.getMetaData();
				int columnCount = metadata.getColumnCount();

				StringBuffer sb = new StringBuffer();

				while (rst.next()) {
					for (int i = 1; i <= columnCount; i++) {
						try {
							sb.append(rst.getString(i) + ", ");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				contents = sb.toString().getBytes();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}

		return contents;

	}

}

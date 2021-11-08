package com.dennis.models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import com.dennis.db.DB;

public class FloorPlan {
	
	public long floor_plan_id;
	public long property_id;
	public String floor_plan_name;
	public float area;
	public int area_unit_of_measure_id;
	public String notes;
	public String image_path;
	
	public FloorPlan(long floor_plan_id, long property_id, String floor_plan_name, float area,
			int area_unit_of_measure_id, String notes, String image_path) {
		super();
		this.floor_plan_id = floor_plan_id;
		this.property_id = property_id;
		this.floor_plan_name = floor_plan_name;
		this.area = area;
		this.area_unit_of_measure_id = area_unit_of_measure_id;
		this.notes = notes;
		this.image_path = image_path;
	}

	

	/*
	 * No using Lamda expressions to filter, this has been done deliberately.
	 */
	public static FloorPlan getFloorPlan(long  floorPlanId, List<FloorPlan> fl) {

		for (FloorPlan f : fl) {
			if (f.floor_plan_id == floorPlanId) {
				return f;
			}
		}
		return null;
	}

	public static List<FloorPlan> getFloorPlans(long  userId, long  propertyId) {

		if (!Property.hasPropertyInformationAccess(userId, propertyId)) {
			return null;// no rights
		}
		List<FloorPlan> l = new ArrayList<FloorPlan>();
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM floor_plan WHERE property_id=?");
				stmt.setLong (1, propertyId);
				ResultSet rst = stmt.executeQuery();
				while (rst.next()) {
					l.add(new FloorPlan(rst.getLong ("floor_plan_id"), rst.getLong ("property_id"), 
							rst.getString("floor_plan_name"),
							rst.getFloat("area"), rst.getInt("area_unit_of_measure_id"), rst.getString("notes"), rst.getString("image_path")));
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

	

	public static long insertOrUpdateFloorPlan(long user_id, long  property_id, FloorPlan fp) {

		long floor_plan_id=0;
		
		if (!Property.hasPropertyInformationAccess(user_id, property_id)) {
			return 0;// no rights
		}
		
		String stm="";
		if(fp.floor_plan_id==0) {
		 stm = "INSERT INTO floor_plan( "
					+ " floor_plan_name, area, area_unit_of_measure_id, image_path, notes, property_id) "
					+ " VALUES (?, ?, ?, ?, ?, ?)   RETURNING  floor_plan_id";
		}else {
			 stm = "UPDATE floor_plan set floor_plan_name=?, area =?, area_unit_of_measure_id=?, image_path=?, notes=? "
						+ " WHERE  property_id=? AND floor_plan_id= ? RETURNING  floor_plan_id";
			
		}

		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement(stm);
				stmt.setString(1, fp.floor_plan_name);
				stmt.setFloat(2, fp.area);
				stmt.setLong(3, fp.area_unit_of_measure_id);
				stmt.setString(4, fp.image_path);
				stmt.setString(5, fp.notes);
				stmt.setLong(6, fp.property_id);
				if (fp.floor_plan_id > 0) {
					stmt.setLong (7, fp.floor_plan_id);
				}

				ResultSet rst = stmt.executeQuery();

				if (rst.next()) {
					floor_plan_id = rst.getLong (1);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
	
		return floor_plan_id;
	}
	
	static String UPLOAD_DIRECTORY="C:\\dennis\\work\\Property\\WebContent";
	public static String imagefolder="images";
	
	public static boolean hasImageInRequest(HttpServletRequest request) {
		Part part =null;
		try {
		 part =request.getPart("floor_plan_image_upload");
		} catch (Exception e) {
		}
		
		if(part!=null) {
			return true;
		}
		
		return false;
		
	}
	
	public static void saveImage(HttpServletRequest request, String relativeImagePath){
		
		try {
			File upLoadPath = new File(UPLOAD_DIRECTORY);
			if (!upLoadPath.exists()) {
				upLoadPath.mkdir();
			}
			
			Part part =request.getPart("floor_plan_image_upload");

			
			String filePathAndName=upLoadPath + File.separator + relativeImagePath;
			Path fp=Paths.get(filePathAndName);
			
			if (Files.exists(fp)) {
				Files.deleteIfExists(fp);//for now just replace them.
			}
			
			part.write(filePathAndName);

		} catch (Exception e) {
		
			
		}

	}
	
	public static String getFileExtension(Part part) {
		
		String filename="";
	    for (String content : part.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	        	filename= content.substring(content.indexOf("=") + 2, content.length() - 1);
	        break;
	        }
	     }
	    return filename.substring(filename.lastIndexOf("."),filename.length());
	}
	
	
	public static String generateRandomString() {
		 
	    int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 10;
	    Random random = new Random();
	    StringBuilder buffer = new StringBuilder(targetStringLength);
	    for (int i = 0; i < targetStringLength; i++) {
	        int randomLimitedInt = leftLimit + (int) 
	          (random.nextFloat() * (rightLimit - leftLimit + 1));
	        buffer.append((char) randomLimitedInt);
	    }
	    String generatedString = buffer.toString();

	    return generatedString;
	}

	
	public static Map<String,String>  extractPostParameters(HttpServletRequest request) throws IOException{
		
		Map<String,String> m= new HashMap<String,String>();
		try {
			String st="";
			   if ("POST".equalsIgnoreCase(request.getMethod())) {
			        Scanner s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
			        if(s.hasNext()) {
			        	st=s.next();
			        }
			    }
			   
			   
			   String lineDelimiter=st.substring(0,st.indexOf("\r\n"));
			   
			   String str[] = st.split(lineDelimiter);
			   
			   for(String line : str) {
				   if(!line.isEmpty()) {
					   
				    String key=line.substring(line.indexOf("name=")+6, line.indexOf("\r\n\r\n")-1);
				    String value=line.substring(line.indexOf("\r\n\r\n")+4,line.lastIndexOf("\r\n"));
				    m.put(key, value);
				   }
				    
			   }
		} catch (Exception e) {

			e.printStackTrace();
		}
		   
		   
		   
		    return m;
	}
	
}

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.dennis.Auth.Auth;
import com.dennis.db.DB;
import com.dennis.util.Util;

public class User {
	
    public long user_id;
    public String user_name;
    
    public String first_name;
    public String last_name;
    
    public String email;
    public String phone;
    
   
    public String password;
    public String notes;
    

	

	public User(long user_id, String user_name, String first_name, String last_name, String email, String phone, String password, String notes) {
		super();
		this.user_id = user_id;
		this.user_name = user_name;
		this.first_name = first_name;
		this.last_name = last_name;
		this.email = email;
		this.phone = phone;
		this.password = password;
		this.notes = notes;
	}

	public static long getUserInSession(HttpServletRequest request) {
		long userId =0;
		 HttpSession session = request.getSession();
		 if(session.getAttribute("user_id")!=null) {
			 userId = ((Long) session.getAttribute("user_id")).longValue();
		 }
		return userId;
	}
	
	
	public static long getUser(String  user_name,String user_password) {
		
		long user_id=0;
		
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_application where user_name=?");
				stmt.setString (1, user_name);
				ResultSet rst = stmt.executeQuery();
				while (rst.next()) {
					String saltAndHash=rst.getString("password");
					boolean isPasswordCorrect=Auth.verifyPassword(user_password,saltAndHash);
					if(isPasswordCorrect) {
						user_id=rst.getLong("user_id");
					}

				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		
		return user_id;
	}

	public static boolean checkExistingUserName(User user) {

		boolean exists=false;
		
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_application where user_name=?");
				stmt.setString (1, user.user_name);
				ResultSet rst = stmt.executeQuery();
				while (rst.next()) {
					exists=true;
				}
			}

		} catch (Exception e) {
			exists=true;
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		
		return exists;
	
		
	}
	public static long insertUpdateUserRecord(User individual) {

		long  userId = individual.user_id;
		String insert = "INSERT into user_application (first_name,last_name,email,phone,user_name,password,notes) values(?,?,?,?,?,?,?)  RETURNING  user_id";
		String update = "UPDATE  user_application set first_name=?,last_name=?,email=?,phone=?,user_name=?,password=?,notes=? where user_id=?   RETURNING  user_id";
		
	
		String stm = null;
		if (userId==0) {
			stm = insert;
		} else {
			stm = update;
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
				stmt.setString(7, individual.notes);

				if (userId > 0) {
					stmt.setLong (8, userId);
				}

				ResultSet rst = stmt.executeQuery();

				if (rst.next()) {
					userId = rst.getLong (1);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		return userId;

	

	}
	

	
	public static long changePassword(long  user_id,String user_password,String user_password_new) {
		
		long user_id_ret=0;
		
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				
				
				PreparedStatement stmt = conn.prepareStatement("SELECT * FROM user_application where user_id=?");
				stmt.setLong (1, user_id);
				ResultSet rst = stmt.executeQuery();
				while (rst.next()) {
					String saltAndHash=rst.getString("password");
					boolean isPasswordCorrect=Auth.verifyPassword(user_password,saltAndHash);
					if(isPasswordCorrect) {
						String user_password_new_hashed =Auth.generateSaltAndHashPassword(user_password_new);
						PreparedStatement stmt1 = conn.prepareStatement("UPDATE user_application set password=? where user_id=? RETURNING  user_id");
						stmt1.setString (1, user_password_new_hashed);
						stmt1.setLong (2, user_id);
						ResultSet rst1 = stmt1.executeQuery();
						while (rst1.next()) {
							user_id_ret=rst1.getLong("user_id");

						}
					}

				}

				
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		
		return user_id_ret;
	}
	



   
}

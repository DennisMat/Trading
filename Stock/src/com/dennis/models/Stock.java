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

public class Stock {

	public long stock_id;
	public String stock_symbol;
	public boolean active;
	public String stock_description;

	public Stock(long stock_id, String stock_symbol, boolean active, String stock_description) {
		super();
		this.stock_id = stock_id;
		this.stock_symbol = stock_symbol;
		this.active = active;
		this.stock_description = stock_description;
	}

	public static Stock getStock(long  user_id, long stock_id) {
		if(!hasStockInformationAccess(user_id,stock_id)) {
			return null;
		}
		Stock p= null;
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement("select * from stock where stock_id=? AND user_id=?");
				stmt.setLong (1, stock_id);
				stmt.setLong (2, user_id);
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

	static Stock constructObject(ResultSet rst) throws SQLException {
		Stock p;
		p=new Stock(rst.getLong ("stock_id"),rst.getString ("stock_symbol"),  rst.getBoolean("active"), rst.getString ("stock_description"));
		return p;
	}

	/**
	 * Get stocks that the individual has access to
	 * 
	 * @param user_id
	 * @return
	 */
	public static List getStocks(long  user_id) {

		List<Stock> l = new ArrayList<Stock>();
	
		Connection conn = DB.getConnection();
		try {
			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement("select * from stock where user_id=?");
				stmt.setLong (1, user_id);
				ResultSet rst = stmt.executeQuery();

				while (rst.next()) {		
					l.add(constructObject(rst));
					
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


	public static long  changeActiveStatus(long user_id, Stock s) {

		if(s.stock_id!=0 && !hasStockInformationAccess(user_id,s.stock_id)) {
			return 0;
		}

		
		long  stockId = 0;
	String update = "UPDATE  stock set active=? WHERE stock_id=?   RETURNING  stock_id";

	Connection conn = DB.getConnection();
	try {

		if (conn != null) {
			PreparedStatement stmt = conn.prepareStatement(update);
			stmt.setBoolean(1, s.active);
			stmt.setLong(2, s.stock_id);
			ResultSet rst = stmt.executeQuery();
			
			if (rst.next()) {
				stockId = rst.getLong (1);
			}
		}

	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} finally {
		DB.closeConnection(conn);
	}
	return stockId;
		
	}

	public static long  insertUpdateStockRecord(long user_id, Stock p) {

		if(p.stock_id!=0 && !hasStockInformationAccess(user_id,p.stock_id)) {
			return 0;
		}

		long  stockId = 0;
		String insert = "INSERT into stock (stock_symbol, stock_description, active, user_id ) values(?,?,true, ?)  RETURNING  stock_id";
		String update = "UPDATE  stock set stock_symbol=?, stock_description=? WHERE stock_id=?   RETURNING  stock_id";

		String stm = null;
		if (p.stock_id == 0) {
			stm = insert;
		} else {
			stm = update;
			stockId = p.stock_id;
		}

		Connection conn = DB.getConnection();
		try {

			if (conn != null) {
				PreparedStatement stmt = conn.prepareStatement(stm);
				
				stmt.setString(1, p.stock_symbol);
				stmt.setString(2, p.stock_description);
				if (stockId > 0) {
					stmt.setLong (3, stockId);
				}else {
					stmt.setLong(3, user_id);
				}

				ResultSet rst = stmt.executeQuery();

				if (rst.next()) {
					stockId = rst.getLong (1);
				}


			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DB.closeConnection(conn);
		}
		return stockId;

	}


	public static boolean hasStockInformationAccess(long  userId, long  stockId) {

		Boolean hasAccess = false;
		Connection conn = DB.getConnection();
		try {
			String getAccess = "select count(*) FROM stock WHERE stock_id=? AND user_id=?";

			PreparedStatement stmtAccess = conn.prepareStatement(getAccess);
			stmtAccess.setLong (1, stockId);
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
				// log error(user tried to access a stock that he has no access to.)
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

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
import com.dennis.models.User;

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
		long userId =0;
		 HttpSession session = request.getSession();
		 if(session.getAttribute("user_id")!=null) {
			 userId = ((Long) session.getAttribute("user_id")).longValue();
		 }
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
	
}

package com.dennis;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/*
Use everyday:

https://kite.trade/connect/login?api_key=97h12exoc4ne48og&v=3

 *
 */

public class Auth {


	public static String request_token=null;
	public static String accessToken= null;
	public static String publicToken= null;
	public final static String  AUTH_PROP_FILE="C:\\dennis\\work\\Zero\\ZeroMain\\src\\main\\resources\\auth.properties";

	private static Log logger = LogFactory.getLog(Variables.class);

	public static void loadVariables() {


		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(AUTH_PROP_FILE);

			prop.load(input);

			request_token=prop.getProperty("request_token").trim();
			accessToken=prop.getProperty("accessToken").trim();
			publicToken=prop.getProperty("publicToken").trim();

			prop.setProperty("request_token","The answer is always 42!");	

		} catch (Exception ex) {
			logger.error(ex);
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (Exception e) {
					logger.error("",e);e.printStackTrace();
					e.printStackTrace();
				}

			}

		}
	}


	public static void writeTokens(String accessToken, String publicToken) {
		logger.info("writing new access and public tokens...");
		logger.info("accessToken for "+Util.getIndiaTime().toString()+" is " + accessToken);
		logger.info("publicToken for "+Util.getIndiaTime().toString()+" is " + publicToken);

		Properties prop = new Properties();
		InputStream input = null;
		FileOutputStream output=null;
		try {			
			input = new FileInputStream(AUTH_PROP_FILE);
			output=new FileOutputStream(AUTH_PROP_FILE);
			prop.load(input);
			

			prop.setProperty("request_token","RegenerateThis");
			prop.setProperty("publicToken",publicToken);
			prop.setProperty("accessToken",accessToken);

			prop.store(output, null);


		} catch (Exception ex) {
			logger.error(ex);
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
					output.close();
				} catch (Exception e) {
					logger.error("",e);e.printStackTrace();
					e.printStackTrace();
				}

			}

		}
	}
}

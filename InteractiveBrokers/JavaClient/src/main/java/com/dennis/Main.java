/* Copyright (C) 2019 Interactive Brokers LLC. All rights reserved. This code is subject to the terms
 * and conditions of the IB API Non-Commercial License or the IB API Commercial License, as applicable. */

package com.dennis;

import java.util.ArrayList;
import java.util.List;

import com.ib.controller.ApiConnection.ILogger;
import com.ib.client.Contract;
import com.ib.client.MarketDataType;
import com.ib.client.TickAttrib;
import com.ib.client.TickType;
import com.ib.controller.ApiController;
import com.ib.controller.ApiController.IConnectionHandler;
import com.ib.controller.ApiController.TopMktDataAdapter;

import com.ib.controller.Formats;


public class Main  {

	public static Main INSTANCE;

	private final static Logger m_inLogger = new Logger();
	private final static Logger m_outLogger = new Logger();
	private static ApiController m_controller;
	private final static List<String> m_acctList = new ArrayList<>();
	static IConnectionHandler connectionHandler= new ConnectionHandler();
	// getter methods
	List<String> accountList() 	{ return m_acctList; }
	static ILogger getInLogger()            { return m_inLogger; }
	static ILogger getOutLogger()           { return m_outLogger; }

	public static void main(String[] args) {
		
		
		
		
		
		// make initial connection to local host, port 7496, client id 0, no connection options
		controller().connect( "127.0.0.1", 4002, 0, null );


		System.out.println("doing stuff  ...");
		//try {			Thread.sleep(10000);		} catch (InterruptedException e) {}
		
		
	       Contract contract = new Contract();
           contract.symbol("IBM");
           contract.secType("STK");
           contract.currency("USD");
           contract.exchange("SMART");
           contract.primaryExch("ISLAND");
           
           
    	   TopMktDataAdapter m_stockListener = new TopMktDataAdapter() {
	            @Override public void tickPrice(TickType tickType, double price, TickAttrib attribs) {
	                //if (tickType == TickType.LAST || tickType == TickType.DELAYED_LAST) {
	                	System.out.println(" In ticker price= " + price);
	               // }
	            }
	        };
	        
	    controller().client().reqMarketDataType( MarketDataType.DELAYED );
       
		controller().reqTopMktData(contract, "", false, false, m_stockListener);

		try {			Thread.sleep(300000);		} catch (InterruptedException e) {}

		controller().disconnect();//disconnect
		
		
		
		
	}

	


	public static ApiController controller() {
		if ( m_controller == null ) {
			m_controller = new ApiController( connectionHandler, getInLogger(), getOutLogger() );
		}
		return m_controller;
	}




	private static class ConnectionHandler implements IConnectionHandler{

	@Override public void connected() {
		show( "connected");
		System.out.println("in conneceted  ...");

		controller().reqCurrentTime(time -> show( "Server date/time is " + Formats.fmtDate(time * 1000) ));

		controller().reqBulletins( true, (msgId, newsType, message, exchange) -> {
			String str = String.format( "Received bulletin:  type=%s  exchange=%s", newsType, exchange);
			show( str);
			show( message);
		});
	}

	@Override public void disconnected() {
		show( "disconnected");

	}

	@Override public void accountList(List<String> list) {
		show( "Received account list");
		m_acctList.clear();
		m_acctList.addAll( list);
	}

	@Override public void show( final String str) {
		System.out.println(str);
	}

	@Override public void error(Exception e) {
		show( e.toString() );
	}

	@Override public void message(int id, int errorCode, String errorMsg) {
		show( id + " " + errorCode + " " + errorMsg);
	}
}


	private static class Logger implements ILogger {
		@Override public void log(final String str) {
			System.out.println(" In log str = " + str);
		}
	}
	
	
}

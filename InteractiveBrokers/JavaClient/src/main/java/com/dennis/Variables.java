package com.dennis;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ib.controller.ApiController;

/*


https://kite.trade/connect/login?api_key=97h12exoc4ne48og&v=3

 */
public class Variables {

	public static boolean isTest=false;

	public static String zoneId = "US/Eastern";
	public static String indiaTimeStr="T01:01:01.001+01:00["+zoneId+"]"; // a randomly chosen time
	public static String[] holidays={"2020-06-03","2020-09-07",	"2020-11-11","2020-11-26","2020-12-25"};

	public static String userid= "xxxxxx";
	public static String pwd= "xxxxxxxxxx#";
	public static String pin= "xxxxx";

	public static String api_key= "dddddddddddddddd";
	public static String api_secret= "yyyyyyyyyyyyyyy";



	//public static double limitOffsetPercentage=0.015; //for setting the buy/sell limit for an order.
	public static double limitOffsetPercentage=0.0; //setting it to zero for now because it it hard to figure out the correct increment for a share that will not be rejected when an order is placed.
	public static double cashBuffer=10000;//amount of cash to leave in the account.
	//public static double inventoryThreshold=1.2;//Beyond this limit check order status before placing a buy order
	public static int timeBetweenQuoteChecks=20;// in seconds
	public static int timeBetweenPriceChecks=1;// in seconds
	public static int timeBetweenDunps=500;// in seconds
	public static int timeBetweenBuyAndSell=5;// in seconds. waiting for a sell transaction to get through before buying
	public static int sellCompleteAttempts=3;//  attempts after waiting for sell to complete
	public static int timeBetweenStopAppAttempts=5;// in seconds
	public static int timeBetweenUpdatingCache=30;// in seconds
	public static int timeToWaitForModifyingOrder=30;// in seconds
	public static int timeToWaitAfterCancellingOrder=30;// in seconds
	public static int timeToWaitForCancellingOrder=900;// in seconds

	// 9:30 to 3:30 (for my purpose 9:45 to 3:25)
	public static int exchangeOpenHour=9;
	public static int exchangeOpenMinute=45;
	public static int exchangeCloseHour=15;
	public static int exchangeCloseMinute=25;

	//mon =1, fri=5, sat= 6 sun=7
	public static int exchangeFirstDayofWeek=1;
	public static int exchangeLastDayofWeek=5;

	//form page https://misc.interactivebrokers.com/cstools/contract_info/v3.10/index.php
	public static String[] exchangeArr= {"EBS","IBIS","MEXI","NYSE","SMART"};
	public static String[] primaryExchangeArr= {"EBS","IBIS","MEXI","NYSE","ISLAND"};
	
	
	public static int[] costofSingleShare={  116};//costofSingleShare 
	public static String[] symbolArr= {"IBM","AAPL"};	
	//minimum price difference between 2 exchanges to trigger buy and sell.
	public static double[] minPriceDifferencePercentageArr={0.45,0.50,0.45,0.50,0.50,		0.45,0.50,0.45,0.50,						0.70,0.70,0.70,0.50,							1.30,1.50,1.50,1.20};	
	public static int[] tradeQuantityArr={80,2,5,200,5,										10,500,20,1,								10,10,1000,1,									10,1,1,10};
	//public static int[] incrementTradeQuantityArr={1,0,0,0,0,								0,1,0,0,									0,1,0,0,										0,0,0,0};
	//public static int[] incrementTradeQuantityArr={1,1,1,1,1,								1,10,1,1,									1,1,10,1,										10,1,1,10};//incrementTradeQuantityArr
	public static int[] incrementTradeQuantityArr={10,5,5,100,1,								10,100,10,1,							5,10,100,1,										10,10,10,100};//incrementTradeQuantityArr
	public static int[] dumpTradeQuantityArr={0,0,0,0,0,								    0,0,0,0,									0,0,0,0,										0,0,0,0,100};//dumpTradeQuantityArr
	public static int[] dumpBuyQuantityArr={0,0,0,0,0,								    0,0,0,0,									0,0,0,0,										0,0,0,0,1000};//dumpBuyQuantityArr
	public static boolean[] dumpFlagArr={false,false,false,false,false,						false,false,false,false,					false,false,false,false,						false,false,false,false};
	public static boolean[] sellAllArr={false,false,false,false,false,						false,false,false,false,					false,false,false,false,						false,false,false,false};
	public static int[] maxInventoryArr={100,20,10,500,5,										100,500,20,5,							20,50,1000,5,									100,100,10,500};//maxInventoryArr
	//l=limit, m=market
	public static char[] tradeTypeArr=	{'l','l','l','l','l',								'l','l','l','l',							'l','l','l','l',								'l','l','l','l'};
	public static boolean[] doNothingFlag={false,false,false,false,false,					false,false,false,false,					false,false,false,false,						false,false,false,false};
	public static int[] maxOpenOrdersArr={1,1,1,1,1,										1,1,1,1,									1,1,1,1,										1,1,1,1};
	//public static boolean[] doNothingFlag={true,true,true,true,true,						true,true,true,true,						true,true,true,true,							true,true,true,false};
	private static Log logger = LogFactory.getLog(Variables.class);

	public static void loadVariables(ApiController apiController) {
		try {

			if(apiController!=null) {
				//Account.loadAccount(apiController);
			}

		}catch (Exception ex) {
			logger.error(ex);
			ex.printStackTrace();
		} 

	}




}

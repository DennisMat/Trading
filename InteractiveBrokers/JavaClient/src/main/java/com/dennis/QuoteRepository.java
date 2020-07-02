package com.dennis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ib.client.Contract;
import com.ib.client.MarketDataType;
import com.ib.client.TickAttrib;
import com.ib.client.TickType;
import com.ib.controller.ApiController;
import com.ib.controller.Formats;
import com.ib.controller.ApiController.IConnectionHandler;
import com.ib.controller.ApiController.TopMktDataAdapter;

public class QuoteRepository {

	private static Log logger = LogFactory.getLog(QuoteRepository.class);
	public final static boolean[]  stopBuySell= {false};
	public final static boolean[]  extractionInProgress= {false};
	public static  Map<String, Quote> quotes=new HashMap<String, Quote>();

	static void extractQuotes(String exchange, TickType tickType, double price, long currentTime){
		extractionInProgress[0]=true;

		//NumberFormat formatter = new DecimalFormat();H
		//System.out.println("ticks size "+ticks.size());
		Map<String, Quote> quotesFromTicker= new HashMap<String, Quote>();

		Quote q= new Quote();
		q.price=price;





		if (tickType == TickType.LAST || tickType == TickType.DELAYED_LAST) {
			quotesFromTicker.put(exchange, q);
		}


		//replace entries in the global quote with local ones
		for (Entry<String, Quote> entry : quotesFromTicker.entrySet()) {
			quotes.put(entry.getKey() , entry.getValue());
		}

		if(Variables.isTest) {
			for(int i=0;i<3;i++) {	
				//System.out.println("In test still doing stuff " + i +" for time "+ currentTime + " in extractQuotes");
				//try {Thread.sleep(1000);} catch (InterruptedException e) {}
			}
		}

		extractionInProgress[0]=false;

	}

	public static void getQuotesThroughRestCall(ApiController apiController){

		try {

			Map<String, Quote> quotesRestCall =Operations.getQuotes(apiController,
					Variables.symbolArr,
					Variables.exchangeArr
					);

			//replace entries in the global quote with local ones
			for (Entry<String, Quote> entry : quotesRestCall.entrySet()) {
				quotes.put(entry.getKey() , entry.getValue());
			}

			if(Variables.isTest) {
				for(int i=0;i<3;i++) {	
					//System.out.println("In test. Still doing stuff in getQuotesThoughRestCall " + i );
					//try {Thread.sleep(1000);} catch (InterruptedException e) {}
				}
			}			
			if(Util.isExchangeOpen()) {
				//Util.collectData(apiController);
				Trading.lastChecked.set(1, System.currentTimeMillis());
			}
		} catch (Exception e) {
			logger.error("Exception in getQuotesThroughRestCall",e);
			e.printStackTrace();
		}			


	}

	public void setUpTicker(ApiController apiController) {


		try {

			apiController.connect( "127.0.0.1", 4002, 0, null );

			boolean isConnected = apiController.client().isConnected();
			apiController.client().reqMarketDataType( MarketDataType.DELAYED );

			for(int i=0;i<Variables.exchangeArr.length;i++) {
				for(int j=0;j<Variables.symbolArr.length;j++) {
					final String  exchange=Variables.exchangeArr[i]+ ":" + Variables.symbolArr[j];
					Contract contract = new Contract();
					contract.symbol(Variables.symbolArr[j]);
					contract.secType("STK");
					contract.currency("USD");
					contract.exchange(Variables.exchangeArr[i]);//"SMART"
					contract.primaryExch(Variables.primaryExchangeArr[i]);//"ISLAND"
					System.out.println("exchange= " + exchange);
					TopMktDataAdapter m_stockListener = new TopMktDataAdapter() {
						@Override public void tickPrice(TickType tickType, double price, TickAttrib attribs) {
							//if (tickType == TickType.LAST || tickType == TickType.DELAYED_LAST) {
							System.out.println(" In ticker price= " + price);
							// }

							long currentTime= System.currentTimeMillis();
							Trading.lastChecked.set(0, currentTime);
							QuoteRepository.extractQuotes(exchange,tickType, price, currentTime); 



							/*
										Runnable extractQuoteRunnable = new Runnable(){
											public void run() {
												//QuoteRepository.extractQuotes(tickType, price, currentTime);      		
											}//end of run						
										};
										if(!QuoteRepository.extractionInProgress[0]) {
											Thread extractQuotes=new Thread(extractQuoteRunnable);
											extractQuotes.start();
										}else {
											//logger.info("Skipping extraction for time: "+currentTime);
										}
							 */



						}
					};

					apiController.reqTopMktData(contract, "", false, false, m_stockListener);
					

				}
			}



			
		
			//System.out.println(isConnected);


			while(!QuoteRepository.stopBuySell[0]) {
				if(Util.stopApp()) {
					break; // break 
				}
				try {Thread.sleep(Variables.timeBetweenStopAppAttempts*1000);} catch (InterruptedException e) {}
			}



			System.out.println("About to disconnected");
			// Unsubscribe for a token.
			//tickerProvider.unsubscribe(tokens);

			// After using com.zerodhatech.com.zerodhatech.ticker, close websocket connection.
			//tickerProvider.disconnect();
			apiController.disconnect();
			System.out.println("apiController disconnected");

		} catch (Exception e) {
			logger.error("",e);e.printStackTrace();
		}
	}






}

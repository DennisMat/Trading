package com.dennis;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLongArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ib.controller.ApiController;


public class Trading {

	static long now = System.currentTimeMillis();
	static long[] l={now,now,now};//ticker, quoteRepository, loadAccountCaches
	public static AtomicLongArray lastChecked=new AtomicLongArray(l); 
	private static Log logger = LogFactory.getLog(Trading.class);

	public void doStuff(ApiController apiController) {
		try {

			setUpTicker(apiController);//this is threaded
			//setUpQuoteRepository(apiController);//this is threaded
			//loadAccountCaches(apiController);//this is threaded
			//doBuySellNormal(apiController);//this is threaded

		} catch (Exception e) {			
			logger.error("",e);e.printStackTrace();
		}
	}

	/*

	private void setUpQuoteRepository(ApiController apiController) {
		System.out.println("before setUpQuoteRepository");
		Runnable r = new Runnable(){
			public void run() {

				while(!QuoteRepository.stopBuySell[0]) {				
					if(Util.stopApp()) {
						break; 
					}
					QuoteRepository.getQuotesThroughRestCall(apiController);  
					try {Thread.sleep(Variables.timeBetweenQuoteChecks*1000);} catch (InterruptedException e) {}
				}

			}//end of run						
		};
		Thread tr=new Thread(r);
		tr.start();			
		logger.info("After setUpQuoteRepository");

	}
	*/


	private void setUpTicker(ApiController apiController) {
		Runnable runTicker = new Runnable(){
			public void run() {
				QuoteRepository q= new QuoteRepository();
				q.setUpTicker(apiController);     		
			}//end of run						
		};
		Thread t=new Thread(runTicker);
		t.start();

		logger.info("after setUpTicker");
	}

/*
	private void loadAccountCaches(ApiController apiController) {
		Runnable r = new Runnable(){
			public void run() {
				while(!QuoteRepository.stopBuySell[0]) { 
					if(QuoteRepository.stopBuySell[0] || Util.stopApp()) {
						break; 
					}
					Account.loadAccount(apiController);
					try {Thread.sleep(Variables.timeBetweenUpdatingCache*1000);} catch (InterruptedException e) {}					
					SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");		
					logger.info("Last accessed, Ticker =" +formatter.format(new Date(lastChecked.get(0))) 
					+"  QuoteRepository=" +formatter.format(new Date(lastChecked.get(1)))  + "  AccountCaches=" +formatter.format(new Date(lastChecked.get(2)))  );					
				}

			}//end of run						
		};
		Thread t=new Thread(r);
		t.start();
		logger.info("After loadAccountCaches");
	}



	void doBuySellNormal(ApiController apiController){

		Runnable r = new Runnable(){
			public void run() {

				boolean stopBuySellLocal=false;

				while(!QuoteRepository.stopBuySell[0]) {			
					if(!Variables.isTest && !Util.isExchangeOpen()) {
						break; // break if exchange is closed and if it is not a test
					}
					if(!stopBuySellLocal) {
						if(QuoteRepository.quotes!=null) {
							Operations.iterateThroughAllStocksAndSellBuy(apiController, Variables.symbolArr, 
									Variables.exchangeArr, Variables.minPriceDifferencePercentageArr, 
									Variables.limitOffsetPercentage, QuoteRepository.quotes);				
						}

					}

					for(int i=0;i<Variables.symbolArr.length;i++) {	
						if(Account.getSellableQuantityCached(i) >1) {
							break; //break from this for loop even if one symbol is to be sold 
						}
						if(i==Variables.symbolArr.length-1) {
							//this will be true only if the orders flags are false, 
							//that mean no more buy/sells to be done
							stopBuySellLocal=true;	
						}			
					}

					QuoteRepository.stopBuySell[0]=stopBuySellLocal;
					try {Thread.sleep(Variables.timeBetweenPriceChecks*1000);} catch (InterruptedException e) {}
				}

			}//end of run						
		};
		Thread t=new Thread(r);
		t.start();
		logger.info("Started doBuySellNormal");
	}

*/

}

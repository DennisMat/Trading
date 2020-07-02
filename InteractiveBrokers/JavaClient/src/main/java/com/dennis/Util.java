package com.dennis;

import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.ib.client.Order;
import com.ib.controller.ApiController;
import com.ib.controller.Formats;
import com.ib.controller.ApiController.IConnectionHandler;


public class Util {


	private static Log rawdatalogger = LogFactory.getLog("rawdatalogger");
	private static Log logger = LogFactory.getLog(Util.class);

	
/*
	public static void onOrderUpdate(Order order){
		try {
			//logger.info("order update received "+order.orderId + ", order status=" + order.status);
			if(order.status.equals(ConstantValues.ORDER_STATUS_COMPLETE)){
				if(order.transactionType.equals(Constants.TRANSACTION_TYPE_SELL)) {	
					Account.removefromCurrentInventoryCached(order);
					Sound.playOderCompleteSell();
				}
				if(order.transactionType.equals(Constants.TRANSACTION_TYPE_BUY)) {
					Account.addToCurrentInventoryCached(order);
					Sound.playOderCompleteBuy();
					
				}
				logger.info("Order fulfilled Symbol=" + order.symbol+ " order id=" + order.orderId
						+ " order staus=" + order.status);
				Account.addOrReplaceExistingOrdersForTheDay(order);
			}

		} catch (Exception e) {
		}

	}

	public static boolean isTrade(MinMax mm,double minPriceDifferencePercentage){
		double priceDifference= mm.max-mm.min;
		//System.out.println(" mm.min="+mm.min +" mm.max="+mm.max);
		// it won't make a difference weather mm.max or mm.min is selected in the below code.
		double minPriceDifference=Util.getPercentage(mm.max, minPriceDifferencePercentage);
		if(priceDifference<0.000001) {
			//logger.info("price diff is:" + priceDifference + ", price = " + mm.max );
			return false;
		}
		//System.out.println(" not traded mm.min="+mm.min +" mm.max="+mm.max);
		if(mm.min<mm.max && mm.min>0.000001 && mm.max>0.000001) {
			if(priceDifference>minPriceDifference) {
				//				logger.info("Sell price at " + Variables.exchangeArr[mm.indexMax] + ":"+ mm.max 
				//						+ " Buy price at " + Variables.exchangeArr[mm.indexMin] + ":"+ mm.min
				//						+ " price diff is:" + priceDifference +", minPriceDifference:" 
				//						+ minPriceDifference + " percentage = "+(priceDifference/mm.max)*100);
				return true;				
			}

		}else {
			System.out.println(" not traded mm.min="+mm.min +" mm.max="+mm.max);

		}
		return false;
	}


	static MinMax  computeMinMax(String symbol, String[] exchangeArr, Map<String, Quote> quotes, double limitOffsetPercentage){
		double[] lastPrice= new double[exchangeArr.length];
		StringBuilder sb = new StringBuilder();
		MinMax mm= new MinMax();
		for(int i=0;i<exchangeArr.length;i++) {	
			String inst=exchangeArr[i] +":"+ symbol;
			if(quotes.get(inst)!=null && quotes.get(inst).depth!=null) {
				lastPrice[i]=quotes.get(inst).lastPrice;
				// All seller offers are listed here, the minimums for buying can be found here. 
				//Pick lowest offer to buy from.
				List<Depth> offers=quotes.get(inst).depth.sell;
				// All buyer bid are here. The maximum for selling can be found here. Pick higger bidder to sell to
				List<Depth> bids=quotes.get(inst).depth.buy;
				//System.out.println();
				sb.append(" offers from sellers " +inst+ "  ");
				for(int j=0;j<offers.size();j++) {
					sb.append(offers.get(j).getPrice() +",");
					if(i==0 && j==0) {
						mm.min=offers.get(j).getPrice();//Initialize to some figure
					}else {
						if (offers.get(j).getPrice()<mm.min) {
							mm.min=offers.get(j).getPrice();
							mm.indexMinExchange=i;
						}
					}
				}
				//System.out.println();
				sb.append(" bids from buyers " +inst + "  ");
				for(int j=0;j<bids.size();j++) {
					sb.append(bids.get(j).getPrice() +",");
					if(i==0 && j==0) {
						mm.max=bids.get(j).getPrice();//Initialize to some figure
					}else {
						if (bids.get(j).getPrice()>mm.max) {
							mm.max=bids.get(j).getPrice();
							mm.indexMaxExchange=i;
						}
					}
				}			

			}			
		}


		if(mm.max<mm.min
				||mm.max==0.0 ||mm.min==0.0 ) {
			//logger.info("using last price for " + symbol);
			mm=computeMinMax(lastPrice);
		}else {
			//logger.info("using market dept price for " + symbol + " " + sb.toString());
		}

		//MinMax mm= Util.computeMinMax(price);
		//for line below it does not matter if it's mm.max or mm.min because they are approximately the same
		double limitOffset= getLimitOffset( mm.max,limitOffsetPercentage);

		mm=Util.setBuySellLimit(mm, limitOffset);
		//System.out.println();
		//sb.append(" indexMinExchange="+ mm.indexMinExchange + " buy="+ mm.buyLimit );
		//sb.append(" indexMaxExchange="+ mm.indexMaxExchange + " sell="+ mm.sellLimit );

		//logger.info(sb.toString());
		return mm;
	}





	public static double getLimitOffset(double price, double limitOffsetPercentage){
		double limitOffset=Util.getPercentage( price,limitOffsetPercentage);
		if(limitOffset<0.05) {
			limitOffset=0;
		}else {
			limitOffset=0.05;//sometime the order is rejected if the figure is not a multiple of 0.05
		}
		return limitOffset;
	}


	/* Given an array of prices of a share across various exchanges return the
	  indices and values of the maximum and minimum	  
	 *  /

	public static MinMax computeMinMax(double[] price){
		MinMax mm= new MinMax();
		//StringBuilder sb = new StringBuilder();
		//sb.append("price array=");							
		for(int i=0;i<price.length;i++) {
			//sb.append(price[i] +", ");			
			if(price[i]==0.0) {
				continue;
			}

			if(i==0) {
				mm.min=price[i];
				mm.max=price[i];
			}else {
				if (price[i]<mm.min) {
					mm.min=price[i];
					mm.indexMinExchange=i;
				}else if(price[i]>mm.max) {
					mm.max=price[i];
					mm.indexMaxExchange=i;
				}
			}

		}	
		//logger.info(sb.toString());
		return mm;
	}


	public static MinMax setBuySellLimit(MinMax mm, double limitOffset){		
		mm.buyLimit=mm.min+limitOffset;
		mm.sellLimit=mm.max-limitOffset;
		return mm;
	}

	static long getOrderAge(Order o){
		Date d =o.orderTimestamp;
		if(o.orderTimestamp==null) {
			logger.info("Order timestamp null. because it's a new order." + o.orderId);
			return 0;
		}

		LocalDateTime dt = LocalDateTime.now();
		ZonedDateTime fromZonedDateTime = dt.atZone(ZoneId.of("Asia/Kolkata"));
		ZonedDateTime toZonedDateTime = dt.atZone(ZoneId.of("America/New_York"));
		long diff = Duration.between(fromZonedDateTime, toZonedDateTime).toMillis();

		long age=System.currentTimeMillis() + diff - d.toInstant().toEpochMilli();

		//System.out.println("order age in minutes = "+ age/(60*1000) );
		return age;
	}

	static long getOrderAgeSinceLastModified(Order o){
		long age=0;
		if(Account.modifiedOrders.containsKey(o.orderId)) {
			age=System.currentTimeMillis()-Account.modifiedOrders.get(o.orderId);
		}else {			
			age=getOrderAge(o);
		}
		return age;
	}

	static long getOrderAgeSinceLastCancelled(Order o){
		long age=0;
		if(Account.cancelledOrders.containsKey(o.orderId)) {
			age=System.currentTimeMillis()-Account.cancelledOrders.get(o.orderId);
		}else {			
			age=getOrderAge(o);
		}
		return age;
	}

*/
	public static ZonedDateTime getIndiaTime(){
		ZonedDateTime indiaTime = Instant.now().atZone(ZoneId.of(Variables.zoneId));
		//ZonedDateTime indiaTime = Instant.now().atZone(ZoneId.of("America/New_York"));
		// System.out.print(indiaTime);
		return indiaTime;
	}

	//	public static boolean areTokensCurrent(){
	//		//example format 2019-05-06T07:00:45.930+05:30[Asia/Kolkata]
	//		String currentIndiaDate=Util.getIndiaTime().toString().substring(0, Util.getIndiaTime().toString().indexOf('T'));
	//		return Variables.tokenDate.equals(currentIndiaDate);
	//	}
	
	public static boolean isExchangeOpen() {
		return true;
	}

	public static boolean isExchangeOpen1() {

		ZonedDateTime indiaTime=getIndiaTime();
		int h=indiaTime.getHour();
		int m=indiaTime.getMinute();

		boolean afterOpenTime=false;
		boolean beforeCloseTime=false;
		boolean correctDayOfweek=false;

		if(h==Variables.exchangeOpenHour && m>Variables.exchangeOpenMinute) {				
			afterOpenTime=true;		
		}else if(h>Variables.exchangeOpenHour){
			afterOpenTime=true;
		}

		if(h<Variables.exchangeCloseHour) {				
			beforeCloseTime=true;		
		}else if(h==Variables.exchangeCloseHour && m<Variables.exchangeCloseMinute){
			beforeCloseTime=true;
		}

		//Mon =1, fri=5, sat= 6 sun=7
		if (Variables.exchangeFirstDayofWeek<=indiaTime.getDayOfWeek().getValue()
				&& indiaTime.getDayOfWeek().getValue()<= Variables.exchangeLastDayofWeek) {
			correctDayOfweek=true;
		}

		if(correctDayOfweek && afterOpenTime && beforeCloseTime) {
			if(!isHoliday()) {
				return true;
			}
		}

		return false;
	}
	
	static boolean isHoliday() {
        ZonedDateTime zoneNow = ZonedDateTime.now(TimeZone.getTimeZone(Variables.zoneId).toZoneId());
        for (int i=0; i<Variables.holidays.length;i++){            
            ZonedDateTime h = ZonedDateTime.parse(Variables.holidays[i] + Variables.indiaTimeStr );
            if(h.getYear()==zoneNow.getYear() && h.getMonth().equals(zoneNow.getMonth()) && h.getDayOfMonth()==zoneNow.getDayOfMonth()){
            	logger.info(Variables.holidays[i] + " is a holiday");
                return true;
            }
        }
        return false;
	
	}

/*

	static void writeQuotesToLog(Map<String, Quote> quotes, String[] symbolArr, String[] exchangeArr){
		try {
			if(quotes==null) {
				return;
			}
			//NumberFormat formatter = new DecimalFormat();
			DecimalFormat df = new DecimalFormat("###0.000");
			for(int i=0;i<symbolArr.length;i++) {
				
				MinMax mm= Util.computeMinMax(symbolArr[i], exchangeArr, quotes, Variables.limitOffsetPercentage );
				String s=exchangeArr[mm.indexMinExchange]+":"+symbolArr[i];
				if(quotes.get(s)!=null) {
					rawdatalogger.info(exchangeArr[mm.indexMaxExchange]+":"+symbolArr[i] + "="+mm.max +"," 
							+exchangeArr[mm.indexMinExchange]+":"+symbolArr[i] + "="+mm.min +","
							+df.format((mm.max-mm.min)*100/mm.max)+","+quotes.get(s).volumeTradedToday
							);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static void writeQuotesToLog(Map<String, Quote> quotes, Log l){
		if(l==null);{
			l=rawdatalogger;
		}
		for (Entry<String, Quote> entry : quotes.entrySet()) {
			l.info(entry.getKey() + "," + entry.getValue().lastPrice
					+ "," + entry.getValue().volumeTradedToday
					);
		}

	}

	static void writeQuotesToRegularlog(Map<String, Quote> quotes){	
		writeQuotesToLog(quotes,logger);

	}

	static void writeCSVHeader() {
		rawdatalogger.info("Excahnge1,Excahnge2,percentage,volumeTradedToday");
	}



	public static void collectData(ApiController apiController){

		try {
			String[] a1=Arrays.copyOfRange(Variables.symbolArrNIFTY, 0, 100);
			String[] a2=Arrays.copyOfRange(Variables.symbolArrNIFTY, 101, 200);
			String[] a3=Arrays.copyOfRange(Variables.symbolArrNIFTY, 201, 300);
			String[] a4=Arrays.copyOfRange(Variables.symbolArrNIFTY, 301, 400);
			String[] a5=Arrays.copyOfRange(Variables.symbolArrNIFTY, 401, Variables.symbolArrNIFTY.length-1);



			Map<String, Quote> quotes =Operations.getQuotes(apiController,a1,Variables.exchangeArr);
			Util.writeQuotesToLog(quotes,a1,Variables.exchangeArr);
			
			quotes =Operations.getQuotes(apiController,a2,Variables.exchangeArr);
			Util.writeQuotesToLog(quotes,a2,Variables.exchangeArr);
			
			quotes =Operations.getQuotes(apiController,a3,Variables.exchangeArr);
			Util.writeQuotesToLog(quotes,a3,Variables.exchangeArr);
			
			quotes =Operations.getQuotes(apiController,a4,Variables.exchangeArr);
			Util.writeQuotesToLog(quotes,a4,Variables.exchangeArr);
			
			quotes =Operations.getQuotes(apiController,a5,Variables.exchangeArr);
			Util.writeQuotesToLog(quotes,a5,Variables.exchangeArr);
		} catch (Exception e) {
			logger.error("Exception in collectData",e);
			e.printStackTrace();
		}
	}

*/

	//if exchange is closed and if it is not a test. stopp the app
	public static boolean stopApp() {
		if(!Variables.isTest && !Util.isExchangeOpen()) {
			return true; // break if exchange is closed and if it is not a test
		}

		return false;
	}
	/**
	 * 
	 * @param num the number
	 * @param p the percentage.
	 * @return find percentage p of num
	 * /
	static double getPercentage(double num, double p){

		return (num*p)/100;
	}

*/
	public static ApiController obtainApiController() {
		
		IConnectionHandler connectionHandler= new IConnectionHandler(){

			@Override public void connected() {
				show( "connected");
				System.out.println("in conneceted  ...");

//				apiController.reqCurrentTime(time -> show( "Server date/time is " + Formats.fmtDate(time * 1000) ));
//
//				apiController.reqBulletins( true, (msgId, newsType, message, exchange) -> {
//					String str = String.format( "Received bulletin:  type=%s  exchange=%s", newsType, exchange);
//					show( str);
//					show( message);
//				});
			}

			@Override public void disconnected() {
				show( "disconnected");

			}

			@Override public void accountList(List<String> list) {
				show( "Received account list");
				//m_acctList.clear();
				//m_acctList.addAll( list);
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
		};
		
		ApiController a=new ApiController( connectionHandler);
		
		return a;
	
}
	/*
	public static ApiController obtainApiController() 
	{ 
		ApiController apiController = null;
		try {

			Variables.loadVariables(null);
			Auth.loadVariables();

			apiController = new KiteConnect(Variables.api_key);
			logger.info("obtaining apiController ");
			apiController.setUserId(Variables.userid);

			//Enable logs for debugging purpose. This will log request and response.
			//apiController.setEnableLogging(true);

			// Get login url.The url looks like this https://kite.trade/connect/login?api_key=xxxxx&v=3
			String url = apiController.getLoginURL();

			boolean request_tokenValid=true;
			try {
				apiController.setAccessToken(Auth.accessToken);
				apiController.setPublicToken(Auth.publicToken);
				apiController.getHoldings();
			} catch (Exception e) {
				request_tokenValid=false;
				//generate token here
			}

			if(!request_tokenValid) {
				try {
					//manually obtain request_token
					//User user =  apiController.generateSession(Auth.request_token, Variables.api_secret);									

					//automated generation of request_token
					String request_token =getRequestToken(url);
					User user =  apiController.generateSession(request_token, Variables.api_secret);

					apiController.setAccessToken(user.accessToken);
					
					apiController.setPublicToken(user.publicToken);
					
					Auth.writeTokens(user.accessToken, user.publicToken);


				} catch (Exception e) {
					//String url="https://kite.trade/connect/login?api_key="+Variables.api_key+"&v=3";
					logger.info("Generate request_token using:");
					logger.info(url);
					//logger.error("Please generate request_token using " + url,e);
					e.printStackTrace();
				}
			}




			// Set session expiry callback.
			apiController.setSessionExpiryHook(new SessionExpiryHook() {
				@Override
				public void sessionExpired() {
					System.out.println("session expired");
				}
			});

		} catch (Exception e) {
			logger.error("Most likely a token exception",e);e.printStackTrace();
		}
		return apiController;
	}


	public static Map<String, Margin> getMargins(ApiController apiController) {
		Map<String, Margin>  margins=null;
		try {
			 margins= apiController.getMargins();
		} catch (Exception e) {
			logger.info("apiController.getMargins() error message " + e.getMessage());
			apiController= Util.obtainApiController();
		} 		
		return margins;
	}
	
	*/
//
//	public static String getRequestToken(String url) {
//		logger.info("Generating request_token via selenium");
//		WebDriver  driver=null;
//		String request_token =null;
//		try {
//
//			System.setProperty("webdriver.gecko.driver","C:\\work\\geckodriver\\geckodriver.exe");
//			String u="(//input[@type='text'])[1]";
//			String p="(//input[@type='password'])[1]";
//			String b="//button[1]";
//
//
//			FirefoxOptions o= new FirefoxOptions();
//
//			driver = new FirefoxDriver(o);
//			driver.get(url);
//
//			WebDriverWait wait = new WebDriverWait(driver,20);
//
//			WebElement userid= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(u)));
//			WebElement pwd= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(p)));
//			WebElement btn= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(b)));
//
//			System.out.println("In first page");
//
//			userid.sendKeys(Variables.userid);
//			pwd.sendKeys(Variables.pwd);
//
//			btn.click();
//
//
//			System.out.println("after first button click");
//			System.out.println("5 sec wait...");
//			Thread.sleep(10000);// only this is working driver.manage().timeouts().implicitlyWait
//			WebElement btnContinue= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(b)));
//			WebElement pin= wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(p)));
//			//System.out.println("line3 ="+driver.getPageSource());
//			System.out.println("likely in second page");
//			pin.sendKeys(Variables.pin);  
//			System.out.println("putting pin");
//			btnContinue.click();
//			System.out.println("another 5 sec wait...");
//			Thread.sleep(10000);
//
//			System.out.println("after waiting for 10 sec");
//			wait.until(ExpectedConditions.urlContains("127.0.0.1"));
//			System.out.println("url is " + driver.getCurrentUrl());
//			String param = driver.getCurrentUrl().split("\\?")[1];
//			String[] pairs = param.split("&");
//			for (int i=0;i<pairs.length;i++) {
//				int idx = pairs[i].indexOf("=");
//				String key=URLDecoder.decode(pairs[i].substring(0, idx), "UTF-8");
//				String value=URLDecoder.decode(pairs[i].substring(idx + 1), "UTF-8");
//				if(key.equals("request_token")) {
//					request_token=value;
//					System.out.println("request_token  is "+value);
//					break;
//				}
//
//			}
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}finally {
//			driver.quit();
//		}
//
//
//		return request_token;
//	}
//
//
//
//






}


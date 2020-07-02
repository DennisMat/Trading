package com.dennis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ib.client.Order;
import com.ib.controller.ApiController;


public class Account {

	/*


https://kite.trade/connect/login?api_key=97h12exoc4ne48og&v=3

	 */
	private static Log logger = LogFactory.getLog(Account.class);
	private static  AtomicLong remainingCashCached=new AtomicLong();
	private static AtomicIntegerArray sellableQuantityCached=null;
	private static AtomicIntegerArray currentInventoryCached=null;
	private static Map<String,Order>  ordersForTheDay=null;

	//Has orders and time that they were modified
	public static Map<String, Long> modifiedOrders= new HashMap<String, Long>();
	
	//Has orders and time that they were cancelled
	public static Map<String, Long> cancelledOrders= new HashMap<String, Long>();



	public static void subtractCashFromCached(double cash){
		remainingCashCached.getAndAdd(-(int) cash);
	}



	public static double getCashCached(){
		return (double)remainingCashCached.get();
	}


	public static void setCashCached(double cash){	
		remainingCashCached.set((int)cash);
	}

	/*
	public static double getCashfromRemote(ApiController apiController){
		double cash = 0;
	
		try {
			Map<String, Margin>  margins= Util.getMargins(apiController);
			if(margins!=null) {
				Margin m=margins.get("equity");// 3 types: cash, collateral, equity
				cash=Double.parseDouble(m.net);
			}
			
		} catch (Exception e) {	
			//often apiController.getMargins() can throw an exception, don't know why
			logger.info("error message " + e.getMessage());
			logger.error("",e);e.printStackTrace();
			apiController= Util.obtainApiController();
		}

	
		return cash;
	}


	public static void updateCashCache(ApiController apiController){
		double cashFromRemote=getCashfromRemote(apiController);
		setCashCached(cashFromRemote);		
		//logger.info("Cash From Remote = " +cashFromRemote + " Cash From Cache  = " +getCashCached() );

	}

	public static void subtractfromSellableQuantityCached(int index, int delta){
		sellableQuantityCached.getAndAdd(index, -delta);
	}

	public static void setSellableQuantityCached(int index, int quantity){		
		sellableQuantityCached.set(index, quantity);
	}


	public static int getSellableQuantityCached(int index) {
		return sellableQuantityCached.get(index);
	}


	public static void setCurrentInventoryCached(int index, int quantity){		
		currentInventoryCached.set(index, quantity);
	}

	public static void addToCurrentInventoryCached(int index, int delta){
		currentInventoryCached.getAndAdd(index, delta);
	}

	public static void subtractfromCurrentInventoryCached(int index, int delta){
		currentInventoryCached.getAndAdd(index, -delta);
	}



	public static void addToCurrentInventoryCached(Order order){		
		for(int i=0;i<Variables.symbolArr.length;i++) {			
			if(order.tradingSymbol.equals(Variables.symbolArr[i])) {
				try {
					currentInventoryCached.set(i, 
							currentInventoryCached.get(i)+Integer.parseInt(order.filledQuantity));
				} catch (NumberFormatException e) {
				}

			}
			break;
		}
	}

	public static void removefromCurrentInventoryCached(Order order){		
		for(int i=0;i<Variables.symbolArr.length;i++) {			
			if(order.tradingSymbol.equals(Variables.symbolArr[i])) {
				try {
					currentInventoryCached.set(i, 
							currentInventoryCached.get(i)-Integer.parseInt(order.filledQuantity));
				} catch (NumberFormatException e) {
				}
			}
			break;
		}

	}

	public static void setCurrentInventoryCached(int[] quantity){
		currentInventoryCached=new AtomicIntegerArray(quantity);
	}

	public static int getCurrentInventoryCached(int index) {
		return currentInventoryCached.get(index);
	}




	public static void setSellableQuantityCached(int[] quantity){		
		sellableQuantityCached=new AtomicIntegerArray(quantity);
	}


	public static int[] getAndSetSellableAndInventoryQuantity(String[] symbolArr,ApiController apiController){
		int[] sellableQuantity = new int[symbolArr.length];
		int[] currentInventory = new int[symbolArr.length];	
		//StringBuilder sb = new StringBuilder();
		//sb.append("sellable quantity array=");	
		try {
			List<Holding> holdings = apiController.getHoldings();
			for(int i=0;i<holdings.size();i++) {
				Holding h =holdings.get(i);
				for(int j=0;j<symbolArr.length;j++) {
					if(h.tradingSymbol.equals(symbolArr[j])){

						int q=Integer.parseInt(h.quantity);
						int t1=Integer.parseInt(h.t1Quantity);
						sellableQuantity[j]=q;
						currentInventory[j]=	currentInventory[j]+q+t1;
						//sb.append(symbolArr[j]+ ":" +h.quantity +",");
					}
				}
			}
			//logger.info(sb.toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		if(!Variables.isTest) {
			sellableQuantityCached=new AtomicIntegerArray(sellableQuantity);	
		}else {
			if(sellableQuantityCached==null) {
				int[] fotTestingOnly= new int[Variables.symbolArr.length];
				for(int i=0;i<fotTestingOnly.length;i++) {
					fotTestingOnly[i]=4;// this is a random value, not significance to it.
				}
				sellableQuantityCached=new AtomicIntegerArray(fotTestingOnly);

			}

		}
		 setCurrentInventoryCached(currentInventory);

		return sellableQuantity;
	}

	public static String getOrderStatus(String orderId){
		String status="";
		if(orderId==null || orderId.equals("")){
			status="orderid is null or empty";
		}
		return ordersForTheDay.get(orderId).status;
	}

	public static List<Order>  getExistingOrders(String symbol){
		List<Order> existingOrders= new ArrayList<Order>();			
		for (Order or : ordersForTheDay.values()) {
			if(or.tradingSymbol.equals(symbol)) {
				existingOrders.add(or);
			}
		}
		return existingOrders;

	}

	public static void addOrReplaceExistingOrdersForTheDay(Order o){
		if(ordersForTheDay==null) {
			ordersForTheDay=new ConcurrentHashMap<String,Order>();
		}

		ordersForTheDay.put(o.orderId,o);
	}


	public static Map<String, List<Order>> getExistingOpenOrdersForASymbol(String symbol){
		Map<String, List<Order>> existingOrdersForASymbol=new HashMap<String, List<Order>> ();
		List<Order> existingOrdersBuy= new ArrayList<Order>();
		List<Order> existingOrdersSell= new ArrayList<Order>();

		try {
			for (Order o : ordersForTheDay.values()) {
				if(o.status.equals(ConstantValues.ORDER_STATUS_OPEN)
						|| (o.status.equals(ConstantValues.ORDER_STATUS_JUST_PUT))) {
					if(o.tradingSymbol.equals(symbol)) {
						if(o.transactionType.equals(Constants.TRANSACTION_TYPE_BUY)) {					
							existingOrdersBuy.add(o);						
						}
						if(o.transactionType.equals(Constants.TRANSACTION_TYPE_SELL)) {
							existingOrdersSell.add(o);
						}

					}
				}
			}


			existingOrdersForASymbol.put("buy", existingOrdersBuy);
			existingOrdersForASymbol.put("sell", existingOrdersSell);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return existingOrdersForASymbol;

	}

	public static List<Order> getExistingOpenBuyOrdersForASymbol(String symbol){
		return getExistingOpenOrdersForASymbol(symbol).get("buy");
	}
	public static void updateTodaysOrders(ApiController apiController){


		try {
			List<Order> immutableOrderList= apiController.getOrders();
			ordersForTheDay=new ConcurrentHashMap<String,Order> ();
			//we do this because the incoming list in immutable and cannot be added to
			for(int i=0;i<immutableOrderList.size();i++) {
				ordersForTheDay.put(immutableOrderList.get(i).orderId,immutableOrderList.get(i));
			}
		} catch (Exception e) {
			logger.error("",e);e.printStackTrace();
			apiController= Util.obtainApiController();
		}
	}

	public static boolean isBuy(ApiController apiController, int i, int currentInventory, Order orderSell,
			int buyQuantity, boolean isCashSufficient, Map<String, Integer> existingOrdersCount) {
		boolean isBuy = false;
		int existingBuyorders=0;
		if(existingOrdersCount!=null && existingOrdersCount.containsKey("buy")) {
			existingBuyorders=existingOrdersCount.get("buy");
		}

		if(buyQuantity>0 && isCashSufficient){
//			if((currentInventory + existingBuyorders)
//					< Variables.maxInventoryArr[i]*Variables.inventoryThreshold) {
			if((currentInventory + existingBuyorders)< Variables.maxInventoryArr[i]) {
				isBuy=true; //If inventory is less than this threshold buy else check status.
				//				logger.info("Inventory less that " + Variables.inventoryThreshold 
				//						+ " of max hence will buy, sellableQuantity=" + sellableQuantity
				//						+ " maxInventory=" + Variables.maxInventoryArr[i]);
			}else {
				logger.info("Will not buy "+Variables.symbolArr[i]+" inventory more than " 
						+ " maxInventory=" + Variables.maxInventoryArr[i]
						+ " currentInventory=" + currentInventory
						+ " existingBuyorders="+existingBuyorders
						);
			}
		}
		if(isBuy) {
			if(existingOrdersCount==null){
				isBuy=true;
			}else if((existingOrdersCount!=null && existingOrdersCount.get("buy")<Variables.maxOpenOrdersArr[i])) {
				isBuy=true;
			}else {
				logger.info("Will not buy "+Variables.symbolArr[i]+" because of existing buy orders. Buy order count = "+ existingOrdersCount.get("buy") );
				isBuy=false;
			}
		}


		return isBuy;
	}

	public static boolean isSell(int sellableQuantity,int i, Map<String, Integer> existingOrdersCount) {

		boolean isSell = false;
		if(sellableQuantity>0) {
			isSell=true;
		}

		if(isSell) {
			if(existingOrdersCount==null){
				isSell=true;
			}else if((existingOrdersCount!=null && existingOrdersCount.get("sell")<Variables.maxOpenOrdersArr[i])) {
				isSell=true;
			}else {
				logger.info("Will not sell "+Variables.symbolArr[i]+" because of existing sell orders. Existing sell order count = "+ existingOrdersCount.get("sell") );
				isSell=false;
			}
		}


		return isSell;
	}


	public static boolean isCancelOrder(int existingSellOrderCount, 
			int existingBuyOrderCount, int maxOpenOrders,Order o){
		boolean isCancelOrder = false;

		if(existingSellOrderCount>maxOpenOrders){
			//if there are too many orders, cancel the order,  do not modify it.
			logger.info("total sell orders are more than " + maxOpenOrders + " hence will cancel order");
			isCancelOrder = true;
		}
		if(existingBuyOrderCount>maxOpenOrders){
			//if there are too many orders, cancel the order,  do not modify it.
			logger.info("total buy orders are more than " + maxOpenOrders + " hence will cancel order");
			isCancelOrder = true;

		}

		if(Util.getOrderAgeSinceLastModified(o)> Variables.timeToWaitForCancellingOrder*1000){
			logger.info("Order has not been executed for " + Variables.timeToWaitForCancellingOrder/60 +"  minutes hence will be cancelled, orderid=" + o.orderId);
			isCancelOrder = true;
		}

		if(o.statusMessage!=null && o.statusMessage.contains("multiple")) {
			//full message string is "Order price is not a multiple of tick size "
			isCancelOrder = true;
		}
		return isCancelOrder;

	}

	public static boolean isModify(OrderParams modifiedOrderParams,Order o){

		double oldPrice=Double.parseDouble(o.price);

		double delta=0.0001;
		boolean isModify=false;
		//either price has to be different or exchange has to be diffrent
		if(oldPrice!=modifiedOrderParams.price || !modifiedOrderParams.exchange.equals(o.exchange)) {

			if((oldPrice+delta)<modifiedOrderParams.price || modifiedOrderParams.price< (oldPrice-delta) 
					|| !modifiedOrderParams.exchange.equals(o.exchange)
					) { 
				isModify=true;
			}else{
				logger.info("The " + modifiedOrderParams.transactionType + " order with orderId= " 
						+ o.orderId +" will not be modified from " + oldPrice+" to "+ o.price
						+ " because of a tiny price difference");
			}
		}else {
			logger.info("The " + modifiedOrderParams.transactionType + " order with orderId= " 
					+ o.orderId +" will not be modified from " + oldPrice+" to "+ o.price
					+ " because the old and new price is equal");
		}
		if(!modifiedOrderParams.exchange.equals(o.exchange)){
			logger.info("exchange is different hence order will be modified ");
		}

		if(isModify) {
			if(Util.getOrderAgeSinceLastModified(o)> Variables.timeToWaitForModifyingOrder*1000){
				isModify=true;// it is old enough to modify
			}else {
				isModify=false;
			}
		}		
		return isModify;		
	}

	static int computeBuyableQuantity(String[] symbolArr, int i, int currentInventory) {
		int buyQuantity=0;
		int existingBuyorders=Account.getExistingOpenBuyOrdersForASymbol(symbolArr[i]).size();
		if((currentInventory + existingBuyorders)<Variables.maxInventoryArr[i]) {
			buyQuantity=Variables.tradeQuantityArr[i];
		}

		if(Variables.incrementTradeQuantityArr[i]>0){
			buyQuantity=Variables.incrementTradeQuantityArr[i];
		}
		return buyQuantity;
	}

	static int computeSellableQuantity(int i, int currentInventory) {
		//currentInventory includes purchases that have not been settled yet so currentInventory cannot be used.
		int sellableQuantity=Account.getSellableQuantityCached(i);
		if(Variables.incrementTradeQuantityArr[i]>0 
				&& sellableQuantity>=Variables.incrementTradeQuantityArr[i]){
				sellableQuantity=Variables.incrementTradeQuantityArr[i];
		}
		return sellableQuantity;
	}
	


	static void loadAccount(ApiController apiController) {
		Trading.lastChecked.set(2, System.currentTimeMillis());

		if(apiController==null) {
			apiController= Util.obtainApiController();
		}



		getAndSetSellableAndInventoryQuantity(Variables.symbolArr,apiController);
		updateCashCache(apiController);
		updateTodaysOrders(apiController);

	}
	
	
	*/

}

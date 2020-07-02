package com.dennis;

import static org.apache.commons.text.CharacterPredicates.DIGITS;
import static org.apache.commons.text.CharacterPredicates.LETTERS;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.TickAttrib;
import com.ib.client.TickType;
import com.ib.controller.ApiController;
import com.ib.controller.ApiController.TopMktDataAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.text.RandomStringGenerator;


public class Operations {

	private static Log logger = LogFactory.getLog(Operations.class);

	public static Map<String, Quote> getQuotes(ApiController apiController,
			String[] symbolArr,
			String[] exchangeArr
			){
		Map<String, Quote> quotes = null;
		try {

			// Get quotes returns quote for desired tradingsymbol.
			//String[] instruments = {"256265","BSE:INFY", "NSE:APOLLOTYRE", "NSE:NIFTY 50"};

			/*
			String[] instruments= new String[exchangeArr.length*symbolArr.length];
			int k=0;
			for(int i=0;i<exchangeArr.length;i++) {
				for(int j=0;j<symbolArr.length;j++) {
					instruments[k]=exchangeArr[i]+ ":" + symbolArr[j];
					k++;
				}
			}
			
			*/

			for(int i=0;i<exchangeArr.length;i++) {
				for(int j=0;j<symbolArr.length;j++) {
					
				       Contract contract = new Contract();
			           contract.symbol(symbolArr[j]);
			           contract.secType("STK");
			           contract.currency("USD");
			           contract.exchange(exchangeArr[i]);//"SMART"
			           contract.primaryExch(exchangeArr[i]);//"ISLAND"
			           
			           final String symbol= symbolArr[j];
			    	   TopMktDataAdapter m_stockListener = new TopMktDataAdapter() {
				            @Override public void tickPrice(TickType tickType, double price, TickAttrib attribs) {
				                //if (tickType == TickType.LAST || tickType == TickType.DELAYED_LAST) {
				                	System.out.println(symbol +" ticker price= " + price);
				               // }
				            }
				        };

					apiController.reqTopMktData(contract, "", false, false, m_stockListener);
					
				}
			}




		}catch (Exception e) {
			logger.error("",e);e.printStackTrace();
			apiController= Util.obtainApiController();
		}

		return quotes;
	}

/*
	public static Order placeOrder(com.ib.controller.ApiController apiController, 
			String tradingsymbol, int i, String exchange, 
			double price, int quantity, String transactionType, Log logger)  {
		if(!Util.isExchangeOpen()) {
			logger.error("Exchange is closed and order will not be placed");
			return null;
		}

		if(quantity<1) {
			logger.error("Quantity is "+quantity+ "so order will not be placed");
			return null;
		}


		com.ib.client.Order order=null;
		try {
			OrderParams orderParams = new OrderParams();
			orderParams.quantity = quantity;
			if(Variables.tradeTypeArr[i]=='m') {
				orderParams.orderType =Constants.ORDER_TYPE_MARKET;
				orderParams.price = 0.0;
			}else {
				orderParams.orderType = Constants.ORDER_TYPE_LIMIT;
				orderParams.price = price;
			}

			orderParams.tradingsymbol = tradingsymbol;
			orderParams.product = Constants.PRODUCT_CNC;
			orderParams.exchange = exchange;
			orderParams.transactionType = transactionType;//buy or sell
			orderParams.validity = Constants.VALIDITY_DAY;

			/*
			//orderParams.triggerPrice = 0.0;
			RandomStringGenerator generator = new RandomStringGenerator.Builder()
					.withinRange('0', 'z')
					.filteredBy(LETTERS, DIGITS)
					.build();
			String tag = generator.generate(8);
			orderParams.tag = tag; //tag is optional and it cannot be more than 8 characters and only alphanumeric is allowed
			 * /
			StringBuilder sBefore= new StringBuilder(tradingsymbol+" currentInventory before = " +  Account.getCurrentInventoryCached(i));
						
			order = apiController.placeOrder(orderParams, Constants.VARIETY_REGULAR);
			Sound.playOrderPlaced();
			

			if(order!=null && !order.orderId.equals("")) {
				StringBuilder sb= new StringBuilder("Placed "+transactionType+" order.OrderType="+orderParams.orderType+", Symbol:"+ exchange+ ":"+tradingsymbol 
						+ ",limitPrice:"+ orderParams.price + " , quantity:" + quantity + " orderId= " + order.orderId + " ");
				sb.append(sBefore);
				// these 3 values below are set, because they are null right after an order is put.
				order.tradingSymbol=tradingsymbol;
				order.transactionType=transactionType;
				order.status=ConstantValues.ORDER_STATUS_JUST_PUT;
				Account.addOrReplaceExistingOrdersForTheDay(order);

				// this will ensure a simultaneous sell and buy, by ensuring that is inventory 
				//is updated even if the sell or buy is not complete.
				if(transactionType.equals(Constants.TRANSACTION_TYPE_BUY)) {
					Account.addToCurrentInventoryCached(i, quantity);
				}else if(transactionType.equals(Constants.TRANSACTION_TYPE_SELL)) {
					Account.subtractfromCurrentInventoryCached(i, quantity);
				}

				sb.append(" currentInventory after = " +  Account.getCurrentInventoryCached(i));
				logger.info(sb.toString());

			}

		} catch (Exception e) {
			logger.error("",e);		
			e.printStackTrace();
			logger.info("attempting to obtain apiController again. ");
			apiController=Util.obtainApiController();
		}
		return order;

	}


	public static Order placeBuyOrder(ApiController apiController, 
			String tradingsymbol,int i, String exchange, 
			double price, int quantity)  {

		Order or=Operations.placeOrder(apiController, 
				tradingsymbol, i, exchange, 
				price,  quantity,Constants.TRANSACTION_TYPE_BUY,logger
				); 

		return or;
	}

	public static Order  placeSellOrder(ApiController apiController, 
			String tradingsymbol,int i, String exchange, 
			double price,  int quantity)  {
		Order or=null;
		if(price>0.10) {//make sure that  price is not zero
			or=Operations.placeOrder(apiController, 
					tradingsymbol, i, exchange, 
					price,  quantity,Constants.TRANSACTION_TYPE_SELL,logger
					); 
		}
		return or;
	}

	public static Order  placeCancelOrder(ApiController apiController, Order or)  {	
		try {

			StringBuilder sb= new StringBuilder("In  placeCancelOrder or.symbol=" +or.symbol+", orderId " + or.orderId 
					+" order status = " +or.status );
			
			
			if(!or.status.contains(ConstantValues.ORDER_STATUS_CANCELLED)){
				
				if(Util.getOrderAgeSinceLastCancelled(or)> Variables.timeToWaitAfterCancellingOrder*1000){
					or=apiController.cancelOrder(or.orderId, Constants.VARIETY_REGULAR);
					if(or!=null && !or.orderId.equals("")) {
						Account.cancelledOrders.put(or.orderId, System.currentTimeMillis());
//						logger.info("CANCEL order on "+ or.symbol+", orderId " + or.orderId 
//								+ " placed.");
						sb.append(" after placing cancel  order status = " +or.status );
					}else {
						sb.append(" cancel not placed   order status = " +or.status );
					}
				}else {
					sb.append("previous cancel session still in progress");
				}
				

			}
			
			logger.info(sb.toString());
			
		} catch (Exception e) {
			logger.error("",e);e.printStackTrace();
			logger.info("order "+or.orderId+" could not be cancelled.Message = " + e.getMessage() );
			apiController= Util.obtainApiController();
		}

		return or;
	}


	public static void  iterateThroughAllStocksAndSellBuy(
			ApiController apiController,
			String[] symbolArr,
			String[] exchangeArr,
			double[] minPriceDifferencePercentageArr, double limitOffsetPercentage, 
			Map<String, Quote> quotes) {

		if(Variables.isTest) {
			iterateThroughAllStocksAndSellBuyTest(apiController, Variables.symbolArr, 
					exchangeArr, minPriceDifferencePercentageArr, 
					limitOffsetPercentage, quotes);
		}else {
			iterateThroughAllStocksAndSellBuyReal(apiController, Variables.symbolArr, 
					exchangeArr, minPriceDifferencePercentageArr, 
					limitOffsetPercentage, quotes);

		}
	}

	public static void  iterateThroughAllStocksAndSellBuyTest(
			ApiController apiController,
			String[] symbolArr,
			String[] exchangeArr,
			double[] minPriceDifference, double limitOffsetPercentage, 
			Map<String, Quote> quotes) {

		try {Thread.sleep(1000);} catch (InterruptedException e) {}
		System.out.println("Simulating buy/sell");
		Sound.playOrderPlaced();
		int r=(new Random()).nextInt(Variables.symbolArr.length);
		System.out.println("test order placed at index = " + r);
		Account.setSellableQuantityCached(r, 0);
	}

	public static void iterateThroughAllStocksAndSellBuyReal(
			ApiController apiController,
			String[] symbolArr,
			String[] exchangeArr,
			double[] minPriceDifferencePercentage, double limitOffsetPercentage, 
			Map<String, Quote> quotes) {

		for(int i=0;i<symbolArr.length;i++) {
			if(Variables.doNothingFlag[i] || Variables.dumpFlagArr[i]) {
				continue;
			}

			MinMax mm= Util.computeMinMax(symbolArr[i], exchangeArr,quotes, limitOffsetPercentage );
			int currentInventory = Account.getCurrentInventoryCached(i);
			int sellableQuantity = Account.computeSellableQuantity(i,currentInventory);



			//sell all stocks of this stock 
			if( Variables.sellAllArr[i]==true) {
				if(sellableQuantity==0) {
					continue;// break from this loop in for statement and go to next loop;
				}else if(sellableQuantity>0){
					logger.info("Going to only sell  shares of " + symbolArr[i]);
					Order orderSell=Operations.placeSellOrder(apiController, 
							symbolArr[i],i,exchangeArr[mm.indexMaxExchange], 
							mm.sellLimit, sellableQuantity
							);					
				}	

			}

			if(Util.isTrade(mm,minPriceDifferencePercentage[i])) {

				Map<String, Integer> existingOrdersCount=null;
				List<Order> existingOrders = Account.getExistingOrders(symbolArr[i]);
				if(existingOrders.size()>0) {
					existingOrdersCount=modifyOrCancelOrders(apiController,existingOrders,i,mm);				
				}

				Order orderSell=null;
				if(Account.isSell(sellableQuantity, i,existingOrdersCount)){
					orderSell=Operations.placeSellOrder(apiController, 
							symbolArr[i],i,exchangeArr[mm.indexMaxExchange], 
							mm.sellLimit, sellableQuantity
							);
				}

				int buyQuantity = Account.computeBuyableQuantity(symbolArr, i, currentInventory);

				//logger.info(" for " + symbolArr[i] + " buyQuantity="+buyQuantity + " sellableQuantity="+sellableQuantity);
				boolean isCashSufficient = isCashsufficient(symbolArr, i, mm, buyQuantity);

				boolean isBuy = Account.isBuy(apiController, i, currentInventory, orderSell, buyQuantity,
						isCashSufficient, existingOrdersCount);

				if(isBuy) {
					Order orB = Operations.placeBuyOrder(apiController, 
							symbolArr[i], i, exchangeArr[mm.indexMinExchange], 
							mm.buyLimit, buyQuantity
							);

					if(orB!=null && !orB.orderId.equals("")) {
						Account.subtractCashFromCached(mm.min*buyQuantity);
						//logger.info("Order status of just placed orderid =" + orB.orderId + " is " + orB.status);
					}


				}else {
					System.out.println(symbolArr[i] + " will not be bought");
				}
			}
			System.out.print(".");
		}
	}


	public static Map<String, Integer> modifyOrCancelOrders(ApiController apiController,List<Order> existingOrders,int i,MinMax mm){
		int existingSellOrderCount=0;
		int existingBuyOrderCount=0;


		if(Variables.incrementTradeQuantityArr[i] >0) {
			for(int j=0;j<existingOrders.size();j++) {
				Order o=existingOrders.get(j);
				if(o.status.equals(ConstantValues.ORDER_STATUS_OPEN) 
						|| o.status.equals(ConstantValues.ORDER_STATUS_JUST_PUT)){
					OrderParams modifiedOrderParams =  new OrderParams();
					modifiedOrderParams.quantity = Variables.incrementTradeQuantityArr[i];
					if(Variables.tradeTypeArr[i]=='m') {
						modifiedOrderParams.orderType =Constants.ORDER_TYPE_MARKET;
						modifiedOrderParams.price = 0.0;
					}else {
						modifiedOrderParams.orderType = Constants.ORDER_TYPE_LIMIT;

					}
					if(o.transactionType.equals(Constants.TRANSACTION_TYPE_SELL)) {
						existingSellOrderCount++;
						modifiedOrderParams.price = mm.sellLimit;//change the selling price
						modifiedOrderParams.exchange = Variables.exchangeArr[mm.indexMaxExchange];
						modifiedOrderParams.transactionType = Constants.TRANSACTION_TYPE_SELL;
					}
					if(o.transactionType.equals(Constants.TRANSACTION_TYPE_BUY)) {
						existingBuyOrderCount++;
						modifiedOrderParams.price = mm.buyLimit;//change the buying price
						modifiedOrderParams.exchange = Variables.exchangeArr[mm.indexMinExchange];
						modifiedOrderParams.transactionType = Constants.TRANSACTION_TYPE_BUY;
					}

					boolean isCancelOrder= Account.isCancelOrder(existingSellOrderCount, 
							existingBuyOrderCount,Variables.maxOpenOrdersArr[i],o);

					if(isCancelOrder) {
						placeCancelOrder(apiController, o);
						continue;
					}

					modifiedOrderParams.tradingsymbol = o.tradingSymbol;
					modifiedOrderParams.product = Constants.PRODUCT_CNC;

					modifiedOrderParams.validity = Constants.VALIDITY_DAY;
					try { 
						if(o.price==null) {
							logger.info("Order id " + o.orderId + " will not be modified because o.price is null"); 
							continue;
						}
						String oldPrice=o.price;;
						boolean isModify=Account.isModify(modifiedOrderParams, o);

						if(isModify) {
							Order modifiedOrder = apiController.modifyOrder(o.orderId, modifiedOrderParams, Constants.VARIETY_REGULAR);
							if(modifiedOrder!=null && !modifiedOrder.orderId.equals("")) {
								Account.modifiedOrders.put(o.orderId, System.currentTimeMillis());
								logger.info("The " + modifiedOrderParams.transactionType + " order with orderId= " 
										+ o.orderId +" modified from " + oldPrice +" to "+ modifiedOrderParams.price + " old exchange : "
										+ o.exchange + " new exchange :" + modifiedOrderParams.exchange);
							}
						}
					}catch (Exception e) {
						logger.error("",e);e.printStackTrace();
						apiController= Util.obtainApiController();
					}
				}				
			}

			logger.info("Today's order count for " +Variables.symbolArr[i] + " is  " + existingOrders.size() 
			+". Existing open SellOrderCount="+existingSellOrderCount
			+" Existing open BuyOrderCount="+existingBuyOrderCount +" suggested buying price " 
			+ Variables.exchangeArr[mm.indexMinExchange] + ":" +  Variables.symbolArr[i]  + " at " + mm.buyLimit
			+ " sell price " 
			+ Variables.exchangeArr[mm.indexMaxExchange] + ":" +  Variables.symbolArr[i]  + " at " + mm.sellLimit
					);

		}

		Map<String, Integer> existingOrdersCount=new HashMap<String, Integer>();
		existingOrdersCount.put("sell", existingSellOrderCount);
		existingOrdersCount.put("buy", existingBuyOrderCount);


		return existingOrdersCount;
	}


	private static boolean isCashsufficient(String[] symbolArr, int i, MinMax mm, int buyQuantity) {
		boolean isCashSufficient=false;

		if(buyQuantity>0) {
			if((Account.getCashCached()-Variables.cashBuffer)>mm.min*buyQuantity){
				isCashSufficient=true;						
			}else{
				logger.info("Cash insufficent for "+ symbolArr[i]+". "
						+ "Cash needed=" + mm.min*buyQuantity 
						+ " Cash available for buying =" + (Account.getCashCached()-Variables.cashBuffer)
						+ " Total cash available= " + Account.getCashCached()
						+ " Cash Buffer =" + Variables.cashBuffer);
			}

		}
		return isCashSufficient;
	}


*/



}

function makeGrid(){

	$.ajax({
		url : "processrequest",
		type : "POST", 
		contentType : "application/json; charset=utf-8",
		dataType : 'json',
		headers: { 'action': 'get_stock_data'},
		success : function(linesData) {
			fillStockData(linesData);
		},
		error : function(xhr, resp, text) {
			console.log(xhr, resp, text);
		}
	});

}




function fillStockData(linesData){

	
	var stocksTable=$('#stocksTable').DataTable({
		//"bProcessing" : true,
		"aaData" : linesData.history,
		"aoColumns" : [
			{ "title" : "Trade Date", "mData": null, render: function ( data, type, row ) {
		       return data.date.year+'-'+data.date.month+'-'+data.date.day;
				//return "";
		    } }, 
			{"sTitle"  :"Stock Price","mData" : "stockPrice"},
			//{"sTitle"  : "Stock Value","mData" : "stockValue"},
			//{"sTitle"  : "Safe","mData" : "safe"},
			{"sTitle"  : "Cash","mData" : "cash"},
			{"sTitle"  : "Shares buy and sell","mData" : "sharesBoughtSold"},
			{"sTitle"  : "Stock Owned","mData" : "stockOwned"},
			//{"sTitle"  : "Portfolio Control","mData" : "portfolioControl"},
			//{"sTitle"  : "Buy or Sell Advise","mData" : "buyOrSellAdvice"},
			//{"sTitle"  : "Market Order","mData" : "marketOrder"},
			{"sTitle"  : "Cash Added","mData" : "interest"},
			{"sTitle"  : "Portfolio Value","mData" : "portfolioValue"},
			{"sTitle"  : "Buy/Sell","mData" : "action"}
			],
			dom : 'Bfrtip',
			buttons : [ {
				text : 'Add Parking',
				action : function(e, dt, node, config) {
					popUpParkingForm(property_id,null) ;	
				}
			} ]
	
	});	
	
	var buyAdvice="Buy "+ linesData.buyPredict.sharesBoughtSold  +" shares for " + linesData.buyPredict.stockPrice 
	var sellAdvice="Sell "+ linesData.sellPredict.sharesBoughtSold  +" shares for " + linesData.sellPredict.stockPrice 
	
	$( "#buyAdvice" ).text(buyAdvice);
	$( "#sellAdvice" ).text(sellAdvice);

}


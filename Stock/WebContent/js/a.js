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
		"aaData" : linesData,

		/*
		"aoColumns" : [
			{"sTitle": "Date", "mData" : "date"}, 
			{"sTitle": "Stock Price", "mData" : "stockPrice"}, 
			{"sTitle" : "Stock Owned","mData" : "stockOwned"}, 
			{"sTitle" : "Cash","mData" : "cash"},
			{"sTitle" : "Portfolio Control","mData" : "portfolioControl"}, 
			{"sTitle" : "Action","mData" : "action"}, 
			{"defaultContent" : '<button id="removeParking">Delete</button>'}, 
			{"defaultContent" : '<button id="editParking">Edit</button>'} 
			
			],
			
			
			*/

		"aoColumns" : [
			{"sTitle": "Date", "mData" : "date"}, 
			{"sTitle"  :"Stock Price","mData" : "stockPrice"},
			{"sTitle"  : "Stock Value","mData" : "stockValue"},
			{"sTitle"  : "Safe","mData" : "safe"},
			{"sTitle"  : "Cash","mData" : "cash"},
			{"sTitle"  : "Shares buy and sell","mData" : "sharesBoughtSold"},
			{"sTitle"  : "Stock Owned","mData" : "stockOwned"},
			{"sTitle"  : "Portfolio Control","mData" : "portfolioControl"},
			{"sTitle"  : "Buy or Sell Advise","mData" : "buyOrSellAdvice"},
			{"sTitle"  : "Market Order","mData" : "marketOrder"},
			{"sTitle"  : "interest","mData" : "interest"},
			{"sTitle"  : "Portfolio Value","mData" : "portfolioValue"},
			{"sTitle"  : "Action"}
			],
			dom : 'Bfrtip',
			buttons : [ {
				text : 'Add Parking',
				action : function(e, dt, node, config) {
					popUpParkingForm(property_id,null) ;	
				}
			} ]
	
	});	

}


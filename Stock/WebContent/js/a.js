function makeGrid(){

			$.ajax({
				url : "processrequest",
				type : "POST", 
				contentType : "application/json; charset=utf-8",
				dataType : 'json',
				headers: { 'action': 'get_stock_data'},
				success : function(stocksData) {
					fillStockData(stocksData);
				},
				error : function(xhr, resp, text) {
					console.log(xhr, resp, text);
				}
			});

}


function fillStockData(stocksData){
	  for (var stockSymbol in stocksData) {
          var linesData = stocksData[stockSymbol];
          fillTableData(stockSymbol,linesData);
	  }
}


function fillTableData(stockSymbol,linesData){
	
	
	
	var stock_id=linesData.stock_id;
	
	$('#stocksDiv').append(
			'<div id="stock_name_'+stockSymbol+'"></div>'
			+'<div><table id="stocksTable_'+stockSymbol+'" class="display compact" style="width: 100%"></table></div>'
			+'<div id="buyAdvice_'+stockSymbol+'"></div>'
			+'<div id="sellAdvice_'+stockSymbol+'"></div> <br><br><br>');

	$( "#stock_name_"+stockSymbol ).text("Stock Symbol: "+ stockSymbol);
	
	var stocksTable=$("#stocksTable_"+stockSymbol).DataTable({
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
				text : 'Add Transaction',
				action : function(e, dt, node, config) {
					popUpTransationForm(stock_id,null) ;	
				}
			} ]
	
	});	
	
	var buyAdvice="Buy "+ linesData.buyPredict.sharesBoughtSold  +" shares for " + linesData.buyPredict.stockPrice 
	var sellAdvice="Sell "+ linesData.sellPredict.sharesBoughtSold  +" shares for " + linesData.sellPredict.stockPrice 
	
	$( "#buyAdvice_"+stockSymbol ).text(buyAdvice);
	$( "#sellAdvice_"+stockSymbol).text(sellAdvice);

}


function popUpTransationForm(property_id,parking) {

	
	$("#dialog").remove();
	$(document.body).append('<div id="dialog" title="Parking" style="display:none; min-width: 50px;">Its the money not the principles</div>');
	$("#dialog").attr("title", "Parking Entry");

	$("#dialog").load("include/parking.html?" + Math.random(), function() {

		$("#property_id").attr("value", property_id);
		if (typeof parking!== 'undefined' && parking!=null) {
			$("#parking_id").attr("value", parking.parking_id);
			$("#parking_spot_number").attr("value", parking.parking_spot_number);
			$("#notes").attr("value", parking.notes);
			
		}

		
		
		$("#parking_submit_button").on('click', function(e) {
			e.preventDefault();

			var parking = {
				'property_id' : $('input[id=property_id]').val(),
				'parking_spot_number' : $('input[id=parking_spot_number]').val(),
				'notes' : $('input[id=notes]').val()			
			};
			
			if($('input[id=parking_id]').val().length>0){
				parking.parking_id=$('input[id=parking_id]').val();
			}

			$("body").css("cursor", "progress");
			// send ajax
			$.ajax({
				url : 'processrequest', // url where to submit the request
				type : "POST", // type of action POST || GET
				contentType : "application/json; charset=utf-8",
				dataType : 'json', // data type
				data : JSON.stringify(parking), // post data || get data
				headers: { 'action': 'insertupdate_parking'},
				success : function(parkingData) {
	
					//console.log(result);
					$("body").css("cursor", "default");
					populateParkingTable("parkingsDiv",parking.property_id,null, parkingData)
					$('#dialog').dialog('close');
					
				},
				error : function(xhr, resp, text) {
					console.log(xhr, resp, text);
					$("body").css("cursor", "default");
					$('#dialog').dialog('close');
				}
			})

			return false;// because e.preventDefault(); may not work
		});

	});
	

	$("#dialog").dialog({
		width : "50%"
	});
	
	
	
}


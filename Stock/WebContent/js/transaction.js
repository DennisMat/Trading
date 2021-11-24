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
	$('#stocksDiv').html("");
	  for (var stockSymbol in stocksData) {
          var linesData = stocksData[stockSymbol];
          fillTableData(stockSymbol,linesData);
	  }
}


function fillTableData(stockSymbol,linesData){
	removeTable("stocksTable_" + stockSymbol);

	var stock_id = linesData.stock_id;
	
	$('#stocksDiv').append(
			'<div id="stock_name_'+stockSymbol+'"></div>'
			+'<div><table id="stocksTable_'+stockSymbol+'" class="display compact" style="width: 100%"></table></div>'
			+'<div id="buyAdvice_'+stockSymbol+'"></div>'
			+'<div id="sellAdvice_'+stockSymbol+'"></div> <br><br><br>');

	$( "#stock_name_"+stockSymbol ).text("Stock Symbol: "+ stockSymbol);
	
	
	var stocksTable=$("#stocksTable_"+stockSymbol).DataTable({
		//"bProcessing" : true,
		"aaData" : linesData.trades,
		"aoColumns" : [
			{ "title" : "Trade Date", "mData": null, render: function ( data, type, row ) {
				return formatDateString(data.date_trade);
		    } }, 
			{"sTitle"  :"Stock Price","mData" : "stock_price"},

			{"sTitle"  : "Cash","mData" : "cash"},
			{"sTitle"  : "Shares Quantity","mData" : "stock_quantity_traded"},
			{"sTitle"  : "Stock Owned","mData" : "stock_owned"},
			{"sTitle"  : "Cash Added","mData" : "cash_added"},
			{"sTitle"  : "Portfolio Value","mData" : "portfolio_value"},
			{"sTitle"  : "Buy/Sell","mData" : "action"},
			{"defaultContent" : '<button id="editTrade_'+stockSymbol+'">Edit</button>'},
			{"defaultContent" : '<button id="deleteTrade_'+stockSymbol+'">Delete</button>'}
			],
			dom : 'Bfrtip',
			buttons : [ {
				text : 'Add Trade',
				action : function(e, dt, node, config) {
					popUpTradeForm(stock_id,null) ;	
				}
			} ]
	
	});	
	
	
	$('#stocksTable_'+stockSymbol+' tbody').on('click', '#deleteTrade_'+stockSymbol, function() {
		var tradeDetails = stocksTable.row($(this).parents('tr')).data();
		showTradeDeleteDialog(tradeDetails);

	});

	$('#stocksTable_'+stockSymbol+' tbody').on('click', '#editTrade_'+stockSymbol, function() {
		var tradeDetails = stocksTable.row($(this).parents('tr')).data();
		popUpTradeForm(stock_id,tradeDetails);
	});
	
	var buyAdvice="Buy "+ linesData.buyPredict.sharesBoughtSold  +" shares for " + linesData.buyPredict.stockPrice 
	var sellAdvice="Sell "+ linesData.sellPredict.sharesBoughtSold  +" shares for " + linesData.sellPredict.stockPrice 
	
	$( "#buyAdvice_"+stockSymbol ).text(buyAdvice);
	$( "#sellAdvice_"+stockSymbol).text(sellAdvice);

}


function popUpTradeForm(stock_id,tradeDetails) {

	
	$("#dialog").remove();
	$(document.body).append('<div id="dialog" title="Trade" style="display:none; min-width: 50px;">Its the money not the principles</div>');
	$("#dialog").attr("title", "Trade Entry");

	$("#dialog").load("include/trade.html?" + Math.random(), function() {

		$("#date_trade").datepicker({
			dateFormat : 'yy-mm-dd'
		});
		
		$("#stock_id").attr("value", stock_id);
		
		if (tradeDetails!=null) {
			$("#trade_id").attr("value", tradeDetails.trade_id);
			$("#date_trade").attr("value", tradeDetails.date_trade);
			$("#stock_quantity_traded").attr("value", tradeDetails.stock_quantity_traded);	
			$("#stock_price").attr("value", tradeDetails.stock_price);
			$("#cash_added").attr("value", tradeDetails.cash_added);
			$("#notes").attr("value", tradeDetails.notes);
		}

		
		
		$("#trade_submit_button").on('click', function(e) {
			e.preventDefault();

			var trade = {
				
				'stock_id' : $('input[id=stock_id]').val(),
				'date_trade' : $('input[id=date_trade]').val(),
				'stock_quantity_traded' : $('input[id=stock_quantity_traded]').val(),
				'stock_price' :$('input[id=stock_price]').val(),
				'cash_added' : $('input[id=cash_added]').val(),
				'notes' : $('input[id=notes]').val()			
			};
			
			if($('input[id=trade_id]').val().length>0){
				trade.trade_id=$('input[id=trade_id]').val();
				//trade.trade_id=0;
			}

			$("body").css("cursor", "progress");
			// send ajax
			$.ajax({
				url : 'processrequest', // url where to submit the request
				type : "POST", // type of action POST || GET
				contentType : "application/json; charset=utf-8",
				dataType : 'json', // data type
				data : JSON.stringify(trade), // post data || get data
				headers: { 'action': 'insert_trade'},
				success : function(stocksData) {
					$("body").css("cursor", "default");
					fillStockData(stocksData);
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


function showTradeDeleteDialog(tradeDetails) {
	
	var message="Are you sure that you want to delete this trade?";
		

	$("#dialog").remove()
	$(document.body).append('<div id="dialog" title="Delete trade" style="display:none;">'+message+'</div>');
	$("#dialog").dialog({
		resizable : false,
		height : "auto",
		width : 400,
		modal : true,
		buttons : {
			"Yes" : function() {

				$("body").css("cursor", "progress");

				$.ajax({
					url : 'processrequest',
					type : "POST", // type of action POST || GET
					contentType : "application/json; charset=utf-8",
					dataType : 'json', // data type
					headers : {
						'action' : 'delete_trade'
					},
					data : JSON.stringify(tradeDetails), 
					success : function(stocksData) {
						$("body").css("cursor", "default");
						fillStockData(stocksData);
						$('#dialog').dialog('close');

					},
					error : function(xhr, resp, text) {
						console.log(xhr, resp, text);
						$("body").css("cursor", "default");
						$('#dialog').dialog('close');
					}
				})

				$(this).dialog("close");
			},
			Cancel : function() {
				$(this).dialog("close");
			}
		}
	});

}

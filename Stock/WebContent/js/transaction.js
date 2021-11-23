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
		"aaData" : linesData.trade,
		"aoColumns" : [
			{ "title" : "Trade Date", "mData": null, render: function ( data, type, row ) {
				//console.log(data.cash);
				return formatDateString(data.date);
		      // return data.date.year+'-'+data.date.month+'-'+data.date.day;
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
			{"sTitle"  : "Buy/Sell","mData" : "action"},
			{"defaultContent" : '<button id="editTransaction_'+stockSymbol+'">Edit</button>'},
			{"defaultContent" : '<button id="deleteTransaction_"'+stockSymbol+'>Delete</button>'}
			],
			dom : 'Bfrtip',
			buttons : [ {
				text : 'Add Transaction',
				action : function(e, dt, node, config) {
					popUpTransactionForm(stock_id,null) ;	
				}
			} ]
	
	});	
	
	
	$('#stocksTable tbody').on('click', '#deleteProperty_'+'stockSymbol', function() {
		var transactionDetails = stocksTable.row($(this).parents('tr')).data();
		//showPropertyChangeStatusDialog(propertyDetails);

	});

	$('#stocksTable tbody').on('click', '#editProperty_'+'stockSymbol', function() {
		var transactionDetails = stocksTable.row($(this).parents('tr')).data();
		popUpTransactionForm(stock_id,transactionDetails);
	});
	
	var buyAdvice="Buy "+ linesData.buyPredict.sharesBoughtSold  +" shares for " + linesData.buyPredict.stockPrice 
	var sellAdvice="Sell "+ linesData.sellPredict.sharesBoughtSold  +" shares for " + linesData.sellPredict.stockPrice 
	
	$( "#buyAdvice_"+stockSymbol ).text(buyAdvice);
	$( "#sellAdvice_"+stockSymbol).text(sellAdvice);

}


function popUpTransactionForm(stock_id,transactionDetails) {

	
	$("#dialog").remove();
	$(document.body).append('<div id="dialog" title="Transaction" style="display:none; min-width: 50px;">Its the money not the principles</div>');
	$("#dialog").attr("title", "Transaction Entry");

	$("#dialog").load("include/transaction.html?" + Math.random(), function() {

		$("#date_trade").datepicker({
			dateFormat : 'yy-mm-dd'
		});
		
		$("#stock_id").attr("value", stock_id);
		
		if (transactionDetails!=null) {
			$("#trade_id").attr("value", transactionDetails.trade_id);
			$("#date_trade").attr("value", transactionDetails.date_trade);
			$("#stock_quantity_traded").attr("value", transactionDetails.stock_quantity_traded);	
			$("#stock_price").attr("value", transactionDetails.stock_price);
			$("#cash_added").attr("value", transactionDetails.cash_added);
			$("#notes").attr("value", transactionDetails.notes);
		}

		
		
		$("#transaction_submit_button").on('click', function(e) {
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
	
					//console.log(result);
					$("body").css("cursor", "default");
					//fillTableData(stockSymbol,linesData)
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


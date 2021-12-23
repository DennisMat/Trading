function listStocks() {
	$.ajax({
		url : "processrequest",
		type : "POST",
		contentType : "application/json; charset=utf-8",
		headers : {
			"action" : "get_stock"
		},
		dataType : 'json',
		success : function(stocks) {
			populateStockTable(stocks);
			// console.log(result);
		},
		error : function(xhr, resp, text) {
			console.log(xhr, resp, text);
		}
	})

}

function populateStockTable(stocks) {
	removeTable("stocksTable");
	$('#stocksDiv').html("");
	$("#mainContentsPlaceHolder").html(
			'<div class="sectionStyle">List of stocks</div><div id="stockListDiv"><table id="stocksTable" class="display compact" style="width: 100%"></table><div>');

	stocksTable = $('#stocksTable').DataTable({
		"aaData" : stocks,
        "createdRow": function( row, data, dataIndex){
        	if (data.active == false) {
                $(row).addClass('dimmedText');
            }
        },
		"columns" : [ {
			"title" : "Stock Symbol",
			"data" : "stock_symbol"
		}, 

		
		{
			//invisible column but is needed for filtering, this is columns(1)
			"title" : "Active",
			"data" : "active",
			"visible" : false,
			"defaultContent" : ""
		}, {

			"defaultContent" : null,
			render : function(data, type, row) {
				var buttonText = "Inactivate";
				if (row.active == false) {
					buttonText = "Activate";
				}
				return '<button id="inactivateStock">' + buttonText + '</button>';
			}
		

		}, {
			"defaultContent" : '<button id="editStock">Edit</button>'
		}
		
		 ],

		dom : 'Bfrtip',
		buttons : [ {
			text : 'New Stock',
			action : function(e, dt, node, config) {
				popUpStockForm(null);
			}
		},

		{
			text : 'Show All',
			action : function(e, dt, node, config) {

				if (this.buttons(1).text()[0] == "Show All") {
					this.columns().every(function() {
						this.search('');//show all records
					});
					this.buttons(1).text("Show Active");

				} else {

					this.columns().every(function() {
						this.columns(1).search(true);//show only active records
					});
					this.buttons(1).text("Show All");
				}

				this.draw();
			}
		} ]

	}); 

	stocksTable.columns(1).search(true).draw();// filter the inactive records
													

	$('#stocksTable tbody').on('click', '#inactivateStock', function() {
		var stockDetails = stocksTable.row($(this).parents('tr')).data();
		showStockChangeStatusDialog(stockDetails);

	});

	$('#stocksTable tbody').on('click', '#editStock', function() {
		var stockDetails = stocksTable.row($(this).parents('tr')).data();
		popUpStockForm(stockDetails);
	});


}



function popUpStockForm(stockDetails) {

	$("#dialog").remove();
	$(document.body).append('<div id="dialog" title="Dialog Title" style="display:none;">Its the money not the principles</div>');
	$("#dialog").attr("title", "Edit Record");
	$("#dialog").load("include/stock.html?" + Math.random(), function() {

		$("#stock_name").tooltip();

		if (stockDetails != null) {
			$("#stock_id").attr("value", stockDetails.stock_id);
			$("#stock_symbol").attr("value", stockDetails.stock_symbol);
			$("#stock_description").attr("value", stockDetails.stock_description);

		}

		$("#stock_submit_button").on('click', function(e) {
			e.preventDefault();
			var stock = {
				'stock_symbol' : $('input[id=stock_symbol]').val(),
				'stock_description' : $('#stock_description').val()
			}
			
			if($('input[id=stock_id]').val().length>0){
				stock.stock_id=$('input[id=stock_id]').val();
			}
			

			// send ajax
			$("body").css("cursor", "progress");
			$.ajax({
				url : 'processrequest', // url where to submit the request
				type : "POST", // type of action POST || GET
				contentType : "application/json; charset=utf-8",
				dataType : 'json', // data type
				data : JSON.stringify(stock),
				headers : {
					"action" : "post_stock"
				},
				success : function(stocks) {

					populateStockTable(stocks);
					$("body").css("cursor", "default");
					$('#dialog').dialog('close');
				},
				error : function(xhr, resp, text) {
					$('#dialog').dialog('close');
					$("body").css("cursor", "default");
					console.log(xhr, resp, text);
				}
			})

			return false;// because e.preventDefault(); may not work
		});

	});

	$("#dialog").dialog({
		width : "400px",
		maxWidth : "800px" //both these attributes are needed to maintain width
	});

}

function showStockChangeStatusDialog(stockDetails) {
	
	var isActive=stockDetails.active;
	
	var message="Are you sure that you want to inactivate this stock?";
		
	if(isActive==false){
		message="Are you sure that you want to activate this stock?";
		stockDetails.active=true;
	}else{
		stockDetails.active=false;
	}

	$("#dialog").remove()
	$(document.body).append('<div id="dialog" title="Change status Stock" style="display:none;">'+message+'</div>');
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
						'action' : 'change_stock_status'
					},
					data : JSON.stringify(stockDetails), 
					success : function(response) {

						// console.log(result);
						$("body").css("cursor", "default");

						$('#dialog').dialog('close');
						listStocks();

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

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
	destroyTables();
	removeTable("stocksTable");
	$("#mainContentsPlaceHolder").html(
			'<div class="sectionStyle">List of stocks</div><div id="stocksDiv"><table id="stocksTable" class="display compact" style="width: 100%"></table><div>');

	stocksTable = $('#stocksTable').DataTable({
		"aaData" : stocks,
        "createdRow": function( row, data, dataIndex){
        	if (data.stock.active == false) {
                $(row).addClass('dimmedText');
            }
        },
		"columns" : [ {
			"title" : "Stock Symbol",
			"data" : "stock.stock_name"
		}, {
			"title" : "Active",
			"data" : "stock.active",
			"visible" : false,
			"defaultContent" : ""
		}, {

			"defaultContent" : null,
			render : function(data, type, row) {
				var buttonText = "Inactivate";
				if (row.stock.active == false) {
					buttonText = "Activate";
				}
				return '<button id="inactivateStock">' + buttonText + '</button>';
			}
		

		}, {
			"defaultContent" : '<button id="editStock">Edit</button>'
		}, {
			"defaultContent" : '<button id="detailsStock">Details</button>'
		} ],

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
						this.columns(3).search(true);//show only active records
					});
					this.buttons(1).text("Show All");
				}

				this.draw();
			}
		} ]

	});

	stocksTable.columns(3).search(true).draw();// filter the inactive records
													

	$('#stocksTable tbody').on('click', '#inactivateStock', function() {
		var stockDetails = stocksTable.row($(this).parents('tr')).data();
		showStockChangeStatusDialog(stockDetails);

	});

	$('#stocksTable tbody').on('click', '#editStock', function() {
		var stockDetails = stocksTable.row($(this).parents('tr')).data();
		popUpStockForm(stockDetails);
	});

	$('#stocksTable tbody').on('click', '#detailsStock', function() {
		var stockDetails = stocksTable.row($(this).parents('tr')).data();
		displayStockDetails(stockDetails);
	});

}

function displayStockDetails(stockDetails) {

	destroyTables();
	$("#mainContentsPlaceHolder").load("include/stockDetails.html?" + Math.random(), function() {
		$(this).contents().unwrap();
		$("#stock_name").html(stockDetails.stock.stock_name);
		listApartments("apartmentListDiv", stockDetails);
		listFloorPlans(stockDetails.stock.stock_id);
		listParkingSpots("parkingContentsPlaceHolder", stockDetails.stock.stock_id, null);
	});

}

function popUpStockForm(stockDetails) {

	$("#dialog").remove();
	$(document.body).append('<div id="dialog" title="Dialog Title" style="display:none;">Its the money not the principles</div>');
	$("#dialog").attr("title", "Edit Record");
	$("#dialog").load("include/stock.html?" + Math.random(), function() {

		$("#stock_name").tooltip();

		if (stockDetails != null) {
			$("#stock_id").attr("value", stockDetails.stock.stock_id);
			$("#address_id").attr("value", stockDetails.address.address_id);

			$("#stock_name").attr("value", stockDetails.stock.stock_name);
			$("#address_line1").attr("value", stockDetails.address.address_line1);
			$("#address_line2").attr("value", stockDetails.address.address_line2);
			$("#city").attr("value", stockDetails.address.city);
			$("#state_province").attr("value", stockDetails.address.state_province);
			$("#postal_code").attr("value", stockDetails.address.postal_code);
			$('#notes').val(stockDetails.stock.notes);

		}

		$("#stock_submit_button").on('click', function(e) {
			e.preventDefault();
			var formData = {};
			var stock = {
				'stock_name' : $('input[id=stock_name]').val(),
				'notes' : $('#notes').val()
			}
			var address = {
				'address_line1' : $('input[id=address_line1]').val(),
				'address_line2' : $('input[id=address_line2]').val(),
				'city' : $('input[id=city]').val(),
				'state_province' : $('input[id=state_province]').val(),
				'postal_code' : $('input[id=postal_code]').val()
			}

			if (stockDetails != null) {
				stock.stock_id = $('input[id=stock_id]').val();
				stock.address_id = $('input[id=address_id]').val();
				address.address_id = $('input[id=address_id]').val();
			}

			formData.address = address;
			formData.stock = stock;

			// send ajax
			$("body").css("cursor", "progress");
			$.ajax({
				url : 'processrequest', // url where to submit the request
				type : "POST", // type of action POST || GET
				contentType : "application/json; charset=utf-8",
				dataType : 'json', // data type
				data : JSON.stringify(formData),
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
	
	var isActive=stockDetails.stock.active;
	
	var message="Are you sure that you want to inactivate this stock?";
		
	if(isActive==false){
		message="Are you sure that you want to activate this stock?";
		stockDetails.stock.active=true;
	}else{
		stockDetails.stock.active=false;
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
						listProperties();

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

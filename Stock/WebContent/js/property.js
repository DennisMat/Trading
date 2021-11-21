function listProperties() {
	$.ajax({
		url : "processrequest",
		type : "POST",
		contentType : "application/json; charset=utf-8",
		headers : {
			"action" : "get_property"
		},
		dataType : 'json',
		success : function(properties) {
			populatePropertyTable(properties);
			// console.log(result);
		},
		error : function(xhr, resp, text) {
			console.log(xhr, resp, text);
		}
	})

}

function populatePropertyTable(properties) {
	destroyTables();
	removeTable("propertiesTable");
	$("#mainContentsPlaceHolder").html(
			'<div class="sectionStyle">List of properties</div><div id="propertiesDiv"><table id="propertiesTable" class="display compact" style="width: 100%"></table><div>');

	propertiesTable = $('#propertiesTable').DataTable({
		"aaData" : properties,
        "createdRow": function( row, data, dataIndex){
        	if (data.property.active == false) {
                $(row).addClass('dimmedText');
            }
        },
		"columns" : [ {
			"title" : "Property Name",
			"data" : "property.property_name"
		}, {
			"title" : "Address",
			"data" : "address.address_line1",
			"defaultContent" : ""
		}, {
			"title" : "City",
			"data" : "address.city",
			"defaultContent" : ""
		}, {
			"title" : "Active",
			"data" : "property.active",
			"visible" : false,
			"defaultContent" : ""
		}, {

			"defaultContent" : null,
			render : function(data, type, row) {
				var buttonText = "Inactivate";
				if (row.property.active == false) {
					buttonText = "Activate";
				}
				return '<button id="deleteProperty">' + buttonText + '</button>';
			}
		

		}, 
		{"defaultContent" : '<button id="editProperty">Edit</button>'}, 
		{"defaultContent" : '<button id="detailsProperty">Details</button>'} 
		
		],

		dom : 'Bfrtip',
		buttons : [ {
			text : 'New Property',
			action : function(e, dt, node, config) {
				popUpPropertyForm(null);
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

	propertiesTable.columns(3).search(true).draw();// filter the inactive records
													

	$('#propertiesTable tbody').on('click', '#deleteProperty', function() {
		var propertyDetails = propertiesTable.row($(this).parents('tr')).data();
		showPropertyChangeStatusDialog(propertyDetails);

	});

	$('#propertiesTable tbody').on('click', '#editProperty', function() {
		var propertyDetails = propertiesTable.row($(this).parents('tr')).data();
		popUpPropertyForm(propertyDetails);
	});

	$('#propertiesTable tbody').on('click', '#detailsProperty', function() {
		var propertyDetails = propertiesTable.row($(this).parents('tr')).data();
		displayPropertyDetails(propertyDetails);
	});

}

function displayPropertyDetails(propertyDetails) {

	destroyTables();
	$("#mainContentsPlaceHolder").load("include/propertyDetails.html?" + Math.random(), function() {
		$(this).contents().unwrap();
		$("#property_name").html(propertyDetails.property.property_name);
		listApartments("apartmentListDiv", propertyDetails);
		listFloorPlans(propertyDetails.property.property_id);
		listParkingSpots("parkingContentsPlaceHolder", propertyDetails.property.property_id, null);
	});

}

function popUpPropertyForm(propertyDetails) {

	$("#dialog").remove();
	$(document.body).append('<div id="dialog" title="Dialog Title" style="display:none;">Its the money not the principles</div>');
	$("#dialog").attr("title", "Edit Record");
	$("#dialog").load("include/property.html?" + Math.random(), function() {

		$("#property_name").tooltip();

		if (propertyDetails != null) {
			$("#property_id").attr("value", propertyDetails.property.property_id);
			$("#address_id").attr("value", propertyDetails.address.address_id);

			$("#property_name").attr("value", propertyDetails.property.property_name);
			$("#address_line1").attr("value", propertyDetails.address.address_line1);
			$("#address_line2").attr("value", propertyDetails.address.address_line2);
			$("#city").attr("value", propertyDetails.address.city);
			$("#state_province").attr("value", propertyDetails.address.state_province);
			$("#postal_code").attr("value", propertyDetails.address.postal_code);
			$('#notes').val(propertyDetails.property.notes);

		}

		$("#property_submit_button").on('click', function(e) {
			e.preventDefault();
			var formData = {};
			var property = {
				'property_name' : $('input[id=property_name]').val(),
				'notes' : $('#notes').val()
			}
			var address = {
				'address_line1' : $('input[id=address_line1]').val(),
				'address_line2' : $('input[id=address_line2]').val(),
				'city' : $('input[id=city]').val(),
				'state_province' : $('input[id=state_province]').val(),
				'postal_code' : $('input[id=postal_code]').val()
			}

			if (propertyDetails != null) {
				property.property_id = $('input[id=property_id]').val();
				property.address_id = $('input[id=address_id]').val();
				address.address_id = $('input[id=address_id]').val();
			}

			formData.address = address;
			formData.property = property;

			// send ajax
			$("body").css("cursor", "progress");
			$.ajax({
				url : 'processrequest', // url where to submit the request
				type : "POST", // type of action POST || GET
				contentType : "application/json; charset=utf-8",
				dataType : 'json', // data type
				data : JSON.stringify(formData),
				headers : {
					"action" : "post_property"
				},
				success : function(properties) {

					populatePropertyTable(properties);
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

function showPropertyChangeStatusDialog(propertyDetails) {
	
	var isActive=propertyDetails.property.active;
	
	var message="Are you sure that you want to inactivate this property?";
		
	if(isActive==false){
		message="Are you sure that you want to activate this property?";
		propertyDetails.property.active=true;
	}else{
		propertyDetails.property.active=false;
	}

	$("#dialog").remove()
	$(document.body).append('<div id="dialog" title="Change status Property" style="display:none;">'+message+'</div>');
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
						'action' : 'change_property_status'
					},
					data : JSON.stringify(propertyDetails), 
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

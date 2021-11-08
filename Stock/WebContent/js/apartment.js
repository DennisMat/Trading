function listApartments(divElement, propertyDetails) {
	
	
	$.ajax({
		url : "processrequest",
		type : "POST", 
		contentType : "application/json; charset=utf-8",
		dataType : 'json',
		headers: { 'action': 'get_apartment', 'property_id':propertyDetails.property.property_id},
		success : function(apartmentList) {
			fillApartmentTable(divElement, propertyDetails, apartmentList)
		},
		error : function(xhr, resp, text) {
			console.log(xhr, resp, text);
		}
	});
}
	
	
function fillApartmentTable(divElement, propertyDetails, apartmentList) {
	removeTable("apartmentsTable");
	$("#" + divElement).html('<table id="apartmentsTable"  class="display compact" style="width: 100%"></table>');
	
	apartmentsTable = $('#apartmentsTable').DataTable({
		"bProcessing" : true,
		"aaData" : apartmentList,
        "createdRow": function( row, data, dataIndex){
        	if (data.apartment.active == false) {
                $(row).addClass('dimmedText');
            }
        },
		"columns" : [ 
			{"title" : "Apartment Number","data" : "apartment.apartment_number"}, 
			
			 {

			"defaultContent" : null,
			render : function(data, type, row) {
				var buttonText = "Inactivate";
				if (row.apartment.active == false) {
					buttonText = "Activate";
				}
				return '<button id="deleteApartment">' + buttonText + '</button>';
			}

		},
		{
			"title" : "Active",
			"data" : "apartment.active",
			"visible" : false,
			"defaultContent" : ""
		},
		{"defaultContent" : '<button id="editApartment">Edit</button>'}, 
		{"defaultContent" : '<button id="detailsApartment">Details</button>'}, 
		
		],

		dom : 'Bfrtip',
		buttons : [ {
			text : 'New Apartment',
			action : function(e, dt, node, config) {
				popUpApartmentForm(propertyDetails);
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
						this.columns(2).search(true);//show only active records
					});
					this.buttons(1).text("Show All");
				}

				this.draw();
			}
		} ]

	});
	
	apartmentsTable.columns(2).search(true).draw();// filter out the inactive records

	$('#apartmentsTable tbody').on('click', '#deleteApartment', function() {
		var apartment = apartmentsTable.row($(this).parents('tr')).data().apartment;
		showApartmentChangeStatusDialog(propertyDetails, apartment);
	});

	$('#apartmentsTable tbody').on('click', '#editApartment', function() {
		var apartment = apartmentsTable.row($(this).parents('tr')).data().apartment;
		popUpApartmentForm(propertyDetails, apartment);
	});
	
	$('#apartmentsTable tbody').on('click', '#detailsApartment', function() {
		var apartment = apartmentsTable.row($(this).parents('tr')).data();
		displayApartmentDetails(apartment.apartment.apartment_id);
	});

}





function showApartmentChangeStatusDialog(propertyDetails, apartment) {
	
	var isActive=apartment.active;
	
	var message="Are you sure that you want to inactivate this apartment?";
		
	if(isActive==false){
		message="Are you sure that you want to activate this apartment?";
		apartment.active=true;
	}else{
		apartment.active=false;
	}

	$("#dialog").remove()
	$(document.body).append('<div id="dialog" title="Change status Apartment" style="display:none;">'+message+'</div>');
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
						'action' : 'change_apartment_status'
					},
					data : JSON.stringify(apartment), 
					success : function(response) {

						// console.log(result);
						$("body").css("cursor", "default");

						$('#dialog').dialog('close');
						listApartments("apartmentListDiv", propertyDetails);

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



function displayApartmentDetails(apartment_id) {

	destroyTables();
	$("#mainContentsPlaceHolder").load("include/apartmentDetails.html?" + Math.random(),
			function() {
				$(this).contents().unwrap();

//				$.ajax({
//					url : 'apartments?apartment_id=' + apartment_id,
//					type : "GET", // type of action POST || GET
//					contentType : "application/json; charset=utf-8",
//					dataType : 'json', // data type
//					success : function(response) {
//						populateApartmentDetails(response);
//						// console.log(result);
//					},
//					error : function(xhr, resp, text) {
//						console.log(xhr, resp, text);
//					}
//				})
				

					$.ajax({
						url : "processrequest",
						type : "POST", 
						contentType : "application/json; charset=utf-8",
						dataType : 'json',
						headers: { 'action': 'get_apartment', 'apartment_id':apartment_id},
						success : function(response) {
							populateApartmentDetails(response);
							// console.log(result);
						},
						error : function(xhr, resp, text) {
							console.log(xhr, resp, text);
						}
					});

			});

}

function popUpApartmentForm(propertyDetails, apartment) {

	$("#dialog").remove();
	$(document.body).append('<div id="dialog" title="Dialog Title" style="display:none;">Its the money not the principles</div>');
	$("#dialog").attr("title", "Edit Record");
	$("#dialog").load("include/apartment.html?" + Math.random(),function() {

						$("#property_id").attr("value",propertyDetails.property.property_id);
						createFloorPlanDropDown('#floor_plan_id',propertyDetails.property, apartment);
						createParkingDropDown('#parking_id',propertyDetails.property, apartment);

						if (typeof apartment !== 'undefined') {
							$("#apartment_id").attr("value",apartment.apartment_id);
							$("#apartment_number").attr("value",apartment.apartment_number);
							$('#apartment_type_id [value=' + apartment.apartment_type_id + ']').attr('selected', 'true');
							$("#floor_plan_id").attr("value",apartment.floor_plan_id);
						}

						$("#apartment_submit_button").on('click',function(e) {
							e.preventDefault();

						var formData = {
							'property_id' : $('input[id=property_id]').val(),
							'apartment_number' : $('input[id=apartment_number]').val(),
							'apartment_type_id' : $('#apartment_type_id').val(),
							'floor_plan_id':$('#floor_plan_id').val()
						};
						if($('input[id=apartment_id]').val().length>0){
							formData.apartment_id=$('input[id=apartment_id]').val()
						}
						saveApartmentDetails(formData,propertyDetails);
						return false;// because e.preventDefault(); may not work
					});

					});

	$("#dialog").dialog({
		width : "50%"
	});

}


function saveApartmentDetails(formData,propertyDetails ){
	
	$("body").css("cursor", "progress");
	$.ajax({
		url : 'processrequest', 
		type : "POST", 
		contentType : "application/json; charset=utf-8",
		headers: { 'action': 'post_apartment'},
		dataType : 'json', 
		data : JSON.stringify(formData), 
		success : function(apartmentList) {
			$("body").css("cursor", "default");
			
			//listApartments("apartmentListDiv",propertyDetails);
			fillApartmentTable("apartmentListDiv", propertyDetails, apartmentList) 
			$('#dialog').dialog('close');
			
		},
		error : function(xhr, resp, text) {
			console.log(xhr, resp, text);
			$("body").css("cursor", "default");
			$('#dialog').dialog('close');
		}
	});
}


function createFloorPlanDropDown(dropdown, property, apartment) {

	
	$.ajax({
		url : "processrequest",
		type : "POST", 
		headers: { 'action': 'get_apartment', 'apartment_id':apartment.apartment_id, 
			'property_id':property.property_id,"flag": "get_details"},
		contentType : "application/json; charset=utf-8",
		dataType : 'json', 
		success : function(response) {
			var floorPlans=response.floorplans;
			var apartment=response.apartment;
			$.each(floorPlans, function(val, floorplan) {
				$(dropdown).append($('<option></option>').val(floorplan.floor_plan_id).html(floorplan.floor_plan_name));
			});
			if (typeof apartment !== 'undefined') {
				$(dropdown).val(apartment.floor_plan_id);
			}
			
			$("#apartment_layout").attr("src",getFloorPlanImagePath(floorPlans, apartment.floor_plan_id));
			
			$(dropdown).change(function () {
				var floor_plan_id = $(dropdown).val();
				
				$("#apartment_layout").attr("src",getFloorPlanImagePath(floorPlans, floor_plan_id));
				
			});
			
		},
		error : function(xhr, resp, text) {
			console.log(xhr, resp, text);
		}
	});		


}

function getFloorPlanImagePath(floorPlans, floor_plan_id) {
	var image_path="";
	$.each(floorPlans, function(val, floorplan) {
		if(floor_plan_id==floorplan.floor_plan_id){
			image_path= floorplan.image_path;
			return false; //similar to break
		}
	});
	
	return image_path;
}
function populateApartmentDetails(response) {
	
	
	var apartment=response.apartment;
	var renthistory=response.renthistory;
	var occupants=response.occupants;
	var floorplan=response.floorplan;
	var property=response.property;

	$("#property_name").html(property.property_name);
	$("#apartment_number_display").html( apartment.apartment_number);
	pupulateUsersTable("apartmentDetailsDiv","occupants",apartment,occupants,false);
	fillRentTable(apartment,occupants, renthistory);
	fillFloorLayoutSection(floorplan);
	listParkingSpots("parkingContentsPlaceHolderApt",property.property_id,apartment.apartment_id);
	listLeases("leaseContentsPlaceHolder",apartment.apartment_id)
	

}

function fillRentTable(apartment,occupants, renthistory){
	
	removeTable("rentHistoryTable");	
	$("#rentHistoryDiv").append('<table id="rentHistoryTable" class="display compact" style="width: 100%"></table>');
	$('#rentHistoryTable').dataTable({
		"bProcessing" : true,
		"aaData" : renthistory,
		"aoColumns" : [ 
		{"mData" : "rent.amount"}, 
		{"mData" : "individual.first_name"},
		{"mData" : "individual.last_name"},
		{ "mData": null, render: function ( data, type, row ) {
             return data.rent.date_paid.year+'-'+data.rent.date_paid.month+'-'+data.rent.date_paid.day;
         } }, 

         ],
		
		dom : 'Bfrtip',
		buttons : [ {
			text : 'Add Rent Entry',
			action : function(e, dt, node, config) {

				$.ajax({
					url : "processrequest",
					type : "POST", 
					headers: { 'action': 'get_apartment', 'apartment_id':apartment_id, 'property_id':property_id},
					contentType : "application/json; charset=utf-8",
					dataType : 'json', 
					success : function(response) {
						popUpRentForm(response.apartment, response.occupants) ;
						// console.log(result);
					},
					error : function(xhr, resp, text) {
						console.log(xhr, resp, text);
					}
				});			
			}
		} ]
		
	});
}

function popUpRentForm(apartment, occupants) {

	
	$("#dialog").remove();
	$(document.body).append('<div id="dialog" title="Rent" style="display:none; min-width: 50px;">Its the money not the principles</div>');
	$("#dialog").attr("title", "Rent Entry");

	$("#dialog").load("include/rent.html?" + Math.random(), function() {

		if (typeof apartment!== 'undefined' && apartment.apartment_id !== 'undefined') {
			$("#property_id").attr("value", apartment.property_id);
			$("#apartment_id").attr("value", apartment.apartment_id);
		}

	
		$.each(occupants, function(val, occupant) {
			$('#paid_by_individual_id').append($('<option></option>').val(occupant.individual.individual_id).html(occupant.individual.first_name + " " + occupant.individual.last_name));
			
		});
		
					
		$("#rent_submit_button").on('click', function(e) {
			e.preventDefault();

			var rent = {
				'property_id' : $('input[id=property_id]').val(),
				'apartment_id' : $('input[id=apartment_id]').val(),
				'amount' : $('input[id=amount]').val(),
				'currency' : $('input[id=currency]').val(),
				'paid_by_individual_id' : $('#paid_by_individual_id').val(),
				'notes' : $('input[id=notes]').val()			
			};

			$("body").css("cursor", "progress");
			// send ajax
			$.ajax({
				url : 'rents', // url where to submit the request
				type : "POST", // type of action POST || GET
				contentType : "application/json; charset=utf-8",
				dataType : 'json', // data type
				data : JSON.stringify(rent), // post data || get data
				success : function(renthistory) {
	
					//console.log(result);
					$("body").css("cursor", "default");
					fillRentTable(apartment,occupants, renthistory)
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




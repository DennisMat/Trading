function listParkingSpots(parkingContentsPlaceHolderDiv,property_id, apartment_id){
	var reqHeaders={ 'action': 'get_parking', 'property_id':property_id}
	
	if(apartment_id!=null){
		reqHeaders.apartment_id=apartment_id;
	}
	
	$.ajax({
		url : "processrequest",
		type : "POST", 
		contentType : "application/json; charset=utf-8",
		dataType : 'json',
		headers: reqHeaders,
		success : function(parkings) {
			populateParkingTable(parkingContentsPlaceHolderDiv,property_id,apartment_id,parkings);
			// console.log(result);
		},
		error : function(xhr, resp, text) {
			console.log(xhr, resp, text);
		}
	})
}

function populateParkingTable(parkingContentsDiv,property_id, apartment_id, parkingData) {
	
	removeTable("parkingsTable");
	$("#" + parkingContentsDiv).html('<div id="parkingsDiv"><table id="parkingsTable"   class="display compact" style="width: 100%"></table><div>');

	
	if(apartment_id>0){

		
		var parkingsTable=$('#parkingsTable').DataTable({
			"bProcessing" : true,
			"aaData" : parkingData,

			"aoColumns" : [
				{"mData" : "parking.parking_spot_number"},
				{"defaultContent" : '<button id="releaseParking">Release this parking spot</button>'}			
				],

				dom : 'Bfrtip',
				buttons : [ {
					text : 'Allocate an Existing Parking Spot',
					action : function(e, dt, node, config) {
						allocateParking(property_id,apartment_id) ;	
					}
				} ]
		
		});
		
		$('#parkingsTable tbody').on('click', '#releaseParking', function() {
			var parking = parkingsTable.row($(this).parents('tr')).data().parking;
			
			
			$("#dialog").remove();
			$(document.body).append('<div id="dialog" title="Release Parking" style="display:none;">Are you sure that you want to release this parking spot? </div>');
			
			
		    $( "#dialog" ).dialog({resizable: false, height: "auto", width: 400, modal: true,
		        buttons: {
		          "Yes": function() {
		        	  
		  			$("body").css("cursor", "progress");

					$.ajax({
						url : 'processrequest', 
						type : "POST", // type of action POST || GET
						contentType : "application/json; charset=utf-8",
						dataType : 'json', // data type
						headers: { 'action': 'unallocate_parking'},
						data : JSON.stringify(parking), // post data || get data
						success : function(updated_parkings) {
				
							//console.log(result);
							$("body").css("cursor", "default");
							populateParkingTable("parkingContentsPlaceHolderApt",property_id, apartment_id, updated_parkings); 
							$('#dialog').dialog('close');
							
						},
						error : function(xhr, resp, text) {
							console.log(xhr, resp, text);
							$("body").css("cursor", "default");
							$('#dialog').dialog('close');
						}
					})
		        	  
		        	  
		        	  
		            $( this ).dialog( "close" );
		          },
		          Cancel: function() {
		            $( this ).dialog( "close" );
		          }
		        }
		      });
			
			


			
		});
		
		
		
	}else{
		
		var parkingsTable=$('#parkingsTable').DataTable({
			"bProcessing" : true,
			"aaData" : parkingData,

			"aoColumns" : [
				{"mData" : "parking.parking_spot_number"}, 
				{ "mData": null, render: function ( data, type, row ) {
					if(data.apartment.apartment_id==0){
						return "Not allocated";
					}else{
						return "Allocated to Apartment " + data.apartment.apartment_number;
					}
		             
		         } }, 
				{"mData" : "notes","defaultContent": ""},
				{"defaultContent" : '<button id="removeParking">Delete</button>'}, 
				{"defaultContent" : '<button id="editParking">Edit</button>'} 
				
				],

				dom : 'Bfrtip',
				buttons : [ {
					text : 'Add Transaction',
					action : function(e, dt, node, config) {
						popUpParkingForm(property_id,null) ;	
					}
				} ]
		
		});
		
		$('#parkingsTable tbody').on('click','#editParking',function() {
			var parkingRow = parkingsTable.row($(this).parents('tr')).data();
			popUpParkingForm(property_id,parkingRow.parking) ;
		});
		
		
		$('#parkingsTable tbody').on('click','#removeParking',function() {
		
			var parking = parkingsTable.row($(this).parents('tr')).data().parking;
			var apartment = parkingsTable.row($(this).parents('tr')).data().apartment;
			$("#dialog").remove();
			if(apartment.apartment_id>0){
				$(document.body).append('<div id="dialog" title="Delete Parking" style="display:none;">First deallocate this parking, and then try to remove. </div>');
				$( "#dialog" ).dialog({resizable: false, height: "auto", width: 400, modal: true,
			        buttons: {
			          "OK": function() {
			            $( this ).dialog( "close" );
			          }
			        }
			      });
			}else{
				$(document.body).append('<div id="dialog" title="Delete Parking" style="display:none;">Are you sure that you want to delete this parking spot? </div>');
				$( "#dialog" ).dialog({resizable: false, height: "auto", width: 400, modal: true,
			        buttons: {
			          "Yes": function() {
			        	  
			  			$("body").css("cursor", "progress");

						$.ajax({
							url : 'processrequest', 
							type : "POST", // type of action POST || GET
							contentType : "application/json; charset=utf-8",
							dataType : 'json', // data type
							headers: { 'action': 'remove_parking'},
							data : JSON.stringify(parking), // post data || get data
							success : function(updated_parkings) {
					
								//console.log(result);
								$("body").css("cursor", "default");
								populateParkingTable("parkingContentsPlaceHolder",property_id, apartment_id, updated_parkings); 
								$('#dialog').dialog('close');
								
							},
							error : function(xhr, resp, text) {
								console.log(xhr, resp, text);
								$("body").css("cursor", "default");
								$('#dialog').dialog('close');
							}
						})
			        	  
			        	  
			        	  
			            $( this ).dialog( "close" );
			          },
			          Cancel: function() {
			            $( this ).dialog( "close" );
			          }
			        }
			      });
			}
			
			
		    
			
			
		});
		
	
		
	}
	

	
}

function allocateParking(property_id,apartment_id) {
	
	$("#dialog").remove();
	
	
	$.ajax({
		url : "processrequest",
		type : "POST", 
		contentType : "application/json; charset=utf-8",
		dataType : 'json',
		headers: { 'action': 'get_parking','unallocated':'true','property_id':property_id},
		success : function(parkingData) {
			popUpAllocationForm(property_id,apartment_id,parkingData);
		},
		error : function(xhr, resp, text) {
			console.log(xhr, resp, text);
		}
	})
	
	
   
   
	
}

function popUpAllocationForm(property_id,apartment_id,parkingData) {

	
	var parkingSpotForm="";
	parkingSpotForm+='<form><input type="hidden" id="property_id" value='+property_id+'><input type="hidden" id="apartment_id" value='+apartment_id+'>';
	$.each(parkingData, function(val, parkingRow) {
		parkingSpotForm+='<input type="checkbox" name="parking_id" value="'+parkingRow.parking.parking_id+'"> <label>'+parkingRow.parking.parking_spot_number+'</label><br>';
	});
	parkingSpotForm+='<input type="submit" style="white-space: normal; width: 200px;" id="parking_list_submit_button" value="Add above parking spots to this aparment"></form>';
	$(document.body).append('<div id="dialog" title="Parking">'+parkingSpotForm+'</div>');
    $( "#dialog" ).dialog();
	
		$("#parking_list_submit_button").on('click', function(e) {
			e.preventDefault();
			var parkingList=[];
			$("input[name='parking_id']").each(function() {
				
				if($(this).prop('checked')) {
					var parking = {
							'property_id' : $('input[id=property_id]').val(),
							'apartment_id' : $('input[id=apartment_id]').val(),
							'parking_id' : $(this).val()
						};
				    
					parkingList.push(parking);
			    }
				
				
			});
		
		
			
			$("body").css("cursor", "progress");
			// send ajax
			$.ajax({
				url : 'processrequest', // url where to submit the request
				type : "POST", // type of action POST || GET
				contentType : "application/json; charset=utf-8",
				dataType : 'json', // data type
				headers: { 'action': 'allocate_arking'},
				data : JSON.stringify(parkingList), // post data || get data
				success : function(updated_parkings) {
		
					//console.log(result);
					$("body").css("cursor", "default");
					populateParkingTable("parkingContentsPlaceHolderApt",property_id, apartment_id, updated_parkings); 
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

}

function popUpParkingForm(property_id,parking) {

	
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



function createParkingDropDown(dropdown,property, apartment) {
	

	$.ajax({
		url : "parkings?property_id=" + property.property_id,
		type : "GET", // type of action POST || GET
		contentType : "application/json; charset=utf-8",
		dataType : 'json', // data type
		success : function(parkings) {
			$.each(parkings, function(val, parking) {
				$(dropdown).append($('<option></option>').val(parking.parking_id).html(parking.parking_spot_number));
			});
			
		},
		error : function(xhr, resp, text) {
			console.log(xhr, resp, text);
		}
	})
}

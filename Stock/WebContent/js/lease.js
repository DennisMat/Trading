function listLeases(leaseContentsPlaceHolderDiv, apartment_id){
	var reqHeaders={ 'action': 'getLease', 'apartment_id':apartment_id}

	
	$.ajax({
		url : "processrequest",
		type : "POST", 
		contentType : "application/json; charset=utf-8",
		dataType : 'json',
		headers: reqHeaders,
		success : function(leases) {
			populateLeaseTable(leaseContentsPlaceHolderDiv,apartment_id,leases);
			// console.log(result);
		},
		error : function(xhr, resp, text) {
			console.log(xhr, resp, text);
		}
	});
}

function populateLeaseTable(leaseContentsPlaceHolderDiv, apartment_id, leases) {
	
	
	removeTable("leasesTable");
	$("#" + leaseContentsPlaceHolderDiv).html('<div id="leasesDiv"><table id="leasesTable"   class="display compact" style="width: 100%"></table><div>');


	var buttonsList= [];
	
	var columnsList=[
		{ "title" : "Lease Start Date", "mData": null, render: function ( data, type, row ) {
            return data.lease_start_date.year+'-'+data.lease_start_date.month+'-'+data.lease_start_date.day;
        } }, 
    	{ "title" : "Lease End Date", "mData": null, render: function ( data, type, row ) {
            return data.lease_end_date.year+'-'+data.lease_end_date.month+'-'+data.lease_end_date.day;
        } }, ];
	
		if(apartment_id!=null){
			buttonsList.push({
				text : "New Lease",
				action : function(e, dt, node, config) {
					addLease(apartment_id) ;	
				}
			});
	
			columnsList.push({"defaultContent" : '<button id="detailsLease" >Details</button>'}	);
		}else{
			columnsList.push({"defaultContent" : '<button id="detailsApartment" >Go to Apartment</button>'}	);
		}
	
		var leasesTable=$('#leasesTable').DataTable({
			"bProcessing" : true,
			"aaData" : leases,
			"aoColumns" : columnsList,
			dom : 'Bfrtip',
			buttons: buttonsList
		
		});
		
		$('#leasesTable tbody').on('click', '#detailsApartment',function() {
			var lease = leasesTable.row($(this).parents('tr')).data();
			displayApartmentDetails(lease.apartment_id)
		});


	
}

function addLease(apartment_id){
	
	notYetImplemented();
	
}


function notYetImplemented(){
	
	$("#dialog").remove();
	$(document.body).append('<div id="dialog" title="" style="display:none;">This functionality has not yet being implemented. </div>');
	
	
    $( "#dialog" ).dialog({
        resizable: false,
        height: "auto",
        width: 400,
        modal: true,
        buttons: {
          "Close": function() {
            $( this ).dialog( "close" );
          }
        }
      });
	
}
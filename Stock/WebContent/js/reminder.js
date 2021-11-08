

function listReminders() {

	destroyTables();
	$("#mainContentsPlaceHolder").load("include/reminder.html?" + Math.random(),
			function() {
				$(this).contents().unwrap();
				var reqHeaders={ 'action': 'getLease', 'days':"60"}

				
				$.ajax({
					url : "processrequest",
					type : "POST", 
					contentType : "application/json; charset=utf-8",
					dataType : 'json', // data type
					headers: reqHeaders,
					success : function(leases) {
						populateLeaseTable("expiringLeaseContentsPlaceHolderDiv",null,leases);
					},
					error : function(xhr, resp, text) {
						console.log(xhr, resp, text);
					}
				})

			});

}


function displayRemiderDetails(propertyDetails) {}

function popUpRemiderForm(propertyDetails) {}


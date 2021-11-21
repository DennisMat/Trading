function listUsers(userType, allUsers) {

	destroyTables();

	$("#mainContentsPlaceHolder").load("include/usersTable.html?" + Math.random(), function() {
		$(this).contents().unwrap();
		if (userType == "employee") {
			$('#divUsersSection').html("Users");
		}
		var tableDiv = "allUsersDiv";
		var reqHeaders = {
			'action' : 'get_individuals',
			"user_type" : userType
		}

		$.ajax({
			url : "processrequest",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			dataType : 'json',
			headers : reqHeaders,
			success : function(users) {
				pupulateUsersTable(tableDiv, userType, null, users, allUsers);
			},
			error : function(xhr, resp, text) {
				console.log(xhr, resp, text);
			}
		})
	});

}

function pupulateUsersTable(tableParentDiv, userType, apartment, users, isAllUsers) {

	removeTable("usersTable");
	$('#' + tableParentDiv).html('<table id="usersTable" class="display compact" style="width: 100%"></table>');
	var userLabel = "Tenant";

	var apartment_id = null;

	if (apartment != null) {
		apartment_id = apartment.apartment_id;
	}

	var columnsList = [ {
		"title" : "First Name",
		"data" : "individual.first_name"
	}, {
		"title" : "Last Name",
		"data" : "individual.last_name"
	} ];

	if (userType == "employee") {
		userLabel = "User";
	} else {
		if (tableParentDiv != "apartmentDetailsDiv") {
			columnsList.push({
				"title" : "Location",
				"mData" : null,
				render : function(data, type, row) {
					return data.apartment.apartment_number + ", " + data.property.property_name;
				}
			});
			columnsList.push({
				"defaultContent" : '<button id="detailsApartment" >Go to Apartment</button>'
			});
		}
		columnsList.push({
			"defaultContent" : '<button id="editUser">Edit</button>'
		});
	}

	usersTable = $('#usersTable').DataTable({

		"bProcessing" : true,
		"aaData" : users,
		"aoColumns" : columnsList,
		dom : 'Bfrtip',
		buttons : [ {
			text : 'New ' + userLabel,
			action : function(e, dt, node, config) {
				popUpUserForm(apartment_id, userType, null, isAllUsers);
			}
		} ]

	});

	$('#usersTable tbody').unbind("click").on('click', '#editUser', function() {
		var userData = usersTable.row($(this).parents('tr')).data();
		popUpUserForm(apartment_id, userType, userData, isAllUsers);
	});

	$('#usersTable tbody').on('click', '#detailsApartment', function() {
		var row = usersTable.row($(this).parents('tr')).data();
		displayApartmentDetails(row.apartmentoccupant.apartment_id)
	});
}

function popUpUserForm(apartment_id, userType, userData, isAllUsers) {

	var userLabel = "Occupant";
	if (userType == "employee") {
		userLabel = "User";
	}

	$("#dialog").remove();
	$(document.body).append('<div id="dialog" style="display:none;">Its the money not the principles</div>');

	var apartment = null;
	var individual = null;
	var apartmentOccupant = null;

	if (userData != null) {
		apartment = userData.apartment;
		individual = userData.individual;
		apartmentOccupant = userData.apartmentoccupant;
	}

	var newIndividual = true;
	if (individual != null) {
		newIndividual = false;
	}

	$("#dialog").attr("title", "New " + userLabel);
	if (!newIndividual) {
		$("#dialog").attr("title", "Edit " + userLabel);
	}

	$("#dialog").load("include/user.html?" + Math.random(), function() {

		$("#date_from").datepicker({
			dateFormat : 'yy-mm-dd'
		});
		$("#date_to").datepicker({
			dateFormat : 'yy-mm-dd'
		});

		if (apartment != null && apartment.apartment_id !== 'undefined') {
			$("#property_id").attr("value", apartment.property_id);
			$("#apartment_id").attr("value", apartment.apartment_id);
		}
		if (apartment_id != null) {
			$("#apartment_id").attr("value", apartment_id);
		}

		if (userType == "employee") {
			$("#div_date_from").hide();
			$("#div_date_to").hide();
		} else {
			$("#div_user_name").hide();
		}

		if (!newIndividual) {
			$("#dialog").attr("title", "Edit " + userLabel);
			$("#individual_id").attr("value", individual.individual_id);
			$("#first_name").attr("value", individual.first_name);
			$("#last_name").attr("value", individual.last_name);
			$("#email").attr("value", individual.email);
			$("#date_from").datepicker("setDate", formatDateString(apartmentOccupant.date_from));
			$("#date_to").datepicker("setDate", formatDateString(apartmentOccupant.date_to));
		}

		$("#user_submit_button").on('click', function(e) {
			e.preventDefault();

			var apartmentoccupant = {
				'individual_id' : $('input[id=individual_id]').val(),
				'apartment_id' : $('input[id=apartment_id]').val()

			};

			if ($('input[id=date_from]').val() != "") {
				apartmentoccupant.date_from = $('input[id=date_from]').val()
			}

			if ($('input[id=date_to]').val() != "") {
				apartmentoccupant.date_to = $('input[id=date_to]').val()
			}

			var individual = {
				'user_name' : $('input[id=user_name]').val(),
				'first_name' : $('input[id=first_name]').val(),
				'last_name' : $('input[id=last_name]').val(),
				'email' : $('input[id=email]').val()

			};

			if (!newIndividual) {
				individual.individual_id = $('input[id=individual_id]').val();
			}

			var formData = {
				'individual' : individual,
				'apartmentoccupant' : apartmentoccupant
			};
			var reqHeaders = {
				'action' : 'post_individuals',
				"user_type" : "occupants"
			}

			if (isAllUsers == true) {
				formData = {
					'individual' : individual
				};
			}
			if (userType == "employee") {
				reqHeaders = {
					'action' : 'post_individuals',
					"user_type" : "employee"
				}
			}

			$("body").css("cursor", "progress");
			// send ajax
			$.ajax({
				url : 'processrequest', // url where to submit the request
				type : "POST", // type of action POST || GET
				contentType : "application/json; charset=utf-8",
				headers : reqHeaders,
				dataType : 'json', // data type
				data : JSON.stringify(formData), // post data || get data
				success : function(result) {
					$("body").css("cursor", "default");
					$('#dialog').dialog('close');
					if (userType == "employee") {
						$("#dialog").remove();
						$(document.body).append('<div id="dialog" min-width: 50px;">A email has been sent to you to assist you in setting up a password.</div>');
						$("#dialog").attr("title", "New " + userLabel);
						$("#dialog").dialog({
							modal : true,
							buttons : {
								"OK" : function() {
									$(this).dialog("close");
								}
							}
						});

					}

					var parentDiv = "apartmentDetailsDiv";
					if (isAllUsers) {
						parentDiv = "allUsersDiv";
					}

					pupulateUsersTable(parentDiv, userType, apartment, result, isAllUsers);
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
		width : "500px",
		maxWidth : "800px" //both these attributes are needed to maintain width
	});

}

function listHistory() {

	var reqHeaders = {
		'action' : 'get_history'
	}

	$.ajax({
		url : "processrequest",
		type : "POST",
		contentType : "application/json; charset=utf-8",
		dataType : 'json',
		headers : reqHeaders,
		success : function(history) {
			populateHistoryTable(history);
			// console.log(result);
		},
		error : function(xhr, resp, text) {
			console.log(xhr, resp, text);
		}
	})

}

function populateHistoryTable(history) {

	removeTable("historyTable");
	$('#mainContents').html('<div id="mainContentsPlaceHolder" style="float: left;min-width: 80%"></div>');
	$("#mainContentsPlaceHolder").html('<div id="historyDiv"><table id="historyTable"   class="display compact" style="width: 100%"></table><div>');

	var historyTable = $('#historyTable').DataTable(
			{
				"bProcessing" : true,
				"aaData" : history,

				"aoColumns" : [
						{
							"title" : "Date and time",
							"mData" : null,
							render : function(data, type, row) {
								//TODO: use formatDateString later
								return data.date_time.dateTime.date.year + '-' + data.date_time.dateTime.date.month + '-' + data.date_time.dateTime.date.day + " "
										+ data.date_time.dateTime.time.hour + ':' + data.date_time.dateTime.time.minute + ':' + data.date_time.dateTime.time.second;
							}
						},

						{
							"title" : "What was done",
							"mData" : "notes"
						}, {
							"defaultContent" : '<button id="detailsHistory">Details</button>'
						}

				]

			});

}

function signOutLink() {
	$("#sign_out").on('click', function(e) {
		e.preventDefault();

		$.ajax({
			url : "processrequest",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			dataType : 'json',
			headers : {
				"action" : "logout"
			},
			success : function(response) {
				if (response.logged_out == true) {
					setCookie("logged_in", "false", 1);
					window.location.href = "";
				}

			},
			error : function(xhr, resp, text) {
				console.log(xhr, resp, text);
			}
		});

		return false;// because e.preventDefault(); may not work
	});

}

function loginPage() {

	$("#regular_login").hide();
	$("#new_account").show();
	$("#forgot_password").show();

	$("#div_reenter_password").hide();
	$("#div_email").hide();
	$("#user_create_button").hide();
	$("#reset_password_button").hide();

	$("#user_login_button").on('click', function(e) {
		e.preventDefault();

		$.ajax({
			url : "processrequest",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			dataType : 'json',
			headers : {
				"action" : "login",
				"user_name" : $('input[id=user_name]').val(),
				"user_password" : $('input[id=user_password]').val()
			},
			success : function(response) {
				if (response.logged_in == true) {
					setCookie("logged_in", "true", 1);
					window.location.href = "index.html";
					/*
					 * setCookie("logged_in","true",1);
					 * 
					 * createMenu(); listProperties();
					 */
				} else {
					$("#message").show();
					$("#message").html("your username or password is incorrect.");
				}

			},
			error : function(xhr, resp, text) {
				console.log(xhr, resp, text);
			}
		})

		return false;// because e.preventDefault(); may not work
	});

	$("#user_create_button").on('click', function(e) {
		e.preventDefault();

		$.ajax({
			url : "processrequest",
			type : "POST",
			contentType : "application/json; charset=utf-8",
			dataType : 'json',
			headers : {
				"action" : "createUser",
				"user_name" : $('input[id=user_name]').val(),
				"user_password" : $('input[id=user_password]').val()
			},
			success : function(response) {
				listProperties();
			},
			error : function(xhr, resp, text) {
				console.log(xhr, resp, text);
			}
		})

		return false;// because e.preventDefault(); may not work
	});

	$("#regular_login").on('click', function(e) {
		e.preventDefault();

		$("#regular_login").hide();
		$("#new_account").show();
		$("#forgot_password").show();

		$("#user_login_button").show();
		$("#div_remember_me_checkbox").show();
		$("#div_reenter_password").hide();
		$("#div_email").hide();
		$("#user_create_button").hide();
		$("#reset_password_button").hide();

		return false;// because e.preventDefault(); may not work
	});

	$("#new_account").on('click', function(e) {
		e.preventDefault();

		$("#regular_login").show();
		$("#new_account").hide();
		$("#forgot_password").show();

		$("#user_login_button").hide();
		$("#div_remember_me_checkbox").hide();
		$("#div_reenter_password").show();
		$("#div_email").show();
		$("#user_create_button").show();
		$("#reset_password_button").hide();

		return false;// because e.preventDefault(); may not work
	});

	$("#forgot_password").on('click', function(e) {
		e.preventDefault();

		$("#regular_login").show();
		$("#new_account").show();
		$("#forgot_password").hide();

		$("#user_login_button").hide();
		$("#div_remember_me_checkbox").hide();
		$("#div_reenter_password").hide();
		$("#div_email").show();
		$("#user_create_button").hide();
		$("#reset_password_button").show();

		return false;// because e.preventDefault(); may not work
	});

}

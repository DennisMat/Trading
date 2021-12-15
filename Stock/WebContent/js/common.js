
var usersTable;
var propertiesTable;
var addressTable;
var apartmentsTable;



function doStuff(){


	$('#mainContents').html('<div id="mainContentsPlaceHolder" style="float: left;min-width: 80%"></div>');
	//alert("doc ready");
	$("#header").load("include/header.html", function() {
		$(this).contents().unwrap();

		/*
		$("#target").click(function() {
			alert("Handler for .click() called.");
		});
		*/
		
		signOutLink();


	});
	
	if(getCookie("logged_in")=="true"){//cookies store only strings so it's not boolean
		createMenu();
	}
	
	if(getCookie("logged_in")=="true"){
		
		setCookie("logged_in","true",1);
		
		createMenu();
		listStockTransactions();

	}else{
		window.location.href = 'login.html';
		
	}
	
	
	$("#footerPlaceholder").load("include/footer.html", function() {
		$(this).contents().unwrap();

	});



}



function removeTable(someTable) {
	$( "#"+someTable+"_wrapper").remove();
	// this is done because the destroy removes the parent div of a table, which
	// is an odd behavior.
	//$('#mainContents').html('<div id="mainContentsPlaceHolder" style="float: left;min-width: 80%"></div>');
}



function destroyTable(someTable) {
//	if (typeof someTable !== 'undefined' || someTable != null) {
//
//		someTable.destroy(true);
//	}
	try {
		someTable.destroy(true);
		someTable = null;
	} catch (err) {
		 var message=err.message;
	}
}

function destroyTables() {
	destroyTable(usersTable);
	destroyTable(propertiesTable);
	destroyTable(apartmentsTable);

	// this is done because the destroy removes the parent div of a table, which
	// is an odd behavior.
	$('#mainContents').html('<div id="mainContentsPlaceHolder" style="float: left;min-width: 80%"></div>');


}

function exportData(){
	window.location.replace("processrequest");
}


function setCookie(cname, cvalue, exdays) {
	  var d = new Date();
	  d.setTime(d.getTime() + (exdays*24*60*60*1000));
	  var expires = "expires="+ d.toUTCString();
	  document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}


function getCookie(cname) {
	  var name = cname + "=";
	  var decodedCookie = decodeURIComponent(document.cookie);
	  var ca = decodedCookie.split(';');
	  for(var i = 0; i <ca.length; i++) {
	    var c = ca[i];
	    while (c.charAt(0) == ' ') {
	      c = c.substring(1);
	    }
	    if (c.indexOf(name) == 0) {
	      return c.substring(name.length, c.length);
	    }
	  }
	  return "";
}

function formatDateString(dateJsonObject){
	try {
	if(dateJsonObject!== 'undefined'){
		var dayString=dateJsonObject.day;


		if(dayString.length==1){
			dayString="0"+dayString;
		}

		var monthString=dateJsonObject.month;

		if(monthString.length==1){
			monthString="0"+monthString;
		}

		return dateJsonObject.year+"-"+monthString+"-"+dayString;
	}
	}catch(err) {} 
	return "";
}



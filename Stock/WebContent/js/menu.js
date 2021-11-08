function createMenu(){
	
	$("#menuPlaceholder").load("include/menu.html", function() {
		$(this).contents().unwrap();

			$( "#menu" ).menu({
				select: function( event, ui ) {
				
					var menuSelected = $(ui.item).find('div').attr('id');
	
					if(menuSelected=='properties_menu'){
						listProperties();
					}
					
					if(menuSelected=='reminders_menu'){
						listReminders();
					}				
					if(menuSelected=='tenants_menu'){
						listUsers("occupants",true);
					}
					if(menuSelected=='users_menu'){					
						listUsers("employee");
					}
					if(menuSelected=='history_menu'){					
						listHistory();
					}
					if(menuSelected=='export_data'){					
						exportData();
					}
					
	
				}
			});//end of $( "#menu" ).menu({

	});
}
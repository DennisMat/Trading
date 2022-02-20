function createMenu(){
	
	$("#menuPlaceholder").load("include/menu.html", function() {
		$(this).contents().unwrap();

			$( "#menu" ).menu({
				select: function( event, ui ) {
				
					var menuSelected = $(ui.item).find('div').attr('id');
	
					if(menuSelected=='main_page_menu'){
						listStockTransactions();
					}
					
					if(menuSelected=='stock_list_menu'){
						listStocks();
					}	
					if(menuSelected=='settings_menu'){
						settings();
					}	

	
				}
			});//end of $( "#menu" ).menu({

	});
}
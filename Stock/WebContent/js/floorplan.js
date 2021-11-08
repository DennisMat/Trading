function listFloorPlans(property_id) {
	
	
	
	$.ajax({
		url : "processrequest",
		type : "POST", 
		contentType : "application/json; charset=utf-8",
		headers:{"action":"get_floorplan","property_id":property_id},
		dataType : 'json',
		success : function(floorplans) {
			populateFloorPlanTable(property_id,floorplans);
			// console.log(result);
		},
		error : function(xhr, resp, text) {
			console.log(xhr, resp, text);
		}
	})
	
}


function populateFloorPlanTable(property_id, floorplans) {
	
	removeTable("floorPlansTable");
	$("#floorContentsPlaceHolder").html('<div id="floorplansDiv"><table id="floorPlansTable"  class="display compact" style="width: 100%"></table><div>');

	var floorPlansTable=$('#floorPlansTable').DataTable({
		"bProcessing" : true,
		"aaData" : floorplans,

		"aoColumns" : [ {
			"mData" : "floor_plan_name"}, 
			{"mData" : "area"},
			{"defaultContent" : '<button id="editFloorPlan">Edit</button>'} 
			
			],

	dom : 'Bfrtip',
		buttons : [ {
			text : 'New Floor Plan',
			action : function(e, dt, node, config) {
				popUpFloorPlanForm(property_id,null);			
			}
		} ]
	
	});
	
	$('#floorPlansTable tbody').on('click', '#editFloorPlan', function() {
		var floor_plan =floorPlansTable.row($(this).parents('tr')).data();
		popUpFloorPlanForm(property_id,floor_plan);
	});
	
}



function popUpFloorPlanForm(property_id,floor_plan) {

	$("#dialog").remove();
	$(document.body).append('<div id="dialog" title="Dialog Title" style="display:none;">Its the money not the principles</div>');
	$("#dialog").attr("title", "Edit Record");
	$("#dialog").load("include/floorplan.html?" + Math.random(), function() {

		$("#property_id").attr("value", property_id);
		
		if (floor_plan != null) {
			$("#floor_plan_id").attr("value", floor_plan.floor_plan_id);
			$("#floor_plan_name").attr("value", floor_plan.floor_plan_name);
			$("#area").attr("value", floor_plan.area);
			$("#area_unit_of_measure_id").attr("value", floor_plan.area_unit_of_measure_id);
			//$("#notes").attr("value", floor_plan.notes);
			$('#notes').val(floor_plan.notes);
			
			if (typeof floor_plan.image_path!== 'undefined' && floor_plan.image_path.length>0) {
				$("#floor_plan_image").attr("src", floor_plan.image_path);
				$("#image_upload_text").html('Change above Image');
				
			}else{
				$("#div_floor_plan_image").hide();
				$("#image_upload_text").html('Upload an Image');
				
			}

		}else{
			$("#div_floor_plan_image").hide();
			$("#image_upload_text").html('Upload an Image');
			
		}

		$("#floor_plan_submit_button").on('click', function(e) {
			e.preventDefault();

			$("body").css("cursor", "progress");

			var formData=new FormData(); 
			if($('#floor_plan_image_upload').val().length>5){
				formData.append('floor_plan_image_upload', $('#floor_plan_image_upload')[0].files[0]);
			}
			
			formData.append('floor_plan_id', $('#floor_plan_id').val());
			formData.append('floor_plan_name', $('#floor_plan_name').val());
			formData.append('property_id', $('#property_id').val());
			formData.append('area', $('#area').val());
			formData.append('area_unit_of_measure_id', $('#area_unit_of_measure_id').val());
			formData.append('notes', $('#notes').val());

			if($('#floor_plan_name').val().length>0){
				$.ajax({
					url : 'processrequest',  
					type : "POST", 
					processData: false,  //  this setting is needed for uploading images
			        contentType: false,   //  this setting is needed for uploading images
			    	headers: { 'action': 'post_floorplan'},
			        data : formData,
					success : function(result) {		
						listFloorPlans(property_id);
						$("body").css("cursor", "default");
						$('#dialog').dialog('close');
						
					},
					error : function(xhr, resp, text) {
						console.log(xhr, resp, text);
						$("body").css("cursor", "default");
						$('#dialog').dialog('close');
					}
				});	
			}


			return false;// because e.preventDefault(); may not work
		});

	});

	$("#dialog").dialog({
		width : "50%"
	});

}

function fillFloorLayoutSection(floor_plan){
	
	if (typeof propertyDetails == 'undefined') {
		$("#floorPlanSection").hide();
	
	}else{
		$("#floorPlanSection").show();
		$("#floor_plan_name").html(floor_plan.floor_plan_name);
		$("#floor_plan_image").attr("src", floor_plan.image_path);
	}
	
	
}


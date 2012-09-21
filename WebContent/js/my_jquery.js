$(function() {
	
	$('#buttonExecute').click(function() {
		executeAnalize();
	});

	function split( val ) {
		return val.split( / \s*/ );
	}
	function extractLast( term ) {
		return split( term ).pop();
	}

	$( "#textToanalize" )
		// don't navigate away from the field on tab when selecting an item
		.bind( "keydown", function( event ) {
			if ( event.keyCode === $.ui.keyCode.TAB &&
					$( this ).data( "autocomplete" ).menu.active ) {
				event.preventDefault();
			}
		})
		.autocomplete({
			source: function( request, response ) {
				$.getJSON( "autocomplete_natural_query_commands.html", {
					term: extractLast( request.term )
				}, response );
			},
			search: function() {
				// custom minLength
				var term = extractLast( this.value );
				if ( term.length < 2 ) {
					return false;
				}
			},
			focus: function() {
				// prevent value inserted on focus
				return false;
			},
			select: function( event, ui ) {
				var terms = split( this.value );
				// remove the current input
				terms.pop();
				// add the selected item
				terms.push( ui.item.value );
				// add placeholder to get the comma-and-space at the end
				terms.push( "" );
				this.value = terms.join( " " );
				return false;
			}
		});
	
	
	
});



function executeList(queryId) {
	$.getJSON('get_query.html', { queryId: queryId }, function(nplResponse) {
		var data = nplResponse.responseData;
		if(nplResponse.responseType == 'text') {
			$('#divOutput').html(data.simpleText);	
		} else if(nplResponse.responseType == 'list') {
			var tbl_body = "";
		    $.each(data, function() {
		        var tbl_row = "";
		        $.each(this, function(k , v) {
		            tbl_row += "<td>"+v+"</td>";
		        })
		        tbl_body += "<tr>"+tbl_row+"</tr>";                 
		    })
		    $("#target_table_id").html(tbl_body);
		}
	});
}


function executeAnalize() {
	var nplRequest = { text: $('#textToanalize').val(), userAgent: 'webbrowser'};
	$.getJSON('analize.html', nplRequest, function(nplResponse) {
		var data = nplResponse.responseData;
		if(nplResponse.responseType == 'text') {
			$('#divOutput').html(data.simpleText);	
		} else if(nplResponse.responseType == 'list') {
			var tbl_body = "";
		    $.each(data, function() {
		        var tbl_row = "";
		        $.each(this, function(k , v) {
		            tbl_row += "<td>"+v+"</td>";
		        })
		        tbl_body += "<tr>"+tbl_row+"</tr>";                 
		    })
		    $("#target_table_id").html(tbl_body);
		} else if(nplResponse.responseType == 'pie-chart') {
			var data = nplResponse.responseData;
			var small = new Array();
			var tmp;
			var result = new Array();
			var i = 0;
			$.each(data, function() {
			    $.each(this, function(k , v) {
			    	if (i==0){
			    		tmp = v;
			    		i++;
			    	}
			    	else{
			    		small = [tmp,v];
			    		result.push(small);
			    		i = 0;
			    	}
			    })                
			})

		    $(document).ready(function(){
				  plot2 = jQuery.jqplot('pieChart',[big],
				    {
				      title: ' ',
				      seriesDefaults: {
				        shadow: false,
				        renderer: jQuery.jqplot.PieRenderer,
				        rendererOptions: {
				          startAngle: 180,
				          sliceMargin: 4,
				          showDataLabels: true }
				      },
				      legend: { show:true, location: 'w' }
				    }
				  );
				});
		}
	});
}
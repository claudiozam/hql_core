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
	
	$(document).ready(function(){
		  plot2 = jQuery.jqplot('pieChart',
		    [[['Verwerkende industrie', 9],['Retail', 0], ['Primaire producent', 0],
		    ['Out of home', 0],['Groothandel', 0], ['Grondstof', 0], ['Consument', 3], ['Bewerkende industrie', 2]]],
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
	
});

function executeAnalize() {
	var nplRequest = { text: $('#textToanalize').val(), userAgent: 'webbrowser'};
	$.getJSON('analize.html', nplRequest, function(nplResponse) {
		var data = nplResponse.responseData;
		$('#divOutput').html(data.simpleText);
	});
}
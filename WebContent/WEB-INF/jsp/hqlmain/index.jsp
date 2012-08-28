<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Ejemplo HQL</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<style>
.ui-autocomplete-loading { background: white url('images/ui-anim_basic_16x16.gif') right center no-repeat; }
</style>
      <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet" type="text/css"/>
      <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.5/jquery.min.js"></script>
      <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js"></script>
<script type="text/javascript">


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
			
			
		}
		
	});
}


</script>
</head>
<body>
	Texto entrada<textarea rows="7" cols="30" id="textToanalize"></textarea><br />
	<input type="button" id="buttonExecute" value="Analizar"/><br />
	<div id="divOutput"></div>
	<table id="target_table_id"></table>
</body>
</html>
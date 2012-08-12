<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Ejemplo HQL</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript" src="<c:url value="/js/jquery-1.7.2.js" />"></script> 
<script type="text/javascript">
	$(function() {
		$('#buttonExecute').click(function() {
			executeAnalize();
		});
		
	});
	
	
	
	function executeAnalize() {
		var nplRequest = { text: $('#textToanalize').val(), userAgent: 'webbrowser'};
		$.getJSON('analize.html', nplRequest, function(nplResponse) {
			var data = nplResponse.responseData;
			$('#divOutput').html(data.simpleText);
		});
	}
	
</script>
</head>
<body>
	Texto entrada<textarea rows="7" cols="30" id="textToanalize"></textarea><br />
	<input type="button" id="buttonExecute" value="Analizar"/><br />
	<div id="divOutput"></div>
	
</body>
</html>
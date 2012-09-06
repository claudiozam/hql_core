<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Listado</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <link href="<c:url value="/css/jquery-ui.css" />" rel="stylesheet" type="text/css"/>
      <link href="<c:url value="/css/jquery.jqplot.min.css" />" rel="stylesheet" type="text/css"/>
      <script type="text/javascript" src="<c:url value="/js/jquery-1.8.js" />"></script>
      <script type="text/javascript" src="<c:url value="/js/jquery-ui.js" />"></script>
      <script type="text/javascript" src="<c:url value="/js/jquery.jqplot.min.js" />"></script>    
      <script type="text/javascript" src="<c:url value="/js/jqplot.pieRenderer.min.js" />"></script>
      <script type="text/javascript" src="<c:url value="/js/my_jquery.js" />"></script>
    <script type="text/javascript">
    	$(function() {
    		executeList(${queryId});
    	});
    </script>
</head>
<body>
	<div id="divOutput"></div>
	<table id="target_table_id"></table>
</body>
</html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Ejemplo HQL</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<style>
.ui-autocomplete-loading { background: white url('images/ui-anim_basic_16x16.gif') right center no-repeat; }
</style>
      <link href="<c:url value="/css/jquery-ui.css" />" rel="stylesheet" type="text/css"/>
      <link href="<c:url value="/css/jquery.jqplot.min.css" />" rel="stylesheet" type="text/css"/>
      <script type="text/javascript" src="<c:url value="/js/jquery-1.8.js" />"></script>
      <script type="text/javascript" src="<c:url value="/js/jquery-ui.js" />"></script>
      <script type="text/javascript" src="<c:url value="/js/jquery.jqplot.min.js" />"></script>    
      <script type="text/javascript" src="<c:url value="/js/jqplot.pieRenderer.min.js" />"></script>
      <script type="text/javascript" src="<c:url value="/js/my_jquery.js" />"></script>
</head>
<body>
	Texto entrada<textarea rows="1" cols="70" id="textToanalize"></textarea><br />
	<input type="button" id="buttonExecute" value="Analizar"/><br />
	<div id="divOutput"></div>
	<table id="target_table_id"></table>
	<div id="pieChart" style="margin-top:20px; margin-left:20px; width:460px; height:300px;"></div>
</body>
</html>
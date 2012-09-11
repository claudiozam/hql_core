<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Gráfico</title>
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
    		executeGraph(${queryId});
    	});
    </script>
</head>
<body>
	<div id="pieChart" style="margin-top:20px; margin-left:20px; width:460px; height:300px;"></div>
</body>
</html>
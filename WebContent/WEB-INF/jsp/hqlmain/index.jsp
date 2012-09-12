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
      <link href="<c:url value="/css/bootstrap.css" />" rel="stylesheet" type="text/css"/>
      <link href="<c:url value="/css/my_css.css" />" rel="stylesheet" type="text/css"/>
      <script type="text/javascript" src="<c:url value="/js/jquery-1.8.js" />"></script>
      <script type="text/javascript" src="<c:url value="/js/jquery-ui.js" />"></script>
      <script type="text/javascript" src="<c:url value="/js/jquery.jqplot.min.js" />"></script>    
      <script type="text/javascript" src="<c:url value="/js/jqplot.pieRenderer.min.js" />"></script>
      <script type="text/javascript" src="<c:url value="/js/my_jquery.js" />"></script>
      <script type="text/javascript" src="<c:url value="/js/bootstrap.js" />"></script>
</head>
<body>
	<div id="header" style="background-color:#333333; color:white; heigth:20px; padding-left:25px">
		<h3> Proyecto HQL </h3>
	</div>
	<table id="search" style="width:95%; margin-left:auto; margin-right:auto;">
	<tr>
		<td>
			<form class="form-inline">
				<textarea rows="3"id="textToanalize" placeholder="Ingresa tu consulta."></textarea>
				<input type="button" id="buttonExecute" value="Analizar" class="btn btn-primary"/>
			</form>
		</td>
	</tr>
	<tr>
		<td style="width:50%" class="well">
		<table>
			<tr id="result"> 
				<td id="divOutput"></td>
				<td><table id="target_table_id" class="table table-striped table-condensed"></table></td>
				<td id="chartOutput" style="margin-top:20px; margin-left:20px; width:460px; height:300px;"></td>	
			</tr>	
		</table>
		</td>
		<td id="td_log" class="well">
		  <div id="log"> <ul id="ul_log"></ul> </div>
		</table>
		</td>
	</tr>
	</table>
</body>
</html>
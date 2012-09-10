<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Bootstrap, from Twitter</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le styles -->
    <link href="<c:url value="/css/bootstrap.css" />" rel="stylesheet">
    
    <link href="<c:url value="/css/bootstrap-responsive.css" />" rel="stylesheet">
<script type="text/javascript" src="<c:url value="/js/jquery-1.8.js" />"></script>
      <script type="text/javascript" src="<c:url value="/js/jquery-ui.js" />"></script>
      <script type="text/javascript" src="<c:url value="/js/jquery.jqplot.min.js" />"></script>    
      <script type="text/javascript" src="<c:url value="/js/jqplot.pieRenderer.min.js" />"></script>
      <script type="text/javascript" src="<c:url value="/js/my_jquery.js" />"></script>
    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

  </head>

  <body>


    <div class="container">

      <h1>Listado</h1>
	<div id="divOutput"></div>
	<table id="target_table_id" class="table table-bordered"></table>

    </div> <!-- /container -->

      <script type="text/javascript">
    	$(function() {
    		executeList(${queryId});
    	});
        setTimeout(function(){executeList(${queryId});},3000);
    </script>
  

  </body>
</html>

<!DOCTYPE html>
<%@ page import="java.util.*" %>
<html lang="en" ng-app="tabletuing">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
	<title>openHDS Angularjs Tablet UI</title>
	<link rel="stylesheet" href="resources/css/app.css" />
	<!-- bootstrap CSS -->
	<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css">
	<!-- bootstrap optional theme -->
	<link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap-theme.min.css">
	
	<script type="text/javascript">
    	var contextPath = "<%= application.getContextPath()  %>";
	</script>
	
	<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>  -->
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.14/angular.js"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.14/angular-route.js"></script>
	<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.2.14/angular-resource.js"></script>
	<script src="http://angular-ui.github.io/bootstrap/ui-bootstrap-tpls-0.10.0.js"></script>
	<script src="resources/scripts/ng/app.js"></script>
	<script src="resources/scripts/ng/services.js"></script>
	<script src="resources/scripts/ng/controllers.js"></script>
	<script src="resources/scripts/ng/filters.js"></script>
	<script src="resources/scripts/ng/directives.js"></script>
	<script src="resources/scripts/ng/modal.js"></script>
	
	<!-- angular busy resources -->
	<!-- <script src="resources/lib/ng/angular-busy/angular-busy.js"></script>
	<link rel="stylesheet" href="resources/lib/ng/angular-busy/angular-busy.css"> -->
</head>
<body>
	
	<div class="navbar navbar-default navbar-fixed-top" role="navigation">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>

          <a class="navbar-brand" href="#">OpenHDS</a>
        </div>
        <div class="collapse navbar-collapse">
          <ul class="nav navbar-nav">
<!--             <li class="active"><a href="#">Home</a></li> -->
<!--             <li><a href="#about">About</a></li> -->
<!--             <li><a href="#contact">Contact</a></li> -->
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </div>
     <div class="container" ng-controller="MainCtrl" ng-view></div>
	
</body>
</html>

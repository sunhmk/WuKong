<!DOCTYPE html>
<html ng-app>
<head>
	<title>two way databinding</title>
	<meta charset="utf-8">
</head>
<body>
	<div>
		<input ng-model="greeting.text"/>
		<p>{{greeting.text}}, AngularJS</p>
	</div>
</body>
	<script type="text/javascript" src="resources/lib/angularjs-1.6.5/angular.js"></script>
</html>
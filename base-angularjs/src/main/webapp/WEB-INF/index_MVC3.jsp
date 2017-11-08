<!DOCTYPE html>
<html ng-app="mvc3">
<head>
	<title>MVC3</title>
	<meta charset="utf-8">
</head>
<body>
	<div ng-controller="CommonController">
		<div ng-controller="Controller1">
			<p>{{greeting.text}},Angular</p>
			<button ng-click="test1()">test1</button>
		</div>

		<div ng-controller="Controller2">
			<p>{{greeting.text}},Angular</p>
			<button ng-click="test2()">test2</button>
		</div>
		<button ng-click="commonFn()">通用</button>
	</div>
</body>
	<script type="text/javascript" src="resources/lib/angularjs-1.6.5/angular.js"></script>
	<script type="text/javascript" src="resources/test/MVC3.js"></script>
</html>
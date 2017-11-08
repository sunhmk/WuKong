<!doctype html>
<html>
   <head>
    	<script src="resources/lib/angularjs-1.6.5/angular.js"></script>
   		<title>第一个AngularJS程序</title>
   </head>
   <body ng-app="myapp">
      <div ng-controller="HelloController" >
         <h2>你好 ！第一个{{helloTo.title}}程序示例</h2>
      </div>
      <script>
         angular.module("myapp", [])
         .controller("HelloController", function($scope) {
            $scope.helloTo = {};
            $scope.helloTo.title = "AngularJS";
         });
      </script>
   </body>
</html>
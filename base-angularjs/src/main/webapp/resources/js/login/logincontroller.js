angular.module('mainApp')
	.controller('logincontroller',['$scope','$state',function($scope,$state){
		$scope.userInfo = {
			usename:"13180@qq.com",
			password:"131",
			autoLogin:true
		};
		var login ={
			'usename' :$scope.userInfo.usename,
			'password':$scope.userInfo.password,
			'auto'    :$scope.userInfo.autoLogin
		};
		$scope.register = function()
		{
			alert("register:" + login);
		};
		$scope.login    = function()
		{
			alert("login:" + login);
			$state.go("frame.views.dashboard");
		};
	}]);
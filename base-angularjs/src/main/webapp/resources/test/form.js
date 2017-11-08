var useInfoMofule=angular.module("UserInfoModule",[]);
useInfoMofule.controller("UserInfoCtrl",['$scope',function($scope){
	$scope.userInfo = {
		email:"13180@qq.com",
		password:"131",
		autoLogin:true
	};
	$scope.getFormData=function()
	{
		console.log($scope.userInfo);
	};
	$scope.setFormData=function(){
		$scope.userInfo={
			email:"111@126.com",
			password:"qq",
			autoLogin:false
		}
	};
	$scope.resetForm=function(){
			$scope.userInfo = {
		email:"13180@qq.com",
		password:"131",
		autoLogin:true
	};
	};
}])
var mymodule = angular.module("HelloAngular",[]);
mymodule.controller("Hello",['$scope',
    function Hello($scope){
		$scope.greeting = {
			text:'Hello'
		};
	}
]);
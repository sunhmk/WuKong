!function(){
    var app = angular.module('mainApp');
    //模态框对应的Controller
    app.controller('cfgCtrl', function ($scope, $uibModalInstance, items1) {
            $scope.items = items1;
            $scope.selected = {
                item: $scope.items[0]
            };

            $scope.ok = function () {
                $uibModalInstance.close($scope.selected.item);
            };

            $scope.cancel = function () {
                $uibModalInstance.dismiss('cancel');
            };
    });

}(),function(){
    var app = angular.module('mainApp');
    app.run(function($templateCache){
        $templateCache.put('cfg.html',
            '<div class="modal-header">'+
                '<h3 class="modal-title">I\'m a modal!</h3>'+
            '</div>'+
            '<div class="modal-body">'+
                '<ul>'+
                    '<li ng-repeat="item in items">'+
                        '<a href="#" ng-click="$event.preventDefault(); selected.item = item">{{ item }}</a>'+
                    '</li>'+
            '</ul>'+
            'Selected: <b>{{ selected.item }}</b>'+
            '</div>'+
            '<div class="modal-footer">'+
                '<button class="btn btn-primary" type="button" ng-click="ok()">OK</button>'+
                '<button class="btn btn-warning" type="button" ng-click="cancel()">Cancel</button>'+
            '</div>)');
    });
}();
'use strict';
var mainApp=angular.module('mainApp',['ui.router','oc.lazyLoad','ui.load','ngAnimate','ui.bootstrap']);
window.APP = { version : 'v=20170805' };
//上下文路径配置
var cxtPath="base-angularjs/resources/";
var headerMenuList = [
	{
	  name:'首页',
	  href:'#'
	},
	  {
	  name:'功能',
	  href:'#'
	},
	  {
	  name:'Profile',
	  href:'#'
	},
	  {
	  name:'帮助',
	  href:'#'
	},
	  {
	  name:'设置',
	  href:'#',
	  data:[
	  {
	  	name:'我的资料',
	  	href:'#'
	  },
	  {
	  	name:'修改密码',
	  	href:'#'
	  },
	  {
	  	name:'退出',
	  	href:''
	  }
	  ]
	},
	{
		name:'修改主题',
		href:'#',
		data:[
		{
			name:'主题1',
			href:'#'
		},
		{
			name:'主题2',
			href:'#'
		},
		{
			name:'主题3',
			href:'#'
		}
		]
	}
];

var sideMenuList = [
	{
		item:'Dashboard',
		icon:'dashboard',
		href:'#/frame/views/dashboard'
	},
	{
		item:'Cards',
		icon:'cards',
		href:'#'
	},
	{
		item:'Charts',
		icon:'charts',
		subItems:[
		{
			item:'Flot',
			href:'#/frame/views/charts/flot'
		},
		{
			item:'Radial',
			href:'#/frame/views/charts/radial'
		},
		{
			item:'Rickshaw',
			href:'#/frame/views/charts/rickshaw'
		}
		]
	},
	{
		item:'Forms',
		icon:'forms',
		subItems:[
		{
			item:'Classic',
			href:'#'
		},
		{
			item:'Validation',
			href:'#'
		},
		{
			item:'Advanced',
			href:'#'
		},
		{
			item:'Material',
			href:'#'
		},
		{
			item:'Editors',
			href:'#'
		},
		{
			item:'Upload',
			href:'#'
		},
		{
			item:'Dropzone',
			href:'#'
		},
		{
			item:'xEditable',
			href:'#'
		}
		]
	},
	{
		item:'Tables',
		icon:'tables',
		subItems:[
		{
			item:'Classic',
			href:'#'
		},
		{
			item:'DataTable',
			href:'#'
		},
		{
			item:'ngTable',
			href:'#'
		},
		{
			item:'xEditable',
			href:'#'
		}
		]
	},
	{
		item:'Layouts',
		icon:'layouts',
		subItems:[
		{
			item:'Columns',
			href:'#'
		},
		{
			item:'Overlap',
			href:'#'
		},
		{
			item:'Boxed',
			href:'#'
		},
		{
			item:'Tabs Deep Link',
			href:'#'
		},
		{
			item:'Containers',
			href:'#'
		}
		]
	},
	{
		item:'Elements',
		icon:'elements',
		subItems:[
		{
			item:'Colors',
			href:'#'
		},
		{
			item:'Whiteframes',
			href:'#'
		},
		{
			item:'Lists',
			href:'#'
		},
		{
			item:'Bootstrapui',
			href:'#'
		},
		{
			item:'Buttons',
			href:'#'
		},
		{
			item:'Sweet-alert',
			href:'#'
		},
		{
			item:'Spinners',
			href:'#'
		},
		{
			item:'Navtree',
			href:'#'
		},
		{
			item:'Grid',
			href:'#'
		},
		{
			item:'Grid Masonry',
			href:'#'
		},
		{
			item:'Typography',
			href:'#'
		},
		{
			item:'Icons',
			href:'#'
		},
		{
			item:'Utilities',
			href:'#'
		}
		]
	},
	{
		item:'Maps',
		icon:'maps',
		subItems:[
		{
			item:'Google Maps Full',
			href:'#'
		},
		{
			item:'Google Maps',
			href:'#'
		},
		{
			item:'Vector Maps',
			href:'#'
		},
		{
			item:'Datamaps',
			href:'#'
		}
		]
	},
	{
		item:'Pages',
		icon:'pages',
		subItems:[
		{
			item:'Timeline',
			href:'#'
		},
		{
			item:'Invoice',
			href:'#'
		},
		{
			item:'Pricing',
			href:'#'
		},
		{
			item:'Contacts',
			href:'#'
		},
		{
			item:'FAQ',
			href:'#'
		},
		{
			item:'Projects',
			href:'#'
		},
		{
			item:'Blog',
			href:'#'
		},
		{
			item:'Article',
			href:'#'
		},
		{
			item:'Profile',
			href:'#'
		},
		{
			item:'Gallery',
			href:'#'
		},
		{
			item:'Wall',
			href:'#'
		},
		{
			item:'Search',
			href:'#'
		},
		{
			item:'Message Board',
			href:'#'
		}
		]
	},
	{
		item:'User',
		icon:'user',
		subItems:[
		{
			item:'Login',
			href:'#'
		},
		{
			item:'Signup',
			href:'#'
		},
		{
			item:'Lock',
			href:'#'
		},
		{
			item:'Recover',
			href:'#'
		}
		]
	},
	{
		item:'HTML5/jQuery',
		icon:'jtml',
		href:'#'
	}
];

mainApp.controller('mainAppController',['$scope','$rootScope','$uibModal','$log',
	function controller($scope,$rootScope,$uibModal,$log){
		headerMenuList = headerMenuList.reverse();

		$scope.items = ['item1', 'item2', 'item3'];

        $scope.open = function (size) {
            var modalInstance = $uibModal.open({
                templateUrl: 'cfg.html',
                controller: 'cfgCtrl',
                backdrop: "static",
                size: size,
                resolve: {
                    items1: function () {
                        return $scope.items;
                    }
                }
            });

            modalInstance.result.then(function (selectedItem) {
                $scope.selected = selectedItem;
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };
        /*$scope.toggleAnimation = function () {
            $scope.animationsEnabled = !$scope.animationsEnabled;
        };*/

		//$scope.sideMenuList = sideMenuList;
	},
]).config(['$controllerProvider', '$compileProvider', '$filterProvider', '$provide',
	    function ($controllerProvider,   $compileProvider,   $filterProvider,   $provide) {
	        // lazy controller, directive and service
	        mainApp.controller = $controllerProvider.register;
	        mainApp.directive  = $compileProvider.directive;
	        mainApp.filter     = $filterProvider.register;
	        mainApp.factory    = $provide.factory;
	        mainApp.service    = $provide.service;
	        mainApp.constant   = $provide.constant;
	        mainApp.value      = $provide.value;
	    }
	])
.config(['$ocLazyLoadProvider', function($ocLazyLoadProvider) {
      // We configure ocLazyLoad to use the lib script.js as the async loader
      $ocLazyLoadProvider.config({
          debug:false,
          events: true,
          modules: [{
              name: 'toaster',
              files: [
                  cxtPath + 'lib/angularjs-1.6.5/toaster.js',
                  cxtPath + 'lib/angularjs-1.6.5/toaster.css'
              ]
          }]
      });
    }])
.directive('header',[function(){
	return{
		scope:{
			onClick:'&'
		},
		restrict:'E',
		templateUrl:cxtPath + "js/tpl/header.tpl",
		controller:function($scope,$rootScope)
		{
			$scope.headerMenuList = headerMenuList;
		}
		/*link: function(scope, el, attr) {
			el.
			<li><a href="#">Settings</a></li>
		}*/
	};
}])
.directive('footer',[function(){
	return{
		scope:{},
		restrict:'E',
		templateUrl:cxtPath + "js/tpl/footer.tpl",
	};
}]).directive('asidecontent',['$window',function($window){
	return {
		restrict:'C',
		scope:{},
		link:function($scope, element, attrs){
			var winowHeight = $window.innerHeight; //获取窗口高度
                var headerHeight = 50;
                var footerHeight = 50;
                element.css('overflow-y','scroll');
                element.css('height',
                        (winowHeight - headerHeight - footerHeight-10) + 'px');
		}
	}
}])
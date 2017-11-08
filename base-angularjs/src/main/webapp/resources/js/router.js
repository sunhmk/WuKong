'use strict';
var mainApp=angular.module("mainApp");
mainApp.config(['$stateProvider','$urlRouterProvider',function($stateProvider,$urlRouterProvider){
	//不合法路由重定向
	$urlRouterProvider.otherwise('/login');
	$stateProvider.state("login",{
		url:'/login',
		title:'login',
		templateUrl:cxtPath + '/js/login/login.html',
		controller:'logincontroller',
		resolve:{
			/*deps:['$ocLazyLoad',function($ocLazyLoad){
				$ocLazyLoad.load("base-angularjs/resources/js/login/logincontroller.js");
			}]*/
			deps:['uiLoad','$ocLazyLoad',function(uiLoad,$ocLazyLoad){
				return uiLoad.load(cxtPath + '/js/login/logincontroller.js').then(function(){
					return $ocLazyLoad.load('toaster');
				});
			}]
		}
	}).state("register",{
		url:'/register',
		templateUrl:'base-angularjs/resources/js/register/register.html'
	}).state("frame",{
		abstract:true,
		title:'frame',
		url:'/frame',
		template:"<div ui-view='frame'></div>"
		//template:"<div ui-view='header' class='panel-heading'></div>  <div ui-view='nav'></div> <div ui-view='body'></div>"
		//templateUrl:'base-angularjs/resources/js/frame/frame.html',
		
	}).state("frame.views",{
		abstract:true,
		url:'/views',
		title:'frame views',
		//template:"<div> <div class='panel-title'>头部内容header</div></div>
		views:{
                'frame':{
                	scope:{},
                	templateUrl:cxtPath + "js/tpl/app.tpl",
                	controller:function($scope,$state,$uibModal,$log){
                		$scope.sideMenuList = sideMenuList;
                		sideMenuStructTransform($scope.sideMenuList);
                		$scope.showsubmenu = true;
                		$scope.asideclick = function(event)
                		{
                			//$(".submenu").each(function(key,value){
                			var value = $(event.target).siblings().first();
            				if($scope.lastSelected==event.target)
            				{
            					value.css("display") == 'block'?value.css('display','none'):value.css('display','block');
            				}
            				else{
            					if($scope.lastSelected)
            					$($scope.lastSelected).siblings().first().css('display','none');
            					value.css('display','block');
            					$scope.lastSelected = event.target;
            				}
                			//});
                		};
                		$.oncfgclick = function()
                		{
                			alert(1);
                		};
                	}
                	/*指令的controller在directive中实现，此处只关联template的变量
                	controller:function($scope){
                		$scope.aaa = "wwwwwwww";
                		$scope.menu = "22222";
                		//alert(222);
                		/*错误
                		$scope.menu.href = "#";
                		$scope.menu.name = "test";
                		
                		$scope.menu={
                			href:"#",
                			name:"test"
                		}
                		//$scope.context = cxtPath;
                		$scope.headerMenuList = [
			                {
			                  name:'Dashboard',
			                  href:'#'
			                },
			                  {
			                  name:'Settings',
			                  href:'#'
			                },
			                  {
			                  name:'Profile',
			                  href:'#'
			                },
			                  {
			                  name:'Help',
			                  href:'#'
			                }
			              ];
                	},*/
                	/*resolve:{
	                	deps:['uiLoad','$ocLazyLoad',function(uiLoad,$ocLazyLoad){
	                		return uiLoad.load(cxtPath + 'js/tpl/headercontroller.js').then(function(){
	                			return $ocLazyLoad.load('toaster');
	                		});
	                	}]
                	}*/
                },
                //'aside':{
               // 	templateUrl:cxtPath + "js/tpl/aside.html",
               // }
            }
         		//'header':{templateUrl:cxtPath + "js/frame/frame.html"}
            
	}).state("frame.views.dashboard",{
		url:"/dashboard",
		title:'frame views info',
		//template:"<div> <div class='panel-title'>头部内容header</div></div>"
		views:{
			'content':{
					templateUrl:cxtPath + "js/dashboard/dashboard.html",
					controller:function($scope){
						$scope.ddd = "dashboard";
						//alert(111);
				}

			}
		}
	}).state("frame.views.charts",{
		url:"/charts",
		title:'charts',
		views:{
			'content':{
				template:"<div ui-view='charts'></div>"
			}
		}
	}).state("frame.views.charts.flot",{
		url:"/flot",
		title:'charts flot',
		views:{
			'charts':{
					template:"<div>{{ddd}}</div>",
					controller:function($scope){
						$scope.ddd = "charts flot";
						//alert(111);
					}
			}
		}
	})

}]);
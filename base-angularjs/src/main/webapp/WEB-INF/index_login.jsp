<!DOCTYPE html>
<html ng-app="UserInfoModule">
<head>
	<title>Form</title>
	<meta charset="utf-8">
	<link rel="stylesheet" type="text/css" href="resources/css/bootstrap/3.0/bootstrap.css">
	<script type="text/javascript" src="resources/lib/angularjs-1.6.5/angular.js"></script>
	<script type="text/javascript" src="resources/test/form.js"></script>
</head>
<body>
	<div class="panel panel-primary">
		<div class="panel-heading">
			<div class="panel-title">双向数据绑定</div>
		</div>
		<div class="panel-body">
			<div class="row">
				<div class="col-md-12">
					<form class="form-horizontal" role="form" ng-controller="UserInfoCtrl">
						<div class="form-group">
							<label class="col-md-2 control-label">
								邮箱:
							</label>
							<div class="col-md-10">
								<input type="email" class="form-control" placeholder="推荐使用126邮箱" ng-model="userInfo.email">
							</div>
						</div>
						<div class="form-group">
							<label class="col-md-2 control-label">
								密码:
							</label>
							<div class="col-md-10">
								<input type="password" class="form-control" placeholder="只能是数字字母下划线" ng-model="userInfo.password">
							</div>
						</div>
						<div class="form-group">
							<div class="col-md-offset-2 col-md-10">
								<div class="checkbox">
									<label>
										<input type="checkbox" ng-model="userInfo.autoLogin">自动登录
									</label>
								</div>
							</div>
						</div>
						<div class="form-group">
							<div class="col-md-offset-2 col-md-10">
								<button class="btn btn-default" ng-click="getFormData()">获取表单</button>
								<button class="btn btn-default" ng-click="setFormData()">设置表单</button>
								<button class="btn btn-default" ng-click="resetForm()">重置表单</button>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
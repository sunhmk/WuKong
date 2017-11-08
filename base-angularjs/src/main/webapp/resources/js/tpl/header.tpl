 <nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container-fluid">
      <div class="navbar-header">
        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
          <span class="sr-only">Toggle navigation</span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
          <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="#"><span class="glyphicon glyphicon-home" aria-hidden="true"></span> 大圣系统</a>
      </div>
      <div id="navbar" class="navbar-collapse collapse">
        <ul class="nav navbar-nav navbar-right">
            <li>
             <a href="javascript:;" class="navbar-right" ng-click="onClick()"><span class="glyphicon glyphicon-cog " aria-hidden="true"></span>配置</a>
            </li>
        </ul>
        <ul class="nav navbar-nav navbar-right"  ng-repeat="menu in headerMenuList">
          <li ng-if="menu.data" class="dropdown">
            <a href="" class="dropdown-toggle" data-toggle="dropdown">{{menu.name}}<span class="caret"></span></a>
            <ul class="dropdown-menu" role="menu">
              <li ng-repeat="dt in menu.data"> <a href="{{dt.href}}">{{dt.name}}</a></li>
            </ul>
          </li>
          <li ng-if="!menu.data"><a href='{{menu.href}}'>{{menu.name}} </a></li>
          <!--<li><a href="#">Dashboard</a></li>
          <li><a href="#">Settings</a></li>
          <li><a href="#">Profile</a></li>
          <li><a href="#">Help</a></li>-->
        </ul>
        <form class="navbar-form navbar-right">
          <input type="text" class="form-control" placeholder="Search...">
        </form>
          
      </div>
    </div>
</nav>
<aside class="asidecontent">
    <ul class="nav nav-list navbar-inverse" ng-repeat="items in sideMenuList">
      <li ng-if="items.subItems" ng-class="{ 'active': $state.includes(items.sref) || $state.is(items.sref) || $state.includes(items.srefParent)}">
        <a href="javascript:;" ng-click="asideclick($event)">
          {{items.item}}
          <span class="glyphicon glyphicon-triangle-right pull-right" aria-hidden="true"></span>
        </a>
        <ul class="nav nav-list submenu" style="display: none;">
          <li ng-repeat="item in items.subItems">
             <a ui-sref='{{item.sref}}' href="{{item.href}}">
              <span class="glyphicon glyphicon-triangle-right" aria-hidden="true"></span>
              {{item.item}}
            </a>
          </li>
        </ul>
      </li>
      <li ng-if="!items.subItems" ng-class="{ 'active': $state.includes(items.sref) || $state.is(items.sref) || $state.includes(items.srefParent)}">
        <a  href="{{items.href}}" ui-sref="{{items.sref}}" ng-click="onclick($event)"><span class="glyphicon glyphicon-align-left pull-right" aria-hidden="true"></span> {{items.item}}</a>
      </li>
      <!--li class="active"><a href="#"><span class="glyphicon glyphicon-align-left" aria-hidden="true"></span> Home</a></li>ng-show="false"
      <li><a ui-sref="#"><i class="glyphicon glyphicon-book"></i> Library</a></li>
      <li><a ui-sref="#"><i class="glyphicon glyphicon-pencil"></i> Applications</a></li>
      <li><a ui-sref="#"><i class="glyphicon glyphicon-music"></i> Misc</a></li-->
    </ul>
</aside>
<div class="container-fluid" id="asidemenu">
	<div class="row">
		<header on-click='oncfgclick()'></header> 
	</div>
	<div class="row">
		<div class="col-md-2 " ng-include="'base-angularjs/resources/js/tpl/aside.tpl'">
			<!--div ui-view='aside'>ddd</div--> 
		</div>
		<div class="col-md-10">
			<div ui-view='content' ></div>
		</div>
	</div>	
</div>
<footer ng-include="'base-angularjs/resources/js/tpl/footer.tpl'"></footer>
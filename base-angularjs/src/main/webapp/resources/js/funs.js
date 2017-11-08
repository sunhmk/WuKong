//菜单结构字符串转换为系统需要的结构
var sideMenuStructTransform = function(sideMenu)
{
	if(sideMenu == null || sideMenu.length ==0)return;
	for(var i = 0; i < sideMenu.length;i++)
	{
		if(sideMenu[i].subItems==undefined)
		{
			sideMenu[i].srefParent = "";
			var strs = sideMenu[i].href.split("/");
			if(strs==undefined || strs.length == 0 || strs.length == 1)continue;
			if(strs[0]!='#')continue;
			var sref = '';
			for (var k = 1; k < strs.length-1; k++) {
				sref += strs[k];
				if(k < strs.length-2)
				{
					sref+='.';
				}
			}
			sideMenu[i].srefParent = "";
			sideMenu[i].sref = sref + '.' + strs[strs.length -1];
		}
		else
		{
			sideMenu[i].srefParent = "";
			for (var j = 0; j < sideMenu[i].subItems.length; j++) {
				var strs = sideMenu[i].subItems[j].href.split("/");
				if(strs==undefined || strs.length == 0 || strs.length == 1)continue;
				if(strs[0]!='#')continue;
				var sref = '';
				for (var k = 1; k < strs.length-1; k++) {
					sref += strs[k];
					if(k < strs.length-2)
					{
						sref+='.';
					}
				}
				sideMenu[i].subItems[j].srefParent = sref;
				sideMenu[i].subItems[j].sref = sref + '.' + strs[strs.length -1];
			}
		}
	}
}
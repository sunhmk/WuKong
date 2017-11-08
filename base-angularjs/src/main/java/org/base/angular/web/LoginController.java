package org.base.angular.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
	@RequestMapping(value="${adminPath}",method=RequestMethod.GET)
	public String login(HttpServletRequest request, HttpServletResponse response)
	{
		return "index";
	}
}

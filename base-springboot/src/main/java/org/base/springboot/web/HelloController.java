package org.base.springboot.web;

import org.base.springboot.services.BlogProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller//@RestController
public class HelloController {
	@Autowired
	BlogProperties blog;
    @RequestMapping("/hello")
    public String index() {
    	blog.getBignumber();
        return "Hello World";
    }
    
    @RequestMapping("/index")
    public String index(ModelMap map) {
        map.addAttribute("host", "http://blog.didispace.com");
        return "index";
    }
    @RequestMapping("/tmp")
    public String index2(ModelMap map) {
        map.addAttribute("host", "http://blog.didispace.com");
        return "tmp";
    }
}
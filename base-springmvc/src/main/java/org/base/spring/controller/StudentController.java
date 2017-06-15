package org.base.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.base.spring.Student;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

@Controller
public class StudentController {

   @RequestMapping(value = "/student", method = RequestMethod.GET)
   public ModelAndView student(Model model) {
	   Student st =new Student();
	   st.setAge(10);
	   st.setId(1);
	   st.setName("test");
	   model.addAttribute("name","test");
      return new ModelAndView("student", "command", st);
   }

   @RequestMapping(value = "/addStudent", method = RequestMethod.POST)
   public String addStudent(@ModelAttribute("SpringWeb")Student student, 
   ModelMap model) {
	  System.out.println(student.getName() + " " + student.getAge() + " " + student.getId());
      model.addAttribute("name", "test");//student.getName());
      model.addAttribute("age", student.getAge());
      model.addAttribute("id", student.getId());

      return "result";
   }
}

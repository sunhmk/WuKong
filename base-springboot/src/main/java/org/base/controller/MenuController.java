package org.base.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiOperation;

import org.base.springboot.entity.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.io.ByteStreams;

@RestController
@RequestMapping(value="/api/menu")
public class MenuController {

	@ApiOperation(value="获取全部菜单列表",notes="")
	@RequestMapping(value={""},method=RequestMethod.GET)
	public String getMenuList()
	{
		/*FileInputStream input = null;
		byte[] bytes = null;
		try {
			 input = new FileInputStream("resources/menu/data");
			 
			 try {
				 bytes = ByteStreams.toByteArray(input);
				 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		if(bytes != null && bytes.length > 0)
		{
			try {
				return new String(bytes, 0, bytes.length, "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
		}*/
		return "1";
	}
}

package org.base.spring.mybatis;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.base.spring.mybatis.pojo.Customer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ConstructorDI {
	public static interface A{
		public void print();
	}
	public static class B implements A{

		@Override
		public void print() {
			// TODO Auto-generated method stub
			System.out.println("B");
		}
		
	}
	
	public static class C implements A{

		@Override
		public void print() {
			// TODO Auto-generated method stub
			System.out.println("C");
		}
		
	}
	
	public static class D{
		A a ;
		public D(A a)
		{
			this.a = a;
		}
		public void print()
		{
			a.print();
		}
	}
	
	public static void main(String[]args)
	{
		File programRootDir = new File(System.getProperty("user.dir") + "/src");

    	ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    	URLClassLoader classLoader = (URLClassLoader) classloader;//ClassLoader.getSystemClassLoader();
    	Method add = null;
		try {
			add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	add.setAccessible(true);
    	try {
			add.invoke(classLoader, programRootDir.toURI().toURL());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	ApplicationContext ctx = new ClassPathXmlApplicationContext("/config/applicationContext.xml",
				"/config/Configuration.xml");
    	D d = (D) ctx.getBean("d");
    	d.print();
	}
}

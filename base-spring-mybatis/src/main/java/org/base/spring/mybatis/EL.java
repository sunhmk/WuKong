package org.base.spring.mybatis;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

public class EL {

	@Component("customerBean")
	public static class Customer {

		@Value("#{addressBean}")
		private Address address;

		@Value("#{addressBean.country}")
		private String country;
		
		@Value("#{addressBean.getFullAddress('yiibai')}")
		private String fullAddress;

		//getter and setter methods
		
		@Override
		public String toString() {
			return "Customer [address=" + address + "\n, country=" + country
					+ "\n, fullAddress=" + fullAddress + "]";
		}

	}
	
	@Component("addressBean")
	public static class Address {

		@Value("GaoDeng, QiongShang")
		private String street;

		@Value("571100")
		private int postcode;

		@Value("CN")
		private String country;

		public String getFullAddress(String prefix) {

			return prefix + " : " + street + " " + postcode + " " + country;
		}

		//getter and setter methods
		
		public String getCountry(){
			return this.country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		@Override
		public String toString() {
			return "Address [street=" + street + ", postcode=" + postcode
					+ ", country=" + country + "]";
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
		Customer obj = (Customer) ctx.getBean("customerBean");
		System.out.println(obj);
	}
}

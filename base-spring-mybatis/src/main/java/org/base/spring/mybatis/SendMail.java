package org.base.spring.mybatis;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class SendMail {

	public static class MailMail {
		private MailSender mailSender;
		private SimpleMailMessage simpleMailMessage;

		public void setSimpleMailMessage(SimpleMailMessage simpleMailMessage) {
			this.simpleMailMessage = simpleMailMessage;
		}

		public void setMailSender(MailSender mailSender) {
			this.mailSender = mailSender;
		}

		public void sendMail(String dear, String content) {

			SimpleMailMessage message = new SimpleMailMessage(simpleMailMessage);

			message.setText(String.format(simpleMailMessage.getText(), dear,
					content));

			mailSender.send(message);

		}

	}
	
	public static void main( String[] args )
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
    	ApplicationContext context = new ClassPathXmlApplicationContext("/config/applicationContext.xml");
    	System.setProperty("https.protocols", "TLSv1");//java 1.8 
    	MailMail mm = (MailMail) context.getBean("mailMail");
        mm.sendMail("Yiibai", "This is text content");
        
    }

}

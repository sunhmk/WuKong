package org.base.spring.mybatis;

import org.base.spring.mybatis.pojo.Person;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
	@Bean(name="person")
    public Person person() {
        Person p = new Person();
        p.setName("appconfig");
        p.setAge(10);
        p.setAddress("hangzhou");
        return p;
    }
	
	public static void main(String[]args)
	{
		ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		Person obj = (Person) context.getBean("person");
	    
	    obj.print();

	}
}

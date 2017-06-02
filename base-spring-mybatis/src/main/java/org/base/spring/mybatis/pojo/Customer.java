package org.base.spring.mybatis.pojo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class Customer {
	private Person person;
	private int type;
	private String action;
	//getter and setter methods
	public void setType(int type)
	{
		this.type = type;
	}
	
	public int getType()
	{
		return this.type;
	}
	
	public void setAction(String action)
	{
		this.action = action;
	}
	
	public String getAction()
	{
		return this.action;
	}
	@Autowired
	@Qualifier("personA")
	//@Resource(name="personA")
	public void setPerson(Person person) {
		this.person = person;
	}
	public void print()
	{
		System.out.println(person.name + "  " + person.address + "  " + person.age + "  "+ type + "  " + action);
	}
}

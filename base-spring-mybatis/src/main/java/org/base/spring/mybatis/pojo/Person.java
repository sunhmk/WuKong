package org.base.spring.mybatis.pojo;

import org.springframework.beans.factory.annotation.Autowired;

public class Person {
	public String name;
	public int age;
	public String address;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
	public void setAge(int age)
	{
		this.age = age;
	}
	
	public int getAge()
	{
		return this.age;
	}
	
	public void setAddress(String address)
	{
		this.address = address;
	}
	
	public String getAddress()
	{
		return this.address;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + "]";
	}
	public void print()
	{
		System.out.println(name + "  " + age + " " + address);
	}
}

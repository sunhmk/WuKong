package org.base.rest.entity;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

public class User {
	private String name;
	private String password;
	private Date lastLogintime;
	private Date timeout;
	private PrivateKey privatekey; 
	private PublicKey publickey;
	
	public String GetName()
	{
		return this.name;
	}
	public void SetName(String name)
	{
		this.name = name;
	}
	
	public String GetPassword()
	{
		return this.password;
	}
	public void SetPassword(String password)
	{
		this.password = password;
	}
	
	public Date GetlastLogintime()
	{
		return this.lastLogintime;
	}
	public void SetlastLogintime(Date lastLogintime)
	{
		this.lastLogintime = lastLogintime;
	}
	public Date GetTimeout()
	{
		return this.timeout;
	}
	public void SetTimeout(Date timeout)
	{
		this.timeout = timeout;
	}
	public PrivateKey GetPrivatekey()
	{
		return this.privatekey;
	}
	public void SetPrivatekey(PrivateKey privatekey)
	{
		this.privatekey = privatekey;
	}
	public PublicKey GetPublickey()
	{
		return this.publickey;
	}
	public void SetPublickey(PublicKey publickey)
	{
		this.publickey = publickey;
	}
}

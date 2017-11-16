package org.base.rest.entity;

import org.apache.juneau.annotation.BeanConstructor;

public class RSAPublicKey {
	public String key;
	public int code;
	//@BeanConstructor(properties = "modulus,exponent,code")
	public RSAPublicKey(String key,int code)
	{
		this.key = key;
		this.code = code;
	}
	/*public String GetModulus()
	{
		return this.modulus;
	}
	public void SetModulus(String modulus)
	{
		this.modulus = modulus;
	}
	
	public String GetExponent()
	{
		return this.exponent;
	}
	
	public void SetExponent(String exponent)
	{
		this.exponent = exponent;
	}
	public int GetCode()
	{
		return this.code;
	}
	public void SetCode(int code)
	{
		this.code = code;
	}*/
}

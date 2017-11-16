package org.base.rest;

import static org.apache.juneau.http.HttpMethodName.*;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.juneau.json.JsonSerializer;
import org.apache.juneau.json.JsonSerializerBuilder;
import org.apache.juneau.microservice.*;
import org.apache.juneau.rest.annotation.*;
import org.apache.juneau.serializer.SerializeException;
import org.base.rest.utils.RSAUtils;
import org.base.rest.utils.FormatConversions;
import org.base.rest.entity.RSAPublicKey;
import org.base.rest.entity.User;

/**
* Sample REST resource that prints out a simple "Hello world!" message.
*/
@RestResource(
	messages="nls/LoginResources",
	title="Login",
	description="login  resource",
	path="/login",
	htmldoc=@HtmlDoc(
		navlinks={
			"up: request:/..",
			"options: servlet:/?method=OPTIONS",
			"source: $C{Source/gitHub}/org/apache/juneau/examples/rest/$R{servletClassSimple}.java"
		},
		aside={
			"<div style='max-width:400px' class='text'>",
			"	<p>This page shows a resource that simply response with a 'Hello world!' message</p>",
			"	<p>The POJO serialized is a simple String.</p>",
			"</div>"
		}
	)
)
public class LoginResources extends Resource {
	private static final long serialVersionUID = 1L;
	private ConcurrentHashMap<String,User> userMap = new ConcurrentHashMap<String,User>();
	public static KeyPair InitRSA()
	{
		try {
			return RSAUtils.GenKeyPair(1024);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/** GET request handler */
	@RestMethod(name=POST, path="/key", summary="Responds with \"get login key!\"")
	public String getPublicKey(){
		KeyPair keypair = InitRSA();
		RSAPublicKey key = null;
		if(keypair == null)
		{
			key = new RSAPublicKey("", 1);
		}
		else{
			PublicKey pk = keypair.getPublic();
			//FormatConversions.bytesToHexString(pk.getEncoded());
			User user = new User();
			user.SetPublickey(pk);
			user.SetPrivatekey(keypair.getPrivate());
			key = new RSAPublicKey(RSAUtils.getPublicKey(pk),0);
		}
		JsonSerializer jsonSerializer = JsonSerializer.DEFAULT;
		String strJson = "";
		try {
			strJson = jsonSerializer.serialize(key);
		} catch (SerializeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strJson;
	}
}

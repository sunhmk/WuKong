package org.base.rest.utils;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

public class RSAUtils {
	//生成密钥对  
    public static KeyPair GenKeyPair(int keyLength) throws Exception{  
        KeyPairGenerator keyPairGenerator=KeyPairGenerator.getInstance("RSA");  
        keyPairGenerator.initialize(keyLength);        
        return keyPairGenerator.generateKeyPair();  
    }
    
  //公钥加密  
    public static byte[] Encrypt(byte[] content, PublicKey publicKey) throws Exception{  
        Cipher cipher=Cipher.getInstance("RSA");//java默认"RSA"="RSA/ECB/PKCS1Padding"  
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
        return cipher.doFinal(content);  
    }  
      
    //私钥解密  
    public static byte[] Decrypt(byte[] content, PrivateKey privateKey) throws Exception{  
        Cipher cipher=Cipher.getInstance("RSA");  
        cipher.init(Cipher.DECRYPT_MODE, privateKey);  
        return cipher.doFinal(content);  
    }  
	
    //将base64编码后的公钥字符串转成PublicKey实例  
    public static PublicKey getPublicKey(String publicKey) throws Exception{  
        byte[ ] keyBytes=Base64.getDecoder().decode(publicKey.getBytes());  
        X509EncodedKeySpec keySpec=new X509EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory=KeyFactory.getInstance("RSA");  
        return keyFactory.generatePublic(keySpec);    
    }  

    public static String getPublicKey(PublicKey publicKey)
    {
    	return new String(Base64.getEncoder().encode(publicKey.getEncoded()));
    } 
    public static String getPrivateKey(PrivateKey privateKey)
    {
    	return new String(Base64.getEncoder().encode(privateKey.getEncoded()));
    } 
    //将base64编码后的私钥字符串转成PrivateKey实例  
    public static PrivateKey getPrivateKey(String privateKey) throws Exception{  
        byte[ ] keyBytes=Base64.getDecoder().decode(privateKey.getBytes());  
        PKCS8EncodedKeySpec keySpec=new PKCS8EncodedKeySpec(keyBytes);  
        KeyFactory keyFactory=KeyFactory.getInstance("RSA");  
        return keyFactory.generatePrivate(keySpec);  
    }  
      
    
    public static String data="hello world";  
    
    public static void main(String[] args) throws Exception {  
        // TODO Auto-generated method stub  
    	//Cipher ciper = Cipher.getInstance("RSA/ECB/PKCS1Padding");//java默认"RSA"="RSA/ECB/PKCS1Padding"  
        KeyPair keyPair=GenKeyPair(1024);  
          
        //获取公钥，并以base64格式打印出来  
        PublicKey publicKey=keyPair.getPublic();   
        System.out.println("公钥："+new String(Base64.getEncoder().encode(publicKey.getEncoded())));  
       // System.out.println("加密后：" + new String(Base64.getEncoder().encode(Encrypt(data.getBytes(),publicKey))));
        //System.out.println("公钥："+new String(Base64.getEncoder().encode(publicKey.getEncoded())));  
          
        //获取私钥，并以base64格式打印出来  
        PrivateKey privateKey=keyPair.getPrivate();       
        System.out.println("私钥："+new String(Base64.getEncoder().encode(privateKey.getEncoded())));  
          
        //公钥加密  
        byte[] encryptedBytes=Encrypt(data.getBytes(), publicKey);    
        //System.out.println("加密后："+new String(encryptedBytes));  
        byte[] b = Base64.getEncoder().encode(encryptedBytes);
        System.out.println("加密后：" + new String(b));
        //b = Base64.getDecoder().decode("FtgTFqTxOa3FFaUvqV0hQ2XgXAcccbq3tJJQ3rjs7Gi6Ws8zfacNQ7LZg9n7myrkO6KEkMkxXkx9CZoOLJ/f9SQqgrWQjJXmeu9yarcQcUT7rmtUuxhYTM8iQuW6jAmZhkVg78EhNnTMSHWcHIP3M1gokPdJw2bIKPaNFYEGq7g=");
        //私钥解密  
        byte[] decryptedBytes=Decrypt(encryptedBytes, privateKey);        
        System.out.println("解密后："+new String(decryptedBytes));  
    }  
}

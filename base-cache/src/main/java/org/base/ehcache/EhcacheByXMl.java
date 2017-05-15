package org.base.ehcache;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.xml.XmlConfiguration;

public class EhcacheByXMl {
	public static void main (String[]args)
	{
		File programRootDir = new File(System.getProperty("user.dir") + "/");

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

		URL myUrl = classLoader.getResource("ehcache.xml"); 
    	//String url = this.getClass().getClassLoader().getResource("/").getPath();
		Configuration xmlConfig = new XmlConfiguration(myUrl); 
		CacheManager cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
		cacheManager.init();
		Cache<Long, String> preConfigured = cacheManager.getCache(
				"myDefaults", Long.class, String.class);

		Cache<Long, String> myCache = cacheManager
				.createCache("myCache", CacheConfigurationBuilder
						.newCacheConfigurationBuilder(Long.class, String.class,
								ResourcePoolsBuilder.heap(10000)));
		for(long i =0;i<10000;i++)
		myCache.put(i, "da oneffffffffffffffffffffffffffffffffffffffffffffffffffffffff!");
	
		String value = myCache.get(1L);

		cacheManager.removeCache("myDefaults");

		cacheManager.close();
	}
}

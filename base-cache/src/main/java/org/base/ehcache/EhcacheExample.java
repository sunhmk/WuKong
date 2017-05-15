package org.base.ehcache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

public class EhcacheExample {
	public static void main(String[] args) {
		CacheManager cacheManager = CacheManagerBuilder
				.newCacheManagerBuilder()
				.withCache(
						"preConfigured",
						CacheConfigurationBuilder.newCacheConfigurationBuilder(
								Long.class, String.class,
								ResourcePoolsBuilder.heap(10000))).build();
		cacheManager.init();

		Cache<Long, String> preConfigured = cacheManager.getCache(
				"preConfigured", Long.class, String.class);

		Cache<Long, String> myCache = cacheManager
				.createCache("myCache", CacheConfigurationBuilder
						.newCacheConfigurationBuilder(Long.class, String.class,
								ResourcePoolsBuilder.heap(10000)));
		for(long i =0;i<10000;i++)
		myCache.put(i, "da oneffffffffffffffffffffffffffffffffffffffffffffffffffffffff!");
	
		String value = myCache.get(1L);

		cacheManager.removeCache("preConfigured");

		cacheManager.close();
	}
}

package org.base.ehcache;

import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;

public class MyBytesHeap {
	public static void main(String[]args)
	{
		CacheConfiguration<Long, String> usesConfiguredInCacheConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
		        ResourcePoolsBuilder.newResourcePoolsBuilder()
		            .heap(10, MemoryUnit.KB) 
		            .offheap(10, MemoryUnit.MB))
		    .withSizeOfMaxObjectGraph(1000)
		    .withSizeOfMaxObjectSize(1000, MemoryUnit.B) 
		    .build();

		CacheConfiguration<Long, String> usesDefaultSizeOfEngineConfig = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
		        ResourcePoolsBuilder.newResourcePoolsBuilder()
		            .heap(10, MemoryUnit.KB)
		            .offheap(10, MemoryUnit.MB))
		    .build();

		CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
		    .withDefaultSizeOfMaxObjectSize(500, MemoryUnit.B)
		    .withDefaultSizeOfMaxObjectGraph(2000) 
		    .withCache("usesConfiguredInCache", usesConfiguredInCacheConfig)
		    .withCache("usesDefaultSizeOfEngine", usesDefaultSizeOfEngineConfig)
		    .build(true);

		org.ehcache.Cache<Long, String> usesConfiguredInCache = cacheManager.getCache("usesConfiguredInCache", Long.class, String.class);

		usesConfiguredInCache.put(1L, "one");
		//assertThat(usesConfiguredInCache.get(1L), equalTo("one"));
		System.out.println(usesConfiguredInCache.get(1L));
		org.ehcache.Cache<Long, String> usesDefaultSizeOfEngine = cacheManager.getCache("usesDefaultSizeOfEngine", Long.class, String.class);

		usesDefaultSizeOfEngine.put(1L, "one");
		//assertThat(usesDefaultSizeOfEngine.get(1L), equalTo("one"));
		System.out.println(usesConfiguredInCache.get(1L));
		cacheManager.close();
	}
}

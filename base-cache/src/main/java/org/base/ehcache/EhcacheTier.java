package org.base.ehcache;

import java.io.File;

import org.ehcache.Cache;
import org.ehcache.PersistentCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;

public class EhcacheTier {
	public static void main(String[]args)
	{
		File file = new File("/work/code/pic/", "myData");
		PersistentCacheManager persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
			    .with(CacheManagerBuilder.persistence(file)) //getStoragePath()
			    .withCache("threeTieredCache",
			        CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
			            ResourcePoolsBuilder.newResourcePoolsBuilder()
			                .heap(10000, EntryUnit.ENTRIES) 
			                .offheap(2, MemoryUnit.MB) 
			                .disk(20, MemoryUnit.MB, true) 
			            )
			    ).build(true);

			Cache<Long, String> threeTieredCache = persistentCacheManager.getCache("threeTieredCache", Long.class, String.class);
			//for(long i = 0 ; i < 1000000;i++)
			{
				//threeTieredCache.put(i, "stillAvailableAfterRestart" + i); 
				//System.out.println(i);
			}
			for(long i = 0 ; i < 1000000;i++)
			System.out.println(threeTieredCache.get(i));
			persistentCacheManager.close();
	}
}

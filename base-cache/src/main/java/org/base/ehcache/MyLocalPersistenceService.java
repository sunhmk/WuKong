package org.base.ehcache;

import java.io.File;

import org.ehcache.CachePersistenceException;
import org.ehcache.PersistentUserManagedCache;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.UserManagedCacheBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.spi.service.LocalPersistenceService;
import org.ehcache.impl.config.persistence.DefaultPersistenceConfiguration;
import org.ehcache.impl.config.persistence.UserManagedPersistenceContext;
import org.ehcache.impl.persistence.DefaultLocalPersistenceService;

public class MyLocalPersistenceService {
	public static void main(String[]args){
		LocalPersistenceService persistenceService = new DefaultLocalPersistenceService(new DefaultPersistenceConfiguration(new File("/work/code/pic/", "myUserData"))); 

		PersistentUserManagedCache<Long, String> cache = UserManagedCacheBuilder.newUserManagedCacheBuilder(Long.class, String.class)
		    .with(new UserManagedPersistenceContext<Long, String>("cache-name", persistenceService)) 
		    .withResourcePools(ResourcePoolsBuilder.newResourcePoolsBuilder()
		        .heap(10L, EntryUnit.ENTRIES)
		        .disk(10L, MemoryUnit.MB, true)) 
		    .build(true);

		// Work with the cache
		cache.put(42L, "The Answer!");
		//assertThat(cache.get(42L), is("The Answer!"));
		System.out.println(cache.get(42L));
		cache.close(); 
		try {
			cache.destroy();
		} catch (CachePersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		persistenceService.stop();
	}
}

package org.base.ehcache;

import org.ehcache.UserManagedCache;
import org.ehcache.config.builders.UserManagedCacheBuilder;

public class MyUserManagedCache {
	public static void main(String[]args){
		/*CacheConfiguration<Long, String> cacheConfiguration = CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, String.class,
		        ResourcePoolsBuilder.heap(100)) 
		    .withExpiry(Expirations.timeToLiveExpiration(Duration.of(20, TimeUnit.SECONDS))) 
		    .build();*/
		UserManagedCache<Long, String> userManagedCache = UserManagedCacheBuilder
				.newUserManagedCacheBuilder(Long.class, String.class).build(
						false);
		userManagedCache.init();
		userManagedCache.put(1L, "da one!");
		System.out.println(userManagedCache.get(1L));
		userManagedCache.close();
	}
}

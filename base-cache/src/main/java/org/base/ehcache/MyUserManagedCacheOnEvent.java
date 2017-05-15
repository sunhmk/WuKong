package org.base.ehcache;

import java.util.concurrent.Executors;

import org.ehcache.UserManagedCache;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.UserManagedCacheBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;

public class MyUserManagedCacheOnEvent {
	public static class MyCacheEventListener<K, V> implements CacheEventListener<K,V>
	{
		@Override
		public void onEvent(CacheEvent<? extends K, ? extends V> event) {
			// TODO Auto-generated method stub
			System.out.println("type= " + event.getType().toString() + "key:" + event.getKey() + "  oldvalue:"+event.getOldValue() + "newValue:" + event.getNewValue());
		}
	}
	public static void main(String[]args)
	{
		EventType [] types = {EventType.UPDATED};
		UserManagedCache<Long, String> cache = UserManagedCacheBuilder.newUserManagedCacheBuilder(Long.class, String.class)
			    .withEventExecutors(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(5)) 
			    .withEventListeners(CacheEventListenerConfigurationBuilder
			        .newEventListenerConfiguration((Class<? extends CacheEventListener<?,?>>)MyCacheEventListener.class, EventType.CREATED, EventType.UPDATED)
			        .asynchronous()
			        .unordered()) 
			    .withResourcePools(ResourcePoolsBuilder.newResourcePoolsBuilder()
			        .heap(3, EntryUnit.ENTRIES))
			    .build(true);

			cache.put(1L, "Put it");
			cache.put(1L, "Update it");

			cache.close();
	}
}

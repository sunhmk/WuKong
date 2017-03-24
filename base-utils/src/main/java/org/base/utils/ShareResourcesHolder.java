package org.base.utils;

import java.util.IdentityHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;

import java.util.concurrent.ScheduledExecutorService;

public class ShareResourcesHolder {
	public static final boolean IS_RESTRICTED_APPENGINE =
		      "Production".equals(System.getProperty("com.google.appengine.runtime.environment"))
		          && "1.7".equals(System.getProperty("java.specification.version"));
	private static IdentityHashMap<Resource<?>, Instance> instances = new IdentityHashMap<Resource<?>, Instance>();
	public static ShareResourcesHolder Holder = new ShareResourcesHolder(
		new ScheduledExecutorFactory(){
			//@Override
			public ScheduledExecutorService createScheduledExecutor()
			{
				return Executors.newSingleThreadScheduledExecutor(wkutils.getThreadFactory("wukong-util-%d", true));
			}
		});

	private ScheduledExecutorFactory scheduledExecutorFactory;
	private ScheduledExecutorService destroyer;
	public interface Resource<T> {
		public T Create();

		public void Close(T instance);
	}

	private ShareResourcesHolder(ScheduledExecutorFactory scheduledExecutorFactory) {
		this.scheduledExecutorFactory = scheduledExecutorFactory;
	}

	public static <T> T get(Resource<T> rs) {
		return Holder.getInternal(rs);
	}

	public static <T> T release(final Resource<T> rs,final T instance) {
		return Holder.releaseInternal(rs,instance);
	}

	@SuppressWarnings("unchecked")
	private synchronized <T> T getInternal(Resource<T> rs) {
		Instance instance = instances.get(rs);
		if (instance == null) {
			instance = new Instance(rs.Create());
			instances.put(rs, instance);
		}
		if(instance.destroyTask != null)
		{
			instance.destroyTask.cancel(false);
			instance.destroyTask = null;
		}
		instance.refcount ++;
		return (T) instances.get(rs);
	}

	private synchronized <T> T releaseInternal(final Resource<T>rs, final T instance)
	{
		final Instance cached = instances.get(rs);
		if(cached == null)
		{
			throw new IllegalArgumentException("No cached instance found for " + rs);
		}
	    Preconditions.checkArgument(instance == cached.payload, "Releasing the wrong instance");
	    Preconditions.checkState(cached.refcount > 0, "Refcount has already reached zero");
	    cached.refcount--;
		if(cached.refcount == 0)
		{
			if(IS_RESTRICTED_APPENGINE)//system release automatically
			{
				rs.Close(instance);
				instances.remove(rs);
			}
			else
			{
				Preconditions.checkState(cached.destroyTask == null, "Destroy task already scheduled");
				if(destroyer == null)
				{
					destroyer = this.scheduledExecutorFactory.createScheduledExecutor();
				}
				cached.destroyTask = destroyer.schedule(new LogExceptionRunnable(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						synchronized (ShareResourcesHolder.this) {
				              // Refcount may have gone up since the task was scheduled. Re-check it.
				              if (cached.refcount == 0) {
				                rs.Close(instance);
				                instances.remove(rs);
				                if (instances.isEmpty()) {
				                  destroyer.shutdown();
				                  destroyer = null;
				                }
				              }
				            }
					}
					
				}), 1, TimeUnit.SECONDS);
			}
		}
		return null;
	}
	
	interface ScheduledExecutorFactory{
		ScheduledExecutorService createScheduledExecutor();
	}
	
	public class Instance {
		final Object payload;
		int refcount;
		ScheduledFuture<?> destroyTask;

		public Instance(Object payload) {
			this.payload = payload;
		}
	}
}

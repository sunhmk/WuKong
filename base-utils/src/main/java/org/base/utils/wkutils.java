package org.base.utils;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import org.base.utils.ShareResourcesHolder.Resource;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
public final class wkutils {
	public static ThreadFactory getThreadFactory(String nameFormat, boolean daemon)
	{
		ThreadFactory threadFactory = MoreExecutors.platformThreadFactory();
	    if (ShareResourcesHolder.IS_RESTRICTED_APPENGINE) {
	      return threadFactory;
	    } else {
	      return new ThreadFactoryBuilder()
	          .setThreadFactory(threadFactory)
	          .setDaemon(daemon)
	          .setNameFormat(nameFormat)
	          .build();
	    }
	}
	
	public static Resource<ExecutorService> CACHED_SERVICE = new Resource<ExecutorService>(){
		private String Name = "cached_service";
		@Override
		public ExecutorService Create() {
			// TODO Auto-generated method stub
			
			return Executors.newCachedThreadPool(getThreadFactory("cached_service_%d",true));
		}

		@Override
		public void Close(ExecutorService instance) {
			// TODO Auto-generated method stub
			instance.shutdown();
		}
		@Override 
		public String toString()
		{
			return Name;
		}
	};
	
	public static Resource<ScheduledExecutorService> SINGLE_SERVICE = new Resource<ScheduledExecutorService>(){
		@Override
		public ScheduledExecutorService Create()
		{
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(getThreadFactory("TIME_SERVICE_%d",true));
			try
			{
				Method method = service.getClass().getMethod("setRemoveOnCancelPolicy", Boolean.class);
				method.invoke(service, true);
			}
			catch (NoSuchMethodException e) {
	            // no op
	          } catch (RuntimeException e) {
	            throw e;
	          } catch (Exception e) {
	            throw new RuntimeException(e);
	          }
			return service;
		}

		@Override
		public void Close(ScheduledExecutorService instance) {
			// TODO Auto-generated method stub
			instance.shutdown();
		}
	};
}

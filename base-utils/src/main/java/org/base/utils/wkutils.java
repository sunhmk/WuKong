package org.base.utils;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.base.utils.ShareResourcesHolder.Resource;

import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
public final class wkutils {
	public static final int DEFAULT_MAX_MESSAGE_SIZE = 4 * 1024 * 1024;
	public static final int DEFAULT_FLOW_CONTROL_WINDOW = 1048576; // 1MiB
	public static final int DEFAULT_MAX_HEADER_LIST_SIZE = 8192;

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
	
	public static Resource<EventLoopGroup> DEFAULT_BOSS_EVENT_LOOP_GROUP = new DefaultEventLoopGroupResource(1, "wk-default-boss-ELG");
	public static Resource<EventLoopGroup> DEFAULT_WORKER_EVENT_LOOP_GROUP = new DefaultEventLoopGroupResource(0, "wk-default-worker-ELG");

	private static class DefaultEventLoopGroupResource implements Resource<EventLoopGroup> {
	    private final String name;
	    private final int numEventLoops;

	    DefaultEventLoopGroupResource(int numEventLoops, String name) {
	      this.name = name;
	      this.numEventLoops = numEventLoops;
	    }
	    
	    @Override
	    public EventLoopGroup Create() {
	      // Use Netty's DefaultThreadFactory in order to get the benefit of FastThreadLocal.
	      boolean useDaemonThreads = true;
	      ThreadFactory threadFactory = new DefaultThreadFactory(name, useDaemonThreads);
	      int parallelism = numEventLoops == 0
	          ? Runtime.getRuntime().availableProcessors() * 2 : numEventLoops;
	      return new NioEventLoopGroup(parallelism, threadFactory);
	    }

	    @Override
	    public void Close(EventLoopGroup instance) {
	      instance.shutdownGracefully(0, 0, TimeUnit.SECONDS);
	    }

	    @Override
	    public String toString() {
	      return name;
	    }
	  }
}

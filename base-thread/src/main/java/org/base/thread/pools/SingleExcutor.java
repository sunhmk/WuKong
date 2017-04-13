package org.base.thread.pools;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.common.util.concurrent.UncaughtExceptionHandlers;

public class SingleExcutor {
	
	 
	public static  class LoggerThreadFactory  implements ThreadFactory {  
		 @Override  
		 public Thread newThread(Runnable r) {  
		   Thread t = new Thread(r);  
		   t.setDaemon(true);
		   t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){  
		      @Override  
		      public void uncaughtException(Thread t, Throwable e) {  
		      
		        //LoggerFactory.getLogger(t.getName()).error(e.getMessage(), e);  
		      }  
		   });  
		 return t;  
		 }  
		}  
	
	public class MaxPriorityThreadFactory implements ThreadFactory {  
		@Override
	    public Thread newThread(Runnable r) {  
	       Thread t = new Thread(r);  
	       t.setPriority(Thread.MAX_PRIORITY);  
	       return t;  
	    }  
	}
	public static void main(String[] args) {
		ExecutorService singleThreadExecutor = Executors
				.newSingleThreadExecutor();
		for (int i = 0; i < 10; i++) {
			final int index = i;
			singleThreadExecutor.execute(new Runnable() {
				public void run() {
					try {
						System.out.println(index);
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		
		ExecutorService executor1 = Executors.newSingleThreadExecutor(new LoggerThreadFactory ());  
		   
		executor1.submit(new Runnable() {  
		 @Override  
		 public void run() {  
		    
		 }  
		});  
		ExecutorService singleThreadExecutor2 = MoreExecutors.listeningDecorator(
	            createSingleThreadExecutor());
	      try {
			singleThreadExecutor2.submit(new Runnable() {
			      @Override
			      public void run() {
			      }
			    }).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected static ExecutorService createSingleThreadExecutor() {
	    return Executors.newSingleThreadExecutor(
	        new ThreadFactoryBuilder()
	          .setDaemon(true)
	          .setNameFormat("Logger channel (from single-thread executor) to " +
	              "test")
	          .setUncaughtExceptionHandler(
	              UncaughtExceptionHandlers.systemExit())
	          .build());
	  }
	
	  /**
	   * Separated out for easy overriding in tests.
	   */
	  @VisibleForTesting
	  protected ExecutorService createParallelExecutor() {
	    return Executors.newCachedThreadPool(
	        new ThreadFactoryBuilder()
	            .setDaemon(true)
	            .setNameFormat("Logger channel (from parallel executor) to " + "test2")
	            .setUncaughtExceptionHandler(
	                UncaughtExceptionHandlers.systemExit())
	            .build());
	  }
}

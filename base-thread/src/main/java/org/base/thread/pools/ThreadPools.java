package org.base.thread.pools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Maps;

public class ThreadPools {
	/**
	 * Create a new CachedThreadPool with a bounded number as the maximum thread
	 * size in the pool.
	 *
	 * @param maxCachedThread
	 *            the maximum thread could be created in the pool
	 * @param timeout
	 *            the maximum time to wait
	 * @param unit
	 *            the time unit of the timeout argument
	 * @param threadFactory
	 *            the factory to use when creating new threads
	 * @return threadPoolExecutor the cachedThreadPool with a bounded number as
	 *         the maximum thread size in the pool.
	 */
	public static ThreadPoolExecutor getBoundedCachedThreadPool(
			int maxCachedThread, long timeout, TimeUnit unit,
			ThreadFactory threadFactory) {
		ThreadPoolExecutor boundedCachedThreadPool = new ThreadPoolExecutor(
				maxCachedThread, maxCachedThread, timeout, unit,
				new LinkedBlockingQueue<Runnable>(), threadFactory);
		// allow the core pool threads timeout and terminate
		boundedCachedThreadPool.allowCoreThreadTimeOut(true);
		return boundedCachedThreadPool;
	}

	/**
	 * A subclass of ThreadPoolExecutor that keeps track of the Runnables that
	 * are executing at any given point in time.
	 */
	static class TrackingThreadPoolExecutor extends ThreadPoolExecutor {
		private ConcurrentMap<Thread, Runnable> running = Maps
				.newConcurrentMap();

		public TrackingThreadPoolExecutor(int corePoolSize,
				int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		}

		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			running.remove(Thread.currentThread());
		}

		@Override
		protected void beforeExecute(Thread t, Runnable r) {
			Runnable oldPut = running.put(t, r);
			assert oldPut == null : "inconsistency for thread " + t;
			super.beforeExecute(t, r);
		}

		/**
		 * @return a map of the threads currently running tasks inside this
		 *         executor. Each key is an active thread, and the value is the
		 *         task that is currently running. Note that this is not a
		 *         stable snapshot of the map.
		 */
		public ConcurrentMap<Thread, Runnable> getRunningTasks() {
			return running;
		}
	}

	static ThreadPoolExecutor getOpenAndCloseThreadPool(int maxThreads,
			final String threadNamePrefix) {
		return getBoundedCachedThreadPool(maxThreads, 30L, TimeUnit.SECONDS,
				new ThreadFactory() {
					private int count = 1;

					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r, threadNamePrefix + "-" + count++);
					}
				});
	}

	public void count1() throws Exception {
		ExecutorService exec = Executors.newCachedThreadPool();
		BlockingQueue<Future<Integer>> queue = new LinkedBlockingQueue<Future<Integer>>();
		for (int i = 0; i < 10; i++) {
			Future<Integer> future = exec.submit(getTask());
			queue.add(future);
		}
		int sum = 0;
		int queueSize = queue.size();
		for (int i = 0; i < queueSize; i++) {
			sum += queue.take().get();
		}
		System.out.println("总数为：" + sum);
		exec.shutdown();
	}

	// 使用CompletionService(完成服务)保持Executor处理的结果
	public void count2() throws InterruptedException, ExecutionException {
		ExecutorService exec = Executors.newCachedThreadPool();
		ThreadPoolExecutor threadpool = getOpenAndCloseThreadPool(3,
				"threadpool-");
		// ThreadFactoryBuilder tfb = new ThreadFactoryBuilder();
		// tfb.setNameFormat(this.name + "-%d");
		// this.threadpool.setThreadFactory(tfb.build());
		// CompletionService<Integer> execcomp = new
		// ExecutorCompletionService<Integer>(exec);
		CompletionService<Integer> execcomp = new ExecutorCompletionService<Integer>(
				threadpool);
		for (int i = 0; i < 10; i++) {
			execcomp.submit(getTask());
		}
		int sum = 0;
		for (int i = 0; i < 10; i++) {
			// 检索并移除表示下一个已完成任务的 Future，如果目前不存在这样的任务，则等待。
			Future<Integer> future = execcomp.take();
			sum += future.get();
		}
		System.out.println("总数为：" + sum);
		exec.shutdown();
	}

	// 得到一个任务
	public Callable<Integer> getTask() {
		final Random rand = new Random();
		Callable<Integer> task = new Callable<Integer>() {
			@Override
			public Integer call() throws Exception {
				int i = rand.nextInt(10);
				int j = rand.nextInt(10);
				int sum = i * j;
				System.out.print(sum + "\t");
				return sum;
			}
		};
		return task;
	}

	public static void main(String[] args) throws Exception {
		ThreadPools threadpools = new ThreadPools();
		threadpools.count1();
		threadpools.count2();

		final int numApps = 5;
		List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
		for (int i = 0; i < numApps; i++) {
			Callable<Boolean> task = new Callable<Boolean>() {
				public Boolean call() {
					return true;
				}
			};
			tasks.add(task);
		}
		ExecutorService executor = Executors.newFixedThreadPool(numApps);
		try {
			List<Future<Boolean>> futures = executor.invokeAll(tasks);
			for (Future<Boolean> future : futures) {
				future.get();
			}
		} finally {
			executor.shutdownNow();
		}
	}
}

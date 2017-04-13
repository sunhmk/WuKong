package org.base.thread.queues;

import java.util.concurrent.Semaphore;

public class SemaphoreExample {
	public static void main(String[]args)
	{
		Semaphore semaphore = new Semaphore(1);  
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		semaphore.release();
	}
}

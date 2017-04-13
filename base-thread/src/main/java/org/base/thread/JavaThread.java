package org.base.thread;

public class JavaThread {
	public static void main(String[]args)
	{
		Thread th = new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				System.out.println("thread out\r\n");
				
			}
			
		});
		th.setDaemon(true);
		th.start();
		try {
			th.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Thread th1 = new Thread("th1"){
			@Override
			public void run(){
				System.out.println("th1\r\n");
			}
		};
		th1.start();
		try {
			th1.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* if (initThread != null && initThread.isAlive()) {
		      initThread.interrupt()
		      initThread.join()
		    }*/
	}
}

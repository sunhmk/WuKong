package org.base.thread.queues;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Blocks {
	public static class Producer implements Runnable{  
		  
	    protected BlockingQueue queue = null;  
	  
	    public Producer(BlockingQueue queue) {  
	        this.queue = queue;  
	    }  
	  
	    public void run() {  
	        try {  
	            queue.put("1");  
	            Thread.sleep(1000);  
	            queue.put("2");  
	            Thread.sleep(1000);  
	            queue.put("3");  
	        } catch (InterruptedException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	} 
	public static class Consumer implements Runnable{  
		  
	    protected BlockingQueue queue = null;  
	  
	    public Consumer(BlockingQueue queue) {  
	        this.queue = queue;  
	    }  
	  
	    public void run() {  
	        try {  
	            System.out.println(queue.take());  
	            System.out.println(queue.take());  
	            System.out.println(queue.take());  
	        } catch (InterruptedException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	}  
	public static class Student implements Runnable,Delayed{    
	    private String name;    
	    private long submitTime;//交卷时间    
	    private long workTime;//考试时间    
	    public Student() {    
	              
	    }    
	    public Student(String name, long submitTime) {    
	        super();    
	        this.name = name;    
	        workTime = submitTime;    
	        //都转为转为ns    
	        this.submitTime = TimeUnit.NANOSECONDS.convert(submitTime, TimeUnit.MILLISECONDS) + System.nanoTime();    
	    }    
	    
	    @Override    
	    public void run() {    
	        System.out.println(name + " 交卷,用时" + workTime/100 + "分钟");    
	    }    
	    
	    @Override    
	    public long getDelay(TimeUnit unit) {    
	        return unit.convert(submitTime - System.nanoTime(), unit.NANOSECONDS);    
	    }    
	    
	    @Override    
	    public int compareTo(Delayed o) {    
	        Student that = (Student) o;    
	        return submitTime > that.submitTime?1:(submitTime < that.submitTime ? -1 : 0);    
	    }    
	    public static class EndExam extends Student{    
	        private ExecutorService exec;    
	        public EndExam(int submitTime,ExecutorService exec) {    
	            super(null,submitTime);    
	            this.exec = exec;    
	        }    
	        @Override    
	        public void run() {    
	            exec.shutdownNow();    
	        }    
	    }    
	        
	}  
	static class Teacher implements Runnable{    
	    private DelayQueue<Student> students;    
	    private ExecutorService exec;    
	        
	    public Teacher(DelayQueue<Student> students,ExecutorService exec) {    
	        super();    
	        this.students = students;    
	        this.exec = exec;    
	    }    
	    
	    
	    @Override    
	    public void run() {    
	        try {    
	            System.out.println("考试开始……");    
	            while (!Thread.interrupted()) {    
	                students.take().run();    
	            }    
	            System.out.println("考试结束……");    
	        } catch (InterruptedException e) {    
	            e.printStackTrace();    
	        }    
	    
	    }    
	        
	}    
    static final int STUDENT_SIZE = 45; 

	public static void main(String[] args) throws Exception {  
		  
        BlockingQueue queue = new ArrayBlockingQueue(1024);  
  
        Producer producer = new Producer(queue);  
        Consumer consumer = new Consumer(queue);  
  
        new Thread(producer).start();  
        new Thread(consumer).start();  
        Random r = new Random();    
        DelayQueue<Student> students = new DelayQueue<Student>();    
        ExecutorService exec = Executors.newCachedThreadPool();    
        for(int i = 0; i < STUDENT_SIZE; i++){    
            students.put(new Student("学生" + ( i + 1), 3000 + r.nextInt(9000)));    
        }    
        students.put(new Student.EndExam(12000,exec));//1200为考试结束时间    
        exec.execute(new Teacher(students, exec));    
            
        Thread.sleep(1000);  
        /*DelayQueue<Delayed>  dq = new DelayQueue<Delayed>();
        dq.put(new Delayed(){

			@Override
			public int compareTo(Delayed arg0) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public long getDelay(TimeUnit unit) {
				// TODO Auto-generated method stub
				return unit.convert(2, TimeUnit.MILLISECONDS);
			}
    		
    	});
        @SuppressWarnings("unused")
		Delayed d = dq.poll(2,TimeUnit.MILLISECONDS);
        d = dq.poll(2,TimeUnit.MILLISECONDS);
        d = dq.take();
        new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub

				Delayed a;
				try {
					a = dq.take();
					if(a == null)
					{
						
					}

			    	a = null;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        	
        }).start();*/
    	
        //Thread.sleep(20000);
        PriorityBlockingQueue<String> queue1 = new PriorityBlockingQueue<String>();
        
        
    }  
}

package org.base.utils;
import java.util.logging.Level;
import java.util.logging.Logger;
import static com.google.common.base.Preconditions.checkNotNull;


public class LogExceptionRunnable implements Runnable{
	Logger log = Logger.getLogger(LogExceptionRunnable.class.getName());
	private Runnable task;
	public LogExceptionRunnable(Runnable task)
	{
		this.task = task;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			task.run();
		}
		catch(Throwable t) {
		      log.log(Level.SEVERE, "Exception while executing runnable " + task, t);
		      //MoreThrowables.throwIfUnchecked(t);
		      throw new AssertionError(t);
		}
	}
	 @Override
	  public String toString() {
	    return "LogExceptionRunnable(" + task + ")";
	  }
	 
}

package org.base.utils.collections;

import java.util.ArrayDeque;
import java.util.Deque;

/*
 * head,tail operation
 * not thread safe
 */
public class ArrayDequeExample {
	  private final Deque<String> events = new ArrayDeque<String>(4);
	  public void iter()
	  {
		  for (String fireableEvent : events) {
			  
		  }
	  }
	  protected void handleEvent(String event) {
		    events.add(event);//add to tail
	        events.removeLast();
	  }
}

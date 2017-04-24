package org.base.utils.collections;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
/*
 * one is queuing,new copy and refrence new .
 * 
 */
public class CopyonWriteExample {
	 private final Set<String> listeners = new CopyOnWriteArraySet<String>();
	 public void add(String str)
	 {
		 listeners.add(str);
	 }
	 public void add(Set<String>sets)
	 {
		 listeners.addAll(sets);
	 }
}

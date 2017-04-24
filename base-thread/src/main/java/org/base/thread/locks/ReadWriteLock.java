package org.base.thread.locks;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLock<K> {
	private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	private final ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
	private final ReentrantReadWriteLock.WriteLock writeLock = rwLock
			.writeLock();

	public void delete(K key) throws Exception {
		readLock.lock();
		try {
		} finally {
			readLock.unlock();
		}
	}
	
	 public void write(K key) throws Exception {
		    readLock.lock();
		    try {
		    } finally {
		      readLock.unlock();
		    }
		  }
}

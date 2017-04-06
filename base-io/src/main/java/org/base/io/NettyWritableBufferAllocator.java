package org.base.io;

import io.netty.buffer.ByteBufAllocator;

public class NettyWritableBufferAllocator implements WritableBufferAllocator{
	 // Use 4k as our minimum buffer size.
	  private static final int MIN_BUFFER = 4096;

	  // Set the maximum buffer size to 1MB
	  private static final int MAX_BUFFER = 1024 * 1024;

	  private final ByteBufAllocator allocator;

	  NettyWritableBufferAllocator(ByteBufAllocator allocator) {
	    this.allocator = allocator;
	  }

	  @Override
	  public WritableBuffer allocate(int capacityHint) {
	    capacityHint = Math.min(MAX_BUFFER, Math.max(MIN_BUFFER, capacityHint));
	    return new NettyWritableBuffer(allocator.buffer(capacityHint, capacityHint));
	  }
}

package org.base.io;

import io.netty.buffer.ByteBuf;

public class NettyWritableBuffer implements WritableBuffer {

	private final ByteBuf bytebuf;

	NettyWritableBuffer(ByteBuf bytebuf) {
		this.bytebuf = bytebuf;
	}

	@Override
	public void write(byte[] src, int srcIndex, int length) {
		// TODO Auto-generated method stub
		bytebuf.writeBytes(src, srcIndex, length);
	}

	@Override
	public void write(byte b) {
		// TODO Auto-generated method stub
		bytebuf.writeByte(b);
	}

	@Override
	public int writableBytes() {
		// TODO Auto-generated method stub
		return bytebuf.writableBytes();
	}

	@Override
	public int readableBytes() {
		// TODO Auto-generated method stub
		return bytebuf.readableBytes();
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		bytebuf.release();
	}
	ByteBuf bytebuf() {
	    return bytebuf;
	  }

}

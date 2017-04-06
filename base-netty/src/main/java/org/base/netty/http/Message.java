package org.base.netty.http;

import io.netty.buffer.ByteBuf;

public class Message implements Encodable{

	@Override
	public int encodedLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void encode(ByteBuf buf) {
		// TODO Auto-generated method stub
		buf.writeInt(4);
		buf.writeBytes("http".getBytes());
	}
	
}

package org.base.netty.http;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class HttpEncoder extends MessageToMessageEncoder<Message>{

	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg,
			List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		
	}

}

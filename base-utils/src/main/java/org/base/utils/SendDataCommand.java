package org.base.utils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelPromise;

public class SendDataCommand extends WriteQueue.AbstractQueuedCommand {
	private int streamId;
	private ByteBuf content;
	private boolean endStream;
	private ChannelPromise promise;
	public SendDataCommand(int streamId,ByteBuf content,boolean endStream)
	{
		this.streamId = streamId;
		this.content = content;
		this.endStream = endStream;
	}
	
	public void setPromise(ChannelPromise promise)
	{
		this.promise = promise;
	}
	
	public ChannelPromise getPromise()
	{
		return this.promise;
	}
	
	
	public boolean getEndStream()
	{
		return this.endStream;
	}

	public ByteBuf getContent() {
		// TODO Auto-generated method stub
		return content;
	}
}

package org.base.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import org.base.netty.NettyHttp2ClientHandler;
import org.base.utils.CreateStreamCommand;
import org.base.utils.ForcefulCloseCommand;
import org.base.utils.GracefulCloseCommand;
import org.base.utils.SendDataCommand;
import org.base.utils.SendPingCommand;
import org.base.utils.WriteQueue.QueuedCommand;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.MoreExecutors;

import org.base.io.MappedBiggerFileReader;
import org.base.io.NettyWritableBufferAllocator;

public class NettyHttp2ClientStream {
	NettyHttp2ClientHandler ch;

	private final Queue<QueuedCommand> queue;
	private volatile boolean initialized = false;
	private final Executor excutor;

	public NettyHttp2ClientStream(ChannelHandler ch, Channel channel) {
		Preconditions.checkNotNull(ch, "connection");
		this.ch = (NettyHttp2ClientHandler) ch;
		this.ch.startWriteQueue(channel);
		queue = new ConcurrentLinkedQueue<QueuedCommand>();
		excutor = MoreExecutors.directExecutor();
	}

	public void SetInitialized(boolean b) {
		this.initialized = b;
		this.ResetCmmds();
	}

	public void CreateStream() {
		queue.add(new CreateStreamCommand());
		if(initialized)
		ResetCmmds();
	}
	
	public void SendDataStream()
	{
		try {
			//NettyWritableBufferAllocator allocator = new NettyWritableBufferAllocator(UnpooledByteBufAllocator.DEFAULT);
			//ByteBuffer dst = ByteBuffer.allocate(1024*2);
			MappedBiggerFileReader reader = new MappedBiggerFileReader("/Users/sunanan/Downloads/[电影天堂www.dy2018.com]罗曼蒂克xw史HD高清国语中英双字.mkv", 1024*20);
			ByteBuf bytebuf = null;
			boolean endStream = false;
			while(reader.read() != -1)
			{
				endStream = false;
				bytebuf = Unpooled.wrappedBuffer(reader.getArray());
				if(bytebuf.readableBytes() <1024*20)
				{
					endStream = true;
				}
				queue.add(new SendDataCommand(0,bytebuf,endStream));
				if(initialized)
				ResetCmmds();
			}
			//this.SendForcefulCloseCommand();//triger onRstStreamRead event
			//this.SendGracefulCloseCommand();//normal close
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void SendForcefulCloseCommand()
	{
		queue.add(new ForcefulCloseCommand());
		if(initialized)
		ResetCmmds();
	}
	
	public void SendPingCommand()
	{
		queue.add(new SendPingCommand());
		if(initialized)
		ResetCmmds();
	}
	public void SendGracefulCloseCommand()
	{
		queue.add(new GracefulCloseCommand());
		if(initialized)
		ResetCmmds();
	}
	private void ResetCmmds()
	{
		class addcommands implements Runnable{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				QueuedCommand cmd;
				while((cmd = NettyHttp2ClientStream.this.queue.poll()) != null)
				{
					NettyHttp2ClientStream.this.ch.getWriteQueue().enqueue(cmd, true);
				}
				
			}
			
		}
		synchronized(this){
		if(this.initialized&&queue.size()>0)
		excutor.execute(new addcommands());
		else if(!this.initialized)
			this.queue.clear();
		}
	}
	
	
	
	
}

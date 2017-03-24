package org.base.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.Executor;

import io.grpc.Attributes;
import io.grpc.CallOptions;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.Status;
import io.grpc.internal.ClientStream;
import io.grpc.internal.ConnectionClientTransport;
import io.grpc.internal.LogId;
import io.grpc.internal.StatsTraceContext;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.discard.DiscardClientHandler;

public class NettyClientTransport {
	private final EventLoopGroup group;
	private final Map<ChannelOption<?>, ?> channelOptions;
	private final SocketAddress address;
	private final Class<? extends Channel> channelType;
	private Channel channel;

	public NettyClientTransport(SocketAddress address,
			Class<? extends Channel> channelType,
			Map<ChannelOption<?>, ?> channelOptions, EventLoopGroup group) {
		this.group = group;
		this.channelOptions = channelOptions;
		this.address = address;
		this.channelType = channelType;
	}

	public void start() {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch)
								throws Exception {
							ChannelPipeline p = ch.pipeline();
							// if (sslCtx != null) {
							// p.addLast(sslCtx.newHandler(ch.alloc(), HOST,
							// PORT));
							// }
							// p.addLast(new DiscardClientHandler());
						}
					});
			ChannelFuture f = null;
			// Make the connection attempt.
			try {
				f = b.connect(this.address).sync();
				channel = f.channel();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Wait until the connection is closed.
		} finally {
		}
	};

	public void write(String str)
	{
		//ChannelBuffer buf = Pool.
		//channel.write(msg)
		//channel.write().addListener(new ChannelFutureListener() {
		  //    @Override
		   //   public void operationComplete(ChannelFuture future) throws Exception {
		    //    if (!future.isSuccess()) {
		          // Need to notify of this failure, because NettyClientHandler may not have been added to
		          // the pipeline before the error occurred.
		         // lifecycleManager.notifyTerminated(Utils.statusFromThrowable(future.cause()));
		     //   }
		     // }
		  //  });
		//channel.writeAndFlush(msg)
	}
	public void shutdown() {
		if (channel == null || !channel.isOpen()) {
			// Already closed.
			return;
		}
		channel.close().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (!future.isSuccess()) {
					// log.log(Level.WARNING, "Error shutting down server",
					// future.cause());
				}
			}
		});
	}

	public int getPort() {
		// TODO Auto-generated method stub
		if (channel == null) {
			return -1;
		}
		SocketAddress localAddr = channel.localAddress();
		if (!(localAddr instanceof InetSocketAddress)) {
			return -1;
		}
		return ((InetSocketAddress) localAddr).getPort();
	}
	
	public static void main(String[] args) {
		Class<? extends ServerChannel> channelType = io.netty.channel.socket.nio.NioServerSocketChannel.class;// io.netty.channel.socket.ServerSocketChannel.class;
		InetSocketAddress addr = new InetSocketAddress("localhost", 8989);
		NettyClientTransport nt = new NettyClientTransport(addr,channelType,null,NettyServerTransport.create(0));
		nt.start();
		
	}
	
}

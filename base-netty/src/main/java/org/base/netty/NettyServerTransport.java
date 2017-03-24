package org.base.netty;

import static io.netty.channel.ChannelOption.SO_BACKLOG;
import static io.netty.channel.ChannelOption.SO_KEEPALIVE;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;

import javax.annotation.Nullable;

import io.grpc.Internal;
import io.grpc.internal.InternalServer;
import io.grpc.internal.ServerListener;
import io.grpc.internal.ServerTransportListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

public class NettyServerTransport implements InternalServer {
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private SocketAddress address;
	private final Class<? extends ServerChannel> channelType;
	private Channel channel;

	public NettyServerTransport(SocketAddress address,
			Class<? extends ServerChannel> channelType,
			@Nullable EventLoopGroup bossGroup,
			@Nullable EventLoopGroup workerGroup) {
		this.address = address;
		this.channelType = channelType;
		this.bossGroup = bossGroup;
		this.workerGroup = workerGroup;
	}

	@Override
	public void start(ServerListener listener) throws IOException {
		// TODO Auto-generated method stub
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup);
		b.channel(channelType);
		if (NioServerSocketChannel.class.isAssignableFrom(channelType)) {
			b.option(SO_BACKLOG, 128);
			b.childOption(SO_KEEPALIVE, true);
		}
		b.childHandler(new ChannelInitializer<Channel>() {
			@Override
			public void initChannel(Channel ch) throws Exception {
				/*
				 * NettyServerTransport transport = new NettyServerTransport(ch,
				 * protocolNegotiator, maxStreamsPerConnection,
				 * flowControlWindow, maxMessageSize, maxHeaderListSize);
				 * ServerTransportListener transportListener; // This is to
				 * order callbacks on the listener, not to guard access to
				 * channel. synchronized (NettyServer.this) { if (channel !=
				 * null && !channel.isOpen()) { // Server already shutdown.
				 * ch.close(); return; } // `channel` shutdown can race with
				 * `ch` initialization, so this is only safe to increment //
				 * inside the lock. eventLoopReferenceCounter.retain();
				 * transportListener = listener.transportCreated(transport); }
				 * transport.start(transportListener);
				 * ch.closeFuture().addListener(new ChannelFutureListener() {
				 * 
				 * @Override public void operationComplete(ChannelFuture future)
				 * { eventLoopReferenceCounter.release(); } });
				 */
			}
		});
		// Bind and start to accept incoming connections.
		ChannelFuture future = b.bind(address);
		try {
			future.await();
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted waiting for bind");
		}
		if (!future.isSuccess()) {
			throw new IOException("Failed to bind", future.cause());
		}
		channel = future.channel();
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
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
				synchronized (NettyServerTransport.this) {
				}
			}
		});
	}

	@Override
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
		NettyServerTransport st = new NettyServerTransport(new InetSocketAddress(8989), channelType, create(1), create(0));
	}
	
	public static EventLoopGroup create(int numEventLoops)
	{
		boolean useDaemonThreads = true;
		ThreadFactory threadFactory = new DefaultThreadFactory(
				"nettyservertransport", useDaemonThreads);
		int parallelism = numEventLoops == 0 ? Runtime.getRuntime()
				.availableProcessors() * 2 : numEventLoops;
		return  new NioEventLoopGroup(parallelism,
				threadFactory);
	}
}

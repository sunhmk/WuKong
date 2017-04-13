package org.base.netty;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.netty.channel.ChannelOption.SO_BACKLOG;
import static io.netty.channel.ChannelOption.SO_KEEPALIVE;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.base.utils.ShareResourcesHolder;
import org.base.utils.wkutils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.ReferenceCounted;

import java.util.logging.Level;
import java.util.logging.Logger;

public class NettyServer {
	public static enum ProtocolType {
		HTTP("http"), HTTP2("http2"), SOCKET("socket"), FTP("ftp");
		private String Protocol = "http";

		ProtocolType(final String protocol) {
			this.Protocol = protocol;
		}

		public String Protocol() {
			return this.Protocol;
		}
		/*
		 * public String getProtocol() { return this.Protocol; } public String
		 * toString() { return Protocol; } public void setProtocol(String
		 * protocol) { this.Protocol = protocol; }
		 */
	}

	private static final Logger log = Logger.getLogger(NettyServer.class
			.getName());
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private Class<? extends ServerChannel> channelType;
	private Channel channel;
	private SocketAddress address;
	private boolean usingSharedBossGroup;
	private boolean usingSharedWorkerGroup;
	private final ReferenceCounted eventLoopReferenceCounter = new EventLoopReferenceCounter();
	//private ChannelHandler channelHandler;
	private ProtocolType protocolType = ProtocolType.HTTP;


	public NettyServer(Class<? extends ServerChannel> channelType, int port) {
		this.channelType = channelType;
		address = new InetSocketAddress("localhost",port);
	}

	public NettyServer setBossGroup(EventLoopGroup bossGroup) {
		this.bossGroup = bossGroup;
		return this;
	}

	public NettyServer setWorkerGroup(EventLoopGroup workerGroup) {
		this.workerGroup = workerGroup;
		return this;
	}

	public NettyServer setProtocolType(ProtocolType protocolType) {
		this.protocolType = protocolType;
		return this;
	}

	public void start() throws IOException {
		ServerBootstrap b = new ServerBootstrap();
		
		allocateSharedGroups();
		b.group(bossGroup, workerGroup);
		b.channel(channelType);
		if (NioServerSocketChannel.class.isAssignableFrom(channelType)) {
			b.option(SO_BACKLOG, 128);
			b.childOption(SO_KEEPALIVE, true);
		}
		b.childHandler(new ChannelInitializer<Channel>() {
			@Override
			public void initChannel(Channel ch) throws Exception {
				ch.closeFuture().addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) {
						eventLoopReferenceCounter.release();
					}
				});
				if(protocolType == ProtocolType.HTTP2)
				{
					ChannelHandler channelHandler = ChannelHandlers.newServerChannelHandler(protocolType);
					channelHandler = checkNotNull(channelHandler, "channel handler is null");
					ch.pipeline().replace(this, null, channelHandler);
				}
				else if(protocolType == ProtocolType.HTTP)
				{
					//ch.pipeline().
				}
				eventLoopReferenceCounter.retain();
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
					log.log(Level.WARNING, "Error shutting down server",
							future.cause());
				}
				synchronized (NettyServer.this) {
				}
				eventLoopReferenceCounter.release();
			}
		});
	}

	private void allocateSharedGroups() {
		if (bossGroup == null) {
			bossGroup = ShareResourcesHolder
					.get(wkutils.DEFAULT_BOSS_EVENT_LOOP_GROUP);
			usingSharedBossGroup = true;
		}
		if (workerGroup == null) {
			workerGroup = ShareResourcesHolder
					.get(wkutils.DEFAULT_WORKER_EVENT_LOOP_GROUP);
			this.usingSharedWorkerGroup = true;
		}
	}

	public int getPort() {
		if (channel == null) {
			return -1;
		}
		SocketAddress localAddr = channel.localAddress();
		if (!(localAddr instanceof InetSocketAddress)) {
			return -1;
		}
		return ((InetSocketAddress) localAddr).getPort();
	}

	class EventLoopReferenceCounter extends AbstractReferenceCounted {
		@Override
		protected void deallocate() {
			try {
				if (usingSharedBossGroup && bossGroup != null) {
					ShareResourcesHolder.release(
							wkutils.DEFAULT_BOSS_EVENT_LOOP_GROUP, bossGroup);
				}
			} finally {
				bossGroup = null;
				try {
					if (usingSharedWorkerGroup && workerGroup != null) {
						ShareResourcesHolder.release(
								wkutils.DEFAULT_WORKER_EVENT_LOOP_GROUP,
								workerGroup);
					}
				} finally {
					workerGroup = null;
				}
			}
		}

		@Override
		public ReferenceCounted touch(Object hint) {
			return this;
		}
	}
}

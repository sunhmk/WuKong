package org.base.netty;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.netty.channel.ChannelOption.SO_KEEPALIVE;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.base.netty.NettyServer.ProtocolType;
import org.base.utils.KeepAliveManager;
import org.base.utils.NettyHttp2ClientStream;
import org.base.utils.ShareResourcesHolder;
import org.base.utils.wkutils;
import org.base.utils.KeepAliveManager.PingCallback;

import com.google.common.base.Preconditions;

import io.grpc.netty.ProtocolNegotiator;
import io.grpc.netty.ProtocolNegotiators;
import io.grpc.netty.ProtocolNegotiators.AbstractBufferingHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.discard.DiscardClientHandler;
import io.netty.util.AsciiString;

public class NettyClientTransport {
	private final Object lock = new Object();
	private EventLoopGroup group;
	private final Map<ChannelOption<?>, Object> channelOptions = new HashMap<ChannelOption<?>, Object>();
	private final SocketAddress address;
	private final Class<? extends Channel> channelType;
	private Channel channel;
	private ProtocolType protocolType = ProtocolType.HTTP;
	private NettyHttp2ClientStream nettyHttp2ClientStream = null;
	private volatile boolean initilized = false;  
	KeepAliveManager keyalive;
	public NettyClientTransport(SocketAddress address,
			Class<? extends Channel> channelType, EventLoopGroup group) {

		this.group = group;
		this.address = address;
		this.channelType = channelType;
		
	}

	public NettyClientTransport setProtocolType(ProtocolType protocolType) {
		this.protocolType = protocolType;
		return this;
	}

	public NettyClientTransport setEventGroup(EventLoopGroup group) {
		this.group = group;
		return this;
	}

	public NettyClientTransport withOption(ChannelOption<?> option, Object value) {
		channelOptions.put(option, value);
		return this;
	}

	public void start() {
		Bootstrap b = new Bootstrap();
		b.group(group);
		b.channel(channelType);
		if (NioSocketChannel.class.isAssignableFrom(channelType)) {
			b.option(SO_KEEPALIVE, true);
		}
		for (Map.Entry<ChannelOption<?>, ?> entry : channelOptions.entrySet()) {
			// Every entry in the map is obtained from
			// NettyChannelBuilder#withOption(ChannelOption<T> option, T value)
			// so it is safe to pass the key-value pair to b.option().
			b.option((ChannelOption<Object>) entry.getKey(), entry.getValue());
		}

		/**
		 * We don't use a ChannelInitializer in the client bootstrap because its
		 * "initChannel" method is executed in the event loop and we need this
		 * handler to be in the pipeline immediately so that it may begin
		 * buffering writes.
		 */

		ChannelHandler channelHandler = ChannelHandlers
				.newClientChannelHandler(protocolType);
		// channelHandler = checkNotNull(channelHandler,
		// "channel handler is null");
		if (channelHandler instanceof NettyHttp2ClientHandler) {
			HandlerSettings
					.setAutoWindow((AbstractNettyHttp2Handler) channelHandler);
		}

		Class cla = null;
		try {
			cla = Class
					.forName("io.grpc.netty.ProtocolNegotiators$BufferUntilChannelActiveHandler");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Constructor<?> isConstructor = null;
		Constructor<?>[] constructors = cla.getDeclaredConstructors();// ("BufferUntilChannelActiveHandler",
																		// parameterTypes);
		for (Constructor<?> constructor : constructors) {
			if (constructor.getName() == "io.grpc.netty.ProtocolNegotiators$BufferUntilChannelActiveHandler")
				isConstructor = constructor;
		}
		isConstructor.setAccessible(true);
		Object obj = Array.newInstance(ChannelHandler.class, 1);
		Array.set(obj, 0, channelHandler);
		ChannelHandler ch = null;
		try {
			ch = (ChannelHandler) isConstructor.newInstance(obj);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		b.handler(channelHandler);
		channel = b.register().channel();
		nettyHttp2ClientStream = new NettyHttp2ClientStream(channelHandler,channel);
		// Start the connection operation to the server.
		channel.connect(address).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (!future.isSuccess()) {
					ChannelHandlerContext ctx = future.channel().pipeline()
							.context(channelHandler);
					if (ctx != null) {
						// NettyClientHandler doesn't propagate exceptions, but
						// the negotiator will need the
						// exception to fail any writes. Note that this fires
						// after handler, because it is as if
						// handler was propagating the notification.
						ctx.fireExceptionCaught(future.cause());
					}
					future.channel().pipeline()
							.fireExceptionCaught(future.cause());
					nettyHttp2ClientStream.SetInitialized(false);
				}
				else
				{
					initilized = true;
					nettyHttp2ClientStream.SetInitialized(true);
					keyalive = new KeepAliveManager(nettyHttp2ClientStream, future.channel(),
							ShareResourcesHolder
							.get(wkutils.DEFAULT_BOSS_EVENT_LOOP_GROUP), TimeUnit.SECONDS.toNanos(10),
							TimeUnit.SECONDS.toNanos(10));
					if (channelHandler instanceof NettyHttp2ClientHandler) {
						 //Method[] declaredMethods = channelHandler.getClass().getDeclaredMethods();
						 //declaredMethods
						//Class<?>[]objs = new Class<?>[1];
						//objs[0] = PingCallback.class;
						Method method = channelHandler.getClass().getMethod("setPingCallBack",PingCallback.class);
						method.setAccessible(true);
						method.invoke(channelHandler,new Object[] { keyalive.getPingCallBack() });
					}
					keyalive.onTransportActive();
					//nettyHttp2ClientStream = new NettyHttp2ClientStream(ch,channel);
				}
			}
		});
		// Start the write queue as soon as the channel is constructed
		// channelHandler.startWriteQueue(channel);
		// This write will have no effect, yet it will only complete once the
		// negotiationHandler
		// flushes any pending writes.
		/*
		 * channel.write(NettyHttp2ClientHandler.NOOP_MESSAGE).addListener(new
		 * ChannelFutureListener() {
		 * 
		 * @Override public void operationComplete(ChannelFuture future) throws
		 * Exception { if (!future.isSuccess()) { // Need to notify of this
		 * failure, because NettyClientHandler may not have been added to // the
		 * pipeline before the error occurred.
		 * //lifecycleManager.notifyTerminated
		 * (Utils.statusFromThrowable(future.cause())); } } });
		 */
		// Handle transport shutdown when the channel is closed.
		channel.closeFuture().addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				// Typically we should have noticed shutdown before this point.
				// lifecycleManager.notifyTerminated(
				// Status.INTERNAL.withDescription("Connection closed with unknown cause"));
				initilized = false;
				nettyHttp2ClientStream.SetInitialized(false);
				keyalive.onTransportShutdown();
			}
		});
	};

	public void CreateCommand()
	{
		this.nettyHttp2ClientStream.CreateStream();
	}
	
	public void SendDataCommand()
	{
		this.nettyHttp2ClientStream.SendDataStream();
	}
	
	public void write(String str) {
		// ChannelBuffer buf = Pool.
		// channel.write(msg)
		// channel.write().addListener(new ChannelFutureListener() {
		// @Override
		// public void operationComplete(ChannelFuture future) throws Exception
		// {
		// if (!future.isSuccess()) {
		// Need to notify of this failure, because NettyClientHandler may not
		// have been added to
		// the pipeline before the error occurred.
		// lifecycleManager.notifyTerminated(Utils.statusFromThrowable(future.cause()));
		// }
		// }
		// });
		// channel.writeAndFlush(msg)
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
		synchronized (lock) {
			lock.notifyAll();
		}
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

	public void awaitTermination() throws InterruptedException {
		synchronized (lock) {
			lock.wait();
		}
	}

	public static void main(String[] args) {
		Class<? extends Channel> channelType = io.netty.channel.socket.nio.NioSocketChannel.class;// io.netty.channel.socket.ServerSocketChannel.class;
		InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 8080);

		NettyClientTransport nt = new NettyClientTransport(addr, channelType,
				ShareResourcesHolder
						.get(wkutils.DEFAULT_WORKER_EVENT_LOOP_GROUP));
		nt.setProtocolType(ProtocolType.HTTP2);
		nt.start();
		nt.CreateCommand();
		nt.SendDataCommand();
		try {
			nt.awaitTermination();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

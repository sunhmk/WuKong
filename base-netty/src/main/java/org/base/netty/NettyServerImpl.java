package org.base.netty;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import io.netty.channel.ServerChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.base.netty.NettyServer.ProtocolType;

public final class NettyServerImpl {
	private final Object lock = new Object();
	private Class<? extends ServerChannel> channelType;
	private NettyServer server;
	private boolean started;
	private boolean terminated;
	private int port;

	private NettyServerImpl(Class<? extends ServerChannel> channelType, int port) {
		this.channelType = channelType;
		checkArgument(port >= 0, "port must be >= 0");
		this.port = port;
	}

	public void start() throws IOException {
		synchronized (lock) {
			server = new NettyServer(channelType, this.port);
			server.setProtocolType(ProtocolType.HTTP2);
			server.start();
			started = true;
		}

	}

	public void shutdown() {
		synchronized (lock) {
			if (started) {
				server.shutdown();
			}
			terminated = true;
		}
		lock.notifyAll();
	}

	public int getport() {
		synchronized (lock) {
			if (!started)
				return -1;
			return server.getPort();
		}
	}

	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		synchronized (lock) {
			long timeoutNanos = unit.toNanos(timeout);
			long endTimeNanos = System.nanoTime() + timeoutNanos;
			while (!terminated
					&& (timeoutNanos = endTimeNanos - System.nanoTime()) > 0) {
				NANOSECONDS.timedWait(lock, timeoutNanos);
			}
			return terminated;
		}
	}

	public void awaitTermination() throws InterruptedException {
		synchronized (lock) {
			while (!terminated) {
				lock.wait();
			}
		}
	}

	public static void main(String[] args) {
		try {
			NettyServerImpl server = new NettyServerImpl(NioServerSocketChannel.class, 8080);
			server.start();
			try {
				server.awaitTermination();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

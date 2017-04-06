package org.base.utils;

import io.netty.channel.Channel;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class KeepAliveManager {
	private static final SystemTicker SYSTEM_TICKER = new SystemTicker();
	private static final long MIN_KEEPALIVE_DELAY_NANOS = TimeUnit.MINUTES
			.toNanos(1);
	KeepAlivePingCallback pingcallback = new KeepAlivePingCallback();
	public PingCallback getPingCallBack()
	{
		return pingcallback;
	}
	private final ScheduledExecutorService scheduler;
	private final Ticker ticker;
	private State state = State.IDLE;
	private long nextKeepaliveTime;
	private ScheduledFuture<?> shutdownFuture;
	private ScheduledFuture<?> pingFuture;
	private Channel channel;
	private NettyHttp2ClientStream stream;
	private final Runnable shutdown = new Runnable() {
		@Override
		public void run() {
			boolean shouldShutdown = false;
			synchronized (KeepAliveManager.this) {
				if (state != State.DISCONNECTED) {
					// We haven't received a ping response within the timeout.
					// The connection is likely gone
					// already. Shutdown the transport and fail all existing
					// rpcs.
					state = State.DISCONNECTED;
					shouldShutdown = true;
				}
			}
			if (shouldShutdown) {
				if (stream != null) {
				     stream.SendForcefulCloseCommand();
				}
				// transport.shutdownNow(Status.UNAVAILABLE.withDescription(
				// "Keepalive failed. The connection is likely gone"));
			}
		}
	};
	private final Runnable sendPing = new Runnable() {
		@Override
		public void run() {
			boolean shouldSendPing = false;
			synchronized (KeepAliveManager.this) {
				if (state == State.PING_SCHEDULED) {
					shouldSendPing = true;
					state = State.PING_SENT;
					// Schedule a shutdown. It fires if we don't receive the
					// ping response within the timeout.
					shutdownFuture = scheduler.schedule(shutdown,
							keepAliveTimeoutInNanos, TimeUnit.NANOSECONDS);
				} else if (state == State.PING_DELAYED) {
					// We have received some data. Reschedule the ping with the
					// new time.
					pingFuture = scheduler.schedule(sendPing, nextKeepaliveTime
							- ticker.read(), TimeUnit.NANOSECONDS);
					state = State.PING_SCHEDULED;
				}
			}
			if (shouldSendPing) {
				stream.SendPingCommand();
				// Send the ping.
				// transport.ping(pingCallback, MoreExecutors.directExecutor());
			}
		}
	};
	private final KeepAlivePingCallback pingCallback = new KeepAlivePingCallback();
	private long keepAliveDelayInNanos;
	private long keepAliveTimeoutInNanos;

	private enum State {
		/*
		 * Transport has no active rpcs. We don't need to do any keepalives.
		 */
		IDLE,
		/*
		 * We have scheduled a ping to be sent in the future. We may decide to
		 * delay it if we receive some data.
		 */
		PING_SCHEDULED,
		/*
		 * We need to delay the scheduled keepalive ping.
		 */
		PING_DELAYED,
		/*
		 * The ping has been sent out. Waiting for a ping response.
		 */
		PING_SENT,
		/*
		 * Transport goes idle after ping has been sent.
		 */
		IDLE_AND_PING_SENT,
		/*
		 * The transport has been disconnected. We won't do keepalives any more.
		 */
		DISCONNECTED,
	}

	/**
	 * Creates a KeepAliverManager.
	 */
	public KeepAliveManager(NettyHttp2ClientStream stream, Channel channel,
			ScheduledExecutorService scheduler, long keepAliveDelayInNanos,
			long keepAliveTimeoutInNanos) {
		this.stream = Preconditions.checkNotNull(stream,
				"NettyHttp2ClientStream");
		this.channel = Preconditions.checkNotNull(channel, "channel");
		this.scheduler = Preconditions.checkNotNull(scheduler, "scheduler");
		this.ticker = SYSTEM_TICKER;
		// Set a minimum cap on keepalive dealy.
		this.keepAliveDelayInNanos = Math.max(MIN_KEEPALIVE_DELAY_NANOS,
				keepAliveDelayInNanos);
		this.keepAliveTimeoutInNanos = keepAliveTimeoutInNanos;
		nextKeepaliveTime = ticker.read() + keepAliveDelayInNanos;
	}

	@VisibleForTesting
	KeepAliveManager(NettyHttp2ClientStream stream, Channel channel,
			ScheduledExecutorService scheduler, Ticker ticker,
			long keepAliveDelayInNanos, long keepAliveTimeoutInNanos) {
		this.stream = Preconditions.checkNotNull(stream,
				"NettyHttp2ClientStream");
		this.channel = Preconditions.checkNotNull(channel, "channel");
		this.scheduler = Preconditions.checkNotNull(scheduler, "scheduler");
		this.ticker = Preconditions.checkNotNull(ticker, "ticker");
		this.keepAliveDelayInNanos = keepAliveDelayInNanos;
		this.keepAliveTimeoutInNanos = keepAliveTimeoutInNanos;
		nextKeepaliveTime = ticker.read() + keepAliveDelayInNanos;
	}

	/**
	 * Transport has received some data so that we can delay sending keepalives.
	 */
	public synchronized void onDataReceived() {
		nextKeepaliveTime = ticker.read() + keepAliveDelayInNanos;
		// We do not cancel the ping future here. This avoids constantly
		// scheduling and cancellation in
		// a busy transport. Instead, we update the status here and reschedule
		// later. So we actually
		// keep one sendPing task always in flight when there're active rpcs.
		if (state == State.PING_SCHEDULED) {
			state = State.PING_DELAYED;
		}
	}

	/**
	 * Transport has active streams. Start sending keepalives if necessary.
	 */
	public synchronized void onTransportActive() {
		if (state == State.IDLE) {
			// When the transport goes active, we do not reset the
			// nextKeepaliveTime. This allows us to
			// quickly check whether the conneciton is still working.
			state = State.PING_SCHEDULED;
			pingFuture = scheduler.schedule(sendPing, nextKeepaliveTime
					- ticker.read(), TimeUnit.NANOSECONDS);
		} else if (state == State.IDLE_AND_PING_SENT) {
			state = State.PING_SENT;
		}
	}

	/**
	 * Transport has finished all streams.
	 */
	public synchronized void onTransportIdle() {
		if (state == State.PING_SCHEDULED || state == State.PING_DELAYED) {
			state = State.IDLE;
		}
		if (state == State.PING_SENT) {
			state = State.IDLE_AND_PING_SENT;
		}
	}

	/**
	 * Transport is shutting down. We no longer need to do keepalives.
	 */
	public synchronized void onTransportShutdown() {
		if (state != State.DISCONNECTED) {
			state = State.DISCONNECTED;
			if (shutdownFuture != null) {
				shutdownFuture.cancel(false);
			}
			if (pingFuture != null) {
				pingFuture.cancel(false);
			}
		}
	}

	public interface PingCallback {

		/**
		 * Invoked when a ping is acknowledged. The given argument is the
		 * round-trip time of the ping, in nanoseconds.
		 *
		 * @param roundTripTimeNanos
		 *            the round-trip duration between the ping being sent and
		 *            the acknowledgement received
		 */
		void onSuccess(long roundTripTimeNanos);

		/**
		 * Invoked when a ping fails. The given argument is the cause of the
		 * failure.
		 *
		 * @param cause
		 *            the cause of the ping failure
		 */
		void onFailure(Throwable cause);
	}

	private class KeepAlivePingCallback implements PingCallback {

		@Override
		public void onSuccess(long roundTripTimeNanos) {
			synchronized (KeepAliveManager.this) {
				shutdownFuture.cancel(false);
				nextKeepaliveTime = ticker.read() + keepAliveDelayInNanos;
				if (state == State.PING_SENT) {
					// We have received the ping response so there's no need to
					// shutdown the transport.
					// Schedule a new keepalive ping.
					pingFuture = scheduler.schedule(sendPing,
							keepAliveDelayInNanos, TimeUnit.NANOSECONDS);
					state = State.PING_SCHEDULED;
				}
				if (state == State.IDLE_AND_PING_SENT) {
					// Transport went idle after we had sent out the ping. We
					// don't need to schedule a new
					// ping.
					state = State.IDLE;
				}
			}
		}

		@Override
		public void onFailure(Throwable cause) {
			// Keepalive ping has failed. Shutdown the transport now.
			synchronized (KeepAliveManager.this) {
				shutdownFuture.cancel(false);
			}
			shutdown.run();
		}
	}

	// TODO(zsurocking): Classes below are copied from Deadline.java. We should
	// consider share the
	// code.

	/**
	 * Time source representing nanoseconds since fixed but arbitrary point in
	 * time.
	 */
	abstract static class Ticker {
		/** Returns the number of nanoseconds since this source's epoch. */
		public abstract long read();
	}

	private static class SystemTicker extends Ticker {
		@Override
		public long read() {
			return System.nanoTime();
		}
	}

}

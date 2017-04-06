package org.base.netty;

import static io.netty.handler.codec.http2.DefaultHttp2LocalFlowController.DEFAULT_WINDOW_UPDATE_RATIO;
import static org.base.utils.wkutils.DEFAULT_FLOW_CONTROL_WINDOW;
import static org.base.utils.wkutils.DEFAULT_MAX_HEADER_LIST_SIZE;
import static org.base.utils.wkutils.DEFAULT_MAX_MESSAGE_SIZE;

import java.nio.channels.ClosedChannelException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.base.utils.Attributes;
import org.base.utils.CreateStreamCommand;
import org.base.utils.ForcefulCloseCommand;
import org.base.utils.GracefulCloseCommand;
import org.base.utils.SendDataCommand;
import org.base.utils.SendPingCommand;
import org.base.utils.WriteQueue;
import org.base.utils.KeepAliveManager.PingCallback;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http2.DefaultHttp2Connection;
import io.netty.handler.codec.http2.DefaultHttp2ConnectionDecoder;
import io.netty.handler.codec.http2.DefaultHttp2ConnectionEncoder;
import io.netty.handler.codec.http2.DefaultHttp2FrameReader;
import io.netty.handler.codec.http2.DefaultHttp2FrameWriter;
import io.netty.handler.codec.http2.DefaultHttp2HeadersDecoder;
import io.netty.handler.codec.http2.DefaultHttp2LocalFlowController;
import io.netty.handler.codec.http2.Http2Connection;
import io.netty.handler.codec.http2.Http2ConnectionAdapter;
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2FrameAdapter;
import io.netty.handler.codec.http2.Http2FrameLogger;
import io.netty.handler.codec.http2.Http2FrameReader;
import io.netty.handler.codec.http2.Http2FrameWriter;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.handler.codec.http2.Http2HeadersDecoder;
import io.netty.handler.codec.http2.Http2InboundFrameLogger;
import io.netty.handler.codec.http2.Http2OutboundFrameLogger;
import io.netty.handler.codec.http2.Http2Settings;
import io.netty.handler.codec.http2.Http2Stream;
import io.netty.handler.codec.http2.Http2StreamVisitor;
import io.netty.handler.codec.http2.StreamBufferingEncoder;
import io.netty.handler.logging.LogLevel;

public class NettyHttp2ClientHandler extends AbstractNettyHttp2Handler {
	private static final Logger logger = Logger
			.getLogger(NettyHttp2ClientHandler.class.getName());

	/**
	 * A message that simply passes through the channel without any real
	 * processing. It is useful to check if buffers have been drained and test
	 * the health of the channel in a single operation.
	 */
	static final Object NOOP_MESSAGE = new Object();

	/**
	 * Status used when the transport has exhausted the number of streams.
	 */
	private static final long USER_PING_PAYLOAD = 1111;

	private final Http2Connection.PropertyKey streamKey;
	private WriteQueue clientWriteQueue;
	private final Ticker ticker;

	// private Http2Ping ping;
	private Attributes attributes = Attributes.EMPTY;

	private int streamId = -1;

	public static NettyHttp2ClientHandler newHandler() {
		return newHandler(DEFAULT_FLOW_CONTROL_WINDOW,
				DEFAULT_MAX_HEADER_LIST_SIZE, Ticker.systemTicker());
	}
	
	private PingCallback pingcallback = null;
	public void setPingCallBack(PingCallback callback)
	{
		this.pingcallback = callback;
	}

	static NettyHttp2ClientHandler newHandler(int flowControlWindow,
			int maxHeaderListSize, Ticker ticker) {
		Preconditions.checkArgument(maxHeaderListSize > 0,
				"maxHeaderListSize must be positive");
		Http2HeadersDecoder headersDecoder = new DefaultHttp2HeadersDecoder();// new
																				// GrpcHttp2ClientHeadersDecoder(maxHeaderListSize);
		Http2FrameReader frameReader = new DefaultHttp2FrameReader(
				headersDecoder);
		Http2FrameWriter frameWriter = new DefaultHttp2FrameWriter();
		Http2Connection connection = new DefaultHttp2Connection(false);

		return newHandler(connection, frameReader, frameWriter,
				flowControlWindow, maxHeaderListSize, ticker);
	}

	@VisibleForTesting
	static NettyHttp2ClientHandler newHandler(Http2Connection connection,
			Http2FrameReader frameReader, Http2FrameWriter frameWriter,
			int flowControlWindow, int maxHeaderListSize, Ticker ticker) {
		Preconditions.checkNotNull(connection, "connection");
		Preconditions.checkNotNull(frameReader, "frameReader");
		Preconditions.checkArgument(flowControlWindow > 0,
				"flowControlWindow must be positive");
		Preconditions.checkArgument(maxHeaderListSize > 0,
				"maxHeaderListSize must be positive");
		Preconditions.checkNotNull(ticker, "ticker");

		Http2FrameLogger frameLogger = new Http2FrameLogger(LogLevel.DEBUG,
				NettyHttp2ClientHandler.class);
		frameReader = new Http2InboundFrameLogger(frameReader, frameLogger);
		frameWriter = new Http2OutboundFrameLogger(frameWriter, frameLogger);

		StreamBufferingEncoder encoder = new StreamBufferingEncoder(
				new DefaultHttp2ConnectionEncoder(connection, frameWriter));

		// Create the local flow controller configured to auto-refill the
		// connection window.
		connection.local().flowController(
				new DefaultHttp2LocalFlowController(connection,
						DEFAULT_WINDOW_UPDATE_RATIO, true));

		Http2ConnectionDecoder decoder = new DefaultHttp2ConnectionDecoder(
				connection, encoder, frameReader);

		Http2Settings settings = new Http2Settings();
		settings.pushEnabled(false);
		settings.initialWindowSize(flowControlWindow);
		settings.maxConcurrentStreams(0);
		settings.maxHeaderListSize(maxHeaderListSize);

		return new NettyHttp2ClientHandler(decoder, encoder, settings, ticker);
	}

	private NettyHttp2ClientHandler(Http2ConnectionDecoder decoder,
			StreamBufferingEncoder encoder, Http2Settings settings,
			Ticker ticker) {
		super(decoder, encoder, settings);
		this.ticker = ticker;
		super.setAutoTuneFlowControl(true);
		// Set the frame listener on the decoder.
		decoder().frameListener(new FrameListener());

		Http2Connection connection = encoder.connection();
		streamKey = connection.newKey();

		connection.addListener(new Http2ConnectionAdapter() {
			@Override
			public void onGoAwayReceived(int lastStreamId, long errorCode,
					ByteBuf debugData) {
				// goingAway(statusFromGoAway(errorCode,
				// ByteBufUtil.getBytes(debugData)));
			}

			@Override
			public void onStreamAdded(Http2Stream stream) {
			}

			@Override
			public void onStreamRemoved(Http2Stream stream) {
				if (connection().numActiveStreams() == 0) {
				}
			}

			@Override
			public void onStreamActive(Http2Stream stream) {
				if (connection().numActiveStreams() == 1) {

				}
			}

			@Override
			public void onStreamClosed(Http2Stream stream) {
				if (connection().numActiveStreams() == 0) {

				}
			}
		});
	}

	/**
	 * The protocol negotiation attributes, available once the protocol
	 * negotiation completes; otherwise returns {@code Attributes.EMPTY}.
	 */
	Attributes getAttributes() {
		return attributes;
	}

	/**
	 * Handler for commands sent from the stream.
	 */
	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		if (msg instanceof CreateStreamCommand) {
			createStream((CreateStreamCommand) msg, promise);
		} else if (msg instanceof SendDataCommand) {
			sendDataCommand(ctx, (SendDataCommand) msg, promise);
		} else if (msg instanceof GracefulCloseCommand) {
			gracefulClose(ctx, (GracefulCloseCommand) msg, promise);
		} else if (msg instanceof ForcefulCloseCommand) {
			forcefulClose(ctx, (ForcefulCloseCommand) msg, promise);
		} else if (msg instanceof SendPingCommand) {
			sendPingFrame(ctx, (SendPingCommand) msg, promise);
		} else {
			throw new AssertionError("Write called for unexpected type: "
					+ msg.getClass().getName());
		}
		/*
		 * if (msg instanceof CreateStreamCommand) {
		 * createStream((CreateStreamCommand) msg, promise); } else if (msg
		 * instanceof SendGrpcFrameCommand) { sendGrpcFrame(ctx,
		 * (SendGrpcFrameCommand) msg, promise); } else if (msg instanceof
		 * CancelClientStreamCommand) { cancelStream(ctx,
		 * (CancelClientStreamCommand) msg, promise); } else if (msg instanceof
		 * SendPingCommand) { sendPingFrame(ctx, (SendPingCommand) msg,
		 * promise); } else if (msg instanceof GracefulCloseCommand) {
		 * gracefulClose(ctx, (GracefulCloseCommand) msg, promise); } else if
		 * (msg instanceof ForcefulCloseCommand) { forcefulClose(ctx,
		 * (ForcefulCloseCommand) msg, promise); } else if (msg == NOOP_MESSAGE)
		 * { ctx.write(Unpooled.EMPTY_BUFFER, promise); } else { throw new
		 * AssertionError("Write called for unexpected type: " +
		 * msg.getClass().getName()); }
		 */
	}

	private void sendPingFrame(ChannelHandlerContext ctx, SendPingCommand msg,
			ChannelPromise promise) {
		// TODO Auto-generated method stub
		long data = USER_PING_PAYLOAD;
		ByteBuf buffer = ctx.alloc().buffer(8);
		buffer.writeLong(data);
		// and then write the ping
		encoder().writePing(ctx, false, buffer, promise);
		ctx.flush();
		promise.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				if (!future.isSuccess()) {
					Throwable cause = future.cause();
					if (cause instanceof ClosedChannelException) {

					}
					forcefulClose(ctx,new ForcefulCloseCommand(),ctx().newPromise());
				}
			}
		});
	}

	private void forcefulClose(ChannelHandlerContext ctx,
			ForcefulCloseCommand msg, ChannelPromise promise) throws Exception {
		// TODO Auto-generated method stub
		super.close(ctx, promise);
		connection().forEachActiveStream(new Http2StreamVisitor() {
			@Override
			public boolean visit(Http2Stream stream) throws Http2Exception {
				Http2Stream http2Stream = connection().stream(streamId);
				if (http2Stream != null) {
					resetStream(ctx, stream.id(), Http2Error.CANCEL.code(),
							ctx.newPromise());
				}

				stream.close();
				return true;
			}
		});
	}

	private void gracefulClose(ChannelHandlerContext ctx,
			GracefulCloseCommand msg, ChannelPromise promise) throws Exception {
		// TODO Auto-generated method stub
		ctx.flush();
		super.close(ctx, promise);
	}

	private void sendDataCommand(ChannelHandlerContext ctx,
			SendDataCommand msg, ChannelPromise promise) {
		// TODO Auto-generated method stub
		encoder().writeData(ctx, streamId, msg.getContent(), 0,
				msg.getEndStream(), promise);
	}

	private void createStream(CreateStreamCommand msg, ChannelPromise promise) {
		// TODO Auto-generated method stub
		streamId = connection().local().incrementAndGetNextStreamId();
		if (streamId < 0) {
			promise.setFailure(new Throwable("create steamid failed"));
			if (!connection().goAwaySent()) {
				try {
					close(ctx(), ctx().newPromise());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// renew a promise
			}
			return;
		}
		ChannelPromise tempPromise = ctx().newPromise();
		encoder().writeHeaders(ctx(), streamId, msg.getHeaders(), 0, false,
				tempPromise).addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future)
					throws Exception {
				// TODO Auto-generated method stub
				if (future.isSuccess()) {
					Http2Stream http2Stream = connection().stream(streamId);
					if (http2Stream != null) {
						// bind some properties for user.
						// http2Stream.setProperty(streamKey, stream);
					}
					promise.setSuccess();
				} else {
					promise.setFailure(future.cause());
				}
			}
		});
	}

	public void startWriteQueue(Channel channel) {
		clientWriteQueue = new WriteQueue(channel);
	}

	public WriteQueue getWriteQueue() {
		return clientWriteQueue;
	}

	/**
	 * Returns the given processed bytes back to inbound flow control.
	 */
	void returnProcessedBytes(Http2Stream stream, int bytes) {
		try {
			decoder().flowController().consumeBytes(stream, bytes);
		} catch (Http2Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void onHeadersRead(int streamId, Http2Headers headers,
			boolean endStream) {
		// header from server
		assert "header modified".compareTo(headers.get("myheadreply")
				.toString()) == 0;
	}

	/**
	 * Handler for an inbound HTTP/2 DATA frame.
	 */
	private void onDataRead(int streamId, ByteBuf data, int padding,
			boolean endOfStream) {
		flowControlPing().onDataRead(data.readableBytes(), padding);

	}

	/**
	 * Handler for an inbound HTTP/2 RST_STREAM frame, terminating a stream.
	 */
	private void onRstStreamRead(int streamId, long errorCode) {

	}

	@Override
	public void close(ChannelHandlerContext ctx, ChannelPromise promise)
			throws Exception {
		logger.fine("Network channel being closed by the application.");
		super.close(ctx, promise);
	}

	/**
	 * Handler for the Channel shutting down.
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}

	@Override
	protected void onConnectionError(ChannelHandlerContext ctx,
			Throwable cause, Http2Exception http2Ex) {
		logger.log(Level.FINE, "Caught a connection error", cause);
		// Parent class will shut down the Channel
		super.onConnectionError(ctx, cause, http2Ex);
	}

	@Override
	protected void onStreamError(ChannelHandlerContext ctx, Throwable cause,
			Http2Exception.StreamException http2Ex) {

		logger.log(Level.FINE,
				"Stream error for unknown stream " + http2Ex.streamId(), cause);

		// Delegate to the base class to send a RST_STREAM.
		super.onStreamError(ctx, cause, http2Ex);
	}

	@Override
	protected boolean isGracefulShutdownComplete() {
		// Only allow graceful shutdown to complete after all pending streams
		// have completed.
		return super.isGracefulShutdownComplete()
				&& ((StreamBufferingEncoder) encoder()).numBufferedStreams() == 0;
	}

	private class FrameListener extends Http2FrameAdapter {
		private boolean firstSettings = true;

		@Override
		public void onSettingsRead(ChannelHandlerContext ctx,
				Http2Settings settings) {
			if (firstSettings) {
				firstSettings = false;
			}
		}

		@Override
		public int onDataRead(ChannelHandlerContext ctx, int streamId,
				ByteBuf data, int padding, boolean endOfStream)
				throws Http2Exception {
			NettyHttp2ClientHandler.this.onDataRead(streamId, data, padding,
					endOfStream);
			return padding;
		}

		@Override
		public void onHeadersRead(ChannelHandlerContext ctx, int streamId,
				Http2Headers headers, int streamDependency, short weight,
				boolean exclusive, int padding, boolean endStream)
				throws Http2Exception {
			NettyHttp2ClientHandler.this.onHeadersRead(streamId, headers,
					endStream);
		}

		@Override
		public void onRstStreamRead(ChannelHandlerContext ctx, int streamId,
				long errorCode) throws Http2Exception {
			NettyHttp2ClientHandler.this.onRstStreamRead(streamId, errorCode);
		}

		@Override
		public void onPingAckRead(ChannelHandlerContext ctx, ByteBuf data)
				throws Http2Exception {
			if (data.getLong(data.readerIndex()) == flowControlPing().payload()) {
		        flowControlPing().updateWindow();
		        if (logger.isLoggable(Level.FINE)) {
		          logger.log(Level.FINE, String.format("Window: %d",
		              decoder().flowController().initialWindowSize(connection().connectionStream())));
		        }
		        if(pingcallback != null)
		        {
		        	pingcallback.onSuccess(10);
		        }
		      }
			else
			{
				if(data.readLong() == 1111)
				{
					 if(pingcallback != null)
				        {
				        	pingcallback.onSuccess(10);
				        }
				}
				else
				logger.warning("Received unexpected ping ack. No ping outstanding");
			}
			/*
			 * Http2Ping p = ping; if (data.getLong(data.readerIndex()) ==
			 * flowControlPing().payload()) { flowControlPing().updateWindow();
			 * if (logger.isLoggable(Level.FINE)) { logger.log(Level.FINE,
			 * String.format("Window: %d",
			 * decoder().flowController().initialWindowSize
			 * (connection().connectionStream()))); } } else if (p != null) {
			 * long ackPayload = data.readLong(); if (p.payload() == ackPayload)
			 * { p.complete(); ping = null; } else { logger.log(Level.WARNING,
			 * String.format(
			 * "Received unexpected ping ack. Expecting %d, got %d",
			 * p.payload(), ackPayload)); } } else {
			 * logger.warning("Received unexpected ping ack. No ping outstanding"
			 * ); }
			 */
		}

		@Override
		public void onPingRead(ChannelHandlerContext ctx, ByteBuf data)
				throws Http2Exception {
			
		}
	}
}

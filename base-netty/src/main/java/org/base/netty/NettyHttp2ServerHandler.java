package org.base.netty;

import static io.netty.handler.codec.http2.DefaultHttp2LocalFlowController.DEFAULT_WINDOW_UPDATE_RATIO;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.base.utils.wkutils.DEFAULT_FLOW_CONTROL_WINDOW;
import static org.base.utils.wkutils.DEFAULT_MAX_HEADER_LIST_SIZE;
import static org.base.utils.wkutils.DEFAULT_MAX_MESSAGE_SIZE;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import org.base.utils.Attributes;
import org.base.utils.WriteQueue;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import io.netty.buffer.ByteBuf;
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
import io.netty.handler.codec.http2.Http2ConnectionDecoder;
import io.netty.handler.codec.http2.Http2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Exception.StreamException;
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
import io.netty.handler.logging.LogLevel;
import io.netty.util.AsciiString;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

public class NettyHttp2ServerHandler extends AbstractNettyHttp2Handler {
	private static Logger logger = Logger
			.getLogger(NettyHttp2ServerHandler.class.getName());

	private final Http2Connection.PropertyKey streamKey;
	private final int maxMessageSize;
	private Attributes attributes;
	private Throwable connectionError;
	private boolean teWarningLogged;
	private WriteQueue serverWriteQueue;
	private AsciiString lastKnownAuthority;

	public static NettyHttp2ServerHandler newHandler() {
		Http2FrameLogger frameLogger = new Http2FrameLogger(LogLevel.DEBUG,
				NettyHttp2ServerHandler.class);
		Http2HeadersDecoder headersDecoder = new DefaultHttp2HeadersDecoder(
				false);// GrpcHttp2ServerHeadersDecoder(
		// maxHeaderListSize);
		Http2FrameReader frameReader = new Http2InboundFrameLogger(
				new DefaultHttp2FrameReader(headersDecoder), frameLogger);
		Http2FrameWriter frameWriter = new Http2OutboundFrameLogger(
				new DefaultHttp2FrameWriter(), frameLogger);
		return newHandler(frameReader, frameWriter, Integer.MAX_VALUE,
				DEFAULT_FLOW_CONTROL_WINDOW, DEFAULT_MAX_HEADER_LIST_SIZE,
				DEFAULT_MAX_MESSAGE_SIZE);
	}

	public static NettyHttp2ServerHandler newHandler(int maxStreams,
			int flowControlWindow, int maxHeaderListSize, int maxMessageSize) {
		Preconditions.checkArgument(maxHeaderListSize > 0,
				"maxHeaderListSize must be positive");
		Http2FrameLogger frameLogger = new Http2FrameLogger(LogLevel.DEBUG,
				NettyHttp2ServerHandler.class);
		Http2HeadersDecoder headersDecoder = new DefaultHttp2HeadersDecoder();// GrpcHttp2ServerHeadersDecoder(
		// maxHeaderListSize);
		Http2FrameReader frameReader = new Http2InboundFrameLogger(
				new DefaultHttp2FrameReader(headersDecoder), frameLogger);
		Http2FrameWriter frameWriter = new Http2OutboundFrameLogger(
				new DefaultHttp2FrameWriter(), frameLogger);
		return newHandler(frameReader, frameWriter, maxStreams,
				flowControlWindow, maxHeaderListSize, maxMessageSize);
	}

	@VisibleForTesting
	static NettyHttp2ServerHandler newHandler(Http2FrameReader frameReader,
			Http2FrameWriter frameWriter, int maxStreams,
			int flowControlWindow, int maxHeaderListSize, int maxMessageSize) {
		Preconditions.checkArgument(maxStreams > 0,
				"maxStreams must be positive");
		Preconditions.checkArgument(flowControlWindow > 0,
				"flowControlWindow must be positive");
		Preconditions.checkArgument(maxHeaderListSize > 0,
				"maxHeaderListSize must be positive");
		Preconditions.checkArgument(maxMessageSize > 0,
				"maxMessageSize must be positive");

		Http2Connection connection = new DefaultHttp2Connection(true);
		// Create the local flow controller configured to auto-refill the
		// connection window.
		connection.local().flowController(
				new DefaultHttp2LocalFlowController(connection,
						DEFAULT_WINDOW_UPDATE_RATIO, true));

		Http2ConnectionEncoder encoder = new DefaultHttp2ConnectionEncoder(
				connection, frameWriter);
		Http2ConnectionDecoder decoder = new DefaultHttp2ConnectionDecoder(
				connection, encoder, frameReader);

		Http2Settings settings = new Http2Settings();
		settings.initialWindowSize(Integer.MAX_VALUE);// flowControlWindow);
		settings.maxConcurrentStreams(maxStreams);
		settings.maxHeaderListSize(maxHeaderListSize);

		return new NettyHttp2ServerHandler(decoder, encoder, settings,
				maxMessageSize);
	}

	FileOutputStream fos = null;
	FileChannel fc = null;

	NettyHttp2ServerHandler(Http2ConnectionDecoder decoder,
			Http2ConnectionEncoder encoder, Http2Settings initialSettings,
			int maxMessageSize) {
		super(decoder, encoder, initialSettings);
		checkArgument(maxMessageSize >= 0, "maxMessageSize must be >= 0");
		super.setAutoTuneFlowControl(true);
		streamKey = encoder.connection().newKey();
		this.maxMessageSize = maxMessageSize;
		decoder().frameListener(new FrameListener());
		try {
			fos = new FileOutputStream("/work/code/pic/1.mkv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fc = fos.getChannel();
		// TODO Auto-generated constructor stub
	}

	@Nullable
	Throwable connectionError() {
		return connectionError;
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		serverWriteQueue = new WriteQueue(ctx.channel());
		super.handlerAdded(ctx);
	}

	@Override
	protected void onConnectionError(ChannelHandlerContext ctx,
			Throwable cause, Http2Exception http2Ex) {
		logger.log(Level.WARNING, "Connection Error", cause);
		connectionError = cause;
		super.onConnectionError(ctx, cause, http2Ex);
	}

	@Override
	protected void onStreamError(ChannelHandlerContext ctx, Throwable cause,
			StreamException http2Ex) {
		logger.log(Level.WARNING, "Stream Error", cause);

		// TODO(ejona): Abort the stream by sending headers to help the client
		// with debugging.
		// Delegate to the base class to send a RST_STREAM.
		super.onStreamError(ctx, cause, http2Ex);
	}

	/**
	 * Handler for the Channel shutting down.
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
	}
	private class ResponseHeader{
		Http2Headers headers;
		final int streamId;
		ResponseHeader(Http2Headers headers,int streamId)
		{
			this.headers = headers;
			this.streamId = streamId;
		}
		Http2Headers getHeaders()
		{
			return this.headers;
		}
		
		int getStreamId()
		{
			return this.streamId;
		}
	}
	@Override
	public void write(ChannelHandlerContext ctx, Object msg,
			ChannelPromise promise) throws Exception {
		if(msg instanceof ResponseHeader)
		{
			ResponseHeader(ctx,(ResponseHeader)msg,promise);
		}
		ctx.write(msg, promise);
	}
	
	
	private void ResponseHeader(ChannelHandlerContext ctx, ResponseHeader response,
			ChannelPromise promise) {
		// TODO Auto-generated method stub

		// if it is the last frame data
		// promise.complete()
		//
		encoder().writeHeaders(ctx, response.getStreamId(), response.getHeaders(), 0, false, promise);
	}

	WriteQueue getWriteQueue() {
		return serverWriteQueue;
	}

	public void onRstStreamRead(int streamId) {
		// TODO Auto-generated method stub

	}

	public void onDataRead(int streamId, ByteBuf data, int padding,
			boolean endOfStream) {
		// TODO Auto-generated method stub
		flowControlPing().onDataRead(data.readableBytes(), padding);
	}

	// self-defined headers
	public void onHeadersRead(ChannelHandlerContext ctx, int streamId,
			Http2Headers headers) {
		// TODO Auto-generated method stub
		CharSequence ch = headers.get("myhead");
		CharSequence ch2 = headers.get("nohead");// ch2:null,ch:myvalue
		Http2Stream stream = connection().stream(streamId);
		if (stream == null) {
			resetStream(ctx, streamId, Http2Error.CANCEL.code(), ctx()
					.newPromise());
			return;
		}
		headers.remove("myhead");
		headers.add("myheadreply", "header modified");
		ctx().channel().write(new ResponseHeader(headers,streamId));
		size = 0;
		timespan = System.currentTimeMillis();
	}

	static int size = 0;
	static long timespan = 0;
	private class FrameListener extends Http2FrameAdapter {

		@Override
		public int onDataRead(ChannelHandlerContext ctx, int streamId,
				ByteBuf data, int padding, boolean endOfStream)
				throws Http2Exception {
			NettyHttp2ServerHandler.this.onDataRead(streamId, data, padding,
					endOfStream);
			try {
				size += data.readableBytes();
				fc.write(data.nioBuffer());
				if(endOfStream)
				{
					fos.flush();
					fos.close();
					timespan = System.currentTimeMillis() - timespan;
					System.out.print(String.format("time span %d \r\n", timespan));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return padding;
		}

		@Override
		public void onHeadersRead(ChannelHandlerContext ctx, int streamId,
				Http2Headers headers, int streamDependency, short weight,
				boolean exclusive, int padding, boolean endStream)
				throws Http2Exception {
			NettyHttp2ServerHandler.this.onHeadersRead(ctx, streamId, headers);
		}

		@Override
		public void onRstStreamRead(ChannelHandlerContext ctx, int streamId,
				long errorCode) throws Http2Exception {
			NettyHttp2ServerHandler.this.onRstStreamRead(streamId);
		}

		@Override
		public void onPingAckRead(ChannelHandlerContext ctx, ByteBuf data)
				throws Http2Exception {
			if (data.getLong(data.readerIndex()) == flowControlPing().payload()) {
				flowControlPing().updateWindow();
				if (logger.isLoggable(Level.FINE)) {
					logger.log(Level.FINE, String.format(
							"Window: %d",
							decoder().flowController().initialWindowSize(
									connection().connectionStream())));
				}
			} else {
				logger.warning("Received unexpected ping ack. No ping outstanding");
			}
		}
	}

}

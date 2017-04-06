package org.base.utils;

import java.util.Queue;

import org.base.utils.WriteQueue.QueuedCommand;

import io.netty.handler.codec.http2.DefaultHttp2Headers;
import io.netty.handler.codec.http2.Http2Headers;
import io.netty.util.AsciiString;

public class CreateStreamCommand extends WriteQueue.AbstractQueuedCommand {
	private Http2Headers headers;

	public CreateStreamCommand() {
		headers = new DefaultHttp2Headers().add(as("myhead"), as("myvalue")).scheme("http");
	}
	public Http2Headers getHeaders()
	{
		return headers;
	}
	private AsciiString as(String string) {
		return new AsciiString(string);
	}
}

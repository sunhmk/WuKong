package org.base.netty;

import org.base.netty.NettyServer.ProtocolType;
import io.netty.channel.ChannelHandler;

public final class ChannelHandlers {
	public static ChannelHandler newServerChannelHandler(ProtocolType protocolType)
	{
		ChannelHandler channelHandler = null;
		switch(protocolType)
		{
		case HTTP:
			break;
		case HTTP2:
			channelHandler = NettyHttp2ServerHandler.newHandler();
			break;
		case FTP:
			break;
		case SOCKET:
			break;
		}
		return channelHandler;
	}
	
	public static ChannelHandler newClientChannelHandler(
			ProtocolType protocolType) 
	{
		ChannelHandler channelHandler = null;
		switch(protocolType)
		{
		case HTTP:
			break;
		case HTTP2:
			channelHandler = newHttp2ClientChannelHandler();
			break;
		case FTP:
			break;
		case SOCKET:
			break;
		}
		return channelHandler;
	}
	
	public static ChannelHandler newHttp2ClientChannelHandler()
	{
		return NettyHttp2ClientHandler.newHandler();
	}
}

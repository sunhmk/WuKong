package io.netty.example.http.file;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpFileClientHandler extends ChannelInboundHandlerAdapter {
	FileChannel filechannel = null;
	FileOutputStream file = null;
	Channel channel = null;
	ChannelHandlerContext ctx;

	HttpFileClientHandler() {
	}

	public void GetFile(String fileName) {
		try {
			if (file != null) {
				if (filechannel != null && filechannel.isOpen()) {
					try {
						filechannel.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				try {
					file.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			file = new FileOutputStream("/work/code/pic/" + "test.file");
			filechannel = file.getChannel();
			DefaultFullHttpRequest request = null;
			try {
				request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
						HttpMethod.GET, fileName,
						Unpooled.wrappedBuffer("httpfileclient"
								.getBytes("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			request.headers().set(HttpHeaders.Names.HOST, "localhost:8080");
			request.headers().set(HttpHeaders.Names.CONNECTION,
					HttpHeaders.Values.KEEP_ALIVE);
			request.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
					request.content().readableBytes());
			if (channel != null && channel.isOpen() && channel.isWritable()) {
				channel.writeAndFlush(request).addListener(
						new ChannelFutureListener() {
							@Override
							public void operationComplete(ChannelFuture future)
									throws Exception {
								// TODO Auto-generated method stub
								if (!future.isSuccess()) {
									System.out.println("http requst error\r\n");
								}
							}
						});
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		len = 0;
	}

	public void GetDir(String dirName) throws UnsupportedEncodingException {
		// HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1,
		// HttpMethod.POST, dirName);
		DefaultFullHttpRequest request = new DefaultFullHttpRequest(
				HttpVersion.HTTP_1_1, HttpMethod.GET, dirName,
				Unpooled.wrappedBuffer("httpfileclient".getBytes("UTF-8")));
		request.headers().set(HttpHeaders.Names.HOST, "localhost:8080");
		request.headers().set(HttpHeaders.Names.CONNECTION,
				HttpHeaders.Values.KEEP_ALIVE);
		request.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
				request.content().readableBytes());
		if (channel != null && channel.isOpen() && channel.isWritable()) {
			channel.writeAndFlush(request).addListener(
					new ChannelFutureListener() {
						@Override
						public void operationComplete(ChannelFuture future)
								throws Exception {
							// TODO Auto-generated method stub
							if (!future.isSuccess()) {
								System.out.println("http requst error\r\n");
							}
						}
					});
		}
	}

	static int len = 0;

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.fireChannelActive();
		this.ctx = ctx;
		channel = ctx.channel();
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		// ctx.write(msg);

		if(msg instanceof HttpResponse)
		{
			HttpResponse httpResponse = (HttpResponse)msg;
			System.out.println("STATUS: " + httpResponse.getStatus());
            System.out.println("VERSION: " + httpResponse.getProtocolVersion());
            System.out.println();

            if (!httpResponse.headers().isEmpty()) {
                for (String name : httpResponse.headers().names()) {
                    for (String value : httpResponse.headers().getAll(name)) {
                        System.out.println("HEADER: " + name + " = " + value);
                    }
                }
                System.out.println();
            }
            if (HttpHeaders.isTransferEncodingChunked(httpResponse)) {
                System.out.println("CHUNKED CONTENT {");
            } else {
                System.out.println("CONTENT {");
            }
		}
		else if(msg instanceof HttpContent)
		{
			HttpContent content = (HttpContent)msg;
			try {
				filechannel.write(content.content().nioBuffer());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (msg instanceof LastHttpContent) {
				try {
					filechannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("} END OF CONTENT");
			}
		}
		else if (msg instanceof FullHttpResponse) {
			FullHttpResponse buf = (FullHttpResponse) msg;
			// byte[] bytes = new byte[4];//buf.readableBytes()];
			// buf.readBytes(bytes);
			System.out.println("本次接收内容长度：");
			String type = buf.headers().get("type");
			if (type.compareTo("list") == 0 || type.compareTo("file") == 0) {
				Charset charset = null;
				CharsetDecoder decoder = null;
				CharBuffer charBuffer = null;
				charset = Charset.forName("UTF-8");
				decoder = charset.newDecoder();
				// 用这个的话，只能输出来一次结果，第二次显示为空
				// charBuffer = decoder.decode(buffer);
				try {
					charBuffer = decoder.decode(buf.content().nioBuffer()
							.asReadOnlyBuffer());
				} catch (CharacterCodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(charBuffer.toString());
			}
		}
		/*
		 * if (buf.readByte() == 'H' && buf.readByte() == 'E' && buf.readByte()
		 * == 'L' && buf.readByte() == 'O') { // buf.resetReaderIndex();
		 * System.out.println("HELO\r\n"); ctx.writeAndFlush(Unpooled
		 * .wrappedBuffer
		 * ("/Users/sunanan/Downloads/[电影天堂www.dy2018.com]罗曼蒂克xw史HD高清国语中英双字.mkv"
		 * .getBytes())); return; } else { buf.resetReaderIndex(); try { len +=
		 * buf.readableBytes(); filechannel.write(buf.nioBuffer());
		 * filechannel.force(false);
		 * System.out.println(String.format("len = %d\r\n", len)); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 */
		try {
			super.channelRead(ctx, msg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
		// obj.notifyAll();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		// obj.notifyAll();
		ctx.close();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		len = 0;
		if (file != null && filechannel != null && filechannel.isOpen()) {
			filechannel.close();
		}
		ctx.fireChannelInactive();
	}
}

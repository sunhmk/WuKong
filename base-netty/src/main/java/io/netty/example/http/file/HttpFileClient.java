package io.netty.example.http.file;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.file.FileClientHandler;
import io.netty.handler.codec.http.ClientCookieEncoder;
import io.netty.handler.codec.http.DefaultCookie;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpFileClient {
	static final boolean SSL = System.getProperty("ssl") != null;
	static final String HOST = System.getProperty("host", "127.0.0.1");
	static final int PORT = Integer
			.parseInt(System.getProperty("port", "8080"));
	static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

	static Object obj = new Object();

	public static void main(String[] args) throws Exception {
		// Configure SSL.
		final SslContext sslCtx;
		if (SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(),
					ssc.privateKey()).build();
		} else {
			sslCtx = null;
		}

		// Configure the server.
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.option(ChannelOption.SO_KEEPALIVE, true)
					.handler(new LoggingHandler(LogLevel.INFO))
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch)
								throws Exception {
							ChannelPipeline p = ch.pipeline();
							if (sslCtx != null) {
								p.addLast(sslCtx.newHandler(ch.alloc()));
							}
							p.addLast(
									 //new HttpRequestDecoder(),
									 //new HttpResponseEncoder(),
									 //new HttpContentDecompressor(),
									//new HttpServerCodec(),
									//new HttpObjectAggregator(65536),
									//new ObjectEncoder(),
									//new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
									new HttpClientCodec(),
									//new HttpObjectAggregator(65536),
									new HttpFileClientHandler());
						}
					});

			// Start the server.
			ChannelFuture f = b.connect(HOST, PORT).sync();
			/*Map<String, String> getData = new HashMap<String, String>();
	        //getData.put("tags", "806:938356;");
	        //getData.put("sort", "_p");
	        String url = "http://localhost:8080";
	        HttpRequest get = getRequestMethod(getData, url, "get");
			f.channel().writeAndFlush(get).sync();*/
			f.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future)
						throws Exception {
					// TODO Auto-generated method stub
					if (future.isSuccess()) {
						 HttpFileClientHandler handler = future.channel().pipeline().get(HttpFileClientHandler.class);
						 //handler.GetDir("/");
						 //handler.GetFile("/src/main/java/io/netty/example/sctp/SctpEchoClientHandler.java");
						 handler.GetFile("/1.mkv");
					}
				}

			});
			/*URI uri = new URI("http://127.0.0.1:8080");
            DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString(), Unpooled.wrappedBuffer("httpfileclient".getBytes("UTF-8")));//生成一个默认的httpRequest。

            httpRequest.headers().set(HttpHeaders.Names.HOST, "127.0.0.1");
            httpRequest.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            httpRequest.headers().set(HttpHeaders.Names.CONTENT_LENGTH, httpRequest.content().readableBytes());//可以在httpRequest.headers中设置各种需要的信息。

            f.channel().writeAndFlush(httpRequest).sync();//发送
			/*
			 * DefaultFullHttpRequest request = new
			 * DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
			 * HttpMethod.PUT,"http://localhost:8080" +
			 * "/",Unpooled.wrappedBuffer("httpfileclient".getBytes("UTF-8")));
			 * request.headers().set(HttpHeaders.Names.HOST, "localhost");
			 * request.headers().set(HttpHeaders.Names.CONNECTION,
			 * HttpHeaders.Values.KEEP_ALIVE);
			 * request.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
			 * request.content().readableBytes());
			 * 
			 * f.channel().writeAndFlush(request).addListener(new
			 * GenericFutureListener(){
			 * 
			 * @Override public void operationComplete(Future future) throws
			 * Exception { // TODO Auto-generated method stub
			 * if(!future.isSuccess()) {
			 * System.out.println("request error\r\n"); } }
			 * 
			 * });
			 */
			// Wait until the server socket is closed.
			f.channel().closeFuture().sync();
			// obj.wait();

		} finally {
			// Shut down all event loops to terminate all threads.
			group.shutdownGracefully();
		}
	}
	
	public static HttpRequest getRequestMethod(Map<String, String> parameter, String url, String method) throws HttpPostRequestEncoder.ErrorDataEncoderException {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }

        String path = uri.getRawPath();
        String host = uri.getHost();

        HttpRequest request = null;
        if ("post".equalsIgnoreCase(method)) {
            request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.POST, path);

            HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);
            // This encoder will help to encode Request for a FORM as POST.
            HttpPostRequestEncoder bodyRequestEncoder = new HttpPostRequestEncoder(factory, request, false);
            // add Form attribute
            if (parameter != null) {
                Set<Map.Entry<String, String>> entrySet = parameter.entrySet();
                for (Map.Entry<String, String> e : entrySet) {
                    String key = e.getKey();
                    String value = e.getValue();
                    bodyRequestEncoder.addBodyAttribute(key, value);
                }
                try {
                    request = bodyRequestEncoder.finalizeRequest();
                } catch (HttpPostRequestEncoder.ErrorDataEncoderException e) {
                    // if an encoding error occurs
                    e.printStackTrace();
                }
            }

            request.headers().set(HttpHeaders.Names.HOST, host);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
            request.headers().set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
            request.headers().set(HttpHeaders.Names.COOKIE, ClientCookieEncoder.encode(
                    new DefaultCookie("my-cookie", "foo"),
                    new DefaultCookie("another-cookie", "bar")));
        } else if ("get".equalsIgnoreCase(method)) {
            //uri.toString()没有查询参数的uri
            QueryStringEncoder encoder = new QueryStringEncoder(uri.toString());
            if (parameter != null) {
                Set<Map.Entry<String, String>> entrySet = parameter.entrySet();
                for (Map.Entry<String, String> e : entrySet) {
                    String key = e.getKey();
                    String value = e.getValue();
                    encoder.addParam(key, value);
                }
            }
            //encoder.toString()有查询参数的uri
            request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, encoder.toString());
            HttpHeaders headers = request.headers();
            headers.set(HttpHeaders.Names.HOST, host);
            headers.set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
            headers.set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP.toString() + ','
                    + HttpHeaders.Values.DEFLATE.toString());

            headers.set(HttpHeaders.Names.ACCEPT_CHARSET, "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
            headers.set(HttpHeaders.Names.ACCEPT_LANGUAGE, "fr");
            headers.set(HttpHeaders.Names.USER_AGENT, "Netty Simple Http Client side");
            headers.set(HttpHeaders.Names.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

            headers.set(HttpHeaders.Names.COOKIE, ClientCookieEncoder.encode(
                    new DefaultCookie("my-cookie", "foo"),
                    new DefaultCookie("another-cookie", "bar"))
            );
        } else {
            System.err.println("this method is not support!");
        }
        return request;
    }
	/*
	 * HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); 
        HttpPostRequestEncoder bodyRequestEncoder =
                new HttpPostRequestEncoder(factory, request, false);  // false => not multipart
 bodyRequestEncoder.addBodyFileUpload("myfile", file, "application/x-zip-compressed", false);
  request = bodyRequestEncoder.finalizeRequest();
	 */
}

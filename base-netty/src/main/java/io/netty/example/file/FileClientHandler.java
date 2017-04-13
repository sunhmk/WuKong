package io.netty.example.file;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class FileClientHandler extends ChannelInboundHandlerAdapter{
	 FileChannel filechannel = null;
	 RandomAccessFile file = null;
	 Object obj;
	 FileClientHandler(Object obj)
	 {
		 this.obj = checkNotNull(obj,"object");
	 }
	 @Override
	    public void channelActive(ChannelHandlerContext ctx) {
		 	try {
		 		file = new RandomAccessFile("/work/code/pic/2,mkv","rw");
		 		filechannel = file.getChannel();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 	len = 0;
	       //ctx.writeAndFlush("/Users/sunanan/Downloads/[电影天堂www.dy2018.com]罗曼蒂克xw史HD高清国语中英双字.mkv");
	    }
	 	static int len = 0;
	    @Override
	    public void channelRead(ChannelHandlerContext ctx, Object msg) {
	        //ctx.write(msg);
	    	 ByteBuf buf = (ByteBuf) msg;
	         //byte[] bytes = new byte[4];//buf.readableBytes()];
	         //buf.readBytes(bytes);
	         System.out.println("本次接收内容长度：");
	         if(buf.readByte() == 'H'&&buf.readByte() == 'E'&&buf.readByte()=='L' &&buf.readByte() == 'O')
		     {
	        	//buf.resetReaderIndex();
	        	 System.out.println("HELO\r\n");
			    ctx.writeAndFlush(Unpooled.wrappedBuffer("/Users/sunanan/Downloads/[电影天堂www.dy2018.com]罗曼蒂克xw史HD高清国语中英双字.mkv".getBytes()));
			    return;
		     }
	         else
	         {
	        	 buf.resetReaderIndex();
	        	 try {
	        		 len += buf.readableBytes();
					filechannel.write(buf.nioBuffer());
					filechannel.force(false);
					System.out.println(String.format("len = %d\r\n", len));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	         }

	    }

	    @Override
	    public void channelReadComplete(ChannelHandlerContext ctx) {
	       ctx.flush();
	       //obj.notifyAll();
	    }

	    @Override
	    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
	        // Close the connection when an exception is raised.
	        cause.printStackTrace();
	        //obj.notifyAll();
	        ctx.close();
	    }
	    @Override
	    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	    	len = 0;
	    	file.close();
	        ctx.fireChannelInactive();
	    }
}

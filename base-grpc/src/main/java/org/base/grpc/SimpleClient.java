package org.base.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.routeguide.RouteSummary;
import io.grpc.stub.StreamObserver;

import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.midi.MidiDevice.Info;

public class SimpleClient {


    private static final Logger logger = Logger.getLogger(SimpleClient.class.getName());

    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;
    private final GreeterGrpc.GreeterStub asyncStub;

    /** Construct client connecting to HelloWorld server at {@code host:port}. */
    // 首先，我们需要为stub创建一个grpc的channel，指定我们连接服务端的地址和端口
    // 使用ManagedChannelBuilder方法来创建channel
    public SimpleClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
        // needing certificates.
        .usePlaintext(true)
        .build();
        // 使用我们从proto文件生成的GreeterGrpc类提供的newBlockingStub方法指定channel创建stubs
        blockingStub = GreeterGrpc.newBlockingStub(channel);
        asyncStub = GreeterGrpc.newStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }
    // 调用服务端方法
    /** Say hello to server. */
    public void greet(String name) {
        logger.info("Will try to greet " + name + " ...");
        // 创建并定制protocol buffer对象，使用该对象调用服务端的sayHello方法，获得response
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = blockingStub.sayHello(request);
        // 如果有异常发生，则异常被编码成Status，可以从StatusRuntimeException异常中捕获
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Greeting: " + response.getMessage());
    }
    /** Say hello to server.  rpc LotsOfGreetings(stream HelloRequest) returns (HelloReply) {} */
    public void lotsOfReplies(String name) {
        logger.info("Will try to greet " + name + " ...");
        // 创建并定制protocol buffer对象，使用该对象调用服务端的sayHello方法，获得response
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        Iterator<HelloReply> response;
        try {
            response = blockingStub.lotsOfReplies(request);
            while(response.hasNext())
            logger.info("Greeting: " + response.next().getMessage());
        // 如果有异常发生，则异常被编码成Status，可以从StatusRuntimeException异常中捕获
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
    }
    
    public void LotsOfGreetings(String name) {
        logger.info("Will try to greet " + name + " ...");
        // 创建并定制protocol buffer对象，使用该对象调用服务端的sayHello方法，获得response

    	StreamObserver<HelloReply> responseObserver =new StreamObserver<HelloReply>(){

			@Override
			public void onNext(HelloReply value) {
				// TODO Auto-generated method stub
				logger.info("data "+value.getMessage());
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onCompleted() {
				// TODO Auto-generated method stub
				
			}
    	};
    	StreamObserver<HelloRequest>requestObserver = asyncStub.lotsOfGreetings(responseObserver);
        try {
        	for(int i = 0 ; i < 50;i++){
        		requestObserver.onNext(HelloRequest.newBuilder().setName(name).build());
        	}
            //logger.info("Greeting: " + response.next().getMessage());
        // 如果有异常发生，则异常被编码成Status，可以从StatusRuntimeException异常中捕获
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        requestObserver.onCompleted();
    }
    
    public CountDownLatch  bidiHello()
    {
    	final CountDownLatch finishLatch = new CountDownLatch(1);
    	io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.HelloReply> responseObserver = 
    	new StreamObserver<io.grpc.examples.helloworld.HelloReply>(){

			@Override
			public void onNext(HelloReply value) {
				// TODO Auto-generated method stub
				logger.info("data "+value.getMessage());
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				finishLatch.countDown();
			}

			@Override
			public void onCompleted() {
				// TODO Auto-generated method stub
				finishLatch.countDown();
			}
    		
    	};
    	StreamObserver<HelloRequest> requestObserver = asyncStub.bidiHello(responseObserver);
    	for(int i = 0;i < 20000;i++)
    	{
    		requestObserver.onNext(HelloRequest.newBuilder().setName("bidiHello").build());
    	}
    	requestObserver.onCompleted();
    	return finishLatch;
    }
    
    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting.
     */
    public static void main(String[] args) throws Exception {
    	SimpleClient client = new SimpleClient("127.0.0.1", 50051);
        try {
            /* Access a service running on the local machine on port 50051 */
            String user = "world";
            if (args.length > 0) {
                user = args[0]; /* Use the arg as the name to greet if provided */
            }
            long a = System.currentTimeMillis();
            //for(int i = 0 ; i < 10000;i++)
            client.greet(user);
            client.lotsOfReplies(user);
            a = System.currentTimeMillis() - a;
            System.out.println(String.format("time:%d\r\n",a));
            client.LotsOfGreetings("test");
            CountDownLatch finishLatch = client.bidiHello();
            if(!finishLatch.await(1, TimeUnit.MINUTES))
            {
            	logger.info("waiting 1 minutes ");
            }
            
        } finally {
            client.shutdown();
        }
    }
}
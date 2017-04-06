package org.base.grpc;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.HandlerSettings;
import io.grpc.stub.StreamObserver;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;

import java.io.IOException;
import java.util.logging.Logger;
public class SimpleServer {
    private static final Logger logger = Logger.getLogger(SimpleServer.class.getName());
    private int port = 50051;
    private Server server;

    private void start() throws IOException {
      // 使用ServerBuilder来构建和启动服务，通过使用forPort方法来指定监听的地址和端口
      // 创建一个实现方法的服务GreeterImpl的实例，并通过addService方法将该实例纳入
      // 调用build() start()方法构建和启动rpcserver
      server = ServerBuilder.forPort(port)
          .addService(new GreeterImpl())
          .build()
          .start();
      logger.info("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
              // Use stderr here since the logger may have been reset by its JVM shutdown hook.
              System.err.println("*** shutting down gRPC server since JVM is shutting down");
              SimpleServer.this.stop();
              System.err.println("*** server shut down");
            }
          });
        }

        private void stop() {
          if (server != null) {
            server.shutdown();
          }
        }

        /**
         * Await termination on the main thread since the grpc library uses daemon threads.
         */
        private void blockUntilShutdown() throws InterruptedException {
          if (server != null) {
            server.awaitTermination();
          }
        }

        /**
         * Main launches the server from the command line.
         */
        public static void main(String[] args) throws IOException, InterruptedException {
          final SimpleServer server = new SimpleServer();
          server.start();
          server.blockUntilShutdown();
        }

    // 我们的服务GreeterImpl继承了生成抽象类GreeterGrpc.GreeterImplBase，实现了服务的所有方法
    private class GreeterImpl extends GreeterGrpc.GreeterImplBase {

        @Override
        public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
          HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
          // 使用响应监视器的onNext方法返回HelloReply
          responseObserver.onNext(reply);
          // 使用onCompleted方法指定本次调用已经完成
          responseObserver.onCompleted();
        }
        
        //  rpc LotsOfGreetings(stream HelloRequest) returns (HelloReply) {}
        @Override
        public io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.HelloRequest> lotsOfGreetings(
                io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.HelloReply> responseObserver) {
        	return new io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.HelloRequest>(){
        		String str = "";
        		int i = 0;
        		@Override
				public void onNext(HelloRequest value){
        			str += " LotsOfGreetings";
        			str += value.getName();
        			str += i;
        			str += "\r\n";
        			i++;
        		}
        		@Override
                public void onError(Throwable t) {
        		}
        		
        		@Override
                public void onCompleted() {
        		  HelloReply reply = HelloReply.newBuilder().setMessage(str).build();
        		  responseObserver.onNext(reply);
                  responseObserver.onCompleted();
                }
        	};
        }
        
        //  
        //  rpc LotsOfReplies(HelloRequest) returns (stream HelloReply){}
        @Override
        public void lotsOfReplies(io.grpc.examples.helloworld.HelloRequest request,
                io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.HelloReply> responseObserver){
        	for(int i = 0 ; i < 10; i++)
        	{  HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + i + request.getName()).build();
              // 使用响应监视器的onNext方法返回HelloReply
              responseObserver.onNext(reply);
        	}
              // 使用onCompleted方法指定本次调用已经完成
            responseObserver.onCompleted();
        }
        
        @Override
        public io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.HelloRequest> bidiHello(
                io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.HelloReply> responseObserver) {
              return new io.grpc.stub.StreamObserver<io.grpc.examples.helloworld.HelloRequest>(){
            	int i = 0;
				@Override
				public void onNext(HelloRequest value) {
					// TODO Auto-generated method stub
					responseObserver.onNext(HelloReply.newBuilder().setMessage("bidiHello " + i + value.getName()).build());
					i++;
				}

				@Override
				public void onError(Throwable t) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onCompleted() {
					// TODO Auto-generated method stub
					responseObserver.onCompleted();
				}
            	  
              };
        }
      }
}
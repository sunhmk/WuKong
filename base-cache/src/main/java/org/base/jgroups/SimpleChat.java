package org.base.jgroups;

import java.io.BufferedReader;  
import java.io.DataInputStream;  
import java.io.DataOutputStream;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.OutputStream;  
import java.util.LinkedList;  
import java.util.List;  
  
import org.jgroups.JChannel;  
import org.jgroups.Message;  
import org.jgroups.ReceiverAdapter;  
import org.jgroups.View;  
import org.jgroups.util.Util;  
  
public class SimpleChat extends ReceiverAdapter {  
    JChannel channel;  
    String user_name = "ABC";  
    private List<String> state = new LinkedList<String>();  
  
    private void start() throws Exception {  
        channel = new JChannel(); //使用默认配置udp.xml  
        channel.setReceiver(this); //指定Receiver用来收消息和得到View改变的通知  
        channel.connect("ChatCluster"); //连接到集群  
          
        //刚加入集群时，我们通过getState()获取聊天历史记录  
        //getState()的第一个参数代表目的地地址，这里传null代表第一个实例（coordinator）  
        //第二个参数代表等待超时时间，我们等待10秒。如果时间到了，State传递不过来，会抛例外。也可以传0代表永远等下去  
        channel.getState(null, 10000);  
        eventLoop();  
        channel.close();  
    }  
  
    private void eventLoop() {  
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));  
  
        while (true) {  
            try {  
                System.out.print("> ");  
                System.out.flush();  
                String line = in.readLine().toLowerCase();  
                if (line.startsWith("quit") || line.startsWith("exit")) {  
                    break;  
                }  
                line = "[" + user_name + "] " + line;  
  
                //Message构造函数的第一个参数代表目的地地址，这里传null代表要发消息给集群内的所有地址  
                //第二个参数表示源地址，传null即可，框架会自动赋值  
                //第三个参数line会被序列化成byte[]然后发送，推荐自己序列化而不是用java自带的序列化  
                Message msg = new Message(null, line);  
                channel.send(msg); //发消息到集群  
  
            } catch (Exception e) {  
            }  
        }  
    }  
      
    @Override  
    //每当有实例加入或者离开集群(或崩溃)的时候，viewAccepted方法会被调用  
    public void viewAccepted(View new_view) {  
         System.out.println("** view: " + new_view);   
    }  
  
    @Override  
    //有消息时，byte[]会被反序列化成Message对象，也可以用Message.getBuffer得到byte[]然后自己反序列化。  
    public void receive(Message msg) {  
        String line = msg.getSrc() + ": " + msg.getObject();  
        System.out.println(line);  
        //加入到历史记录  
        synchronized (state) {  
            state.add(line);  
        }  
    }  
      
    @Override  
    public void getState(OutputStream output) throws Exception {  
        //当JChannel.getState()被调用时，某个原来就在集群中的实例的getState会被调用用来得到集群的共享state  
        //Util.objectToStream方法将state序列化为output二进制流   
        synchronized (state) {  
            Util.objectToStream(state, new DataOutputStream(output));   
        }  
    }  
      
    @Override  
    public void setState(InputStream input) throws Exception {  
        //当以上集群的共享state被得到后，新加入集群的实例的setState方法就会被调用了  
        List<String> list = (List<String>) Util.objectFromStream(new DataInputStream(input));  
        synchronized (state) {  
            state.clear();  
            state.addAll(list);  
        }  
  
        System.out.println(list.size() + " messages in chat history):");  
        for (String str : list) {  
            System.out.println(str);  
        }  
    }  
      
    public static void main(String[] args) throws Exception {  
        new SimpleChat().start();  
    }  
  
}  
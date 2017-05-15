package org.base.jgroups;

import java.util.List;

import org.jgroups.JChannel;
import org.jgroups.MembershipListener;
import org.jgroups.Message;
import org.jgroups.MessageListener;
import org.jgroups.View;
import org.jgroups.blocks.MethodCall;
import org.jgroups.blocks.RequestOptions;
import org.jgroups.blocks.ResponseMode;
import org.jgroups.blocks.RpcDispatcher;
import org.jgroups.util.RspList;
import org.jgroups.util.Util;
import org.apache.log4j.Logger;
/*
 * As before, the example creates a channel off of an XML configuration. It defines a method print() 
 * which will be called by the RpcDispatcher. Then an instance of RpcDispatcher is created on top of 
 * the channel and the channel is connected (this joins the new member to the group). Now messages 
 * can be sent and received. But instead of sending/receiving messages using the channel, the application 
 * invokes a remote method call using RpcDispatcher's callRemoteMethods().

The first argument 'null' means send to all cluster nodes, "print" is the name of the method to be 
invoked, 'new Integer(i)' is the argument to the print() method, GET_ALL means wait until the responses 
from all group members have been received and '0' specifies the timeout (in this case, it means wait 
forever). RpcDispatcher sends a multicast message (containing the method call) to all members 
(e.g. 4 members, including itself) and waits for 4 replies. If one or more of the members crash 
in the meantime, the call nevertheless returns and has those replies marked as 'suspected' in the 
response list. The response list contains an entry for each expected reply, which has the address of 
the replier, the value (if any, in our case it is an integer), and a flag (received, not received (in 
case of timeouts) or suspected). If this member is the only group member, then the method call will 
call its own print() method.
 */
public class RpcDispatcherTest {

	/*
	 * private final static Logger logger = Logger
	 * .getLogger(RpcDispatcherTest.class); MyMessageListener messageListener;
	 * MyMembershipListener membershipListener; JChannel channel; String name;
	 * RpcMethods rpcMethods; RpcDispatcher disp; public void start() { try {
	 * channel = new JChannel(); } catch (Exception e1) { // TODO Auto-generated
	 * catch block e1.printStackTrace(); } if (null != name) {
	 * channel.setName(name); }
	 * 
	 * messageListener = new MyMessageListener(); membershipListener = new
	 * MyMembershipListener(); rpcMethods = new RpcMethods(); disp = new
	 * RpcDispatcher(channel,this);//, messageListener, membershipListener,
	 * //rpcMethods); try { channel.connect("RpcDispatcherContentTestGroup"); }
	 * catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 * 
	 * public class MyMembershipListener implements MembershipListener { public
	 * void viewAccepted(View view) { logger.info("ViewAccepted, " + view);
	 * System.out.println("ViewAccepted, " + view); } }
	 * 
	 * public class MyMessageListener implements MessageListener{
	 * 
	 * @Override public void receive(Message msg) { // TODO Auto-generated
	 * method stub System.out.println("MyMessageListener, " + msg.getObject());
	 * }
	 * 
	 * }
	 * 
	 * public class RpcMethods { public String getNodeName(String name) {
	 * logger.info(name); return channel.getName(); } }
	 */

	JChannel channel;
	RpcDispatcher disp;
	RspList rsp_list;
	String props = "network-udp.xml";

	public int print(int number) {
		System.out.println("print(" + number + ")");
		return number * 2;
	}

	public void start() throws Exception {
		channel = new JChannel(props);
		disp = new RpcDispatcher(channel, this);
		channel.connect("RpcDispatcherTestGroup");

		String param = channel.getName();
		MethodCall call = new MethodCall("print", new Object[] { 10 },
				new Class[] { int.class });
		// logger.info("Call all members getNodeName()");
		RequestOptions requestOptions = new RequestOptions(
				ResponseMode.GET_ALL, 0);
		rsp_list = disp.callRemoteMethods(null, call, requestOptions);

		System.out.println("Responses:");
		List<String> list = rsp_list.getResults();
		for (Object obj : list) {
			System.out.println("  " + obj);
		}
		for (int i = 0; i < 100; i++) {
			Util.sleep(100);
			Object obj[] = { i };
			Class cls[] = { int.class };
			rsp_list = disp.callRemoteMethods(null, "print", obj, cls,
					RequestOptions.SYNC());// GroupRequest.GET_ALL, // 0);
			System.out.println("Responses: " + rsp_list.size());
		}
		channel.close();
	}

	public static void main(String[] args) {
		try {
			new RpcDispatcherTest().start();
		} catch (Exception e) { // TODO
			e.printStackTrace();
		}
	}
}

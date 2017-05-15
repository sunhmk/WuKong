package org.base.jgroups.raft;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.jgroups.JChannel;
import org.jgroups.protocols.raft.RAFT;
import org.jgroups.util.Util;

/**
 * Test jgroups raft algorithm implementation.
 */
public class JGroupsRaftTest {

    private static final String CLUSTER_NAME = "ctr-cluster";
    private static final String COUNTER_NAME = "counter";
    private static final String RAFT_XML = "raft.xml";

    public static void main(String[] args) throws Exception {
    	String path = JChannel.class.getClassLoader().getResource("jg-magic-map.xml").getFile().substring(0,JChannel.class.getClassLoader().getResource("jg-magic-map.xml").getFile().lastIndexOf("/"));
       
        File programRootDir = new File(path + "/");
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    	URLClassLoader classLoader = (URLClassLoader) classloader;//ClassLoader.getSystemClassLoader();
    	Method add = null;
		try {
			add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	add.setAccessible(true);
    	try {
			add.invoke(classLoader, programRootDir.toURI().toURL());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	java.util.Random rd = new java.util.Random();
    	 JChannel ch = new JChannel(RAFT_XML).name(args[0]);
         CounterService counter = new CounterService(ch);
        try {
            doConnect(ch, CLUSTER_NAME);
            doLoop(ch, counter);
        } finally {
            Util.close(ch);
        }
    }

    private static void doConnect(JChannel ch, String clusterName) throws Exception {
        ch.connect(clusterName);
    }

    private static void doLoop(JChannel ch, CounterService counter) {
        boolean looping = true;
        while (looping) {
            int key = Util.keyPress("\n[0] Create [1] Increment [2] Decrement [3] Dump log [4] Snapshot [x] Exit\n" +
                    "first-applied=" + ((RAFT) ch.getProtocolStack().findProtocol(RAFT.class)).log().firstApplied() +
                    ", last-applied=" + counter.lastApplied() +
                    ", commit-index=" + counter.commitIndex() +
                    ", log size=" + Util.printBytes(counter.logSize()) + ": ");

            if ((key == '0' || key == '1' || key == '2') && !counter.isLeaderExist()) {
                System.out.println("Cannot perform cause there is no leader by now");
                continue;
            }

            long val;
            switch (key) {
                case '0':
                    counter.getOrCreateCounter(COUNTER_NAME, 1L);
                    break;
                case '1':
                    val = counter.incrementAndGet(COUNTER_NAME);
                    System.out.printf("%s: %s\n", COUNTER_NAME, val);
                    break;
                case '2':
                    val = counter.decrementAndGet(COUNTER_NAME);
                    System.out.printf("%s: %s\n", COUNTER_NAME, val);
                    break;
                case '3':
                    counter.dumpLog();
                    break;
                case '4':
                    counter.snapshot();
                    break;
                case 'x':
                    looping = false;
                    break;
                case '\n':
                    System.out.println(COUNTER_NAME + ": " + counter.get(COUNTER_NAME) + "\n");
                    break;
            }
        }
    }

}
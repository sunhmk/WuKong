package org.base.rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

import javax.naming.Context;
import javax.naming.InitialContext;

public class Server {
	public static void main(String[] args) {
		try {
			// 实例化实现了IService接口的远程服务ServiceImpl对象
			IService service02 = new ServiceImpl("service02");
			// 备注：如果不想在控制台上开启RMI注册程序RMIRegistry的话，可在RMI服务类程序中添加LocateRegistry.createRegistry(8888);
			LocateRegistry.createRegistry(8888);
			Naming.bind("rmi://localhost:8888/RHello", service02);
			// 初始化命名空间
			// Context namingContext = new InitialContext();
			// 将名称绑定到对象,即向命名空间注册已经实例化的远程服务对象
			// namingContext.rebind("rmi://localhost/service02", service02);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("服务器向命名表注册了1个远程服务对象！");
	}
}
